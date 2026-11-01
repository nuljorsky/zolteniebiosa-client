package pl.zolteniebiosa;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

/** Target HUD, combo counter, damage popup, alarm niskiego HP, kompas PvP - zakladka MODY PvP. */
public final class PvpHud {
    private record Popup(String text, long t0) { }

    private static final List<Popup> POPUPS = new ArrayList<>();
    private static UUID comboTarget;
    private static int combo;
    private static long comboLast;
    private static UUID pendingTarget;
    private static float pendingHealth;
    private static long pendingAt;

    private PvpHud() { }

    /** Wywolywane z AttackEntityCallback - liczy combo i przygotowuje pomiar obrazen. */
    public static void onHit(Entity target) {
        long now = System.currentTimeMillis();
        if (Mods.COMBO.enabled) {
            if (target.getUuid().equals(comboTarget) && now - comboLast < 2500) combo++;
            else combo = 1;
            comboTarget = target.getUuid();
            comboLast = now;
        }
        if (Mods.DMGPOP.enabled && target instanceof LivingEntity le) {
            pendingTarget = target.getUuid();
            pendingHealth = le.getHealth();
            pendingAt = now;
        }
    }

    /** Wolane co tick - domierza obrazenia ~150ms po trafieniu (serwer zdazy przyslac nowe HP). */
    public static void tick(MinecraftClient mc) {
        if (pendingTarget == null || mc.world == null) return;
        if (System.currentTimeMillis() - pendingAt < 150) return;
        Entity e = null;
        for (Entity en : mc.world.getEntities()) {
            if (en.getUuid().equals(pendingTarget)) {
                e = en;
                break;
            }
        }
        if (e instanceof LivingEntity le) {
            float dmg = pendingHealth - le.getHealth();
            if (dmg > 0.05f) {
                POPUPS.add(new Popup(String.format("-%.1f", dmg), System.currentTimeMillis()));
            }
        }
        pendingTarget = null;
    }

    public static void render(DrawContext ctx, MinecraftClient mc, int W, int H) {
        if (Mods.TARGETHUD.enabled) targetHud(ctx, mc, W, H);
        if (Mods.COMBO.enabled) comboCounter(ctx, mc, W, H);
        if (Mods.DMGPOP.enabled) damagePopups(ctx, mc, W, H);
        if (Mods.LOWHP.enabled) lowHp(ctx, mc, W, H);
        if (Mods.COMPASS.enabled) compass(ctx, mc, W, H);
    }

    private static void targetHud(DrawContext ctx, MinecraftClient mc, int W, int H) {
        Entity t = mc.targetedEntity;
        if (!(t instanceof LivingEntity le) || !(t instanceof PlayerEntity || t.getType().isSaveable())) return;
        TextRenderer tr = mc.textRenderer;
        String name = t.getName().getString();
        float hp = le.getHealth(), max = le.getMaxHealth();
        int w = 110, h = 28;
        int x = W / 2 - w / 2, y = H / 2 + 18;
        ctx.fill(x, y, x + w, y + h, 0xAA0A0A18);
        ctx.drawBorder(x, y, w, h, Theme.accent(0.3f));
        ctx.drawCenteredTextWithShadow(tr, tr.trimToWidth(name, w - 8), x + w / 2, y + 4, 0xFFFFFFFF);
        int bw = w - 10, bx = x + 5, by = y + 16;
        ctx.fill(bx, by, bx + bw, by + 6, 0xFF2A1010);
        float f = max > 0 ? MathHelper.clamp(hp / max, 0f, 1f) : 0f;
        int col = f > 0.5f ? 0xFF55DD55 : f > 0.25f ? 0xFFEEDD44 : 0xFFEE4444;
        ctx.fill(bx, by, bx + (int) (bw * f), by + 6, col);
        ctx.fill(bx, by, bx + bw, by + 1, 0x40FFFFFF);
    }

