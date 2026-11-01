package pl.zolteniebiosa;

import net.minecraft.client.gui.DrawContext;

/**
 * Motyw clienta widoczny poza swiatem gry: na ekranie glownym (Singleplayer/Multiplayer) i na ekranie pauzy.
 * Wlacza/wylacza sie w WYGLAD -> Motyw -> "Motyw na ekranie glownym".
 */
public final class ClientTheme {
    private ClientTheme() { }

    public static void renderTitleScreen(DrawContext ctx, int W, int H) {
        if (!Mods.THEME.b("titlescreen")) return;
        Weather.render(ctx, W, H);
        badge(ctx, W, H);
    }

    public static void renderPauseScreen(DrawContext ctx, int W, int H) {
        if (!Mods.THEME.b("titlescreen")) return;
        Weather.render(ctx, W, H);
    }

    /** Male, dyskretne logo ZTN w rogu ekranu - pokazuje ze to inny client, nie czysty vanilla. */
    private static void badge(DrawContext ctx, int W, int H) {
        long t = System.currentTimeMillis();
        int px = 2;
        String s = "ZTN";
        int w = PixelFont.width(s, px);
        int x = W - w - 10;
        int y = H - PixelFont.height(px) - 10;
        float pulse = (float) (0.7 + 0.3 * Math.sin(t / 700.0));
        int top = Theme.rainbow((t % 6000) / 6000f, 0.55f);
        for (int i = 0; i < s.length(); i++) {
            PixelFont.glyph(ctx, s.charAt(i), x + i * 6 * px, y, px, Art.A(top, pulse), Art.A(top, pulse));
        }
        String v = "v" + Branding.VERSION;
        ctx.drawTextWithShadow(net.minecraft.client.MinecraftClient.getInstance().textRenderer, v,
                x, y - 10, Art.A(0xFFCCCCDD, pulse));
    }
}
