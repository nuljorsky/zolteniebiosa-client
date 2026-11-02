package pl.zolteniebiosa.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.zolteniebiosa.FreeCam;

@Mixin(Camera.class)
public class FreeCamMixin {
    @Inject(method = "update", at = @At("RETURN"))
    private void zn$freecam(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (!FreeCam.active() || FreeCam.camPos() == null) return;
        Camera self = (Camera) (Object) this;
        self.setPos(FreeCam.camPos());
    }
}