    private static void comboCounter(DrawContext ctx, MinecraftClient mc, int W, int H) {
        if (combo < 2 || System.currentTimeMillis() - comboLast > 2500) return;
        TextRenderer tr = mc.textRenderer;
        String s = combo + "x COMBO";
        int scale = Math.min(3, 1 + combo / 4);
        ctx.getMatrices().push();
        ctx.getMatrices().translate(W / 2f, H * 0.32f, 0);
        ctx.getMatrices().scale(scale, scale, 1f);
        int col = Theme.rainbow((System.currentTimeMillis() % 2000) / 2000f, 0.7f);
        ctx.drawCenteredTextWithShadow(tr, s, 0, 0, col);
        ctx.getMatrices().pop();
    }

    private static void damagePopups(DrawContext ctx, MinecraftClient mc, int W, int H) {
        long now = System.currentTimeMillis();
        TextRenderer tr = mc.textRenderer;
        POPUPS.removeIf(p -> now - p.t0() > 900);
        int i = 0;
        for (Popup p : POPUPS) {
            float t = (now - p.t0()) / 900f;
            int x = W / 2 + 60, y = (int) (H / 2f - 10 - t * 22) - i * 2;
            int alpha = (int) (255 * (1 - t));
            ctx.drawTextWithShadow(tr, p.text(), x, y, Art.A(0xFFFF5050, alpha / 255f));
            i++;
        }
    }

    private static void lowHp(DrawContext ctx, MinecraftClient mc, int W, int H) {
        if (mc.player == null) return;
        float hp = mc.player.getHealth();
        if (hp <= 0 || hp > Mods.LOWHP.n("hp")) return;
        float pulse = (float) (0.35 + 0.25 * Math.sin(System.currentTimeMillis() / 220.0));
        int edge = Math.max(20, H / 7);
        int col = Art.A(0xFFCC1010, pulse);
        ctx.fillGradient(0, 0, W, edge, col, 0);
        ctx.fillGradient(0, H - edge, W, H, 0, col);
        ctx.fillGradient(0, 0, edge, H, col, 0);
        ctx.fillGradient(W - edge, 0, W, H, 0, col);
    }

    private static void compass(DrawContext ctx, MinecraftClient mc, int W, int H) {
        if (mc.player == null || mc.world == null) return;
        AbstractClientPlayerEntity nearest = null;
        double best = Double.MAX_VALUE;
        for (AbstractClientPlayerEntity p : mc.world.getPlayers()) {
            if (p == mc.player) continue;
            double d = p.squaredDistanceTo(mc.player);
            if (d < best) {
                best = d;
                nearest = p;
            }
        }
        if (nearest == null) return;
        double dx = nearest.getX() - mc.player.getX();
        double dz = nearest.getZ() - mc.player.getZ();
        double angleTo = Math.toDegrees(Math.atan2(dz, dx)) - 90;
        double rel = MathHelper.wrapDegrees(angleTo - mc.player.getYaw());
        int cx = W / 2, cy = 20;
        ctx.getMatrices().push();
        ctx.getMatrices().translate(cx, cy, 0);
        ctx.getMatrices().multiply(new org.joml.Quaternionf().rotateZ((float) Math.toRadians(rel)));
        int col = Theme.accent(0f);
        ctx.fill(-5, -2, 5, 2, col);
        ctx.fill(-2, -8, 2, 2, col);
        // grot strzalki
        for (int k = 0; k < 6; k++) ctx.fill(-k, -8 - k, k + 1, -7 - k, col);
        ctx.getMatrices().pop();
        if (Mods.COMPASS.b("speed")) {
            double sp = Math.hypot(mc.player.getX() - mc.player.prevX, mc.player.getZ() - mc.player.prevZ) * 20;
            String s = String.format("%.1f m/s", sp);
            ctx.drawCenteredTextWithShadow(mc.textRenderer, s, cx, cy + 14, 0xFFCCCCEE);
        }
    }
}
