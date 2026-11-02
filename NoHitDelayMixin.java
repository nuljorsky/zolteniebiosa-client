package pl.zolteniebiosa.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.zolteniebiosa.Mods;

// NoHitDelay: klient zawsze uwaza, ze atak jest w pelni naladowany (wplywa na lokalna animacje/sile, serwer liczy sam).
@Mixin(PlayerEntity.class)
public class NoHitDelayMixin {
    @Inject(method = "getAttackCooldownProgress", at = @At("HEAD"), cancellable = true)
    private void zn$nodelay(float baseTime, CallbackInfoReturnable<Float> cir) {
        if (Mods.NOHITDELAY.enabled && (Object) this == MinecraftClient.getInstance().player) {
            cir.setReturnValue(1.0f);
        }
    }
}
