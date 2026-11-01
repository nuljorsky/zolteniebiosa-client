package pl.zolteniebiosa;

import java.util.Random;
import net.minecraft.client.gui.DrawContext;

/**
 * Deszcz i snieg rysowane na ekranie. Dzialaja zawsze (w kazdym wymiarze, w budynkach, na kazdym serwerze),
 * bo nie zaleza od pogody swiata ani od czasteczek Minecrafta.
 */
public final class Weather {
    private static final int MAX = 700;
    private static final float[] X = new float[MAX], Y = new float[MAX], V = new float[MAX], S = new float[MAX], P = new float[MAX];
    private static final Random R = new Random();
    private static boolean ready;
    private static int lastW, lastH;
    private static long last;

    private Weather() { }

    private static void respawn(int i, int W, int H, boolean anywhere) {
        X[i] = R.nextFloat() * (W + 120) - 60;
        Y[i] = anywhere ? R.nextFloat() * H : -R.nextFloat() * 40 - 5;
        S[i] = R.nextFloat();
        V[i] = 0.75f + R.nextFloat() * 0.5f;
        P[i] = R.nextFloat() * 6.28f;
    }

    public static void render(DrawContext ctx, int W, int H) {
        if (!Mods.WEATHER.enabled) {
            ready = false;
            return;
        }
        boolean snow = Mods.WEATHER.mode("type") == 1;
        int dens = Mods.WEATHER.n("density");
        int count = Math.min(MAX, dens * (snow ? 22 : 30));
        if (!ready || W != lastW || H != lastH) {
            for (int i = 0; i < MAX; i++) respawn(i, W, H, true);
            ready = true;
            lastW = W;
            lastH = H;
            last = System.nanoTime();
        }
        long now = System.nanoTime();
        float dt = Math.min(0.08f, (now - last) / 1_000_000_000f);
        last = now;
        float t = (now / 1_000_000L) / 1000f;

        for (int i = 0; i < count; i++) {
            if (snow) {
                float fall = H * (0.10f + 0.10f * S[i]) * V[i];
                Y[i] += fall * dt;
                X[i] += (float) Math.sin(t * 1.2f + P[i]) * 18f * dt;
                if (Y[i] > H + 4) respawn(i, W, H, false);
                int x = (int) X[i], y = (int) Y[i];
                int a = (int) (150 + 90 * S[i]);
                if (S[i] > 0.78f) {
                    ctx.fill(x, y, x + 3, y + 3, (a << 24) | 0xFFFFFF);
                    ctx.fill(x - 1, y + 1, x + 4, y + 2, ((a / 2) << 24) | 0xFFFFFF);
                    ctx.fill(x + 1, y - 1, x + 2, y + 4, ((a / 2) << 24) | 0xFFFFFF);
                } else if (S[i] > 0.4f) {
                    ctx.fill(x, y, x + 2, y + 2, (a << 24) | 0xFFFFFF);
                } else {
                    ctx.fill(x, y, x + 1, y + 1, (a << 24) | 0xE8F4FF);
                }
            } else {
                float fall = H * 1.25f * V[i];
                Y[i] += fall * dt;
                X[i] -= fall * 0.18f * dt;
                if (Y[i] > H + 20) respawn(i, W, H, false);
                int x = (int) X[i], y = (int) Y[i];
                int len = 6 + (int) (S[i] * 10);
                int a = (int) (90 + 90 * S[i]);
                int col = (a << 24) | 0xA8CCFF;
                // skosna kreska z kilku schodkow
                for (int k = 0; k < len; k += 3) {
                    ctx.fill(x + k / 5, y + k, x + k / 5 + 1, y + Math.min(len, k + 3), col);
                }
            }
        }
    }
}
