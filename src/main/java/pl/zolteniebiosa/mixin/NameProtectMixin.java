package pl.zolteniebiosa.mixin;

import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import pl.zolteniebiosa.NameProtect;

@Mixin(TextVisitFactory.class)
public class NameProtectMixin {
    @ModifyVariable(
            method = "visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
            at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static String zn$protect(String text) {
        return NameProtect.apply(text);
    }
}
