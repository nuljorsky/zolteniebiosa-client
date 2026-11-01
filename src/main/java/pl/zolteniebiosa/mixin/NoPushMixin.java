package pl.zolteniebiosa.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.zolteniebiosa.Mods;

@Mixin({Entity.class, LivingEntity.class, PlayerEntity.class})
public class NoPushMixin {
    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    private void zn$pushFrom(Entity entity, CallbackInfo ci) {
        if (Mods.NOPUSH.enabled && (Object) this == MinecraftClient.getInstance().player) ci.cancel();
    }

    @Inject(method = "pushAway", at = @At("HEAD"), cancellable = true)
    private void zn$pushAway(Entity entity, CallbackInfo ci) {
        if (Mods.NOPUSH.enabled && (Object) this == MinecraftClient.getInstance().player) ci.cancel();
    }

    @Inject(method = "isPushedByFluids", at = @At("HEAD"), cancellable = true)
    private void zn$fluids(CallbackInfoReturnable<Boolean> cir) {
        if (Mods.NOPUSH.enabled && (Object) this == MinecraftClient.getInstance().player) cir.setReturnValue(false);
    }
}
