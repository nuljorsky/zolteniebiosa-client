package pl.zolteniebiosa;

import net.minecraft.util.Identifier;

public class Capes {
    public static final String[] NAMES = {"o.none", "Neon ZTN", "Tęcza", "Złoty ZTN", "Ogień"};

    public static Identifier id(int i) {
        return Identifier.of("zolteniebiosa", "textures/cape/cape" + i + ".png");
    }
}
