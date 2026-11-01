package pl.zolteniebiosa;

import org.lwjgl.glfw.GLFW;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.OrderedText;
import net.minecraft.registry.Registries;

public class MenuScreen extends Screen {
    public static int tab = 0;
    static double scroll = 0, maxScroll = 0;
    static boolean welcomed = false;
    static String toast = "";
    static long toastT = 0;

    static final String[] TABS = {"WYGLĄD", "PETY", "hacksyZOLTEGO", "ZDOBYCZ", "BEZPIECZEŃSTWO", "UŁATWIENIA", "MODY PvP", "MUZYKA", "DISCORD", "#INFO"};
    static final Item[] ICONS = {Items.DIAMOND_SWORD, Items.BONE, Items.ENDER_EYE, Items.DIAMOND, Items.SHIELD, Items.NETHER_STAR,
            Items.NETHERITE_SWORD, Items.JUKEBOX, Items.ENDER_CHEST, Items.BOOK};
    static final Setting QUERY = Setting.text("q", "mu.label", "", "mu.hint");
    static {
        QUERY.onCommit = () -> Music.search(QUERY.text);
    }
    static final String[] PK = {"p.diamond", "p.netherite", "p.gold", "p.iron", "p.emerald", "p.ores", "p.clear"};

    interface Click { void on(double mx, double my, int btn); }

    interface Drag { void on(double mx); }

    static class R {
        int x, y, w, h;
        boolean content;
        Click c;
        Drag d;
    }

    final List<R> regs = new ArrayList<>();
    R dragging;
    Setting focus;
    final long t0 = System.currentTimeMillis();
    int cTop, cBottom;

    public MenuScreen() {
        super(Text.literal("ZolteNiebiosa CLIENT"));
    }

