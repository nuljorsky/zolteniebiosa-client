package pl.zolteniebiosa;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Skiny i pelerynki widoczne tylko dla graczy z tym klientem.
// Przekaznik: publiczny serwis ntfy.sh (bez wlasnego serwera).
public class Sync {
    public record Peer(String skin, int cape, long time) { }

    public static final Map<UUID, Peer> peers = new ConcurrentHashMap<>();
    private static final String TOPIC = "ztn-client-v1-b83f2a9c4e";
    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static String lastSent = "";
    private static long lastSentAt = 0;

    public static void init() {
        Thread t = new Thread(Sync::loop, "ZTN-Sync");
        t.setDaemon(true);
        t.start();
    }

    private static void loop() {
        while (true) {
            try {
                if (Mods.SYNC.enabled) {
                    publish();
                    poll();
                }
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                return;
            } catch (Throwable ignored) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException ie) {
                    return;
                }
            }
        }
    }

    private static void publish() throws Exception {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getGameProfile() == null || mc.getGameProfile().getId() == null) return;
        JsonObject o = new JsonObject();
        o.addProperty("id", mc.getGameProfile().getId().toString());
        o.addProperty("s", Mods.SKIN.enabled ? Mods.SKIN.txt("name").trim() : "");
        o.addProperty("c", Mods.CAPE.enabled ? Mods.CAPE.mode("cape") : 0);
        String json = o.toString();
        long now = System.currentTimeMillis();
        if (json.equals(lastSent) && now - lastSentAt < 8 * 60_000L) return;
        HttpRequest rq = HttpRequest.newBuilder(URI.create("https://ntfy.sh/" + TOPIC))
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> r = HTTP.send(rq, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() / 100 == 2) {
            lastSent = json;
            lastSentAt = now;
        }
    }

    private static void poll() throws Exception {
        HttpRequest rq = HttpRequest.newBuilder(URI.create("https://ntfy.sh/" + TOPIC + "/json?poll=1&since=20m"))
                .timeout(Duration.ofSeconds(10)).GET().build();
        HttpResponse<String> r = HTTP.send(rq, HttpResponse.BodyHandlers.ofString());
        long now = System.currentTimeMillis();
        for (String raw : r.body().split("\n")) {
            String line = raw.trim();
            if (line.isEmpty()) continue;
            try {
                JsonObject o = JsonParser.parseString(line).getAsJsonObject();
                if (!o.has("message")) continue;
                JsonObject m = JsonParser.parseString(o.get("message").getAsString()).getAsJsonObject();
                UUID id = UUID.fromString(m.get("id").getAsString());
                String skin = m.has("s") ? m.get("s").getAsString() : "";
                int cape = m.has("c") ? m.get("c").getAsInt() : 0;
                long tm = o.has("time") ? o.get("time").getAsLong() * 1000L : now;
                Peer old = peers.get(id);
                if (old == null || old.time() <= tm) {
                    peers.put(id, new Peer(skin, Math.max(0, Math.min(4, cape)), tm));
                }
            } catch (Exception ignored) { }
        }
        peers.entrySet().removeIf(e -> now - e.getValue().time() > 25 * 60_000L);
    }
}
