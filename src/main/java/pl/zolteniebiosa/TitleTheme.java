package pl.zolteniebiosa;

import net.minecraft.client.gui.DrawContext;

// Motyw klienta dla menu glownego i innych ekranow poza gra (zamiast panoramy vanilla).
public class TitleTheme {
    private static final Flakes SNOW = new Flakes(true, 240);

    public static boolean active() {
        return Mods.THEME.b("title");
    }

    public static void background(DrawContext ctx) {
        int w = ctx.getScaledWindowWidth(), h = ctx.getScaledWindowHeight();
        long ms = System.currentTimeMillis();
        ctx.fillGradient(0, 0, w, h, 0xFF070514, 0xFF1B0B35);
        // zorza
        for (int x = 0; x < w; x += 6) {
            double wave = Math.sin(x * 0.012 + ms / 1800.0) * 26 + Math.sin(x * 0.027 - ms / 1300.0) * 12;
            int y0 = (int) (h * 0.22 + wave);
            int top = Theme.alpha(Theme.rainbow(x / (float) w * 0.6f, 0.65f), 70);
            ctx.fillGradient(x, y0, x + 6, y0 + (int) (h * 0.28), top, 0x00000000);
        }
        Splash.ambient(ctx, w, h, ms);
        SNOW.draw(ctx, w, h);
        // winieta
        ctx.fillGradient(0, h - 40, w, h, 0x00000000, 0x90000000);
    }

    public static void footer(DrawContext ctx) {
        int w = ctx.getScaledWindowWidth();
        String yt = "yt: zolteniebiosa", dc = "dc.crazy_ol1wk4";
        int yw = PixelFont.width(yt, 2), dw = PixelFont.width(dc, 2);
        PixelFont.draw(ctx, yt, w - yw - 8, 8, 2, 0xFFFFFFFF);
        PixelFont.yt(ctx, w - yw - 34, 6, 1);
        PixelFont.draw(ctx, dc, w - dw - 8, 28, 2, 0xFFFFFFFF);
        PixelFont.dc(ctx, w - dw - 34, 26, 1);
    }
}
