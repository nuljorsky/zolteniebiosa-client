package pl.zolteniebiosa;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.Random;

public class Weather {
    private static float[] x = new float[0], y, s, ph;
    private static final Random R = new Random();
    private static long last = 0;
    private static boolean lastSnow = false;

    public static void render(DrawContext ctx) {
        if (!Mods.WEATHER.enabled) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden) return;
        int W = ctx.getScaledWindowWidth(), H = ctx.getScaledWindowHeight();
        boolean snow = Mods.WEATHER.mode("type") == 1;
        int count = Mods.WEATHER.n("density") * 30;
        if (x.length != count || snow != lastSnow) {
            x = new float[count]; y = new float[count]; s = new float[count]; ph = new float[count];
            for (int i = 0; i < count; i++) reset(i, W, H, true);
            lastSnow = snow;
        }
        long now = System.currentTimeMillis();
        float dt = last == 0 ? 0.016f : Math.min(0.05f, (now - last) / 1000f);
        last = now;
        float t = now / 1000f;
        for (int i = 0; i < count; i++) {
            if (snow) {
                y[i] += (30 + 50 * s[i]) * dt;
                x[i] += (float) Math.sin(t * 1.3 + ph[i]) * 18 * dt;
            } else {
                y[i] += (420 + 380 * s[i]) * dt;
                x[i] -= 60 * dt;
            }
            if (y[i] > H + 10 || x[i] < -10) reset(i, W, H, false);
            int px = (int) x[i], py = (int) y[i];
            if (snow) {
                int sz = 1 + (int) (s[i] * 2.4f);
                int a = 150 + (int) (s[i] * 100);
                ctx.fill(px, py, px + sz, py + sz, (a << 24) | 0xFFFFFF);
                if (sz >= 3) {
                    ctx.fill(px - 1, py + 1, px, py + 2, (a / 2 << 24) | 0xDDEEFF);
                    ctx.fill(px + sz, py + 1, px + sz + 1, py + 2, (a / 2 << 24) | 0xDDEEFF);
                }
            } else {
                int len = 6 + (int) (s[i] * 12);
                int a = 110 + (int) (s[i] * 100);
                ctx.fill(px, py, px + 1, py + len, (a << 24) | 0x9FC8FF);
                ctx.fill(px - 1, py + len, px, py + len + 3, (a / 2 << 24) | 0x9FC8FF);
            }
        }
    }

    private static void reset(int i, int W, int H, boolean anywhere) {
        x[i] = R.nextFloat() * (W + 60);
        y[i] = anywhere ? R.nextFloat() * H : -10 - R.nextFloat() * 40;
        s[i] = R.nextFloat();
        ph[i] = R.nextFloat() * 6.28f;
    }
}
