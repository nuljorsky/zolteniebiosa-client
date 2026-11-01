package pl.zolteniebiosa;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

// Prawdziwy Xray: ukrywa wszystkie bloki poza wybranymi (mixin na rysowanie scian).
public class Xray {
    private static volatile Set<Block> visible = Set.of();
    private static volatile boolean reload = false;

    public static boolean allows(Block b) {
        return visible.contains(b);
    }

    public static void requestReload() {
        rebuild();
        reload = true;
    }

    private static void rebuild() {
        Set<Block> s = new HashSet<>();
        for (String id : Mods.XRAY.get("blocks").blocks) {
            Identifier i = Identifier.tryParse(id.contains(":") ? id : "minecraft:" + id);
            if (i != null && Registries.BLOCK.containsId(i)) s.add(Registries.BLOCK.get(i));
        }
        visible = s;
    }

    public static void tick(MinecraftClient mc) {
        if (reload && mc.worldRenderer != null && mc.world != null) {
            reload = false;
            mc.worldRenderer.reload();
        }
    }
}
