package pl.zolteniebiosa;

import net.minecraft.util.Identifier;

/**
 * Pelerynki ZTN. Tekstury leza w src/main/resources/assets/zolteniebiosa/textures/cape/capeN.png (64x32).
 * Zeby podmienic pelerynke: wrzuc swoj plik o tej samej nazwie i zmien nazwe/kolory podgladu ponizej.
 */
public final class Capes {
    public record Def(String name, String file, int c1, int c2, int c3) { }

    public static final Def[] ALL = {
            new Def("Złote Niebiosa", "cape1", 0xFFFFC21F, 0xFFFF8A00, 0xFFFFF4B0),
            new Def("Galaktyka", "cape2", 0xFF2B1459, 0xFF6C2BD9, 0xFFFFFFFF),
            new Def("YouTube", "cape3", 0xFFE01B1B, 0xFF7A0A0A, 0xFFFFFFFF),
            new Def("Discord", "cape4", 0xFF5865F2, 0xFF2A307A, 0xFFFFFFFF),
    };

    private Capes() { }

    public static int count() {
        return ALL.length;
    }

    public static String[] names() {
        String[] n = new String[ALL.length];
        for (int i = 0; i < n.length; i++) n[i] = ALL[i].name();
        return n;
    }

    public static Identifier tex(int idx) {
        Def d = ALL[Math.max(0, Math.min(ALL.length - 1, idx))];
        return Identifier.of("zolteniebiosa", "textures/cape/" + d.file() + ".png");
    }
}
