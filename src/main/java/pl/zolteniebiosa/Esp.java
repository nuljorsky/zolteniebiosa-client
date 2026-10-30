package pl.zolteniebiosa;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.*;

public class Esp {
    public record Hit(BlockPos pos, int rgb) {}

    public static volatile List<Hit> hits = List.of();

    public static final String[] PRESET_NAMES =
            {"Diamenty", "Netherite", "Zloto", "Zelazo", "Szmaragdy", "Wszystkie rudy"};
    private static final Map<Block, Integer> COLORS = new HashMap<>();
    private static final List<Set<Block>> PRESETS = new ArrayList<>();

    static {
        c(0x00FFFF, Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE);
        c(0xA0522D, Blocks.ANCIENT_DEBRIS);
        c(0xFFD700, Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.NETHER_GOLD_ORE);
        c(0xD8AF93, Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE);
        c(0x00FF55, Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE);
        c(0x555555, Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE);
        c(0xFF2020, Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE);
        c(0x3050FF, Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE);
        c(0xFF8030, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE);
        c(0xFFFFFF, Blocks.NETHER_QUARTZ_ORE);

        PRESETS.add(Set.of(Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE));
        PRESETS.add(Set.of(Blocks.ANCIENT_DEBRIS));
        PRESETS.add(Set.of(Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.NETHER_GOLD_ORE));
        PRESETS.add(Set.of(Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE));
        PRESETS.add(Set.of(Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE));
        PRESETS.add(COLORS.keySet());
    }

    private static void c(int rgb, Block... blocks) {
        for (Block b : blocks) COLORS.put(b, rgb);
    }

    public static void scan(MinecraftClient mc) {
        if (mc.world == null || mc.player == null) return;
        Set<Block> wanted = PRESETS.get(Config.espPreset);
        int r = Config.espDist;
        BlockPos o = mc.player.getBlockPos();
        int minY = Math.max(mc.world.getBottomY(), o.getY() - r);
        int maxY = Math.min(mc.world.getTopYInclusive(), o.getY() + r);
        List<Hit> found = new ArrayList<>();
        BlockPos.Mutable m = new BlockPos.Mutable();
        for (int x = o.getX() - r; x <= o.getX() + r; x++) {
            for (int z = o.getZ() - r; z <= o.getZ() + r; z++) {
                for (int y = minY; y <= maxY; y++) {
                    m.set(x, y, z);
                    Block b = mc.world.getBlockState(m).getBlock();
                    if (wanted.contains(b)) {
                        found.add(new Hit(m.toImmutable(), COLORS.get(b)));
                        if (found.size() > 3000) { hits = found; return; }
                    }
                }
            }
        }
        hits = found;
    }

    public static void init() {
        WorldRenderEvents.LAST.register(Esp::render);
    }

    private static int verts = 0;

    private static void render(WorldRenderContext ctx) {
        if (!Config.tracers && !Config.blockEsp) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;

        Matrix4f mat = ctx.positionMatrix();
        Vec3d cam = ctx.camera().getPos();
        float td = ctx.tickCounter().getTickDelta(true);

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        verts = 0;
        BufferBuilder b = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        if (Config.tracers) {
            RenderSystem.lineWidth(2.5f);
            Vec3d look = Vec3d.fromPolar(ctx.camera().getPitch(), ctx.camera().getYaw());
            Vec3d start = cam.add(look.multiply(1.0));
            int mode = Config.tracerMode;
            if (mode == 0 || mode == 2) {
                for (PlayerEntity p : mc.world.getPlayers()) {
                    if (p == mc.player) continue;
                    if (p.distanceTo(mc.player) > Config.tracerDist) continue;
                    line(b, mat, cam, start, p.getLerpedPos(td).add(0, p.getHeight() / 2, 0), 0xFFFFFF);
                }
            }
            if (mode == 1 || mode == 2) {
                for (Entity e : mc.world.getEntities()) {
                    if (!(e instanceof MobEntity)) continue;
                    if (e.distanceTo(mc.player) > Config.tracerDist) continue;
                    line(b, mat, cam, start, e.getLerpedPos(td).add(0, e.getHeight() / 2, 0), 0xFFFFFF);
                }
            }
        }

        if (Config.blockEsp) {
            for (Hit h : hits) {
                box(b, mat, cam, h.pos(), h.rgb());
            }
        }

        if (verts > 0) {
            BufferRenderer.drawWithGlobalProgram(b.end());
        }

        RenderSystem.lineWidth(1f);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void vert(BufferBuilder b, Matrix4f m, Vec3d cam, double x, double y, double z, int rgb) {
        b.vertex(m, (float) (x - cam.x), (float) (y - cam.y), (float) (z - cam.z))
                .color((rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255, 255);
        verts++;
    }

    private static void line(BufferBuilder b, Matrix4f m, Vec3d cam, Vec3d a, Vec3d c, int rgb) {
        vert(b, m, cam, a.x, a.y, a.z, rgb);
        vert(b, m, cam, c.x, c.y, c.z, rgb);
    }

    private static void box(BufferBuilder b, Matrix4f m, Vec3d cam, BlockPos p, int rgb) {
        double x0 = p.getX(), y0 = p.getY(), z0 = p.getZ();
        double x1 = x0 + 1, y1 = y0 + 1, z1 = z0 + 1;
        double[][] e = {
                {x0,y0,z0,x1,y0,z0},{x1,y0,z0,x1,y0,z1},{x1,y0,z1,x0,y0,z1},{x0,y0,z1,x0,y0,z0},
                {x0,y1,z0,x1,y1,z0},{x1,y1,z0,x1,y1,z1},{x1,y1,z1,x0,y1,z1},{x0,y1,z1,x0,y1,z0},
                {x0,y0,z0,x0,y1,z0},{x1,y0,z0,x1,y1,z0},{x1,y0,z1,x1,y1,z1},{x0,y0,z1,x0,y1,z1}
        };
        for (double[] l : e) {
            vert(b, m, cam, l[0], l[1], l[2], rgb);
            vert(b, m, cam, l[3], l[4], l[5], rgb);
        }
    }
}
