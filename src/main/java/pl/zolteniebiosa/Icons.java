package pl.zolteniebiosa;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class Icons {
    private static final Map<String, Identifier> CACHE = new HashMap<>();

    public static Identifier id(String name) {
        return CACHE.computeIfAbsent(name, n -> Identifier.of("zolteniebiosa", "textures/icon/" + n + ".png"));
    }

    // ikony 32x32 skalowane do podanego rozmiaru
    public static void draw(DrawContext ctx, String name, int x, int y, int size) {
        float s = size / 32f;
        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0.0);
        ctx.getMatrices().scale(s, s, 1f);
        ctx.drawTexture(RenderLayer::getGuiTextured, id(name), 0, 0, 0f, 0f, 32, 32, 32, 32);
        ctx.getMatrices().pop();
    }
}