    @Override
    protected void init() {
        if (!welcomed) {
            welcomed = true;
            Voice.say(Lang.t("v.welcome"));
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    /** Przezroczystosc menu 0..1 (ustawienie w zakladce WYGLAD). */
    static float op() {
        return Mods.THEME.n("opacity") / 100f;
    }

    /** Przepuszcza kolor tla kart przez ustawienie przezroczystosci. */
    static int pa(int argb) {
        return Art.A(argb, 0.25f + 0.75f * op());
    }

    @Override
    public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
        long t = System.currentTimeMillis() - t0;
        float k = op();
        // plynacy, polprzezroczysty gradient
        float h = t / 24000f;
        ctx.fillGradient(0, 0, width, height, Art.A(Art.hue(h, 0.75f, 0.16f), 0.15f + 0.75f * k),
                Art.A(Art.hue(h + 0.22f, 0.85f, 0.30f), 0.15f + 0.75f * k));
        giantTitle(ctx, t);
        decorations(ctx, t);
    }

    /** Ogromny napis w tle - taki sam styl jak przy starcie gry. */
    void giantTitle(DrawContext ctx, long t) {
        String l1 = "ZOLTENIEBIOSA", l2 = "CLIENT";
        int px = Math.max(2, Math.min((int) (width * 0.92f / (l1.length() * 6)), (int) (height * 0.5f / 16)));
        int y0 = height / 2 - px * 8 + (int) (Math.sin(t / 900.0) * px * 0.4);
        String[] lines = {l1, l2};
        for (int li = 0; li < 2; li++) {
            String line = lines[li];
            int w = PixelFont.width(line, px);
            int x0 = (width - w) / 2;
            int y = y0 + li * 9 * px;
            for (int i = 0; i < line.length(); i++) {
                int col = Theme.rainbow(i * 0.06f + li * 0.2f - t / 5000f, 0.6f);
                int xx = x0 + i * 6 * px;
                int yy = y + (int) (Math.sin(t / 500.0 + i * 0.5 + li) * px * 0.35);
                PixelFont.glyph(ctx, line.charAt(i), xx + px / 2, yy + px / 2, px, Art.A(0xFF000000, 0.35f), Art.A(0xFF000000, 0.35f));
                PixelFont.glyph(ctx, line.charAt(i), xx, yy, px, Art.A(col, 0.34f), Art.A(PixelFont.lerp(col, 0xFFFFFFFF, 0.5f), 0.22f));
            }
        }
    }

    /** Gwiazdki, kwiatki, ikonki YT i napisy DC / YT. */
    void decorations(DrawContext ctx, long t) {
        float k = 0.35f + 0.65f * op();
        for (int i = 0; i < 70; i++) {
            int x = (int) (Art.rnd(i, 1) * width);
            int y = (int) (Art.rnd(i, 2) * height);
            double tw = (Math.sin(t / 400.0 + i * 1.7) + 1) / 2;
            Art.star(ctx, x, y, i % 5 == 0 ? 2 : 1, Theme.alpha(Theme.accent(i * 0.02f), (int) (tw * 170 * k)));
        }
        // dryfujace kwiatki
        for (int i = 0; i < 12; i++) {
            float sp = 0.012f + (float) Art.rnd(i, 7) * 0.02f;
            float fx = (float) ((Art.rnd(i, 3) * width + t * sp * 6) % (width + 40)) - 20;
            float fy = (float) (Art.rnd(i, 4) * height + Math.sin(t / 1300.0 + i) * 12);
            Art.flower(ctx, fx, fy, 5 + (i % 4) * 2, t / 900f * (i % 2 == 0 ? 1 : -1),
                    Art.hue(i / 12f + t / 30000f, 0.5f, 1f), 0.55f * k);
        }
        // ikonki YT
        float[][] a = {{0.06f, 0.30f}, {0.94f, 0.34f}, {0.05f, 0.80f}, {0.95f, 0.76f}, {0.5f, 0.97f}};
        for (int i = 0; i < a.length; i++) {
            Art.yt(ctx, a[i][0] * width + (float) Math.sin(t / 1000.0 + i) * 5, a[i][1] * height + (float) Math.cos(t / 900.0 + i) * 4,
                    1.0f, 0.65f * k);
        }
        // napisy na dole
        String dc = Branding.DISCORD, yt = Branding.YT;
        int dcW = textRenderer.getWidth(dc) + 20, ytW = textRenderer.getWidth(yt) + 20;
        int total = dcW + 16 + ytW;
        int x = (width - total) / 2, y = height - 28;
        Art.discord(ctx, x + 8, y + 4, 0.8f, 1f);
        ctx.drawTextWithShadow(textRenderer, dc, x + 20, y, 0xFFC9D0FF);
        Art.yt(ctx, x + dcW + 16 + 8, y + 4, 0.8f, 1f);
        ctx.drawTextWithShadow(textRenderer, yt, x + dcW + 16 + 20, y, 0xFFFFB3B3);
    }

    // ===== pomocnicze =====
    static boolean in(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    R reg(int x, int y, int w, int h, boolean content, Click c) {
        R r = new R();
        r.x = x; r.y = y; r.w = w; r.h = h; r.content = content; r.c = c;
        regs.add(r);
        return r;
    }

    static List<int[]> flow(List<Integer> ws, int maxW, int gap, int rowH) {
        List<int[]> out = new ArrayList<>();
        int x = 0, y = 0;
        for (int w : ws) {
            if (x > 0 && x + w > maxW) { x = 0; y += rowH; }
            out.add(new int[]{x, y});
            x += w + gap;
        }
        return out;
    }

    static int flowH(List<int[]> pos, int rowH) {
        return pos.isEmpty() ? 0 : pos.get(pos.size() - 1)[1] + rowH;
    }

    List<Integer> presetW() {
        List<Integer> ws = new ArrayList<>();
        for (String k : PK) ws.add(textRenderer.getWidth(Lang.t(k)) + 14);
        return ws;
    }

    List<Integer> chipW(Setting s) {
        List<Integer> ws = new ArrayList<>();
        for (String id : s.blocks) ws.add(textRenderer.getWidth(id) + 22);
        return ws;
    }

    int blocksH(Setting s, int iw) {
        List<int[]> p = flow(presetW(), iw, 4, 22);
        List<int[]> c = flow(chipW(s), iw, 4, 18);
        return flowH(p, 22) + 4 + 26 + flowH(c, 18) + (c.isEmpty() ? 0 : 4) + 6;
    }

    int settingH(Setting s, int iw) {
        return switch (s.kind) {
            case Setting.INFO -> textRenderer.wrapLines(Text.literal(Lang.t(s.label)), iw).size() * 10 + 10;
            case Setting.BLOCKS -> blocksH(s, iw);
            case Setting.XHAIR -> 62;
            case Setting.DYN -> textRenderer.wrapLines(Text.literal(s.dyn.get()), iw).size() * 10 + 10;
            default -> 26;
        };
    }

    static void toast(String s) {
        toast = s;
        toastT = System.currentTimeMillis();
    }

    // ===== render =====
    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        regs.clear();
        long t = System.currentTimeMillis() - t0;
        renderBackground(ctx, mx, my, delta);
        drawTitle(ctx, t);
        int rows = drawTabs(ctx, mx, my, t);

        cTop = 38 + rows * 27 + 8;
        cBottom = height - 32;
        ctx.enableScissor(0, cTop, width, cBottom);
        int cw = Math.min(760, width - 36);
        int cx = (width - cw) / 2;
        int y = cTop + 6 - (int) scroll;
        if (tab == Mods.TAB_INFO) {
            y = drawInfo(ctx, cx, y, cw, mx, my);
        } else {
            if (tab == Mods.TAB_MUSIC) y = drawMusic(ctx, cx, y, cw, mx, my);
            for (Mod m : Mods.ALL) if (m.cat == tab) y = drawCard(ctx, m, cx, y, cw, mx, my);
            if (tab == 0) y = drawLang(ctx, cx, y, cw, mx, my);
        }
        int content = y + (int) scroll - cTop;
        maxScroll = Math.max(0, content - (cBottom - cTop) + 8);
        if (scroll > maxScroll) scroll = maxScroll;
        ctx.disableScissor();

        if (System.currentTimeMillis() - toastT < 2500 && !toast.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, toast, width / 2, height - 42, 0xFFFF7070);
        }
        ctx.drawCenteredTextWithShadow(textRenderer, Lang.t("menu.hint"), width / 2, height - 13, 0xFF8888AA);
    }

