package pl.zolteniebiosa.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.zolteniebiosa.Mods;

@Mixin(ClientPlayerEntity.class)
public class ClientPushMixin {
    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void zn$pushOut(double x, double z, CallbackInfo ci) {
        if (Mods.NOPUSH.enabled) ci.cancel();
    }
}
