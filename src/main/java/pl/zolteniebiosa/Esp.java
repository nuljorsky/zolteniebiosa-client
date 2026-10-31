package pl.zolteniebiosa;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4fStack;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Esp {
    public record Hit(BlockPos pos, int rgb) { }

    public static volatile List<Hit> espHits = List.of();
    public static volatile List<Hit> xrayHits = List.of();

    // ===== presety =====
    private static void add(Setting s, String... ids) {
        for (String i : ids) s.blocks.add(i);
    }

    public static void preset(String key, Setting s) {
        switch (key) {
            case "p.diamond" -> add(s, "diamond_ore", "deepslate_diamond_ore");
            case "p.netherite" -> add(s, "ancient_debris");
            case "p.gold" -> add(s, "gold_ore", "deepslate_gold_ore", "nether_gold_ore");
            case "p.iron" -> add(s, "iron_ore", "deepslate_iron_ore");
            case "p.emerald" -> add(s, "emerald_ore", "deepslate_emerald_ore");
            case "p.ores" -> {
                for (Identifier id : Registries.BLOCK.getIds()) {
                    if (id.getNamespace().equals("minecraft")
                            && (id.getPath().endsWith("_ore") || id.getPath().equals("ancient_debris"))) {
                        s.blocks.add(id.getPath());
                    }
                }
            }
            case "p.clear" -> s.blocks.clear();
            default -> { }
        }
    }

    private static Set<Block> resolve(Setting s) {
        Set<Block> r = new HashSet<>();
        for (String id : s.blocks) {
            Identifier i = Identifier.tryParse(id.contains(":") ? id : "minecraft:" + id);
            if (i != null && Registries.BLOCK.containsId(i)) r.add(Registries.BLOCK.get(i));
        }
        return r;
    }

    private static int autoColor(Block b) {
        String p = Registries.BLOCK.getId(b).getPath();
        if (p.contains("diamond")) return 0x3EFFF0;
        if (p.contains("emerald")) return 0x2BFF6A;
        if (p.contains("gold")) return 0xFFD700;
        if (p.contains("iron")) return 0xE8C8A8;
        if (p.contains("coal")) return 0x777777;
        if (p.contains("redstone")) return 0xFF2020;
        if (p.contains("lapis")) return 0x3050FF;
        if (p.contains("copper")) return 0xFF8030;
        if (p.contains("ancient_debris")) return 0xB5651D;
        if (p.contains("quartz")) return 0xFFFFFF;
        if (p.contains("spawner")) return 0xA43EFF;
        return Color.HSBtoRGB((Math.abs(p.hashCode()) % 1000) / 1000f, 0.8f, 1f) & 0xFFFFFF;
    }

    public static List<Hit> scan(Mod m, MinecraftClient mc) {
        if (mc.world == null || mc.player == null) return List.of();
        Set<Block> wanted = resolve(m.get("blocks"));
        if (wanted.isEmpty()) return List.of();
        int r = m.n("dist");
        boolean auto = m.mode("colormode") == 0;
        int cidx = m.get("color").idx;
        int custom = cidx <= 0 ? -1 : (Theme.PAL[Math.min(cidx, Theme.PAL.length - 1)] & 0xFFFFFF);
        BlockPos o = mc.player.getBlockPos();
        int minY = Math.max(mc.world.getBottomY(), o.getY() - r);
        int maxY = Math.min(mc.world.getTopYInclusive(), o.getY() + r);
        List<Hit> found = new ArrayList<>();
        BlockPos.Mutable mp = new BlockPos.Mutable();
        for (int x = o.getX() - r; x <= o.getX() + r; x++) {
            for (int z = o.getZ() - r; z <= o.getZ() + r; z++) {
                for (int y = minY; y <= maxY; y++) {
                    mp.set(x, y, z);
                    Block b = mc.world.getBlockState(mp).getBlock();
                    if (wanted.contains(b)) {
                        found.add(new Hit(mp.toImmutable(), auto ? autoColor(b) : custom));
                        if (found.size() >= 4000) return found;
                    }
                }
            }
        }
        return found;
    }

    public static void init() {
        WorldRenderEvents.LAST.register(Esp::render);
    }

    // ===== rysowanie =====
    private static int verts = 0;
    private static final double K = 0.00065;

    private static void vert(BufferBuilder b, double x, double y, double z, int rgb, int a) {
        b.vertex((float) x, (float) y, (float) z).color((rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255, a);
        verts++;
    }

    // wstega o stalej grubosci na ekranie (px), wspolrzedne wzgledem kamery
    private static void ribbon(BufferBuilder b, double ax, double ay, double az, double bx, double by, double bz,
                               float px, int rgb, int a) {
        double dx = bx - ax, dy = by - ay, dz = bz - az;
        double mx = (ax + bx) / 2, my = (ay + by) / 2, mz = (az + bz) / 2;
        double sx = dy * mz - dz * my, sy = dz * mx - dx * mz, sz = dx * my - dy * mx;
        double len = Math.sqrt(sx * sx + sy * sy + sz * sz);
        if (len < 1e-9) return;
        sx /= len; sy /= len; sz /= len;
        double da = Math.max(Math.sqrt(ax * ax + ay * ay + az * az), 0.1);
        double db = Math.max(Math.sqrt(bx * bx + by * by + bz * bz), 0.1);
        double wa = K * px * da, wb = K * px * db;
        vert(b, ax + sx * wa, ay + sy * wa, az + sz * wa, rgb, a);
        vert(b, ax - sx * wa, ay - sy * wa, az - sz * wa, rgb, a);
        vert(b, bx - sx * wb, by - sy * wb, bz - sz * wb, rgb, a);
        vert(b, bx + sx * wb, by + sy * wb, bz + sz * wb, rgb, a);
    }

    private static void quad(BufferBuilder b, double[] p1, double[] p2, double[] p3, double[] p4, int rgb, int a) {
        vert(b, p1[0], p1[1], p1[2], rgb, a);
        vert(b, p2[0], p2[1], p2[2], rgb, a);
        vert(b, p3[0], p3[1], p3[2], rgb, a);
        vert(b, p4[0], p4[1], p4[2], rgb, a);
    }

    private static void box(BufferBuilder b, double x0, double y0, double z0, int rgb, boolean fill) {
        double e = 0.002;
        double ax = x0 - e, ay = y0 - e, az = z0 - e, bx = x0 + 1 + e, by = y0 + 1 + e, bz = z0 + 1 + e;
        if (fill) {
            double[] c000 = {ax, ay, az}, c100 = {bx, ay, az}, c110 = {bx, by, az}, c010 = {ax, by, az};
            double[] c001 = {ax, ay, bz}, c101 = {bx, ay, bz}, c111 = {bx, by, bz}, c011 = {ax, by, bz};
            quad(b, c000, c100, c110, c010, rgb, 55);
            quad(b, c001, c101, c111, c011, rgb, 55);
            quad(b, c000, c001, c011, c010, rgb, 55);
            quad(b, c100, c101, c111, c110, rgb, 55);
            quad(b, c000, c100, c101, c001, rgb, 55);
            quad(b, c010, c110, c111, c011, rgb, 55);
        }
        double[][] ed = {
                {ax, ay, az, bx, ay, az}, {bx, ay, az, bx, ay, bz}, {bx, ay, bz, ax, ay, bz}, {ax, ay, bz, ax, ay, az},
                {ax, by, az, bx, by, az}, {bx, by, az, bx, by, bz}, {bx, by, bz, ax, by, bz}, {ax, by, bz, ax, by, az},
                {ax, ay, az, ax, by, az}, {bx, ay, az, bx, by, az}, {bx, ay, bz, bx, by, bz}, {ax, ay, bz, ax, by, bz}
        };
        for (double[] l : ed) ribbon(b, l[0], l[1], l[2], l[3], l[4], l[5], 1.6f, rgb, 255);
    }

    private static void drawHits(BufferBuilder b, Vec3d cam, List<Hit> hits, boolean fill) {
        for (Hit h : hits) {
            int rgb = h.rgb();
            if (rgb == -1) rgb = Theme.rainbow((h.pos().getX() + h.pos().getZ()) * 0.013f, 0.8f) & 0xFFFFFF;
            box(b, h.pos().getX() - cam.x, h.pos().getY() - cam.y, h.pos().getZ() - cam.z, rgb, fill);
        }
    }

    private static void render(WorldRenderContext ctx) {
        boolean t = Mods.TRACERS.enabled, e = Mods.BLOCKESP.enabled, x = Mods.XRAY.enabled;
        if (!t && !e && !x) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;

        Vec3d cam = ctx.camera().getPos();
        float td = ctx.tickCounter().getTickDelta(true);

        Matrix4fStack mv = RenderSystem.getModelViewStack();
        mv.pushMatrix();
        try {
            mv.identity();
            mv.mul(ctx.positionMatrix());
            RenderSystem.disableDepthTest();
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

            verts = 0;
            BufferBuilder b = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            if (x) drawHits(b, cam, xrayHits, Mods.XRAY.b("fill"));
            if (e) drawHits(b, cam, espHits, Mods.BLOCKESP.b("fill"));

            if (t) {
                Vec3d look = Vec3d.fromPolar(ctx.camera().getPitch(), ctx.camera().getYaw());
                double sx = look.x, sy = look.y, sz = look.z;
                int tm = Mods.TRACERS.mode("target");
                int maxD = Mods.TRACERS.n("dist");
                float px = Mods.TRACERS.n("width");
                int rgb = Theme.pal(Mods.TRACERS.get("color").idx, 0f) & 0xFFFFFF;
                for (Entity en : mc.world.getEntities()) {
                    if (en == mc.player) continue;
                    boolean isP = en instanceof PlayerEntity;
                    boolean isM = en instanceof MobEntity;
                    if (!((tm == 0 && isP) || (tm == 1 && isM) || (tm == 2 && (isP || isM)))) continue;
                    if (en.distanceTo(mc.player) > maxD) continue;
                    Vec3d p = en.getLerpedPos(td).add(0, en.getHeight() * 0.5, 0).subtract(cam);
                    ribbon(b, sx, sy, sz, p.x, p.y, p.z, px, rgb, 255);
                }
            }

            BuiltBuffer built = b.endNullable();
            if (built != null) BufferRenderer.drawWithGlobalProgram(built);
        } finally {
            mv.popMatrix();
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
    }
}