    void drawTitle(DrawContext ctx, long t) {
        String title = Branding.NAME;
        ctx.getMatrices().push();
        ctx.getMatrices().scale(2.3f, 2.3f, 1f);
        int total = textRenderer.getWidth(Text.literal(title).formatted(Formatting.BOLD));
        int cx = (int) (width / 4f - total / 2f);
        for (int i = 0; i < title.length(); i++) {
            Text ch = Text.literal(String.valueOf(title.charAt(i))).formatted(Formatting.BOLD);
            float appear = Math.min(1f, (t - i * 45L) / 300f);
            if (appear > 0.05f) {
                int yy = 2 + (int) ((1 - appear) * -10) + (int) (Math.sin(t / 220.0 + i * 0.55) * 1.5);
                ctx.drawTextWithShadow(textRenderer, ch, cx, yy, Theme.alpha(Theme.accent(i * 0.045f), (int) (255 * appear)));
            }
            cx += textRenderer.getWidth(ch);
        }
        ctx.getMatrices().pop();
    }

    /** Rysuje zakladki w jednym lub dwoch rzedach (zaleznie od szerokosci okna). Zwraca liczbe rzedow. */
    int drawTabs(DrawContext ctx, int mx, int my, long t) {
        int n = TABS.length;
        int rows = (width - 16) / n >= 70 ? 1 : 2;
        int cols = (n + rows - 1) / rows;
        int tw = Math.min(176, (width - 16) / cols);
        int sx = (width - tw * cols) / 2;
        for (int i = 0; i < n; i++) {
            int x = sx + (i % cols) * tw;
            int ty = 38 + (i / cols) * 27;
            boolean sel = i == tab;
            boolean hov = in(mx, my, x, ty, tw - 3, 25);
            ctx.fill(x, ty, x + tw - 3, ty + 25, sel ? pa(0xE02A2A55) : hov ? pa(0xC01E1E3A) : pa(0xA0141428));
            ctx.drawBorder(x, ty, tw - 3, 25, sel ? Theme.accent(i * 0.1f) : 0x30FFFFFF);
            if (sel) ctx.fill(x, ty + 23, x + tw - 3, ty + 25, Theme.accent(i * 0.1f));
            ctx.drawItem(new ItemStack(ICONS[i]), x + 4, ty + 4);

            String name = TABS[i];
            float k = Math.min(1f, (tw - 28f) / textRenderer.getWidth(name));
            ctx.getMatrices().push();
            ctx.getMatrices().translate(x + 24, ty + 12.5f - 4.5f * k, 0.0);
            ctx.getMatrices().scale(k, k, 1f);
            if (i == Mods.TAB_INFO) {
                ctx.drawTextWithShadow(textRenderer, Text.literal(name).formatted(Formatting.BOLD), 0, 0, 0xFFFFFFFF);
            } else {
                int col = Theme.rainbow(i * 0.11f + (float) Math.sin(t / 600.0) * 0.05f, 0.55f);
                ctx.drawTextWithShadow(textRenderer, name, 0, 0, col);
            }
            ctx.getMatrices().pop();
            if (i == Mods.TAB_INFO) {
                int pulse = (int) (120 + 100 * Math.sin(t / 300.0));
                ctx.fill(x, ty + 22, x + tw - 3, ty + 23, Theme.alpha(0xFFFFFF, pulse));
            }
            final int idx = i;
            reg(x, ty, tw - 3, 25, false, (a, b, c) -> {
                tab = idx;
                scroll = 0;
                focus = null;
            });
        }
        return rows;
    }

