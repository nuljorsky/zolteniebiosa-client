package pl.zolteniebiosa;

import java.awt.Color;

public class Theme {
    public static final int[] PAL = {0, 0xFFFFFF, 0xFF4040, 0xFF9A2E, 0xFFE23E, 0x6BFF3E, 0x3EFFF0, 0x3E7BFF, 0xA43EFF, 0xFF6EC7};

    public static int rainbow(float off, float sat) {
        long t = System.currentTimeMillis();
        float h = ((t % 4000L) / 4000f + off) % 1f;
        if (h < 0) h += 1f;
        return 0xFF000000 | Color.HSBtoRGB(h, sat, 1f);
    }

    public static int pal(int idx, float off) {
        if (idx <= 0 || idx >= PAL.length) return rainbow(off, 0.75f);
        return 0xFF000000 | PAL[idx];
    }

    public static int accent(float off) {
        return pal(Mods.THEME.get("color").idx, off);
    }

    public static int alpha(int argb, int a) {
        return (Math.max(0, Math.min(255, a)) << 24) | (argb & 0xFFFFFF);
    }
}
