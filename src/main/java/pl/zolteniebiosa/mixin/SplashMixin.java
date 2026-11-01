package pl.zolteniebiosa.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.zolteniebiosa.Splash;

@Mixin(SplashOverlay.class)
public class SplashMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void zn$splash(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Splash.drawIn(context);
    }
}
