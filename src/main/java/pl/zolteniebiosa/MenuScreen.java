package pl.zolteniebiosa;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.awt.Color;
import java.net.URI;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class MenuScreen extends Screen {
    private static int tab = 0;
    private static final String[] TABS = {"WYGLĄD", "PETY", "hacksyZOLTEGO", "ZDOBYCZ", "BEZPIECZEŃSTWO", "#INFO"};
    private static final Item[] ICONS = {Items.DIAMOND_SWORD, Items.BONE, Items.ENDER_EYE,
            Items.DIAMOND_PICKAXE, Items.SHIELD, Items.BOOK};
    private static final String[] THEMES = {"Tęczowy", "Czerwony", "Niebieski", "Zielony", "Fioletowy"};
    private static final String[] MODES = {"Gracze", "Moby", "Gracze + Moby"};

    private final long openTime = System.currentTimeMillis();
    private int[] tabX = new int[6];
    private int tabW, tabY = 30;
    private int infoX, infoY, infoW = 200, infoH = 24;

    public MenuScreen() {
        super(Text.literal("ZolteNiebiosa CLIENT"));
    }

    @Override
    protected void init() {
        tabW = Math.min(125, (width - 10) / 6);
        int startX = width / 2 - tabW * 6 / 2;
        for (int i = 0; i < 6; i++) {
            final int idx = i;
            tabX[i] = startX + i * tabW;
            addDrawableChild(ButtonWidget.builder(Text.literal("   " + TABS[i]), b -> {
                tab = idx;
                clearAndInit();
            }).dimensions(tabX[i], tabY, tabW - 2, 20).build());
        }

        int x = width / 2 - 100, y = 70, w = 200, h = 20, gap = 24;
        switch (tab) {
            case 0 -> {
                cycle(x, y, w, h, "Motyw", THEMES, () -> Config.theme, v -> Config.theme = v);
                toggle(x, y + gap, w, h, "Noc (wizualnie)", () -> Config.night, v -> Config.night = v);
            }
            case 2 -> {
                toggle(x, y, w, h, "Tracery", () -> Config.tracers, v -> Config.tracers = v);
                addDrawableChild(new Slider(x, y + gap, w, h, "Dystans tracerów", 1, 1000,
                        Config.tracerDist, v -> Config.tracerDist = v));
                cycle(x, y + gap * 2, w, h, "Cel", MODES, () -> Config.tracerMode, v -> Config.tracerMode = v);
            }
            case 3 -> {
                toggle(x, y, w, h, "Block ESP / Xray", () -> Config.blockEsp, v -> {
                    Config.blockEsp = v;
                    if (v) Esp.scan(client); else Esp.hits = java.util.List.of();
                });
                addDrawableChild(new Slider(x, y + gap, w, h, "Zasięg ESP", 1, 48,
                        Config.espDist, v -> Config.espDist = v));
                cycle(x, y + gap * 2, w, h, "Bloki", Esp.PRESET_NAMES, () -> Config.espPreset, v -> {
                    Config.espPreset = v;
                    if (Config.blockEsp) Esp.scan(client);
                });
            }
            case 4 -> {
                addDrawableChild(ButtonWidget.builder(Text.literal("Anty kostka: ZAPISZ układ EQ"),
                        b -> Modules.saveInventory(client)).dimensions(x, y, w, h).build());
                addDrawableChild(ButtonWidget.builder(Text.literal("Anty kostka: PRZYWRÓĆ EQ teraz"),
                        b -> Modules.restoreInventory(client)).dimensions(x, y + gap, w, h).build());
                toggle(x, y + gap * 2, w, h, "Auto Leave", () -> Config.autoLeave, v -> Config.autoLeave = v);
                addDrawableChild(new Slider(x, y + gap * 3, w, h, "Dystans Auto Leave", 1, 100,
                        Config.leaveDist, v -> Config.leaveDist = v));
            }
            case 5 -> {
                infoX = width / 2 - infoW / 2;
                infoY = y + 40;
                addDrawableChild(ButtonWidget.builder(Text.empty(), b ->
                        Util.getOperatingSystem().open(URI.create("https://www.youtube.com/@zolteNiebiosa")))
                        .dimensions(infoX, infoY, infoW, infoH).build());
            }
            default -> {}
        }
    }

    private void toggle(int x, int y, int w, int h, String name, BooleanSupplier get, Consumer<Boolean> set) {
        addDrawableChild(ButtonWidget.builder(onOff(name, get.getAsBoolean()), b -> {
            set.accept(!get.getAsBoolean());
            b.setMessage(onOff(name, get.getAsBoolean()));
        }).dimensions(x, y, w, h).build());
    }

    private void cycle(int x, int y, int w, int h, String name, String[] opts,
                       java.util.function.IntSupplier get, IntConsumer set) {
        addDrawableChild(ButtonWidget.builder(Text.literal(name + ": " + opts[get.getAsInt()]), b -> {
            set.accept((get.getAsInt() + 1) % opts.length);
            b.setMessage(Text.literal(name + ": " + opts[get.getAsInt()]));
        }).dimensions(x, y, w, h).build());
    }

    private static Text onOff(String name, boolean on) {
        return Text.literal(name + ": ").append(Text.literal(on ? "WŁ" : "WYŁ")
                .formatted(on ? Formatting.GREEN : Formatting.RED));
    }

    private static class Slider extends SliderWidget {
        private final String name;
        private final int min, max;
        private final IntConsumer set;

        Slider(int x, int y, int w, int h, String name, int min, int max, int val, IntConsumer set) {
            super(x, y, w, h, Text.empty(), (val - min) / (double) (max - min));
            this.name = name;
            this.min = min;
            this.max = max;
            this.set = set;
            updateMessage();
        }

        private int val() {
            return (int) Math.round(min + (max - min) * this.value);
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.literal(name + ": " + val()));
        }

        @Override
        protected void applyValue() {
            set.accept(val());
        }
    }

    private int color(float offset) {
        long t = System.currentTimeMillis();
        return switch (Config.theme) {
            case 1 -> 0xFFFF4040;
            case 2 -> 0xFF40A0FF;
            case 3 -> 0xFF40FF60;
            case 4 -> 0xFFB040FF;
            default -> 0xFF000000 | Color.HSBtoRGB(((t % 4000L) / 4000f + offset) % 1f, 0.75f, 1f);
        };
    }

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fillGradient(0, 0, width, height, 0xEE08081A, 0xEE22093A);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        long t = System.currentTimeMillis();
        float fade = Math.min(1f, (t - openTime) / 600f);

        // tytuł - animowane falujące litery
        String title = "ZolteNiebiosa CLIENT";
        float scale = 2f;
        ctx.getMatrices().push();
        ctx.getMatrices().scale(scale, scale, 1f);
        int total = textRenderer.getWidth(Text.literal(title).formatted(Formatting.BOLD));
        int cx = (int) (width / 2f / scale - total / 2f);
        for (int i = 0; i < title.length(); i++) {
            Text ch = Text.literal(String.valueOf(title.charAt(i))).formatted(Formatting.BOLD);
            int yy = 2 + (int) (Math.sin(t / 220.0 + i * 0.55) * 2 * fade);
            ctx.drawTextWithShadow(textRenderer, ch, cx, yy, color(i * 0.045f));
            cx += textRenderer.getWidth(ch);
        }
        ctx.getMatrices().pop();

        // ikonki na zakładkach + podkreślenie aktywnej
        for (int i = 0; i < 6; i++) {
            ctx.drawItem(new ItemStack(ICONS[i]), tabX[i] + 2, tabY + 2);
        }
        ctx.fill(tabX[tab], tabY + 21, tabX[tab] + tabW - 2, tabY + 23, color(0f));

        // napis dla zakładek w budowie
        if (tab == 1) {
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("Pety (papuga, kot, pies, panda) - ETAP 2").formatted(Formatting.GRAY), width / 2, 80, 0xFFFFFFFF);
        }
        if (tab == 0) {
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("Viewmodel miecza, języki, deszcz/śnieg - ETAP 2").formatted(Formatting.GRAY), width / 2, 130, 0xFFFFFFFF);
        }
        if (tab == 5) {
            Text yt = Text.literal("YT-ZolteNiebiosa").formatted(Formatting.BOLD);
            int w = textRenderer.getWidth(yt);
            int x = infoX + infoW / 2 - w / 2;
            String s = "YT-ZolteNiebiosa";
            for (int i = 0; i < s.length(); i++) {
                Text ch = Text.literal(String.valueOf(s.charAt(i))).formatted(Formatting.BOLD);
                ctx.drawTextWithShadow(textRenderer, ch, x, infoY + 8, color(i * 0.06f));
                x += textRenderer.getWidth(ch);
            }
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("Kliknij, aby otworzyć kanał na YouTube").formatted(Formatting.GRAY), width / 2, infoY + 34, 0xFFFFFFFF);
        }
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("N - menu   |   J - anty kostka (przywróć EQ)").formatted(Formatting.DARK_GRAY), width / 2, height - 14, 0xFFFFFFFF);
    }
}
