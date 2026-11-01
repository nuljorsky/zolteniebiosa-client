package pl.zolteniebiosa.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.zolteniebiosa.ClientTheme;

/**
 * Motyw clienta na ekranie glownym (tam gdzie Singleplayer/Multiplayer) - snieg leci juz tutaj,
 * nie tylko w swiecie, plus plywajace logo ZTN w rogu. Dzieki temu to wyglada jak wlasny
 * launcher/client, a nie jak zwykly vanilla ekran z dorzuconym modem.
 */
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void zn$theme(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ClientTheme.renderTitleScreen(ctx, ctx.getScaledWindowWidth(), ctx.getScaledWindowHeight());
    }
}
