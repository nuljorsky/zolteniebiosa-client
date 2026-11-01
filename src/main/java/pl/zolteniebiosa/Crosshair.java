package pl.zolteniebiosa;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * 5 wlasnych celownikow do PvP rysowanych prostokatami - nie potrzeba zadnej paczki tekstur.
 * 0 = domyslny celownik Minecrafta, 1..5 = celowniki ZTN.
 */
public final class Crosshair {
    public static final int COUNT = 6;

    private Crosshair() { }

    /** Czy zamiast celownika Minecrafta rysujemy wlasny. */
    public static boolean custom() {
        return Mods.CROSSHAIR.mode("style") > 0;
    }

    public static void render(DrawContext ctx, int W, int H) {
        if (!custom()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen != null || !mc.options.getPerspective().isFirstPerson()) return;
        Mod m = Mods.CROSSHAIR;
        int col = Theme.pal(m.get("color").idx, (System.currentTimeMillis() % 4000) / 4000f) & 0xFFFFFF;
        draw(ctx, m.mode("style"), W / 2, H / 2, m.n("size"), m.n("thick"), m.n("gap"), col, m.b("outline"), 1f);
    }

    /** Rysuje celownik o danym stylu. cx, cy = srodek. */
    public static void draw(DrawContext ctx, int style, int cx, int cy, int size, int thick, int gap, int rgb,
                            boolean outline, float alpha) {
        if (style <= 0) return;
        if (outline) shape(ctx, style, cx, cy, size, thick, gap, Art.A(0xFF000000, alpha * 0.85f), 1);
        shape(ctx, style, cx, cy, size, thick, gap, Art.A(0xFF000000 | rgb, alpha), 0);
    }

    private static void box(DrawContext ctx, int x1, int y1, int x2, int y2, int col, int pad) {
        ctx.fill(x1 - pad, y1 - pad, x2 + pad, y2 + pad, col);
    }

    private static void shape(DrawContext ctx, int style, int cx, int cy, int size, int t, int gap, int col, int pad) {
        int lo = -(t / 2);
        int hi = lo + t;
        switch (style) {
            case 1 -> { // kropka
                int d = Math.max(2, t + 1);
                int a = -(d / 2);
                box(ctx, cx + a, cy + a, cx + a + d, cy + a + d, col, pad);
            }
            case 2 -> { // krzyz z przerwa
                box(ctx, cx + lo, cy - gap - size, cx + hi, cy - gap, col, pad);
                box(ctx, cx + lo, cy + gap, cx + hi, cy + gap + size, col, pad);
                box(ctx, cx - gap - size, cy + lo, cx - gap, cy + hi, col, pad);
                box(ctx, cx + gap, cy + lo, cx + gap + size, cy + hi, col, pad);
            }
            case 3 -> { // okrag + kropka
                int r = Math.max(3, size);
                double inner = r - t / 2.0 - 0.5, outer = r + t / 2.0 + 0.5;
                for (int dy = -r - t; dy <= r + t; dy++) {
                    for (int dx = -r - t; dx <= r + t; dx++) {
                        double d = Math.sqrt((double) dx * dx + (double) dy * dy);
                        if (d >= inner && d <= outer) box(ctx, cx + dx, cy + dy, cx + dx + 1, cy + dy + 1, col, pad);
                    }
                }
                box(ctx, cx - 1, cy - 1, cx + 1, cy + 1, col, pad);
            }
            case 4 -> { // narozniki (kwadrat bez srodka) + kropka
                int s = Math.max(3, size);
                int arm = Math.max(2, s * 2 / 3);
                for (int sx = -1; sx <= 1; sx += 2) {
                    for (int sy = -1; sy <= 1; sy += 2) {
                        int x = cx + sx * s, y = cy + sy * s;
                        int hx1 = sx > 0 ? x - arm : x, hx2 = sx > 0 ? x : x + arm;
                        int vy1 = sy > 0 ? y - arm : y, vy2 = sy > 0 ? y : y + arm;
                        int ty1 = sy > 0 ? y - t : y, ty2 = sy > 0 ? y : y + t;
                        int tx1 = sx > 0 ? x - t : x, tx2 = sx > 0 ? x : x + t;
                        box(ctx, hx1, ty1, hx2, ty2, col, pad);
                        box(ctx, tx1, vy1, tx2, vy2, col, pad);
                    }
                }
                box(ctx, cx - 1, cy - 1, cx + 1, cy + 1, col, pad);
            }
            default -> { // 5: krzyz X
                for (int i = gap; i < gap + size; i++) {
                    for (int sx = -1; sx <= 1; sx += 2) {
                        for (int sy = -1; sy <= 1; sy += 2) {
                            int x = cx + sx * i, y = cy + sy * i;
                            box(ctx, x - t / 2, y - t / 2, x - t / 2 + t, y - t / 2 + t, col, pad);
                        }
                    }
                }
            }
        }
    }
}
