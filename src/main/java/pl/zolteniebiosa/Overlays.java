package pl.zolteniebiosa;

import java.util.ArrayDeque;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

/** Nakladki na ekran: pogoda, celownik, keystrokes + CPS, status pancerza, "teraz gra". */
public final class Overlays {
    private static final ArrayDeque<Long> LMB = new ArrayDeque<>(), RMB = new ArrayDeque<>();
    private static boolean lWas, rWas;

    private Overlays() { }

    public static void render(DrawContext ctx, RenderTickCounter tc) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;
        int W = ctx.getScaledWindowWidth(), H = ctx.getScaledWindowHeight();
        Weather.render(ctx, W, H);
        Crosshair.render(ctx, W, H);
        if (Mods.KEYSTROKES.enabled) keystrokes(ctx, mc, W, H);
        if (Mods.ARMORHUD.enabled) armor(ctx, mc, W, H);
        if (Mods.MUSIC.b("shownow") && Music.now != null) nowPlaying(ctx, mc, W);
    }

    // ===================== keystrokes + CPS =====================
    private static int cps(ArrayDeque<Long> q, long now) {
        while (!q.isEmpty() && now - q.peekFirst() > 1000) q.pollFirst();
        return q.size();
    }

    private static void key(DrawContext ctx, TextRenderer tr, int x, int y, int w, int h, String label, boolean down, int accent) {
        ctx.fill(x, y, x + w, y + h, down ? Art.A(accent, 0.75f) : 0x88000000);
        ctx.drawBorder(x, y, w, h, down ? 0xFFFFFFFF : 0x55FFFFFF);
        ctx.drawCenteredTextWithShadow(tr, label, x + w / 2, y + (h - 8) / 2, down ? 0xFF000000 : 0xFFFFFFFF);
    }

    private static void keystrokes(DrawContext ctx, MinecraftClient mc, int W, int H) {
        long now = System.currentTimeMillis();
        long win = mc.getWindow().getHandle();
        boolean l = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean r = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        if (mc.currentScreen == null) {
            if (l && !lWas) LMB.addLast(now);
            if (r && !rWas) RMB.addLast(now);
        }
        lWas = l;
        rWas = r;
        TextRenderer tr = mc.textRenderer;
        int accent = Theme.accent(0f);
        int x = 8, y = H / 2 - 30;
        key(ctx, tr, x + 22, y, 20, 20, "W", mc.options.forwardKey.isPressed(), accent);
        key(ctx, tr, x, y + 22, 20, 20, "A", mc.options.leftKey.isPressed(), accent);
        key(ctx, tr, x + 22, y + 22, 20, 20, "S", mc.options.backKey.isPressed(), accent);
        key(ctx, tr, x + 44, y + 22, 20, 20, "D", mc.options.rightKey.isPressed(), accent);
        String ls = Mods.KEYSTROKES.b("cps") ? "LMB " + cps(LMB, now) : "LMB";
        String rs = Mods.KEYSTROKES.b("cps") ? "RMB " + cps(RMB, now) : "RMB";
        key(ctx, tr, x, y + 44, 31, 20, ls, l, accent);
        key(ctx, tr, x + 33, y + 44, 31, 20, rs, r, accent);
        key(ctx, tr, x, y + 66, 64, 14, "SPACE", mc.options.jumpKey.isPressed(), accent);
    }

    // ===================== pancerz =====================
    private static void armor(DrawContext ctx, MinecraftClient mc, int W, int H) {
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        TextRenderer tr = mc.textRenderer;
        int x = W - 62;
        int y = H - 24 - 4 * 18;
        for (EquipmentSlot s : slots) {
            ItemStack st = mc.player.getEquippedStack(s);
            if (!st.isEmpty()) {
                ctx.drawItem(st, x, y);
                if (st.getMaxDamage() > 0) {
                    int pct = (st.getMaxDamage() - st.getDamage()) * 100 / st.getMaxDamage();
                    int col = pct > 60 ? 0xFF55FF55 : pct > 25 ? 0xFFFFDD55 : 0xFFFF5555;
                    ctx.drawTextWithShadow(tr, pct + "%", x + 20, y + 4, col);
                } else {
                    ctx.drawTextWithShadow(tr, "-", x + 20, y + 4, 0xFFAAAAAA);
                }
            }
            y += 18;
        }
    }

    // ===================== teraz gra =====================
    private static void nowPlaying(DrawContext ctx, MinecraftClient mc, int W) {
        String line = Music.nowLine();
        TextRenderer tr = mc.textRenderer;
        String time = Music.posSec / 60 + ":" + String.format("%02d", Music.posSec % 60);
        String full = line + "  " + time;
        int w = tr.getWidth(full);
        int x = W / 2 - w / 2, y = 4;
        ctx.fill(x - 5, y - 2, x + w + 5, y + 10, 0x99000000);
        ctx.fill(x - 5, y + 10, x + w + 5, y + 11, Theme.accent((System.currentTimeMillis() % 3000) / 3000f));
        ctx.drawTextWithShadow(tr, full, x, y, 0xFFFFFFFF);
    }
}
