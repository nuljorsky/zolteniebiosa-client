package pl.zolteniebiosa;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class Crosshair {
    public static void render(DrawContext ctx) {
        if (!Mods.CROSSHAIR.enabled) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden || mc.currentScreen != null || mc.player == null) return;
        int cx = ctx.getScaledWindowWidth() / 2, cy = ctx.getScaledWindowHeight() / 2;
        Mod m = Mods.CROSSHAIR;
        int style = m.mode("style"), size = m.n("size"), th = m.n("thick"), gap = m.n("gap");
        int col = Theme.pal(m.get("color").idx, 0f);
        int a = th / 2;
        switch (style) {
            case 0 -> {
                box(ctx, cx - gap - size, cy - a, cx - gap, cy - a + th, col);
                box(ctx, cx + gap, cy - a, cx + gap + size, cy - a + th, col);
                box(ctx, cx - a, cy - gap - size, cx - a + th, cy - gap, col);
                box(ctx, cx - a, cy + gap, cx - a + th, cy + gap + size, col);
            }
            case 1 -> {
                int d = Math.max(th, size / 3);
                box(ctx, cx - d / 2, cy - d / 2, cx - d / 2 + d, cy - d / 2 + d, col);
            }
            case 2 -> {
                for (int deg = 0; deg < 360; deg += 5) {
                    double r = Math.toRadians(deg);
                    int px = cx + (int) Math.round(Math.cos(r) * (size + 1)), py = cy + (int) Math.round(Math.sin(r) * (size + 1));
                    ctx.fill(px - th / 2, py - th / 2, px - th / 2 + th, py - th / 2 + th, col);
                }
            }
            case 3 -> {
                for (int k = gap; k < gap + size; k++) {
                    ctx.fill(cx + k, cy + k, cx + k + th, cy + k + th, col);
                    ctx.fill(cx - k - th, cy + k, cx - k, cy + k + th, col);
                    ctx.fill(cx + k, cy - k - th, cx + k + th, cy - k, col);
                    ctx.fill(cx - k - th, cy - k - th, cx - k, cy - k, col);
                }
            }
            default -> {
                int len = size + gap + 4;
                box(ctx, cx - len, cy - a, cx - gap, cy - a + th, col);
                box(ctx, cx + gap, cy - a, cx + len, cy - a + th, col);
                box(ctx, cx - a, cy - len, cx - a + th, cy - gap, col);
                box(ctx, cx - a, cy + gap, cx - a + th, cy + len, col);
                box(ctx, cx - 1, cy - 1, cx + 1, cy + 1, col);
            }
        }
        if (m.b("dot") && style != 1) box(ctx, cx - 1, cy - 1, cx + 1, cy + 1, col);
    }

    private static void box(DrawContext ctx, int x1, int y1, int x2, int y2, int col) {
        ctx.fill(x1 - 1, y1 - 1, x2 + 1, y2 + 1, 0xAA000000);
        ctx.fill(x1, y1, x2, y2, col);
    }
}
