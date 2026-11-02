package pl.zolteniebiosa.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.zolteniebiosa.Mods;

// Reach: zwieksza wlasny zasieg interakcji (atrybut klienta).
@Mixin(PlayerEntity.class)
public class ReachMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void zn$reach(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (self != MinecraftClient.getInstance().player) return;
        EntityAttributeInstance ent = self.getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE);
        if (ent == null) return;
        if (Mods.REACH.enabled) {
            double v = Mods.REACH.n("range") / 10.0;
            if (ent.getBaseValue() != v) ent.setBaseValue(v);
        } else if (ent.getBaseValue() != 3.0) {
            ent.setBaseValue(3.0);
        }
    }
}
