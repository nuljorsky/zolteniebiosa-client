package pl.zolteniebiosa;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;

import java.awt.Color;

/** Male elementy graficzne rysowane prostokatami: kwiatki, ikonki YT i Discord, gwiazdki, poswiata. */
public final class Art {
    private Art() { }

    /** Mnozy kanal alpha koloru ARGB przez a (0..1). */
    public static int A(int argb, float a) {
        int al = (int) (((argb >>> 24) & 255) * Math.max(0f, Math.min(1f, a)));
        return (al << 24) | (argb & 0xFFFFFF);
    }

    /** Deterministyczna "losowosc" 0..1 - te same pozycje przy kazdej klatce. */
    public static double rnd(int i, int salt) {
        double a = Math.sin(i * 12.9898 + salt * 78.233) * 43758.5453;
        return a - Math.floor(a);
    }

    public static int hue(float h, float s, float b) {
        return 0xFF000000 | Color.HSBtoRGB(h - (float) Math.floor(h), s, b);
    }

    /** Kwiatek: 6 platkow + srodek. size = promien w pikselach. */
    public static void flower(DrawContext ctx, float cx, float cy, float size, float rot, int petal, float alpha) {
        MatrixStack ms = ctx.getMatrices();
        ms.push();
        ms.translate(cx, cy, 0);
        ms.multiply(new Quaternionf().rotateZ(rot));
        float k = size / 10f;
        ms.scale(k, k, 1f);
        int p1 = A(petal, alpha);
        int p2 = A(PixelFont.lerp(petal, 0xFFFFFFFF, 0.45f), alpha);
        int ctr = A(0xFFFFD21F, alpha);
        int ctr2 = A(0xFFFF9A1F, alpha);
        for (int i = 0; i < 6; i++) {
            ms.push();
            ms.multiply(new Quaternionf().rotateZ((float) (i * Math.PI / 3)));
            ctx.fill(-3, -10, 3, -3, p1);
            ctx.fill(-1, -9, 1, -5, p2);
            ms.pop();
        }
        ctx.fill(-3, -3, 3, 3, ctr);
        ctx.fill(-1, -1, 1, 1, ctr2);
        ms.pop();
    }

    /** Czerwona ikonka YouTube z bialym trojkatem. scale 1 = 20x14 px. */
    public static void yt(DrawContext ctx, float cx, float cy, float scale, float alpha) {
        MatrixStack ms = ctx.getMatrices();
        ms.push();
        ms.translate(cx, cy, 0);
        ms.scale(scale, scale, 1f);
        int red = A(0xFFFF1A1A, alpha);
        ctx.fill(-9, -7, 9, 7, red);
        ctx.fill(-10, -6, 10, 6, red);
        ctx.fill(-8, -6, 8, -4, A(0xFFFF6A6A, alpha * 0.55f));
        int w = A(0xFFFFFFFF, alpha);
        for (int x = 0; x < 7; x++) {
            int h = Math.max(1, 5 - (int) (x * 0.75f));
            ctx.fill(-3 + x, -h, -2 + x, h, w);
        }
        ms.pop();
    }

    /** Fioletowa ikonka Discorda (pad z oczami). */
    public static void discord(DrawContext ctx, float cx, float cy, float scale, float alpha) {
        MatrixStack ms = ctx.getMatrices();
        ms.push();
        ms.translate(cx, cy, 0);
        ms.scale(scale, scale, 1f);
        int bl = A(0xFF5865F2, alpha);
        ctx.fill(-9, -6, 9, 6, bl);
        ctx.fill(-10, -4, 10, 5, bl);
        ctx.fill(-7, 6, -3, 8, bl);
        ctx.fill(3, 6, 7, 8, bl);
        int w = A(0xFFFFFFFF, alpha);
        ctx.fill(-6, -2, -2, 3, w);
        ctx.fill(2, -2, 6, 3, w);
        ms.pop();
    }

    public static void star(DrawContext ctx, int x, int y, int size, int argb) {
        ctx.fill(x, y, x + size, y + size, argb);
        if (size >= 2) {
            ctx.fill(x - 1, y + size / 2, x + size + 1, y + size / 2 + 1, A(argb, 0.45f));
            ctx.fill(x + size / 2, y - 1, x + size / 2 + 1, y + size + 1, A(argb, 0.45f));
        }
    }

    /** Miekka okragla poswiata (kilka polprzezroczystych kol). */
    public static void glow(DrawContext ctx, int cx, int cy, int radius, int rgb, float alpha) {
        for (int ring = 0; ring < 6; ring++) {
            int r = radius * (6 - ring) / 6;
            int col = A(0xFF000000 | rgb, alpha * 0.16f);
            for (int dy = -r; dy <= r; dy += 2) {
                int dx = (int) Math.sqrt((double) r * r - (double) dy * dy);
                ctx.fill(cx - dx, cy + dy, cx + dx, cy + dy + 2, col);
            }
        }
    }
}
