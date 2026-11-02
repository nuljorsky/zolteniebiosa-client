package pl.zolteniebiosa.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.zolteniebiosa.Mods;
import pl.zolteniebiosa.Xray;

// Zgodnosc z Sodium (jesli klasa istnieje w danej wersji)
@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache", remap = false)
public class XraySodiumMixin {
    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true, remap = false)
    private void zn$xraySodium(BlockState selfState, BlockView view, BlockPos pos, Direction facing,
                               CallbackInfoReturnable<Boolean> cir) {
        if (Mods.XRAY.enabled) cir.setReturnValue(Xray.allows(selfState.getBlock()));
    }
}