    // ===== karta modulu =====
    int drawCard(DrawContext ctx, Mod m, int x, int y, int w, int mx, int my) {
        int hh = 36;
        int iw = w - 32;
        int body = 0;
        if (m.open) for (Setting s : m.settings) body += settingH(s, iw);
        int total = hh + body + (m.open && body > 0 ? 6 : 0);
        int accent = Theme.accent(y * 0.0015f);

        ctx.fill(x, y, x + w, y + total, m.enabled ? pa(0xC0191935) : pa(0xB0121224));
        ctx.fill(x, y, x + 3, y + total, m.enabled ? accent : 0xFF3A3A55);
        ctx.drawBorder(x, y, w, total, 0x40FFFFFF);
        boolean hov = in(mx, my, x, y, w, hh) && my >= cTop && my <= cBottom;
        if (hov) ctx.fill(x + 3, y, x + w, y + hh, 0x18FFFFFF);

        ctx.drawItem(new ItemStack(m.icon), x + 10, y + 10);
        ctx.getMatrices().push();
        ctx.getMatrices().translate(x + 34, y + 6, 0);
        ctx.getMatrices().scale(1.12f, 1.12f, 1f);
        ctx.drawTextWithShadow(textRenderer, Text.literal(m.name()).formatted(Formatting.BOLD), 0, 0,
                m.enabled ? accent : 0xFFFFFFFF);
        ctx.getMatrices().pop();
        ctx.drawText(textRenderer, textRenderer.trimToWidth(m.desc(), w - 34 - 110), x + 34, y + 22, 0xFF9A9ABB, false);

        reg(x, y, w, hh, true, (a, b, c) -> {
            if (m.hasSwitch) toggle(m);
            else m.open = !m.open;
        });
        // plus/minus
        int ax = x + w - (m.hasSwitch ? 82 : 32);
        ctx.fill(ax, y + 9, ax + 18, y + 27, 0xFF24243F);
        ctx.drawBorder(ax, y + 9, 18, 18, 0x50FFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, m.open ? "-" : "+", ax + 9, y + 13, 0xFFFFFFFF);
        reg(ax, y + 9, 18, 18, true, (a, b, c) -> m.open = !m.open);
        // przelacznik
        if (m.hasSwitch) {
            int sx = x + w - 52, sy = y + 10;
            boolean on = m.enabled;
            ctx.fill(sx, sy, sx + 38, sy + 16, on ? accent : 0xFF3A3A55);
            ctx.drawBorder(sx, sy, 38, 16, 0x60FFFFFF);
            ctx.fill(on ? sx + 22 : sx + 2, sy + 2, on ? sx + 36 : sx + 16, sy + 14, 0xFFFFFFFF);
            reg(sx, sy, 38, 16, true, (a, b, c) -> toggle(m));
        }

        if (m.open) {
            int sy = y + hh;
            for (Setting s : m.settings) {
                drawSetting(ctx, s, x + 16, sy, iw, mx, my);
                sy += settingH(s, iw);
            }
        }
        return y + total + 10;
    }

    void toggle(Mod m) {
        m.toggle();
        Modules.dirty = true;
        Voice.say(Lang.t("v.sel") + " " + m.name() + (m.hasSwitch ? ", " + Lang.t(m.enabled ? "v.on" : "v.off") : ""));
        Config.save();
    }

    static void setNum(Setting s, double mx, int bx, int bw) {
        double f = Math.max(0, Math.min(1, (mx - bx) / bw));
        s.num = Math.round(s.min + f * (s.max - s.min));
        Modules.dirty = true;
    }

    void drawSetting(DrawContext ctx, Setting s, int lx, int y, int iw, int mx, int my) {
        int wx = lx + iw - 190;
        int accent = Theme.accent(0f);
        switch (s.kind) {
            case Setting.NUM -> {
                ctx.drawTextWithShadow(textRenderer, Lang.t(s.label) + ": " + (int) Math.round(s.num), lx, y + 9, 0xFFDDDDEE);
                int bx = wx, bw = 190, by = y + 10;
                ctx.fill(bx, by, bx + bw, by + 6, 0xFF23233B);
                double f = (s.num - s.min) / (s.max - s.min);
                int fw = (int) (bw * f);
                ctx.fill(bx, by, bx + fw, by + 6, accent);
                ctx.fill(bx + fw - 2, by - 3, bx + fw + 2, by + 9, 0xFFFFFFFF);
                R r = reg(bx - 4, y + 3, bw + 8, 20, true, (a, b, c) -> setNum(s, a, bx, bw));
                r.d = a -> setNum(s, a, bx, bw);
            }
            case Setting.MODE -> {
                ctx.drawTextWithShadow(textRenderer, Lang.t(s.label), lx, y + 9, 0xFFDDDDEE);
                ctx.fill(wx, y + 3, wx + 190, y + 23, 0xFF1E1E36);
                ctx.drawBorder(wx, y + 3, 190, 20, 0x60FFFFFF);
                ctx.drawTextWithShadow(textRenderer, "<", wx + 6, y + 9, accent);
                ctx.drawTextWithShadow(textRenderer, ">", wx + 190 - 12, y + 9, accent);
                ctx.drawCenteredTextWithShadow(textRenderer, Lang.t(s.opts[s.idx]), wx + 95, y + 9, 0xFFFFFFFF);
                reg(wx, y + 3, 190, 20, true, (a, b, btn) -> {
                    int n = s.opts.length;
                    s.idx = (s.idx + (btn == 1 ? n - 1 : 1)) % n;
                    Modules.dirty = true;
                    Config.save();
                });
            }
            case Setting.COLOR -> {
                ctx.drawTextWithShadow(textRenderer, Lang.t(s.label), lx, y + 9, 0xFFDDDDEE);
                int sx = lx + iw - Theme.PAL.length * 18 + 4;
                for (int i = 0; i < Theme.PAL.length; i++) {
                    int px = sx + i * 18;
                    if (i == 0) {
                        for (int k = 0; k < 14; k++) ctx.fill(px + k, y + 5, px + k + 1, y + 19, Theme.rainbow(k / 14f, 0.8f));
                    } else {
                        ctx.fill(px, y + 5, px + 14, y + 19, 0xFF000000 | Theme.PAL[i]);
                    }
                    ctx.drawBorder(px - 1, y + 4, 16, 16, s.idx == i ? 0xFFFFFFFF : 0x40000000);
                    final int idx = i;
                    reg(px, y + 5, 14, 14, true, (a, b, c) -> {
                        s.idx = idx;
                        Modules.dirty = true;
                        Config.save();
                    });
                }
            }
            case Setting.BOOL -> {
                ctx.drawTextWithShadow(textRenderer, Lang.t(s.label), lx, y + 9, 0xFFDDDDEE);
                int sx = lx + iw - 34, sy = y + 6;
                ctx.fill(sx, sy, sx + 34, sy + 14, s.flag ? accent : 0xFF3A3A55);
                ctx.drawBorder(sx, sy, 34, 14, 0x60FFFFFF);
                ctx.fill(s.flag ? sx + 20 : sx + 2, sy + 2, s.flag ? sx + 32 : sx + 14, sy + 12, 0xFFFFFFFF);
                reg(sx, sy, 34, 14, true, (a, b, c) -> {
                    s.flag = !s.flag;
                    Modules.dirty = true;
                    Config.save();
                });
            }
            case Setting.TEXT -> {
                int fw = Math.min(iw, 300);
                int fx = lx + iw - fw;
                drawInput(ctx, s, fx, y + 3, fw);
            }
            case Setting.ACTION -> {
                int bx = lx, bw = iw;
                boolean hv = in(mx, my, bx, y + 3, bw, 20) && my >= cTop && my <= cBottom;
                ctx.fill(bx, y + 3, bx + bw, y + 23, hv ? 0xFF34345A : 0xFF24243F);
                ctx.drawBorder(bx, y + 3, bw, 20, hv ? accent : 0x60FFFFFF);
                ctx.drawCenteredTextWithShadow(textRenderer, Lang.t(s.label), bx + bw / 2, y + 9, 0xFFFFFFFF);
                reg(bx, y + 3, bw, 20, true, (a, b, c) -> {
                    if (s.action != null) s.action.run();
                });
            }
            case Setting.INFO -> {
                List<OrderedText> lines = textRenderer.wrapLines(Text.literal(Lang.t(s.label)), iw);
                for (int i = 0; i < lines.size(); i++) {
                    ctx.drawTextWithShadow(textRenderer, lines.get(i), lx, y + 5 + i * 10, 0xFFAAAAC8);
                }
            }
            case Setting.XHAIR -> drawXhair(ctx, s, lx, y, iw, mx, my);
            case Setting.DYN -> {
                List<OrderedText> lines = textRenderer.wrapLines(Text.literal(s.dyn.get()), iw);
                for (int i = 0; i < lines.size(); i++) {
                    ctx.drawTextWithShadow(textRenderer, lines.get(i), lx, y + 5 + i * 10, 0xFFE8D070);
                }
            }
            case Setting.BLOCKS -> drawBlocks(ctx, s, lx, y, iw, mx, my);
            default -> { }
        }
    }

