package pl.zolteniebiosa;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MenuScreen extends Screen {
    public static int tab = 0;
    static double scroll = 0, maxScroll = 0;
    static boolean welcomed = false;
    static String toast = "";
    static long toastT = 0;

    static final String[] TABS = {"WYGLĄD", "PETY", "hacksyZOLTEGO", "ZDOBYCZ", "BEZPIECZEŃSTWO", "MUZYKA", "#INFO"};
    static final Item[] ICONS = {Items.DIAMOND_SWORD, Items.BONE, Items.ENDER_EYE, Items.DIAMOND_PICKAXE, Items.SHIELD, Items.JUKEBOX, Items.BOOK};
    static final int MUSIC_TAB = 5, INFO_TAB = 6;
    static final String[] TAB_ICONS = {"tab_look", "tab_pets", "tab_hacks", "tab_loot", "tab_safe", "tab_music", "tab_info"};
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
    int lw, lh;

    float ui() {
        return Mods.THEME.n("uiscale") / 100f;
    }

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

    @Override
    public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
        int a = (int) (Mods.THEME.n("opacity") * 2.55);
        ctx.fillGradient(0, 0, width, height, (a << 24) | 0x05050F, (a << 24) | 0x1C0B30);
        if (Mods.THEME.b("bganim")) Splash.ambient(ctx, width, height, System.currentTimeMillis() - t0);
    }

    int cardCol(int rgb) {
        int a = 170 + (int) (Mods.THEME.n("opacity") * 0.45);
        return (Math.min(238, a) << 24) | rgb;
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
            case Setting.INFO -> textRenderer.wrapLines(Text.literal(infoText(s)), iw).size() * 10 + 10;
            case Setting.BLOCKS -> blocksH(s, iw);
            default -> 26;
        };
    }

    static String infoText(Setting s) {
        return s.dyn != null ? s.dyn.get() : Lang.t(s.label);
    }

    void blocksChanged(Setting s) {
        Modules.dirty = true;
        if (s == Mods.XRAY.get("blocks")) Xray.requestReload();
    }

    static void toast(String s) {
        toast = s;
        toastT = System.currentTimeMillis();
    }

    // ===== render =====
    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        regs.clear();
        float k = ui();
        lw = (int) (width / k);
        lh = (int) (height / k);
        mx = (int) (mx / k);
        my = (int) (my / k);
        long t = System.currentTimeMillis() - t0;
        renderBackground(ctx, mx, my, delta);
        ctx.getMatrices().push();
        ctx.getMatrices().scale(k, k, 1f);
        sparkles(ctx, t);
        drawTitle(ctx, t);
        drawTabs(ctx, mx, my, t);

        cTop = 68;
        cBottom = lh - 20;
        ctx.enableScissor(0, cTop, lw, cBottom);
        int cw = Math.min(540, lw - 30);
        int cx = (lw - cw) / 2;
        int y = cTop + 6 - (int) scroll;
        if (tab == INFO_TAB) {
            y = drawInfo(ctx, cx, y, cw, mx, my);
        } else {
            for (Mod m : Mods.ALL) if (m.cat == tab) y = drawCard(ctx, m, cx, y, cw, mx, my);
            if (tab == 0) y = drawLang(ctx, cx, y, cw, mx, my);
            if (tab == MUSIC_TAB) y = drawMusic(ctx, cx, y, cw, mx, my);
        }
        int content = y + (int) scroll - cTop;
        maxScroll = Math.max(0, content - (cBottom - cTop) + 8);
        if (scroll > maxScroll) scroll = maxScroll;
        ctx.disableScissor();

        if (System.currentTimeMillis() - toastT < 2500 && !toast.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, toast, lw / 2, lh - 32, 0xFFFF7070);
        }
        ctx.drawCenteredTextWithShadow(textRenderer, Lang.t("menu.hint"), lw / 2, lh - 13, 0xFF8888AA);
        PixelFont.yt(ctx, 8, lh - 20, 1);
        PixelFont.draw(ctx, "yt: zolteniebiosa", 34, lh - 17, 2, 0xFFFFFFFF);
        String dcs = "dc.crazy_ol1wk4";
        int dw = PixelFont.width(dcs, 2);
        PixelFont.dc(ctx, lw - 34 - dw - 4, lh - 20, 1);
        PixelFont.draw(ctx, dcs, lw - 8 - dw, lh - 17, 2, 0xFFFFFFFF);
        ctx.getMatrices().pop();
    }

    void sparkles(DrawContext ctx, long t) {
        for (int i = 0; i < 70; i++) {
            double a = Math.sin(i * 12.9898) * 43758.5453;
            double b = Math.sin(i * 78.233) * 12345.6789;
            double fx = a - Math.floor(a), fy = b - Math.floor(b);
            int x = (int) (fx * lw);
            int y = (int) (fy * lh);
            double tw = (Math.sin(t / 400.0 + i * 1.7) + 1) / 2;
            int al = (int) (tw * 170);
            int sz = (i % 5 == 0) ? 2 : 1;
            ctx.fill(x, y, x + sz, y + sz, Theme.alpha(Theme.accent(i * 0.02f), al));
        }
    }

    void drawTitle(DrawContext ctx, long t) {
        ctx.fill(0, 0, lw, 30, 0xE00A0A18);
        ctx.fill(0, 29, lw, 30, Theme.accent(0f));
        int sc = 2;
        int x = 10;
        for (int k = 0; k < 3; k++) {
            float p = Math.min(1f, Math.max(0f, (t - 100 - k * 150L) / 280f));
            if (p <= 0f) continue;
            int yy = 8 + (int) ((1f - p) * -10);
            int xx = x + k * 6 * sc;
            PixelFont.letter(ctx, k, xx + 1, yy + 1, sc, PixelFont.c(0x8A5A00, p));
            PixelFont.letter(ctx, k, xx, yy, sc, PixelFont.c(0xFFD21F, p));
        }
        String title = "ZolteNiebiosa CLIENT";
        int tx = x + PixelFont.logoWidth(sc) + 10;
        for (int i = 0; i < title.length(); i++) {
            Text ch = Text.literal(String.valueOf(title.charAt(i))).formatted(Formatting.BOLD);
            int yy = 11 + (int) (Math.sin(t / 260.0 + i * 0.5) * 1.2);
            ctx.drawTextWithShadow(textRenderer, ch, tx, yy, Theme.accent(i * 0.05f));
            tx += textRenderer.getWidth(ch);
        }
        String r = "v5.0   N = zamknij";
        ctx.drawTextWithShadow(textRenderer, r, lw - textRenderer.getWidth(r) - 10, 11, 0xFF9A9ABB);
    }

    void drawTabs(DrawContext ctx, int mx, int my, long t) {
        int tw = Math.min(150, (lw - 16) / TABS.length);
        int sx = (lw - tw * TABS.length) / 2;
        int ty = 36;
        for (int i = 0; i < TABS.length; i++) {
            int x = sx + i * tw;
            boolean sel = i == tab;
            boolean hov = in(mx, my, x, ty, tw - 3, 22);
            ctx.fill(x, ty, x + tw - 3, ty + 22, sel ? 0xE02A2A55 : hov ? 0xC01E1E3A : 0xA0141428);
            ctx.drawBorder(x, ty, tw - 3, 22, sel ? Theme.accent(i * 0.1f) : 0x30FFFFFF);
            if (sel) ctx.fill(x, ty + 20, x + tw - 3, ty + 22, Theme.accent(i * 0.1f));
            Icons.draw(ctx, TAB_ICONS[i], x + 3, ty + 3, 16);

            String name = TABS[i];
            float k = Math.min(1f, (tw - 28f) / textRenderer.getWidth(name));
            ctx.getMatrices().push();
            ctx.getMatrices().translate(x + 22, ty + 11 - 4.5f * k, 0.0);
            ctx.getMatrices().scale(k, k, 1f);
            if (i == TABS.length - 1) {
                // #INFO - gruba biala czcionka, wyrozniona
                ctx.drawTextWithShadow(textRenderer, Text.literal(name).formatted(Formatting.BOLD), 0, 0, 0xFFFFFFFF);
            } else {
                int col = Theme.rainbow(i * 0.13f + (float) Math.sin(t / 600.0) * 0.05f, 0.55f);
                ctx.drawTextWithShadow(textRenderer, name, 0, 0, col);
            }
            ctx.getMatrices().pop();
            if (i == TABS.length - 1) {
                int pulse = (int) (120 + 100 * Math.sin(t / 300.0));
                ctx.fill(x, ty + 19, x + tw - 3, ty + 20, Theme.alpha(0xFFFFFF, pulse));
            }
            final int idx = i;
            reg(x, ty, tw - 3, 22, false, (a, b, c) -> {
                tab = idx;
                scroll = 0;
                focus = null;
            });
        }
    }

    // ===== karta modulu =====
    int drawCard(DrawContext ctx, Mod m, int x, int y, int w, int mx, int my) {
        int hh = 36;
        int iw = w - 28;
        int body = 0;
        if (m.open) for (Setting s : m.settings) body += settingH(s, iw);
        int total = hh + body + (m.open && body > 0 ? 8 : 0);
        int accent = Theme.accent(y * 0.0015f);

        ctx.fill(x, y, x + w, y + total, cardCol(0x13132A));
        ctx.fill(x, y, x + w, y + hh, cardCol(m.enabled ? 0x2A2A52 : 0x1E1E3A));
        ctx.fill(x, y, x + 4, y + total, m.enabled ? accent : 0xFF3A3A55);
        ctx.drawBorder(x, y, w, total, m.enabled ? Theme.alpha(accent, 170) : 0x50FFFFFF);
        boolean hov = in(mx, my, x, y, w, hh) && my >= cTop && my <= cBottom;
        if (hov) ctx.fill(x + 4, y, x + w, y + hh, 0x1AFFFFFF);

        ctx.fill(x + 10, y + 6, x + 34, y + 30, 0xFF0C0C1C);
        ctx.drawBorder(x + 10, y + 6, 24, 24, m.enabled ? accent : 0x60FFFFFF);
        Icons.draw(ctx, m.id, x + 12, y + 8, 20);
        ctx.drawTextWithShadow(textRenderer, Text.literal(m.name()).formatted(Formatting.BOLD), x + 42, y + 7,
                m.enabled ? accent : 0xFFFFFFFF);
        ctx.drawText(textRenderer, textRenderer.trimToWidth(m.desc(), w - 42 - 100), x + 42, y + 21, 0xFFA0A0C4, false);

        reg(x, y, w, hh, true, (a, b, c) -> {
            if (m.hasSwitch) toggle(m);
            else m.open = !m.open;
        });
        int ax = x + w - (m.hasSwitch ? 74 : 28);
        ctx.fill(ax, y + 10, ax + 16, y + 26, 0xFF24243F);
        ctx.drawBorder(ax, y + 10, 16, 16, 0x60FFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, m.open ? "-" : "+", ax + 8, y + 14, 0xFFFFFFFF);
        reg(ax, y + 10, 16, 16, true, (a, b, c) -> m.open = !m.open);
        if (m.hasSwitch) {
            int sx = x + w - 46, sy = y + 11;
            boolean on = m.enabled;
            ctx.fill(sx, sy, sx + 34, sy + 14, on ? accent : 0xFF3A3A55);
            ctx.drawBorder(sx, sy, 34, 14, 0x70FFFFFF);
            ctx.fill(on ? sx + 20 : sx + 2, sy + 2, on ? sx + 32 : sx + 14, sy + 12, 0xFFFFFFFF);
            reg(sx, sy, 34, 14, true, (a, b, c) -> toggle(m));
        }

        if (m.open && body > 0) {
            ctx.fill(x + 4, y + hh, x + w, y + total, 0x55000000);
            int sy = y + hh + 2;
            for (int i = 0; i < m.settings.size(); i++) {
                Setting st = m.settings.get(i);
                int rh = settingH(st, iw);
                drawSetting(ctx, st, x + 14, sy, iw, mx, my);
                sy += rh;
                if (i < m.settings.size() - 1) ctx.fill(x + 14, sy - 1, x + w - 10, sy, 0x1CFFFFFF);
            }
        }
        return y + total + 12;
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
                int fw = iw >= 480 ? 220 : iw / 2;
                int fx = lx + iw - fw;
                String lab = Lang.t(s.label);
                boolean left = s.label.equals("s.query") || textRenderer.getWidth(lab) > fw - 12;
                if (left) {
                    ctx.drawTextWithShadow(textRenderer, textRenderer.trimToWidth(lab, iw - fw - 8), lx, y + 9, 0xFFDDDDEE);
                }
                drawInput(ctx, s, fx, y + 3, fw, left);
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
                List<OrderedText> lines = textRenderer.wrapLines(Text.literal(infoText(s)), iw);
                for (int i = 0; i < lines.size(); i++) {
                    ctx.drawTextWithShadow(textRenderer, lines.get(i), lx, y + 5 + i * 10, 0xFFAAAAC8);
                }
            }
            case Setting.BLOCKS -> drawBlocks(ctx, s, lx, y, iw, mx, my);
            default -> { }
        }
    }

    void drawInput(DrawContext ctx, Setting s, int fx, int fy, int fw) {
        drawInput(ctx, s, fx, fy, fw, false);
    }

    void drawInput(DrawContext ctx, Setting s, int fx, int fy, int fw, boolean quietHint) {
        boolean f = focus == s;
        ctx.fill(fx, fy, fx + fw, fy + 20, 0xFF1E1E36);
        ctx.drawBorder(fx, fy, fw, 20, f ? Theme.accent(0f) : 0xFF44445A);
        String shown = s.text;
        if (f && (System.currentTimeMillis() / 500) % 2 == 0) shown += "_";
        while (textRenderer.getWidth(shown) > fw - 10 && shown.length() > 1) shown = shown.substring(1);
        if (s.text.isEmpty() && !f) {
            String h = quietHint ? "..." : textRenderer.trimToWidth(Lang.t(s.hint.isEmpty() ? "s.nick" : s.hint), fw - 10);
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
                blocksChanged(s);
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
                blocksChanged(s);
                Config.save();
            });
        }
    }

    // ===== jezyk =====
    int drawLang(DrawContext ctx, int x, int y, int w, int mx, int my) {
        int hh = 30, body = 84;
        int total = hh + body;
        int accent = Theme.accent(0.3f);
        ctx.fill(x, y, x + w, y + total, cardCol(0x191935));
        ctx.fill(x, y, x + 3, y + total, accent);
        ctx.drawBorder(x, y, w, total, 0x40FFFFFF);
        Icons.draw(ctx, "lang", x + 10, y + 5, 22);
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

    // ===== MUZYKA =====
    int drawMusic(DrawContext ctx, int x, int y, int w, int mx, int my) {
        List<Music.Track> list = Music.results;
        String st = Music.status;
        if (!st.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, st, x + w / 2, y + 4, 0xFFAAAAC8);
            y += 16;
        }
        List<OrderedText> note = textRenderer.wrapLines(Text.literal(Lang.t("mu.note")), w);
        for (int i = 0; i < note.size(); i++) {
            ctx.drawTextWithShadow(textRenderer, note.get(i), x, y + 2 + i * 10, 0xFF8080A0);
        }
        y += note.size() * 10 + 8;
        for (int i = 0; i < list.size(); i++) {
            Music.Track tr = list.get(i);
            boolean playing = Music.playing == i;
            boolean hv = in(mx, my, x, y, w, 22) && my >= cTop && my <= cBottom;
            ctx.fill(x, y, x + w, y + 22, playing ? cardCol(0x2A2A55) : hv ? cardCol(0x242444) : cardCol(0x181830));
            ctx.drawBorder(x, y, w, 22, playing ? Theme.accent(i * 0.05f) : 0x30FFFFFF);
            String line = (playing ? "> " : "") + tr.title() + " - " + tr.artist();
            ctx.drawTextWithShadow(textRenderer, textRenderer.trimToWidth(line, w - 16), x + 8, y + 7,
                    playing ? Theme.accent(0f) : 0xFFFFFFFF);
            final int idx = i;
            reg(x, y, w, 22, true, (a, b, c) -> Music.play(idx));
            y += 24;
        }
        return y + 8;
    }

    // ===== INFO =====
    int drawInfo(DrawContext ctx, int x, int y, int w, int mx, int my) {
        long t = System.currentTimeMillis();
        int total = 210;
        int accent = Theme.accent(0.5f);
        ctx.fill(x, y, x + w, y + total, cardCol(0x191935));
        ctx.fill(x, y, x + 3, y + total, accent);
        ctx.drawBorder(x, y, w, total, 0x40FFFFFF);

        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("#INFO").formatted(Formatting.BOLD), x + w / 2, y + 12, 0xFFFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, "ZolteNiebiosa CLIENT  v2.0", x + w / 2, y + 30, 0xFFCCCCEE);
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
        reg(bx, by, bw, bh, true, (a, b, c) -> Util.getOperatingSystem().open(URI.create("https://www.youtube.com/@zolteNiebiosa")));

        List<OrderedText> lines = textRenderer.wrapLines(Text.literal(Lang.t("i.help")), w - 28);
        for (int i = 0; i < lines.size(); i++) {
            ctx.drawTextWithShadow(textRenderer, lines.get(i), x + 14, y + 112 + i * 11, 0xFFAAAAC8);
        }
        return y + total + 8;
    }

    // ===== wejscie =====
    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        mx /= ui();
        my /= ui();
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
        mx /= ui();
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
                blocksChanged(s);
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
