package pl.zolteniebiosa;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;

/**
 * Synchronizacja skinow/peleryn z graczami ZTN CLIENT przez maly serwer (folder backend/).
 * Bez serwera client dziala normalnie - po prostu widzisz swoje kosmetyki tylko Ty.
 */
public final class Net {
    /** Wpisz tu adres swojego serwera (np. "https://ztn-sync.onrender.com") ALBO ustaw go w menu (zakladka DISCORD). */
    public static final String DEFAULT_URL = "";

    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private static volatile String status = "";
    private static int ticks = 0;
    private static long failUntil = 0;
    private static String lastPushKey = "";
    private static volatile int online = 0;

    private Net() { }

    public static String url() {
        String u = Mods.ZTNSYNC.txt("url").trim();
        if (u.isEmpty()) u = DEFAULT_URL;
        while (u.endsWith("/")) u = u.substring(0, u.length() - 1);
        return u;
    }

    public static String status() {
        if (!Mods.ZTNSYNC.enabled) return Lang.t("n.off");
        if (url().isEmpty()) return Lang.t("n.nourl");
        return status.isEmpty() ? Lang.t("n.wait") : status;
    }

    public static void tick(MinecraftClient mc) {
        if (!Mods.ZTNSYNC.enabled || mc.player == null || mc.world == null) {
            ticks = 0;
            if (!Cosmetics.REMOTE.isEmpty()) Cosmetics.REMOTE.clear();
            return;
        }
        String base = url();
        if (base.isEmpty() || System.currentTimeMillis() < failUntil) return;
        ticks++;

        String skin = Mods.SKIN.enabled ? Mods.SKIN.txt("name").trim() : "";
        int cape = Mods.CAPE.enabled ? Mods.CAPE.mode("cape") : -1;
        String key = skin + "|" + cape;
        if (ticks % 600 == 1 || !key.equals(lastPushKey)) {
            lastPushKey = key;
            push(base, mc.player.getUuid(), skin, cape);
        }
        if (ticks % 100 == 3) pull(base, mc);
    }

    private static void push(String base, UUID uuid, String skin, int cape) {
        JsonObject o = new JsonObject();
        o.addProperty("uuid", uuid.toString());
        o.addProperty("skin", skin);
        o.addProperty("cape", cape);
        o.addProperty("client", Branding.NAME + " " + Branding.VERSION);
        HttpRequest req = HttpRequest.newBuilder(URI.create(base + "/api/profile"))
                .timeout(Duration.ofSeconds(8))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(o.toString())).build();
        HTTP.sendAsync(req, HttpResponse.BodyHandlers.ofString()).whenComplete((r, ex) -> {
            if (ex != null || r.statusCode() >= 300) fail(); else status = Lang.t("n.ok");
        });
    }

    private static void pull(String base, MinecraftClient mc) {
        StringBuilder sb = new StringBuilder();
        int n = 0;
        for (AbstractClientPlayerEntity p : mc.world.getPlayers()) {
            if (p == mc.player) continue;
            if (n++ >= 100) break;
            if (sb.length() > 0) sb.append(',');
            sb.append(p.getUuid());
        }
        if (n == 0) {
            Cosmetics.REMOTE.clear();
            online = 0;
            return;
        }
        HttpRequest req = HttpRequest.newBuilder(URI.create(base + "/api/profiles?uuids=" + URLEncoder.encode(sb.toString(), StandardCharsets.UTF_8)))
                .timeout(Duration.ofSeconds(8)).GET().build();
        HTTP.sendAsync(req, HttpResponse.BodyHandlers.ofString()).whenComplete((r, ex) -> {
            if (ex != null || r.statusCode() >= 300) {
                fail();
                return;
            }
            try {
                JsonObject root = JsonParser.parseString(r.body()).getAsJsonObject();
                Set<UUID> seen = new HashSet<>();
                for (Map.Entry<String, JsonElement> e : root.entrySet()) {
                    UUID id = UUID.fromString(e.getKey());
                    JsonObject v = e.getValue().getAsJsonObject();
                    String sk = v.has("skin") ? v.get("skin").getAsString() : "";
                    int cp = v.has("cape") ? v.get("cape").getAsInt() : -1;
                    Cosmetics.REMOTE.put(id, new Cosmetics.Remote(sk, cp));
                    seen.add(id);
                }
                Cosmetics.REMOTE.keySet().retainAll(seen);
                online = seen.size();
                status = Lang.t("n.ok") + " (" + online + ")";
            } catch (Exception ex2) {
                fail();
            }
        });
    }

    private static void fail() {
        status = Lang.t("n.fail");
        failUntil = System.currentTimeMillis() + 30_000L;
    }
}