    void drawInput(DrawContext ctx, Setting s, int fx, int fy, int fw) {
        boolean f = focus == s;
        ctx.fill(fx, fy, fx + fw, fy + 20, 0xFF1E1E36);
        ctx.drawBorder(fx, fy, fw, 20, f ? Theme.accent(0f) : 0xFF44445A);
        String shown = s.text;
        if (f && (System.currentTimeMillis() / 500) % 2 == 0) shown += "_";
        while (textRenderer.getWidth(shown) > fw - 10 && shown.length() > 1) shown = shown.substring(1);
        if (s.text.isEmpty() && !f) {
            String h = textRenderer.trimToWidth(Lang.t(s.hint.isEmpty() ? "s.nick" : s.hint), fw - 10);
            ctx.drawText(textRenderer, h, fx + 5, fy + 6, 0xFF70708A, false);
        } else {
            ctx.drawTextWithShadow(textRenderer, shown, fx + 5, fy + 6, 0xFFFFFFFF);
        }
        reg(fx, fy, fw, 20, true, (a, b, c) -> focus = s);
    }

    void drawBlocks(DrawContext ctx, Setting s, int lx, int y, int iw, int mx, int my) {
        int accent = Theme.accent(0f);
        List<Integer> ws = presetW();
        List<int[]> pos = flow(ws, iw, 4, 22);
        for (int i = 0; i < PK.length; i++) {
            int bx = lx + pos.get(i)[0], by = y + pos.get(i)[1] + 2;
            int bw = ws.get(i);
            boolean hv = in(mx, my, bx, by, bw, 18) && my >= cTop && my <= cBottom;
            ctx.fill(bx, by, bx + bw, by + 18, hv ? 0xFF34345A : 0xFF24243F);
            ctx.drawBorder(bx, by, bw, 18, hv ? accent : 0x50FFFFFF);
            ctx.drawCenteredTextWithShadow(textRenderer, Lang.t(PK[i]), bx + bw / 2, by + 5, 0xFFFFFFFF);
            final String key = PK[i];
            reg(bx, by, bw, 18, true, (a, b, c) -> {
                Esp.preset(key, s);
                Modules.dirty = true;
                Config.save();
            });
        }
        int py = y + flowH(pos, 22) + 4;
        drawInput(ctx, s, lx, py, iw);

        int cy = py + 26;
        List<String> ids = new ArrayList<>(s.blocks);
        List<Integer> cws = chipW(s);
        List<int[]> cpos = flow(cws, iw, 4, 18);
        for (int i = 0; i < ids.size(); i++) {
            int bx = lx + cpos.get(i)[0], by = cy + cpos.get(i)[1];
            int bw = cws.get(i);
            boolean hv = in(mx, my, bx, by, bw, 15) && my >= cTop && my <= cBottom;
            ctx.fill(bx, by, bx + bw, by + 15, hv ? 0xFF5A2A2A : 0xFF2A2A48);
            ctx.drawBorder(bx, by, bw, 15, 0x40FFFFFF);
            ctx.drawText(textRenderer, ids.get(i), bx + 5, by + 4, 0xFFE0E0F0, false);
            ctx.drawText(textRenderer, "x", bx + bw - 10, by + 4, hv ? 0xFFFF6060 : 0xFF9090B0, false);
            final String id = ids.get(i);
            reg(bx, by, bw, 15, true, (a, b, c) -> {
                s.blocks.remove(id);
                Modules.dirty = true;
                Config.save();
            });
        }
    }

