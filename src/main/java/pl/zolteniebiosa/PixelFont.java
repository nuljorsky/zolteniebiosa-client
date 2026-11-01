package pl.zolteniebiosa;

import net.minecraft.client.gui.DrawContext;

import java.util.HashMap;
import java.util.Map;

public class PixelFont {
    private static final Map<Character, String[]> G = new HashMap<>();

    private static void g(char c, String... rows) {
        G.put(c, rows);
    }

    private static final String[][] LOGO = {
            {"#####", "....#", "...#.", "..#..", ".#...", "#....", "#####"},
            {"#####", "..#..", "..#..", "..#..", "..#..", "..#..", "..#.."},
            {"#...#", "##..#", "##..#", "#.#.#", "#..##", "#..##", "#...#"}
    };

    static {
        g('a', ".#.", "#.#", "###", "#.#", "#.#");
        g('b', "##.", "#.#", "##.", "#.#", "##.");
        g('c', ".##", "#..", "#..", "#..", ".##");
        g('d', "##.", "#.#", "#.#", "#.#", "##.");
        g('e', "###", "#..", "##.", "#..", "###");
        g('i', "###", ".#.", ".#.", ".#.", "###");
        g('k', "#.#", "#.#", "##.", "#.#", "#.#");
        g('l', "#..", "#..", "#..", "#..", "###");
        g('n', "##.", "#.#", "#.#", "#.#", "#.#");
        g('o', ".#.", "#.#", "#.#", "#.#", ".#.");
        g('r', "##.", "#.#", "##.", "#.#", "#.#");
        g('s', ".##", "#..", ".#.", "..#", "##.");
        g('t', "###", ".#.", ".#.", ".#.", ".#.");
        g('w', "#.#", "#.#", "#.#", "###", "#.#");
        g('y', "#.#", "#.#", ".#.", ".#.", ".#.");
        g('z', "###", "..#", ".#.", "#..", "###");
        g('1', ".#.", "##.", ".#.", ".#.", "###");
        g('4', "#.#", "#.#", "###", "..#", "..#");
        g('.', "...", "...", "...", "...", ".#.");
        g(':', "...", ".#.", "...", ".#.", "...");
        g('_', "...", "...", "...", "...", "###");
    }

    public static int c(int rgb, float a) {
        int al = (int) (Math.max(0f, Math.min(1f, a)) * 255);
        return (al << 24) | (rgb & 0xFFFFFF);
    }

    public static int width(String s, int sc) {
        return Math.max(0, s.length() * 4 * sc - sc);
    }

    public static void draw(DrawContext ctx, String s, int x, int y, int sc, int color) {
        int cx = x;
        for (char ch : s.toLowerCase().toCharArray()) {
            String[] r = G.get(ch);
            if (r != null) {
                for (int row = 0; row < 5; row++) {
                    for (int col = 0; col < 3; col++) {
                        if (r[row].charAt(col) == '#') {
                            ctx.fill(cx + col * sc, y + row * sc, cx + (col + 1) * sc, y + (row + 1) * sc, color);
                        }
                    }
                }
            }
            cx += 4 * sc;
        }
    }

    public static int logoWidth(int sc) {
        return 17 * sc;
    }

    public static void letter(DrawContext ctx, int k, int x, int y, int sc, int color) {
        String[] r = LOGO[k];
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 5; col++) {
                if (r[row].charAt(col) == '#') {
                    ctx.fill(x + col * sc, y + row * sc, x + (col + 1) * sc, y + (row + 1) * sc, color);
                }
            }
        }
    }

    public static void flower(DrawContext ctx, int x, int y, int u, int petal, float a) {
        int p = c(petal, a), mid = c(0xFFD21F, a), st = c(0x3CB043, a * 0.9f);
        ctx.fill(x, y + 3 * u, x + u, y + 6 * u, st);
        ctx.fill(x - u, y - 3 * u, x + u, y - u, p);
        ctx.fill(x - u, y + u, x + u, y + 3 * u, p);
        ctx.fill(x - 3 * u, y - u, x - u, y + u, p);
        ctx.fill(x + u, y - u, x + 3 * u, y + u, p);
        ctx.fill(x - u, y - u, x + u, y + u, mid);
    }

    public static void yt(DrawContext ctx, int x, int y, int s) {
        ctx.fill(x, y, x + 22 * s, y + 15 * s, 0xFFE62117);
        for (int i = 0; i < 5; i++) {
            int h = 9 - 2 * i;
            int top = 7 - h / 2;
            ctx.fill(x + (8 + i) * s, y + top * s, x + (9 + i) * s, y + (top + h) * s, 0xFFFFFFFF);
        }
    }

    public static void dc(DrawContext ctx, int x, int y, int s) {
        ctx.fill(x, y, x + 22 * s, y + 15 * s, 0xFF5865F2);
        ctx.fill(x + 6 * s, y + 5 * s, x + 9 * s, y + 10 * s, 0xFFFFFFFF);
        ctx.fill(x + 13 * s, y + 5 * s, x + 16 * s, y + 10 * s, 0xFFFFFFFF);
        ctx.fill(x + 7 * s, y + 11 * s, x + 15 * s, y + 12 * s, 0xFFFFFFFF);
    }
}
