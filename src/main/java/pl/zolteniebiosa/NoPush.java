package pl.zolteniebiosa;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

/** NoPush: nie odpychaja Cie gracze, bloki ani woda - latwiej klikac perly w trapie i wychodzic z ciasnych miejsc. */
public final class NoPush {
    private NoPush() { }

    /** Czy ta para encji dotyczy lokalnego gracza (wtedy anulujemy odpychanie). */
    public static boolean blockEntityPush(Entity a, Entity b) {
        if (!Mods.NOPUSH.enabled || !Mods.NOPUSH.b("players")) return false;
        Entity me = MinecraftClient.getInstance().player;
        return me != null && (a == me || b == me);
    }

    public static boolean blocks() {
        return Mods.NOPUSH.enabled && Mods.NOPUSH.b("blocks");
    }

    public static boolean water(Entity e) {
        return Mods.NOPUSH.enabled && Mods.NOPUSH.b("water") && e == MinecraftClient.getInstance().player;
    }
}
