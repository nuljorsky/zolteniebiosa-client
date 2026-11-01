package pl.zolteniebiosa;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Skiny i pelerynki widoczne tylko dla graczy z tym klientem (przekaznik: publiczny ntfy.sh).
public class Sync {
    public record Peer(String skin, int cape, long time) { }

    public static final Map<UUID, Peer> byUuid = new ConcurrentHashMap<>();
    public static final Map<String, Peer> byName = new ConcurrentHashMap<>();
    public static volatile String status = "Czekam na pierwszą synchronizację...";
    private static final String TOPIC = "ztn-client-v1-b83f2a9c4e";
    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static final Object LOCK = new Object();
    private static String lastSent = "";
    private static long lastSentAt = 0;
    private static volatile boolean force = false;

    public static Peer peerFor(AbstractClientPlayerEntity e) {
        Peer p = byUuid.get(e.getUuid());
        if (p != null) return p;
        try {
            return byName.get(e.getGameProfile().getName().toLowerCase(Locale.ROOT));
        } catch (Exception ex) {
            return null;
        }
    }

    public static void refreshNow() {
        force = true;
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }

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
                } else {
                    status = "Wyłączone";
                }
            } catch (Throwable e) {
                status = "Błąd: " + e.getClass().getSimpleName() + " " + (e.getMessage() == null ? "" : e.getMessage());
            }
            try {
                synchronized (LOCK) {
                    LOCK.wait(10000);
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private static void publish() throws Exception {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getGameProfile() == null || mc.getGameProfile().getId() == null) return;
        JsonObject o = new JsonObject();
        o.addProperty("id", mc.getGameProfile().getId().toString());
        o.addProperty("n", mc.getGameProfile().getName());
        o.addProperty("s", Mods.SKIN.enabled ? Mods.SKIN.txt("name").trim() : "");
        o.addProperty("c", Mods.CAPE.enabled ? Mods.CAPE.mode("cape") : 0);
        String json = o.toString();
        long now = System.currentTimeMillis();
        if (!force && json.equals(lastSent) && now - lastSentAt < 5 * 60_000L) return;
        HttpRequest rq = HttpRequest.newBuilder(URI.create("https://ntfy.sh/" + TOPIC))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "text/plain; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> r = HTTP.send(rq, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() / 100 == 2) {
            lastSent = json;
            lastSentAt = now;
            force = false;
        } else {
            status = "Błąd wysyłania: HTTP " + r.statusCode();
        }
    }

    private static void poll() throws Exception {
        HttpRequest rq = HttpRequest.newBuilder(URI.create("https://ntfy.sh/" + TOPIC + "/json?poll=1&since=30m"))
                .timeout(Duration.ofSeconds(10)).GET().build();
        HttpResponse<String> r = HTTP.send(rq, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() / 100 != 2) {
            status = "Błąd pobierania: HTTP " + r.statusCode();
            return;
        }
        long now = System.currentTimeMillis();
        int n = 0;
        for (String raw : r.body().split("\n")) {
            String line = raw.trim();
            if (line.isEmpty()) continue;
            try {
                JsonObject o = JsonParser.parseString(line).getAsJsonObject();
                if (!o.has("message")) continue;
                JsonObject m = JsonParser.parseString(o.get("message").getAsString()).getAsJsonObject();
                UUID id = UUID.fromString(m.get("id").getAsString());
                String name = m.has("n") ? m.get("n").getAsString().toLowerCase(Locale.ROOT) : "";
                String skin = m.has("s") ? m.get("s").getAsString() : "";
                int cape = m.has("c") ? m.get("c").getAsInt() : 0;
                long tm = o.has("time") ? o.get("time").getAsLong() * 1000L : now;
                Peer np = new Peer(skin, Math.max(0, Math.min(4, cape)), tm);
                Peer old = byUuid.get(id);
                if (old == null || old.time() <= tm) {
                    byUuid.put(id, np);
                    if (!name.isEmpty()) byName.put(name, np);
                }
                n++;
            } catch (Exception ignored) { }
        }
        byUuid.entrySet().removeIf(e -> now - e.getValue().time() > 40 * 60_000L);
        byName.entrySet().removeIf(e -> now - e.getValue().time() > 40 * 60_000L);
        status = "OK - znane osoby z klientem: " + byUuid.size() + " (wiadomości: " + n + ")";
    }
}
