package pl.zolteniebiosa;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.SkinTextures;

/**
 * Laczy skiny i pelerynki: swoje (z menu) oraz graczy z ZTN CLIENT (z serwera synchronizacji).
 * Gracze bez ZTN CLIENT i nicki customowe (NameProtect) sa widoczne tylko u Ciebie - inaczej sie nie da.
 */
public final class Cosmetics {
    public record Remote(String skin, int cape) { }

    public static final Map<UUID, Remote> REMOTE = new ConcurrentHashMap<>();

    private Cosmetics() { }

    /** Zwraca podmienione tekstury dla gracza albo null, gdy nic nie zmieniamy. */
    public static SkinTextures resolve(AbstractClientPlayerEntity p, SkinTextures base) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String skinNick;
        int cape;
        if (mc.player != null && p.getUuid().equals(mc.player.getUuid())) {
            skinNick = Mods.SKIN.enabled ? Mods.SKIN.txt("name") : "";
            cape = Mods.CAPE.enabled ? Mods.CAPE.mode("cape") : -1;
        } else {
            Remote r = REMOTE.get(p.getUuid());
            if (r == null) return null;
            skinNick = r.skin();
            cape = r.cape();
        }
        SkinTextures out = base;
        boolean changed = false;
        SkinTextures sk = Skin.get(skinNick);
        if (sk != null) {
            out = sk;
            changed = true;
        }
        if (cape >= 0 && cape < Capes.count()) {
            var id = Capes.tex(cape);
            out = new SkinTextures(out.texture(), out.textureUrl(), id, id, out.model(), out.secure());
            changed = true;
        }
        return changed ? out : null;
    }
}
