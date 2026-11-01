package pl.zolteniebiosa.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.zolteniebiosa.BootAnimation;
import pl.zolteniebiosa.Mods;

/**
 * Rysuje animacje ZTN na wierzchu ekranu ladowania Minecrafta.
 * Zasloni logo Mojang oraz logo ladowania z paczek zasobow / launcherow.
 */
@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin {
    @Shadow
    private long reloadCompleteTime;

    @Shadow
    private float progress;

    @Inject(method = "render", at = @At("RETURN"))
    private void zn$boot(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!Mods.THEME.b("bootanim")) return;
        float fade = 1f;
        if (reloadCompleteTime > -1L) {
            float f = (Util.getMeasuringTimeMs() - reloadCompleteTime) / 1000f;
            fade = f < 0.35f ? 1f : 1f - MathHelper.clamp((f - 0.35f) / 0.65f, 0f, 1f);
        }
        float p = reloadCompleteTime > -1L ? 1f : MathHelper.clamp(progress, 0f, 1f);
        BootAnimation.draw(context, context.getScaledWindowWidth(), context.getScaledWindowHeight(), p, fade);
    }
}
