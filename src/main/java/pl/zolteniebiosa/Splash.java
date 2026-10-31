package pl.zolteniebiosa;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class Splash {
    private static long start = -1, lastDraw = 0, endAt = -1;
    private static boolean active = false;
    private static final int[] FL = {0xFF6EC7, 0xFFFFFF, 0x9BE7FF, 0xFF9A2E, 0xC58BFF, 0xFFE23E};
    private static final int YELLOW = 0xFFD21F;

    // wywolywane z mixina na koncu SplashOverlay.render
    public static void drawIn(DrawContext ctx) {
        long now = System.currentTimeMillis();
        if (start < 0) start = now;
        active = true;
        endAt = -1;
        lastDraw = now;
        scene(ctx, (now - start) / 1000f, 1f);
    }

    // plynne znikanie po zakonczeniu ladowania
    public static void fade(DrawContext ctx) {
        if (!active) return;
        if (MinecraftClient.getInstance().getOverlay() != null) return;
        long now = System.currentTimeMillis();
        if (endAt < 0) endAt = now;
        float a = 1f - (now - endAt) / 800f;
        if (a <= 0f) {
            active = false;
            return;
        }
        scene(ctx, (lastDraw - start) / 1000f, a);
    }

    static void scene(DrawContext ctx, float t, float alpha) {
        int W = ctx.getScaledWindowWidth(), H = ctx.getScaledWindowHeight();
        int a = (int) (Math.max(0f, Math.min(1f, alpha)) * 255);
        ctx.fillGradient(0, 0, W, H, (a << 24) | 0x05030F, (a << 24) | 0x1A0B33);

        for (int i = 0; i < 90; i++) {
            double u = Math.sin(i * 12.9898) * 43758.5453, v = Math.sin(i * 78.233) * 12345.6789;
            int x = (int) ((u - Math.floor(u)) * W), y = (int) ((v - Math.floor(v)) * H);
            float tw = (float) ((Math.sin(t * 2.0 + i * 1.7) + 1) / 2);
            int sz = (i % 4 == 0) ? 2 : 1;
            ctx.fill(x, y, x + sz, y + sz, PixelFont.c(0xFFFFFF, tw * 0.7f * alpha));
        }

        float appear = Math.min(1f, t / 0.8f);
        int fs = Math.max(2, H / 150);
        for (int i = 0; i < 14; i++) {
            double ang = t * 0.5 + i * Math.PI * 2 / 14;
            int fx = (int) (W / 2 + Math.cos(ang) * W * 0.40);
            int fy = (int) (H / 2 + Math.sin(ang) * H * 0.36);
            PixelFont.flower(ctx, fx, fy, fs, FL[i % FL.length], alpha * appear);
        }

        int sc = Math.max(3, H / 50);
        int lw = PixelFont.logoWidth(sc);
        int lx = W / 2 - lw / 2;
        int ly = H / 2 - sc * 7;
        for (int k = 0; k < 3; k++) {
            float p = Math.min(1f, Math.max(0f, (t - 0.4f - 0.3f * k) / 0.45f));
            if (p <= 0f) continue;
            float e = 1f - (1f - p) * (1f - p);
            int yy = ly + (int) ((1f - e) * -sc * 8);
            int x = lx + k * 6 * sc;
            float pulse = (float) (0.5 + 0.5 * Math.sin(t * 3.0 - k * 0.8));
            int r = 255, g = (int) (210 + 45 * pulse * 0.6), b = (int) (31 + 120 * pulse * 0.5);
            int main = (r << 16) | (Math.min(255, g) << 8) | Math.min(255, b);
            PixelFont.letter(ctx, k, x + sc / 2, yy + sc / 2, sc, PixelFont.c(0x8A5A00, e * alpha));
            PixelFont.letter(ctx, k, x - sc / 3, yy - sc / 3, sc, PixelFont.c(0xFFE680, e * alpha * 0.25f * (0.5f + pulse)));
            PixelFont.letter(ctx, k, x, yy, sc, PixelFont.c(main, e * alpha));
        }

        float sub = Math.min(1f, Math.max(0f, (t - 1.5f) / 0.6f));
        if (sub > 0f) {
            String s1 = "zolteniebiosa client";
            int s1s = Math.max(2, H / 120);
            PixelFont.draw(ctx, s1, W / 2 - PixelFont.width(s1, s1s) / 2, ly + sc * 9, s1s, PixelFont.c(0xFFFFFF, sub * alpha));
        }

        float soc = Math.min(1f, Math.max(0f, (t - 2.0f) / 0.6f));
        if (soc > 0f) {
            int ts = Math.max(2, H / 150);
            int iy = (int) (H * 0.76);
            String yt = "yt: zolteniebiosa", dc = "dc.crazy_ol1wk4";
            int w1 = 26 * ts / 2 + PixelFont.width(yt, ts), w2 = 26 * ts / 2 + PixelFont.width(dc, ts);
            int ic = Math.max(1, ts / 2);
            int x1 = W / 2 - w1 / 2;
            PixelFont.yt(ctx, x1, iy, ic);
            PixelFont.draw(ctx, yt, x1 + 26 * ic, iy + 2 * ic, ts, PixelFont.c(0xFFFFFF, soc * alpha));
            int x2 = W / 2 - w2 / 2;
            PixelFont.dc(ctx, x2, iy + 14 * ts, ic);
            PixelFont.draw(ctx, dc, x2 + 26 * ic, iy + 14 * ts + 2 * ic, ts, PixelFont.c(0xFFFFFF, soc * alpha));
        }

        int bw = W / 3, bx = W / 2 - bw / 2, by = H - Math.max(14, H / 30);
        ctx.fill(bx, by, bx + bw, by + 4, PixelFont.c(0xFFFFFF, 0.12f * alpha));
        float pos = (t * 0.45f) % 1.4f - 0.2f;
        int sx = bx + (int) (pos * bw), sw = bw / 4;
        int x1 = Math.max(bx, sx), x2 = Math.min(bx + bw, sx + sw);
        if (x2 > x1) ctx.fill(x1, by, x2, by + 4, PixelFont.c(YELLOW, alpha));
    }

    // animowane tlo menu (kwiatki + znaczek ZTN)
    public static void ambient(DrawContext ctx, int W, int H, long ms) {
        int sc = Math.max(5, H / 28);
        int lx = W / 2 - PixelFont.logoWidth(sc) / 2, ly = H / 2 - sc * 4;
        for (int k = 0; k < 3; k++) {
            PixelFont.letter(ctx, k, lx + k * 6 * sc, ly, sc, PixelFont.c(YELLOW, 0.08f + 0.03f * (float) Math.sin(ms / 700.0 + k)));
        }
        for (int i = 0; i < 18; i++) {
            double u = Math.sin(i * 12.9898) * 43758.5453, v = Math.sin(i * 78.233) * 12345.6789;
            double bx = (u - Math.floor(u)) * W, off = (v - Math.floor(v)) * (H + 60);
            double speed = 8 + (i % 5) * 4;
            double y = H + 30 - ((ms / 1000.0 * speed + off) % (H + 60));
            double x = bx + Math.sin(ms / 1500.0 + i) * 14;
            PixelFont.flower(ctx, (int) x, (int) y, 2, FL[i % FL.length], 0.45f);
        }
    }
}
