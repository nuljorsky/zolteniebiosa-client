package pl.zolteniebiosa.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.zolteniebiosa.Mods;
import pl.zolteniebiosa.Xray;

@Mixin(Block.class)
public class XrayBlockMixin {
    @Inject(method = "shouldDrawSide(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/util/math/BlockPos;)Z",
            at = @At("HEAD"), cancellable = true)
    private static void zn$xray(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos otherPos,
                                CallbackInfoReturnable<Boolean> cir) {
        if (Mods.XRAY.enabled) cir.setReturnValue(Xray.allows(state.getBlock()));
    }
}
