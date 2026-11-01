package pl.zolteniebiosa;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;

public class NameTags {
    public static void render(DrawContext ctx, RenderTickCounter tc) {
        if (!Mods.NAMETAGS.enabled) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null || mc.options.hudHidden) return;
        Camera cam = mc.gameRenderer.getCamera();
        Vec3d cp = cam.getPos();
        int W = ctx.getScaledWindowWidth(), H = ctx.getScaledWindowHeight();
        double tanY = Math.tan(Math.toRadians(mc.options.getFov().getValue()) / 2.0);
        double aspect = (double) W / H;
        Vec3d f = Vec3d.fromPolar(cam.getPitch(), cam.getYaw());
        Vec3d r = f.crossProduct(new Vec3d(0, 1, 0)).normalize();
        Vec3d u = r.crossProduct(f);
        float td = tc.getTickDelta(true);
        TextRenderer tr = mc.textRenderer;
        int tm = Mods.NAMETAGS.mode("target");
        double maxD = Mods.NAMETAGS.n("maxdist");
        float sc = Mods.NAMETAGS.n("scale") / 100f;
        boolean showArmor = Mods.NAMETAGS.b("armor"), showItems = Mods.NAMETAGS.b("items");
        boolean showHearts = Mods.NAMETAGS.b("hearts"), showDist = Mods.NAMETAGS.b("showdist");
        int count = 0;

        for (Entity en : mc.world.getEntities()) {
            if (en == mc.player || !(en instanceof LivingEntity le)) continue;
            boolean isP = en instanceof PlayerEntity;
            boolean isM = en instanceof MobEntity;
            if (!((tm == 0 && isP) || (tm == 1 && isM) || (tm == 2 && (isP || isM)))) continue;
            double dist = en.distanceTo(mc.player);
            if (dist > maxD) continue;
            Vec3d pos = en.getLerpedPos(td).add(0, en.getHeight() + 0.45, 0);
            Vec3d d = pos.subtract(cp);
            double df = d.dotProduct(f);
            if (df < 0.1) continue;
            double sx = W / 2.0 * (1 + (d.dotProduct(r) / df) / (tanY * aspect));
            double sy = H / 2.0 * (1 - (d.dotProduct(u) / df) / tanY);
            if (sx < -80 || sx > W + 80 || sy < -80 || sy > H + 80) continue;
            if (++count > 40) break;

            String name = en.getName().getString();
            List<String[]> parts = new ArrayList<>();
            parts.add(new String[]{name, isP ? "FFFFFF" : "FFE066"});
            if (showHearts) {
                float hp = le.getHealth() + le.getAbsorptionAmount();
                parts.add(new String[]{"\u2665 " + String.format(Locale.ROOT, "%.1f", hp), "FF5555"});
            }
            if (showDist) parts.add(new String[]{(int) dist + "m", "AAAAAA"});

            int totalW = 0;
            for (String[] p : parts) totalW += tr.getWidth(p[0]) + 6;
            totalW -= 6;

            ctx.getMatrices().push();
            ctx.getMatrices().translate(sx, sy, 0.0);
            ctx.getMatrices().scale(sc, sc, 1f);

            ctx.fill(-totalW / 2 - 4, -13, totalW / 2 + 4, -2, 0x90000000);
            int cx = -totalW / 2;
            for (String[] p : parts) {
                ctx.drawTextWithShadow(tr, p[0], cx, -11, 0xFF000000 | Integer.parseInt(p[1], 16));
                cx += tr.getWidth(p[0]) + 6;
            }

            if (showArmor || showItems) {
                List<ItemStack> row = new ArrayList<>();
                if (showItems) row.add(le.getMainHandStack());
                if (showArmor) {
                    row.add(le.getEquippedStack(EquipmentSlot.HEAD));
                    row.add(le.getEquippedStack(EquipmentSlot.CHEST));
                    row.add(le.getEquippedStack(EquipmentSlot.LEGS));
                    row.add(le.getEquippedStack(EquipmentSlot.FEET));
                }
                if (showItems) row.add(le.getOffHandStack());
                int iw = 13;
                int rx = -row.size() * iw / 2;
                for (ItemStack st : row) {
                    if (!st.isEmpty()) {
                        ctx.getMatrices().push();
                        ctx.getMatrices().translate(rx, -29, 0.0);
                        ctx.getMatrices().scale(0.8f, 0.8f, 1f);
                        ctx.drawItem(st, 0, 0);
                        ctx.getMatrices().pop();
                    }
                    rx += iw;
                }
            }
            ctx.getMatrices().pop();
        }
    }
}
