package pl.zolteniebiosa;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.SkinTextures;

/**
 * Pobiera skiny po nicku (premium) albo po UUID i trzyma je w pamieci.
 * Ma ponawianie przy bledach sieci, rozroznia "nie ma takiego nicku" od "blad sieci"
 * i pokazuje status w menu (zeby bylo widac DLACZEGO skin sie nie zaladowal).
 */
public class Skin {
    private static final int OK = 1, LOADING = 2, NOTFOUND = 3, NETERR = 4, RATE = 5;

    private static final class Entry {
        volatile SkinTextures tex;
        volatile boolean loading;
        volatile long retryAt;
        volatile int state = 0;
    }

    private static final Map<String, Entry> CACHE = new ConcurrentHashMap<>();
    private static final Pattern NICK = Pattern.compile("[a-z0-9_]{1,16}");
    private static final Pattern UUID_HEX = Pattern.compile("[0-9a-f]{32}");
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(6)).build();

    private static String norm(String name) {
        return name == null ? "" : name.trim().toLowerCase(Locale.ROOT).replace("-", "");
    }

    /** Zwraca skin gotowy do uzycia albo null (wtedy zaczyna ladowanie w tle). */
    public static SkinTextures get(String name) {
        String key = norm(name);
        if (key.isEmpty() || !(NICK.matcher(key).matches() || UUID_HEX.matcher(key).matches())) return null;
        Entry e = CACHE.computeIfAbsent(key, k -> new Entry());
        if (e.tex != null) return e.tex;
        if (!e.loading && System.currentTimeMillis() >= e.retryAt) load(key, e);
        return null;
    }

    /** Wywolywane po zmianie nicku skina w menu - czysci porazki i wymusza natychmiastowe pobranie. */
    public static void apply(String name) {
        String key = norm(name);
        Entry e = CACHE.get(key);
        if (e != null && e.tex == null) e.retryAt = 0;
        get(name);
    }

    /** Tekst do menu. */
    public static String status(String name) {
        String key = norm(name);
        if (key.isEmpty()) return Lang.t("sk.empty");
        if (!(NICK.matcher(key).matches() || UUID_HEX.matcher(key).matches())) return Lang.t("sk.bad");
        Entry e = CACHE.get(key);
        if (e == null) return Lang.t("sk.wait");
        if (e.tex != null) return Lang.t("sk.ok");
        return switch (e.state) {
            case LOADING -> Lang.t("sk.loading");
            case NOTFOUND -> Lang.t("sk.notfound");
            case RATE -> Lang.t("sk.rate");
            case NETERR -> Lang.t("sk.neterr");
            default -> Lang.t("sk.wait");
        };
    }

    private static void load(String key, Entry e) {
        e.loading = true;
        e.state = LOADING;
        MinecraftClient mc = MinecraftClient.getInstance();
        CompletableFuture.runAsync(() -> {
            try {
                UUID uuid = null;
                if (UUID_HEX.matcher(key).matches()) {
                    uuid = UUID.fromString(key.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
                } else {
                    for (int attempt = 0; attempt < 3 && uuid == null; attempt++) {
                        HttpResponse<String> r = HTTP.send(HttpRequest.newBuilder(
                                URI.create("https://api.mojang.com/users/profiles/minecraft/" + key))
                                .timeout(Duration.ofSeconds(8)).build(), HttpResponse.BodyHandlers.ofString());
                        int sc = r.statusCode();
                        if (sc == 200) {
                            JsonObject o = JsonParser.parseString(r.body()).getAsJsonObject();
                            String id = o.get("id").getAsString();
                            uuid = UUID.fromString(id.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
                        } else if (sc == 204 || sc == 404) {
                            fail(e, NOTFOUND, 600_000L);
                            return;
                        } else if (sc == 429) {
                            fail(e, RATE, 90_000L);
                            return;
                        } else {
                            Thread.sleep(700L * (attempt + 1));
                        }
                    }
                    if (uuid == null) {
                        fail(e, NETERR, 20_000L);
                        return;
                    }
                }
                ProfileResult pr = mc.getSessionService().fetchProfile(uuid, false);
                if (pr == null) {
                    fail(e, NETERR, 20_000L);
                    return;
                }
                GameProfile gp = pr.profile();
                mc.getSkinProvider().fetchSkinTextures(gp).whenComplete((opt, ex) -> {
                    if (ex == null && opt.isPresent()) {
                        e.tex = opt.get();
                        e.state = OK;
                        e.loading = false;
                    } else {
                        fail(e, NETERR, 20_000L);
                    }
                });
            } catch (Exception ex) {
                fail(e, NETERR, 20_000L);
            }
        });
    }

    private static void fail(Entry e, int state, long retryMs) {
        e.state = state;
        e.retryAt = System.currentTimeMillis() + retryMs;
        e.loading = false;
    }
}
