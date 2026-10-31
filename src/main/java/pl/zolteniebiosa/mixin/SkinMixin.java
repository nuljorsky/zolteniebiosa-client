package pl.zolteniebiosa.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.zolteniebiosa.Capes;
import pl.zolteniebiosa.Mods;
import pl.zolteniebiosa.Skin;
import pl.zolteniebiosa.Sync;

@Mixin(AbstractClientPlayerEntity.class)
public class SkinMixin {
    @Inject(method = "getSkinTextures", at = @At("RETURN"), cancellable = true)
    private void zn$skin(CallbackInfoReturnable<SkinTextures> cir) {
        SkinTextures base = cir.getReturnValue();
        if (base == null) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        AbstractClientPlayerEntity self = (AbstractClientPlayerEntity) (Object) this;
        String skinName;
        int cape;
        if (mc.player != null && self == mc.player) {
            skinName = Mods.SKIN.enabled ? Mods.SKIN.txt("name") : "";
            cape = Mods.CAPE.enabled ? Mods.CAPE.mode("cape") : 0;
        } else {
            Sync.Peer p = Sync.peers.get(self.getUuid());
            if (p == null) return;
            skinName = p.skin();
            cape = p.cape();
        }
        SkinTextures out = base;
        if (skinName != null && !skinName.isBlank()) {
            SkinTextures s = Skin.get(skinName);
            if (s != null) out = s;
        }
        if (cape > 0) {
            out = new SkinTextures(out.texture(), out.textureUrl(), Capes.id(cape), out.elytraTexture(), out.model(), out.secure());
        }
        if (out != base) cir.setReturnValue(out);
    }
}
