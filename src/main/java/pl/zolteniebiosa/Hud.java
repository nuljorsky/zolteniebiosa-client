package pl.zolteniebiosa;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Hud {
    public static void render(DrawContext ctx, RenderTickCounter tc) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!Mods.HUD.enabled || mc.player == null || mc.options.hudHidden) return;
        TextRenderer tr = mc.textRenderer;
        int W = ctx.getScaledWindowWidth(), H = ctx.getScaledWindowHeight();
        long t = System.currentTimeMillis();

        if (Mods.HUD.b("glow")) {
            int base = Theme.accent(0f);
            double pulse = 0.6 + 0.4 * Math.sin(t / 500.0);
            int n = 10;
            for (int i = 0; i < n; i++) {
                int a = (int) ((1.0 - i / (double) n) * 70 * pulse);
                int c = Theme.alpha(Theme.accent(i * 0.02f), a);
                ctx.fill(i, 0, i + 1, H, c);
                ctx.fill(W - i - 1, 0, W - i, H, c);
                ctx.fill(0, i, W, i + 1, c);
                ctx.fill(0, H - i - 1, W, H - i, c);
            }
        }

        if (Mods.HUD.b("watermark")) {
            String title = "ZolteNiebiosa CLIENT";
            int x = 6;
            for (int i = 0; i < title.length(); i++) {
                Text ch = Text.literal(String.valueOf(title.charAt(i))).formatted(Formatting.BOLD);
                ctx.drawTextWithShadow(tr, ch, x, 6, Theme.accent(i * 0.05f));
                x += tr.getWidth(ch);
            }
            String info = "FPS " + mc.getCurrentFps() + "  XYZ " + (int) mc.player.getX() + " " + (int) mc.player.getY() + " " + (int) mc.player.getZ();
            ctx.drawTextWithShadow(tr, info, 6, 17, 0xFFCCCCDD);
        }

        if (Mods.HUD.b("arraylist")) {
            List<String> names = new ArrayList<>();
            for (Mod m : Mods.ALL) if (m.hasSwitch && m.enabled && m != Mods.HUD && m != Mods.VOICE) names.add(m.name());
            names.sort(Comparator.comparingInt((String s) -> tr.getWidth(s)).reversed());
            int y = 5;
            for (int i = 0; i < names.size(); i++) {
                String s = names.get(i);
                int w = tr.getWidth(s);
                int x = W - w - 8;
                int col = Theme.accent(i * 0.07f);
                ctx.fill(x - 3, y - 1, W - 2, y + 10, 0x90000000);
                ctx.fill(W - 3, y - 1, W - 1, y + 10, col);
                ctx.drawTextWithShadow(tr, s, x, y + 1, col);
                y += 11;
            }
        }
    }
}
