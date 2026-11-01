package pl.zolteniebiosa;

import net.minecraft.client.gui.DrawContext;

import java.util.Random;

// Animowany snieg/deszcz rysowany na ekranie (do menu glownego i menu klienta).
public class Flakes {
    private final float[] x, y, s, ph;
    private final boolean snow;
    private final Random r = new Random();
    private long last = 0;
    private boolean init = false;

    public Flakes(boolean snow, int count) {
        this.snow = snow;
        x = new float[count];
        y = new float[count];
        s = new float[count];
        ph = new float[count];
    }

    private void reset(int i, int w, int h, boolean anywhere) {
        x[i] = r.nextFloat() * (w + 60);
        y[i] = anywhere ? r.nextFloat() * h : -10 - r.nextFloat() * 40;
        s[i] = r.nextFloat();
        ph[i] = r.nextFloat() * 6.28f;
    }

    public void draw(DrawContext ctx, int w, int h) {
        if (!init) {
            for (int i = 0; i < x.length; i++) reset(i, w, h, true);
            init = true;
        }
        long now = System.currentTimeMillis();
        float dt = last == 0 ? 0.016f : Math.min(0.05f, (now - last) / 1000f);
        last = now;
        float t = now / 1000f;
        for (int i = 0; i < x.length; i++) {
            if (snow) {
                y[i] += (28 + 48 * s[i]) * dt;
                x[i] += (float) Math.sin(t * 1.2 + ph[i]) * 16 * dt;
            } else {
                y[i] += (420 + 380 * s[i]) * dt;
                x[i] -= 60 * dt;
            }
            if (y[i] > h + 10 || x[i] < -10) reset(i, w, h, false);
            int px = (int) x[i], py = (int) y[i];
            if (snow) {
                int sz = 1 + (int) (s[i] * 2.6f);
                int a = 140 + (int) (s[i] * 110);
                ctx.fill(px, py, px + sz, py + sz, (a << 24) | 0xFFFFFF);
                if (sz >= 3) {
                    ctx.fill(px - 1, py + 1, px, py + 2, (a / 2 << 24) | 0xDDEEFF);
                    ctx.fill(px + sz, py + 1, px + sz + 1, py + 2, (a / 2 << 24) | 0xDDEEFF);
                    ctx.fill(px + 1, py - 1, px + 2, py, (a / 2 << 24) | 0xDDEEFF);
                }
            } else {
                int len = 6 + (int) (s[i] * 12);
                ctx.fill(px, py, px + 1, py + len, (150 << 24) | 0x9FC8FF);
            }
        }
    }
}
