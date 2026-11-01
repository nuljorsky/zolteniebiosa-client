package pl.zolteniebiosa;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.SkinTextures;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class Skin {
    private static final Map<String, SkinTextures> cache = new ConcurrentHashMap<>();
    private static final Set<String> requested = ConcurrentHashMap.newKeySet();

    // zwraca skin z cache albo null i uruchamia pobieranie w tle
    public static SkinTextures get(String name) {
        if (name == null) return null;
        String n = name.trim();
        if (!n.matches("[A-Za-z0-9_]{1,16}")) return null;
        String k = n.toLowerCase();
        SkinTextures t = cache.get(k);
        if (t == null && requested.add(k)) fetch(n, k);
        return t;
    }

    public static void apply(String name) {
        get(name);
    }

    private static void fetch(String nick, String key) {
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
                mc.getSkinProvider().fetchSkinTextures(gp).thenAccept(opt -> opt.ifPresent(t -> cache.put(key, t)));
            } catch (Exception e) {
                // nick nie istnieje albo brak internetu
            }
        });
    }
}
