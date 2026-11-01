package pl.zolteniebiosa;

import net.minecraft.client.gui.DrawContext;

/**
 * Pelnoekranowa animacja startowa: zolte "ZTN", kwiatki, ikonki YT, napisy DC i YT, pasek postepu.
 * Rysowana na ekranie ladowania zamiast logo Mojang / logo launcherow.
 */
public final class BootAnimation {
    private static long start = 0;
    private static float shown = 0f;

    private static final String LOGO = "ZTN";

    private BootAnimation() { }

    public static void reset() {
        start = 0;
        shown = 0f;
    }

    private static float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    private static float easeOutBack(float t) {
        float c1 = 1.70158f, c3 = c1 + 1f;
        float u = t - 1f;
        return 1f + c3 * u * u * u + c1 * u * u;
    }

    /** prog = prawdziwy postep ladowania (0..1), alpha = przezroczystosc calej animacji (do wygaszania). */
    public static void draw(DrawContext ctx, int W, int H, float prog, float alpha) {
        long now = System.currentTimeMillis();
        if (start == 0) start = now;
        float t = (now - start) / 1000f;
        float a = alpha * clamp01(t / 0.35f);
        shown += (prog - shown) * 0.06f;
        float cxf = W / 2f;
        int cx = W / 2;
        int cy = (int) (H * 0.43f);

        // tlo
        ctx.fillGradient(0, 0, W, H, Art.A(0xFF06061A, alpha), Art.A(0xFF1C0A36, alpha));
        double pulse = 0.75 + 0.25 * Math.sin(t * 2.2);
        Art.glow(ctx, cx, cy, (int) (H * 0.42f), 0xFFC21F, a * (float) pulse * 0.55f);
        Art.glow(ctx, cx, cy, (int) (H * 0.30f), 0xFF8A1F, a * 0.35f);

        // gwiazdki
        for (int i = 0; i < 90; i++) {
            int x = (int) (Art.rnd(i, 1) * W);
            int y = (int) (Art.rnd(i, 2) * H);
            double tw = (Math.sin(t * 2.0 + i * 1.3) + 1) / 2;
            int sz = i % 7 == 0 ? 2 : 1;
            Art.star(ctx, x, y, sz, Art.A(0xFFFFF2B0, a * (float) (0.15 + 0.6 * tw)));
        }

        // kwiatki po orbicie dookola logo
        int N = 16;
        float rx = W * 0.38f, ry = H * 0.34f;
        for (int i = 0; i < N; i++) {
            float ap = clamp01((t - 0.25f - i * 0.09f) / 0.6f);
            if (ap <= 0f) continue;
            double ang = i * (Math.PI * 2 / N) + t * 0.22;
            float fx = cxf + (float) Math.cos(ang) * rx;
            float fy = cy + (float) Math.sin(ang) * ry;
            float size = 6 + (i % 4) * 2.2f + (float) Math.sin(t * 2 + i) * 0.8f;
            int petal = Art.hue(i / (float) N + t * 0.03f, 0.55f, 1f);
            Art.flower(ctx, fx, fy, size * Math.max(1f, W / 520f), t * (0.8f + (i % 3) * 0.35f) * (i % 2 == 0 ? 1 : -1),
                    petal, a * ap);
        }

        // ikonki YT plywajace po bokach
        float[][] anch = {{0.10f, 0.18f}, {0.90f, 0.20f}, {0.07f, 0.62f}, {0.93f, 0.64f}, {0.30f, 0.08f}, {0.72f, 0.07f}};
        for (int i = 0; i < anch.length; i++) {
            float ap = clamp01((t - 0.6f - i * 0.12f) / 0.5f);
            if (ap <= 0f) continue;
            float fx = anch[i][0] * W + (float) Math.sin(t * 0.9 + i) * 8f;
            float fy = anch[i][1] * H + (float) Math.cos(t * 1.1 + i * 2) * 6f;
            Art.yt(ctx, fx, fy, Math.max(1.2f, W / 330f) * (0.85f + 0.15f * (float) Math.sin(t * 2 + i)), a * ap * 0.95f);
        }

        // logo ZTN
        int px = Math.max(4, (int) Math.min(W * 0.36f / 17f, H * 0.30f / 7f));
        int logoW = PixelFont.width(LOGO, px);
        int lx = cx - logoW / 2;
        int ly = cy - PixelFont.height(px) / 2;
        for (int i = 0; i < LOGO.length(); i++) {
            float ap = clamp01((t - 0.4f - i * 0.2f) / 0.6f);
            if (ap <= 0f) continue;
            float e = easeOutBack(ap);
            int xx = lx + i * 6 * px;
            int yy = ly + (int) ((1f - e) * -px * 10) + (int) (Math.sin(t * 2.4 + i * 0.8) * px * 0.25);
            float la = a * ap;
            float wave = (float) (0.5 + 0.5 * Math.sin(t * 3.0 - i * 0.9));
            int top = PixelFont.lerp(0xFFFFF27A, 0xFFFFFFFF, wave * 0.45f);
            int bot = 0xFFFFA800;
            // poswiata liter
            for (int g = 3; g >= 1; g--) {
                int off = g * Math.max(1, px / 3);
                int gc = Art.A(0xFFFFC21F, la * 0.10f);
                PixelFont.glyph(ctx, LOGO.charAt(i), xx - off, yy, px, gc, gc);
                PixelFont.glyph(ctx, LOGO.charAt(i), xx + off, yy, px, gc, gc);
                PixelFont.glyph(ctx, LOGO.charAt(i), xx, yy - off, px, gc, gc);
                PixelFont.glyph(ctx, LOGO.charAt(i), xx, yy + off, px, gc, gc);
            }
            // cien + litera
            int sh = Art.A(0xFF7A4A00, la);
            PixelFont.glyph(ctx, LOGO.charAt(i), xx + Math.max(1, px / 3), yy + Math.max(1, px / 3), px, sh, sh);
            PixelFont.glyph(ctx, LOGO.charAt(i), xx, yy, px, Art.A(top, la), Art.A(bot, la));
        }

        // podpis pod logo
        float sa = clamp01((t - 1.1f) / 0.6f);
        if (sa > 0f) {
            int spx = Math.max(2, px / 4);
            int sw = PixelFont.width(Branding.NAME_UP, spx);
            int sx = cx - sw / 2;
            int sy = ly + PixelFont.height(px) + px * 2;
            for (int i = 0; i < Branding.NAME_UP.length(); i++) {
                int col = Theme.rainbow(i * 0.05f - (float) (t * 0.2), 0.55f);
                PixelFont.glyph(ctx, Branding.NAME_UP.charAt(i), sx + i * 6 * spx, sy, spx, Art.A(col, a * sa), Art.A(col, a * sa));
            }
            int lw = (int) (sw * (0.5f + 0.5f * Math.min(1f, (t - 1.1f) / 1.2f)));
            ctx.fill(cx - lw / 2, sy + PixelFont.height(spx) + spx * 2, cx + lw / 2, sy + PixelFont.height(spx) + spx * 2 + 1,
                    Art.A(0xFFFFD21F, a * sa * 0.7f));
        }

        // pasek postepu
        int bw = (int) (W * 0.42f);
        int bh = Math.max(3, H / 120);
        int bx = cx - bw / 2;
        int by = (int) (H * 0.80f);
        ctx.fill(bx - 1, by - 1, bx + bw + 1, by + bh + 1, Art.A(0xFF2A2440, a));
        ctx.fill(bx, by, bx + bw, by + bh, Art.A(0xFF0D0B1A, a));
        int fw = (int) (bw * clamp01(shown));
        if (fw > 0) {
            ctx.fillGradient(bx, by, bx + fw, by + bh, Art.A(0xFFFFE066, a), Art.A(0xFFFFA800, a));
            ctx.fill(bx + fw - 2, by - 2, bx + fw + 1, by + bh + 2, Art.A(0xFFFFFFFF, a * 0.8f));
        }
        String pct = (int) (clamp01(shown) * 100) + "%";
        int ppx = Math.max(2, H / 200);
        PixelFont.draw(ctx, pct, cx - PixelFont.width(pct, ppx) / 2, by + bh + ppx * 3, ppx, Art.A(0xFFFFE9A0, a));

        // napisy DC i YT na dole
        float fa = clamp01((t - 1.4f) / 0.7f);
        if (fa > 0f) {
            int fpx = Math.max(2, H / 190);
            String dc = Branding.DISCORD;
            String yt = Branding.YT;
            int iconW = (int) (22 * Math.max(1f, fpx * 0.6f));
            int dcW = PixelFont.width(dc, fpx) + iconW;
            int ytW = PixelFont.width(yt, fpx) + iconW;
            int gap = fpx * 8;
            int total = dcW + gap + ytW;
            int fx = cx - total / 2;
            int fy = (int) (H * 0.91f);
            float ik = Math.max(1f, fpx * 0.6f);
            Art.discord(ctx, fx + iconW / 2f, fy + PixelFont.height(fpx) / 2f, ik, a * fa);
            PixelFont.draw(ctx, dc, fx + iconW, fy, fpx, Art.A(0xFFC9D0FF, a * fa));
            int fx2 = fx + dcW + gap;
            Art.yt(ctx, fx2 + iconW / 2f, fy + PixelFont.height(fpx) / 2f, ik, a * fa);
            PixelFont.draw(ctx, yt, fx2 + iconW, fy, fpx, Art.A(0xFFFFB3B3, a * fa));
        }
    }
}