    // ===== jezyk =====
    int drawLang(DrawContext ctx, int x, int y, int w, int mx, int my) {
        int hh = 30, body = 84;
        int total = hh + body;
        int accent = Theme.accent(0.3f);
        ctx.fill(x, y, x + w, y + total, pa(0xC0191935));
        ctx.fill(x, y, x + 3, y + total, accent);
        ctx.drawBorder(x, y, w, total, 0x40FFFFFF);
        ctx.drawItem(new ItemStack(Items.COMPASS), x + 10, y + 7);
        ctx.drawTextWithShadow(textRenderer, Text.literal(Lang.t("menu.lang")).formatted(Formatting.BOLD), x + 32, y + 11, accent);
        int cell = (w - 28 - 3 * 8) / 4;
        for (int i = 0; i < 4; i++) {
            int cx = x + 14 + i * (cell + 8);
            int cy = y + hh + 6;
            boolean sel = Lang.cur == i;
            boolean hv = in(mx, my, cx, cy, cell, 72) && my >= cTop && my <= cBottom;
            ctx.fill(cx, cy, cx + cell, cy + 72, sel ? 0xFF2A2A55 : hv ? 0xFF24243F : 0xFF1A1A30);
            ctx.drawBorder(cx, cy, cell, 72, sel ? accent : 0x40FFFFFF);
            int fw = Math.min(64, cell - 12), fh = fw * 5 / 8;
            Flags.draw(ctx, i, cx + (cell - fw) / 2, cy + 8, fw, fh);
            ctx.drawCenteredTextWithShadow(textRenderer, Lang.NAMES[i], cx + cell / 2, cy + 8 + fh + 8, sel ? 0xFFFFFFFF : 0xFFAAAAC8);
            final int idx = i;
            reg(cx, cy, cell, 72, true, (a, b, c) -> {
                Lang.cur = idx;
                Voice.say(Lang.t("v.lang"));
                Config.save();
            });
        }
        return y + total + 8;
    }

    // ===== wybor celownika =====
    void drawXhair(DrawContext ctx, Setting s, int lx, int y, int iw, int mx, int my) {
        int accent = Theme.accent(0f);
        ctx.drawTextWithShadow(textRenderer, Lang.t(s.label), lx, y + 3, 0xFFDDDDEE);
        int n = s.opts.length;
        int gap = 5;
        int tw = (iw - gap * (n - 1)) / n;
        int th = 42;
        Mod m = Mods.CROSSHAIR;
        int col = Theme.pal(m.get("color").idx, 0f) & 0xFFFFFF;
        for (int i = 0; i < n; i++) {
            int x = lx + i * (tw + gap), ty = y + 15;
            boolean sel = s.idx == i;
            boolean hv = in(mx, my, x, ty, tw, th) && my >= cTop && my <= cBottom;
            ctx.fill(x, ty, x + tw, ty + th, sel ? 0xCC2A2A55 : hv ? 0xCC24243F : 0xAA10101E);
            ctx.drawBorder(x, ty, tw, th, sel ? accent : 0x50FFFFFF);
            int ccx = x + tw / 2, ccy = ty + 15;
            if (i == 0) {
                ctx.fill(ccx - 5, ccy, ccx + 5, ccy + 1, 0xFFFFFFFF);
                ctx.fill(ccx, ccy - 5, ccx + 1, ccy + 5, 0xFFFFFFFF);
            } else {
                Crosshair.draw(ctx, i, ccx, ccy, Math.min(m.n("size"), 8), Math.min(m.n("thick"), 3), Math.min(m.n("gap"), 5), col, m.b("outline"), 1f);
            }
            String nm = Lang.t(s.opts[i]);
            float k = Math.min(0.85f, (tw - 4f) / Math.max(1, textRenderer.getWidth(nm)));
            ctx.getMatrices().push();
            ctx.getMatrices().translate(x + tw / 2f, ty + th - 10, 0);
            ctx.getMatrices().scale(k, k, 1f);
            ctx.drawCenteredTextWithShadow(textRenderer, nm, 0, 0, sel ? 0xFFFFFFFF : 0xFFAAAAC8);
            ctx.getMatrices().pop();
            final int idx = i;
            reg(x, ty, tw, th, true, (a, b, c) -> {
                s.idx = idx;
                Config.save();
            });
        }
    }

