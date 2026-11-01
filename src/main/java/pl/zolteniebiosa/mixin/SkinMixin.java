package pl.zolteniebiosa.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.zolteniebiosa.Cosmetics;

/** Podmienia skin i pelerynke - u Ciebie oraz u graczy z ZTN CLIENT. */
@Mixin(AbstractClientPlayerEntity.class)
public class SkinMixin {
    @Inject(method = "getSkinTextures", at = @At("RETURN"), cancellable = true)
    private void zn$skin(CallbackInfoReturnable<SkinTextures> cir) {
        SkinTextures t = Cosmetics.resolve((AbstractClientPlayerEntity) (Object) this, cir.getReturnValue());
        if (t != null) cir.setReturnValue(t);
    }
}
