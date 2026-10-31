package pl.zolteniebiosa;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;

// Keystrokes + CPS, Armor HUD, lista efektow.
public class Hud2 {
    private static boolean lastL = false, lastR = false;
    private static final ArrayDeque<Long> LC = new ArrayDeque<>(), RC = new ArrayDeque<>();

    private static int cps(ArrayDeque<Long> q, long now) {
        while (!q.isEmpty() && now - q.peekFirst() > 1000) q.pollFirst();
        return q.size();
    }

    private static void key(DrawContext ctx, TextRenderer tr, int x, int y, int w, int h, String s, boolean down, int accent) {
        ctx.fill(x, y, x + w, y + h, down ? (0xC0 << 24) | (accent & 0xFFFFFF) : 0x90101020);
        ctx.drawBorder(x, y, w, h, down ? 0xFFFFFFFF : 0x50FFFFFF);
        ctx.drawText(tr, s, x + (w - tr.getWidth(s)) / 2, y + (h - 8) / 2, down ? 0xFF000000 : 0xFFFFFFFF, false);
    }

    public static void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;
        TextRenderer tr = mc.textRenderer;
        int W = ctx.getScaledWindowWidth(), H = ctx.getScaledWindowHeight();
        long now = System.currentTimeMillis();
        int accent = Theme.accent(0.2f);

        if (mc.currentScreen == null) {
            long win = mc.getWindow().getHandle();
            boolean l = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            boolean r = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
            if (l && !lastL) LC.addLast(now);
            if (r && !lastR) RC.addLast(now);
            lastL = l;
            lastR = r;
        }

        if (Mods.KEYSTROKES.enabled) {
            int x = 8, y = H / 2 - 40, k = 20, g = 2;
            key(ctx, tr, x + k + g, y, k, k, "W", mc.options.forwardKey.isPressed(), accent);
            key(ctx, tr, x, y + k + g, k, k, "A", mc.options.leftKey.isPressed(), accent);
            key(ctx, tr, x + k + g, y + k + g, k, k, "S", mc.options.backKey.isPressed(), accent);
            key(ctx, tr, x + 2 * (k + g), y + k + g, k, k, "D", mc.options.rightKey.isPressed(), accent);
            key(ctx, tr, x, y + 2 * (k + g), 3 * k + 2 * g, 12, "___", mc.options.jumpKey.isPressed(), accent);
            if (Mods.KEYSTROKES.b("cps")) {
                int w2 = (3 * k + 2 * g - g) / 2;
                key(ctx, tr, x, y + 3 * (k + g) - 6, w2, 22, "LPM " + cps(LC, now), lastL, accent);
                key(ctx, tr, x + w2 + g, y + 3 * (k + g) - 6, w2, 22, "PPM " + cps(RC, now), lastR, accent);
            }
        }

        if (Mods.ARMORHUD.enabled) {
            EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
            int x = W - 70, y = H / 2 - 40;
            for (int i = 0; i < 4; i++) {
                ItemStack st = mc.player.getEquippedStack(slots[i]);
                int yy = y + i * 22;
                ctx.fill(x - 3, yy - 2, x + 62, yy + 19, 0x90101020);
                ctx.drawBorder(x - 3, yy - 2, 65, 21, 0x40FFFFFF);
                if (st.isEmpty()) continue;
                ctx.drawItem(st, x, yy);
                if (st.getMaxDamage() > 0) {
                    int left = st.getMaxDamage() - st.getDamage();
                    float pct = left / (float) st.getMaxDamage();
                    int col = pct > 0.5f ? 0xFF6BFF3E : pct > 0.2f ? 0xFFFFE23E : 0xFFFF4040;
                    ctx.drawText(tr, String.valueOf(left), x + 20, yy + 2, col, true);
                    ctx.fill(x + 20, yy + 13, x + 20 + (int) (40 * pct), yy + 15, col);
                }
            }
        }

        if (Mods.EFFECTS.enabled) {
            int x = 8, y = 36;
            for (StatusEffectInstance e : mc.player.getStatusEffects()) {
                int sec = e.getDuration() / 20;
                String s = e.getEffectType().value().getName().getString() + " " + (e.getAmplifier() + 1)
                        + "  " + (sec / 60) + ":" + String.format("%02d", sec % 60);
                int w = tr.getWidth(s);
                ctx.fill(x - 3, y - 2, x + w + 4, y + 10, 0x90101020);
                ctx.fill(x - 3, y - 2, x - 1, y + 10, 0xFF000000 | e.getEffectType().value().getColor());
                ctx.drawTextWithShadow(tr, s, x + 2, y, 0xFFFFFFFF);
                y += 13;
            }
        }
    }
}
