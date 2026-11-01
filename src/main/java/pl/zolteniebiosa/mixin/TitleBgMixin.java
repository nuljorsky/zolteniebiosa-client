package pl.zolteniebiosa.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.zolteniebiosa.TitleTheme;

@Mixin({Screen.class, TitleScreen.class})
public class TitleBgMixin {
    @Inject(method = "renderPanoramaBackground", at = @At("HEAD"), cancellable = true)
    private void zn$bg(DrawContext context, float deltaTicks, CallbackInfo ci) {
        if (TitleTheme.active()) {
            TitleTheme.background(context);
            ci.cancel();
        }
    }
}