    // ===== MUZYKA =====
    static String mmss(int sec) {
        return sec / 60 + ":" + String.format("%02d", sec % 60);
    }

    int drawMusic(DrawContext ctx, int x, int y, int w, int mx, int my) {
        int accent = Theme.accent(0.2f);
        int iw = w - 28;
        String label = Lang.t("mu.label");
        int lw = textRenderer.getWidth(label);
        boolean stacked = lw + 130 > iw;
        List<Music.Track> list = Music.results;
        int shown = Math.min(list.size(), 25);
        List<OrderedText> info = textRenderer.wrapLines(Text.literal(Lang.t("mu.info")), iw);
        int total = 10 + (stacked ? 14 + 24 : 26) + 6 + 24 + 8 + 14 + (Music.status.isEmpty() ? 0 : 14)
                + shown * 22 + 8 + info.size() * 10 + 10;

        ctx.fill(x, y, x + w, y + total, pa(0xC0191935));
        ctx.fill(x, y, x + 3, y + total, accent);
        ctx.drawBorder(x, y, w, total, 0x40FFFFFF);

        int cy = y + 10;
        int lx = x + 14;
        if (stacked) {
            ctx.drawTextWithShadow(textRenderer, Text.literal(label).formatted(Formatting.BOLD), lx, cy + 2, accent);
            cy += 14;
            drawInput(ctx, QUERY, lx, cy, iw);
            cy += 26;
        } else {
            ctx.drawTextWithShadow(textRenderer, Text.literal(label).formatted(Formatting.BOLD), lx, cy + 6, accent);
            drawInput(ctx, QUERY, lx + lw + 10, cy, iw - lw - 10);
            cy += 26;
        }
        cy += 6;

        // przyciski
        String[] names = {Lang.t("mu.local"), Lang.t("mu.stop"), Lang.t("mu.next")};
        Runnable[] acts = {Music::listLocal, Music::stop, Music::next};
        int bw = (iw - 12) / 3;
        for (int i = 0; i < 3; i++) {
            int bx = lx + i * (bw + 6);
            boolean hv = in(mx, my, bx, cy, bw, 20) && my >= cTop && my <= cBottom;
            ctx.fill(bx, cy, bx + bw, cy + 20, hv ? 0xFF34345A : 0xFF24243F);
            ctx.drawBorder(bx, cy, bw, 20, hv ? accent : 0x60FFFFFF);
            ctx.drawCenteredTextWithShadow(textRenderer, names[i], bx + bw / 2, cy + 6, 0xFFFFFFFF);
            final Runnable r = acts[i];
            reg(bx, cy, bw, 20, true, (a, b, c) -> r.run());
        }
        cy += 24 + 8;

        // teraz gra
        Music.Track now = Music.now;
        String nowTxt = now == null ? "-" : textRenderer.trimToWidth(now.label(), iw - 120) + "   " + mmss(Music.posSec);
        ctx.drawTextWithShadow(textRenderer, Lang.t("mu.now") + " " + nowTxt, lx, cy, 0xFFE8D070);
        cy += 14;
        if (!Music.status.isEmpty()) {
            ctx.drawTextWithShadow(textRenderer, Music.status, lx, cy, 0xFFFF9090);
            cy += 14;
        }

        // wyniki
        for (int i = 0; i < shown; i++) {
            Music.Track tr = list.get(i);
            boolean cur = tr == now;
            boolean hv = in(mx, my, lx, cy, iw, 20) && my >= cTop && my <= cBottom;
            ctx.fill(lx, cy, lx + iw, cy + 20, cur ? 0xCC2A2A55 : hv ? 0xCC24243F : 0x99121224);
            ctx.drawBorder(lx, cy, iw, 20, cur ? accent : 0x30FFFFFF);
            ctx.drawTextWithShadow(textRenderer, cur && Music.playing ? "\u25B6" : "\u266A", lx + 6, cy + 6, cur ? accent : 0xFFAAAAC8);
            ctx.drawTextWithShadow(textRenderer, textRenderer.trimToWidth(tr.label(), iw - 70), lx + 20, cy + 6, 0xFFFFFFFF);
            if (tr.seconds() > 0) {
                String d = mmss(tr.seconds());
                ctx.drawTextWithShadow(textRenderer, d, lx + iw - textRenderer.getWidth(d) - 6, cy + 6, 0xFF9A9ABB);
            }
            reg(lx, cy, iw, 20, true, (a, b, c) -> Music.play(tr));
            cy += 22;
        }
        cy += 8;
        for (int i = 0; i < info.size(); i++) {
            ctx.drawTextWithShadow(textRenderer, info.get(i), lx, cy + i * 10, 0xFFAAAAC8);
        }
        return y + total + 8;
    }

