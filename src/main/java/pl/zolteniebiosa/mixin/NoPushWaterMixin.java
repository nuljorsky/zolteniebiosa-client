package pl.zolteniebiosa.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.zolteniebiosa.NoPush;

/** Woda i lawa nie znosza lokalnego gracza. */
@Mixin(PlayerEntity.class)
public class NoPushWaterMixin {
    @Inject(method = "isPushedByFluids", at = @At("HEAD"), cancellable = true)
    private void zn$nowater(CallbackInfoReturnable<Boolean> cir) {
        if (NoPush.water((PlayerEntity) (Object) this)) cir.setReturnValue(false);
    }
}
