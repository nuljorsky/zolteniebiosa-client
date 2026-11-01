package pl.zolteniebiosa.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.zolteniebiosa.NoPush;

/** Anuluje odpychanie przez inne encje (gracze, moby), gdy dotyczy lokalnego gracza. */
@Mixin(Entity.class)
public class NoPushMixin {
    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    private void zn$nopush(Entity other, CallbackInfo ci) {
        if (NoPush.blockEntityPush((Entity) (Object) this, other)) ci.cancel();
    }
}
