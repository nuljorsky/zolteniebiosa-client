package pl.zolteniebiosa;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;

public class Flags {
    public static void draw(DrawContext ctx, int lang, int x, int y, int w, int h) {
        switch (lang) {
            case 0 -> {
                ctx.fill(x, y, x + w, y + h / 2, 0xFFFFFFFF);
                ctx.fill(x, y + h / 2, x + w, y + h, 0xFFDC143C);
            }
            case 1 -> {
                ctx.enableScissor(x, y, x + w, y + h);
                ctx.fill(x, y, x + w, y + h, 0xFF012169);
                double ang = Math.atan2(h, w);
                int len = (int) Math.hypot(w, h);
                MatrixStack ms = ctx.getMatrices();
                for (int s = -1; s <= 1; s += 2) {
                    ms.push();
                    ms.translate(x + w / 2.0, y + h / 2.0, 0.0);
                    ms.multiply(new Quaternionf().rotateZ((float) (s * ang)));
                    ctx.fill(-len, -3, len, 3, 0xFFFFFFFF);
                    ctx.fill(-len, -1, len, 1, 0xFFC8102E);
                    ms.pop();
                }
                ctx.fill(x, y + h / 2 - 4, x + w, y + h / 2 + 4, 0xFFFFFFFF);
                ctx.fill(x + w / 2 - 4, y, x + w / 2 + 4, y + h, 0xFFFFFFFF);
                ctx.fill(x, y + h / 2 - 2, x + w, y + h / 2 + 2, 0xFFC8102E);
                ctx.fill(x + w / 2 - 2, y, x + w / 2 + 2, y + h, 0xFFC8102E);
                ctx.disableScissor();
            }
            case 2 -> {
                int t = h / 3;
                ctx.fill(x, y, x + w, y + t, 0xFF000000);
                ctx.fill(x, y + t, x + w, y + 2 * t, 0xFFDD0000);
                ctx.fill(x, y + 2 * t, x + w, y + h, 0xFFFFCE00);
            }
            default -> {
                int t = h / 3;
                ctx.fill(x, y, x + w, y + t, 0xFFFFFFFF);
                ctx.fill(x, y + t, x + w, y + 2 * t, 0xFF0039A6);
                ctx.fill(x, y + 2 * t, x + w, y + h, 0xFFD52B1E);
            }
        }
        ctx.drawBorder(x, y, w, h, 0xFF000000);
    }
}
