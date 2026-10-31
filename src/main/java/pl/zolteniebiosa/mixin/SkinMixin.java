package pl.zolteniebiosa.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.zolteniebiosa.Mods;
import pl.zolteniebiosa.Skin;

@Mixin(AbstractClientPlayerEntity.class)
public class SkinMixin {
    @Inject(method = "getSkinTextures", at = @At("RETURN"), cancellable = true)
    private void zn$skin(CallbackInfoReturnable<SkinTextures> cir) {
        SkinTextures t = Skin.tex;
        if (t != null && Mods.SKIN.enabled && (Object) this == MinecraftClient.getInstance().player) {
            cir.setReturnValue(t);
        }
    }
}
