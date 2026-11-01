package pl.zolteniebiosa;

import net.minecraft.client.MinecraftClient;

public class NameProtect {
    private static String real;

    public static String apply(String s) {
        if (s == null || s.isEmpty() || !Mods.NAMEPROTECT.enabled) return s;
        String fake = Mods.NAMEPROTECT.txt("name");
        if (fake.isEmpty()) return s;
        if (real == null) {
            try {
                real = MinecraftClient.getInstance().getSession().getUsername();
            } catch (Exception e) {
                return s;
            }
        }
        if (real == null || real.isEmpty() || !s.contains(real)) return s;
        return s.replace(real, fake);
    }
}
