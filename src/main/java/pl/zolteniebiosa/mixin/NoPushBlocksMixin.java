package pl.zolteniebiosa.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.zolteniebiosa.NoPush;

/** Anuluje wypychanie z blokow (np. gdy stoisz w ciasnym miejscu / trapie). */
@Mixin(ClientPlayerEntity.class)
public class NoPushBlocksMixin {
    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void zn$noblockpush(double x, double z, CallbackInfo ci) {
        if (NoPush.blocks()) ci.cancel();
    }
}
