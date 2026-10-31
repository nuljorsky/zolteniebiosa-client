package pl.zolteniebiosa;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.SkinTextures;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Skin {
    public static volatile SkinTextures tex = null;
    private static String loaded = "";

    public static void apply(String name) {
        if (name == null || name.isBlank()) {
            tex = null;
            loaded = "";
            return;
        }
        name = name.trim();
        if (name.equals(loaded)) return;
        loaded = name;
        final String nick = name;
        MinecraftClient mc = MinecraftClient.getInstance();
        CompletableFuture.runAsync(() -> {
            try {
                HttpClient c = HttpClient.newHttpClient();
                HttpResponse<String> r = c.send(HttpRequest.newBuilder(
                        URI.create("https://api.mojang.com/users/profiles/minecraft/" + nick)).build(),
                        HttpResponse.BodyHandlers.ofString());
                JsonObject o = JsonParser.parseString(r.body()).getAsJsonObject();
                String id = o.get("id").getAsString();
                UUID uuid = UUID.fromString(id.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
                ProfileResult pr = mc.getSessionService().fetchProfile(uuid, false);
                if (pr == null) return;
                GameProfile gp = pr.profile();
                mc.getSkinProvider().fetchSkinTextures(gp).thenAccept(opt -> opt.ifPresent(t -> tex = t));
            } catch (Exception e) {
                loaded = "";
            }
        });
    }
}
