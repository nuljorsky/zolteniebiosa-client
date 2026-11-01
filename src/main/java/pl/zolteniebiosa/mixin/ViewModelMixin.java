package pl.zolteniebiosa.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.zolteniebiosa.Mod;
import pl.zolteniebiosa.Mods;

@Mixin(HeldItemRenderer.class)
public class ViewModelMixin {
    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
    private void zn$viewmodel(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand,
                              float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
                              VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        Mod m = Mods.VIEWMODEL;
        if (!m.enabled) return;
        float side = hand == Hand.OFF_HAND ? -1f : 1f;
        matrices.translate(m.n("x") / 100.0 * side, m.n("y") / 100.0, m.n("z") / 100.0);
        matrices.multiply(new Quaternionf().rotateXYZ(
                (float) Math.toRadians(m.n("rx")),
                (float) Math.toRadians(m.n("ry") * side),
                (float) Math.toRadians(m.n("rz") * side)));
        float s = m.n("scale") / 100f;
        matrices.scale(s, s, s);
    }
}
