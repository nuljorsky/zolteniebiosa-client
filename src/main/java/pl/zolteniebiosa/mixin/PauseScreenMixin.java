package pl.zolteniebiosa.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.zolteniebiosa.ClientTheme;

@Mixin(GameMenuScreen.class)
public abstract class PauseScreenMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void zn$theme(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ClientTheme.renderPauseScreen(ctx, ctx.getScaledWindowWidth(), ctx.getScaledWindowHeight());
    }
}