    // ===== INFO =====
    int drawInfo(DrawContext ctx, int x, int y, int w, int mx, int my) {
        long t = System.currentTimeMillis();
        int total = 250;
        int accent = Theme.accent(0.5f);
        ctx.fill(x, y, x + w, y + total, pa(0xC0191935));
        ctx.fill(x, y, x + 3, y + total, accent);
        ctx.drawBorder(x, y, w, total, 0x40FFFFFF);

        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("#INFO").formatted(Formatting.BOLD), x + w / 2, y + 12, 0xFFFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, Branding.NAME + "  v" + Branding.VERSION, x + w / 2, y + 30, 0xFFCCCCEE);
        ctx.drawCenteredTextWithShadow(textRenderer, Lang.t("menu.author") + ": ZolteNiebiosa", x + w / 2, y + 44, 0xFF9A9ABB);

        int bw = 240, bh = 28;
        int bx = x + w / 2 - bw / 2, by = y + 66;
        boolean hv = in(mx, my, bx, by, bw, bh) && my >= cTop && my <= cBottom;
        ctx.fill(bx, by, bx + bw, by + bh, hv ? 0xFF34345A : 0xFF24243F);
        ctx.drawBorder(bx, by, bw, bh, Theme.rainbow(0f, 0.8f));
        String s = "YT-ZolteNiebiosa";
        int tw = 0;
        for (int i = 0; i < s.length(); i++) tw += textRenderer.getWidth(Text.literal(String.valueOf(s.charAt(i))).formatted(Formatting.BOLD));
        int tx = bx + bw / 2 - tw / 2;
        for (int i = 0; i < s.length(); i++) {
            Text ch = Text.literal(String.valueOf(s.charAt(i))).formatted(Formatting.BOLD);
            int yy = by + 10 + (int) (Math.sin(t / 250.0 + i * 0.5) * 2);
            ctx.drawTextWithShadow(textRenderer, ch, tx, yy, Theme.rainbow(i * 0.06f, 0.85f));
            tx += textRenderer.getWidth(ch);
        }
        reg(bx, by, bw, bh, true, (a, b, c) -> Util.getOperatingSystem().open(URI.create(Branding.YT_URL)));

        int sy = by + bh + 12;
        Art.discord(ctx, x + w / 2 - 70, sy + 4, 1f, 1f);
        ctx.drawTextWithShadow(textRenderer, Branding.DISCORD, x + w / 2 - 56, sy, 0xFFC9D0FF);
        Art.yt(ctx, x + w / 2 - 70, sy + 22, 1f, 1f);
        ctx.drawTextWithShadow(textRenderer, Branding.YT, x + w / 2 - 56, sy + 18, 0xFFFFB3B3);

        List<OrderedText> lines = textRenderer.wrapLines(Text.literal(Lang.t("i.help")), w - 28);
        for (int i = 0; i < lines.size(); i++) {
            ctx.drawTextWithShadow(textRenderer, lines.get(i), x + 14, y + 150 + i * 11, 0xFFAAAAC8);
        }
        return y + total + 8;
    }

    // ===== wejscie =====
    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        focus = null;
        for (int i = regs.size() - 1; i >= 0; i--) {
            R r = regs.get(i);
            if (r.content && (my < cTop || my > cBottom)) continue;
            if (in(mx, my, r.x, r.y, r.w, r.h)) {
                if (r.c != null) r.c.on(mx, my, btn);
                if (r.d != null) dragging = r;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        if (dragging != null && dragging.d != null) {
            dragging.d.on(mx);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        if (dragging != null) {
            dragging = null;
            Config.save();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hAmount, double vAmount) {
        scroll = Math.max(0, Math.min(maxScroll, scroll - vAmount * 24));
        return true;
    }

    void commit(Setting s) {
        if (s.kind == Setting.BLOCKS) {
            String id = s.text.trim().toLowerCase();
            if (id.startsWith("minecraft:")) id = id.substring(10);
            Identifier ident = id.isEmpty() ? null : Identifier.tryParse("minecraft:" + id);
            if (ident != null && Registries.BLOCK.containsId(ident)) {
                s.blocks.add(id);
                Modules.dirty = true;
            } else if (!id.isEmpty()) {
                toast("Nie ma takiego bloku: " + id);
            }
            s.text = "";
        } else if (s.onCommit != null) {
            s.onCommit.run();
        }
        Config.save();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (focus != null) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                if (!focus.text.isEmpty()) focus.text = focus.text.substring(0, focus.text.length() - 1);
            } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                Setting f = focus;
                focus = null;
                commit(f);
            } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                focus = null;
            } else if (Screen.hasControlDown() && keyCode == GLFW.GLFW_KEY_V) {
                String clip = client.keyboard.getClipboard();
                if (clip != null) {
                    String add = clip.replaceAll("[\\r\\n]", "");
                    if (focus.text.length() + add.length() <= 32) focus.text += add;
                }
            }
            return true;
        }
        if (ZolteClient.MENU.matchesKey(keyCode, scanCode)) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (focus != null) {
            if (chr >= 32 && chr != 127 && focus.text.length() < 32) focus.text += chr;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void close() {
        Config.save();
        super.close();
    }
}
