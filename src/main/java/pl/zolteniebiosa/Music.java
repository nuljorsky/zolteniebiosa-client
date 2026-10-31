package pl.zolteniebiosa;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Odtwarzacz w launcherze: 30-sekundowe podglady z Deezer (MP3) przez MCI (Windows).
public class Music {
    public record Track(String title, String artist, String url) { }

    public static volatile List<Track> results = List.of();
    public static volatile String status = "";
    public static volatile int playing = -1;
    private static volatile int token = 0;
    private static final ScheduledExecutorService TIMER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "ZTN-Music-Timer");
        t.setDaemon(true);
        return t;
    });

    public static void search(String q) {
        final String query = q == null ? "" : q.trim();
        if (query.isEmpty()) return;
        status = "Szukam...";
        Thread t = new Thread(() -> {
            try {
                String url = "https://api.deezer.com/search?limit=20&q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
                HttpResponse<String> r = HttpClient.newHttpClient().send(
                        HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(10)).build(),
                        HttpResponse.BodyHandlers.ofString());
                JsonArray arr = JsonParser.parseString(r.body()).getAsJsonObject().getAsJsonArray("data");
                List<Track> out = new ArrayList<>();
                for (JsonElement e : arr) {
                    JsonObject o = e.getAsJsonObject();
                    if (!o.has("preview") || !o.has("title")) continue;
                    String pu = o.get("preview").getAsString();
                    if (!pu.startsWith("https://") || !pu.contains("dzcdn.net")) continue;
                    String artist = o.has("artist") && o.get("artist").isJsonObject() && o.getAsJsonObject("artist").has("name")
                            ? o.getAsJsonObject("artist").get("name").getAsString() : "";
                    out.add(new Track(o.get("title").getAsString(), artist, pu));
                }
                results = out;
                playing = -1;
                status = out.isEmpty() ? "Nic nie znaleziono." : "Znaleziono: " + out.size() + " - kliknij utwór, żeby odtworzyć.";
            } catch (Exception ex) {
                status = "Błąd wyszukiwania (internet?).";
            }
        }, "ZTN-Music-Search");
        t.setDaemon(true);
        t.start();
    }

    public static void stop() {
        token++;
        playing = -1;
        Audio.send("STOP|m");
    }

    public static void next() {
        int n = playing + 1;
        if (n < results.size()) play(n);
    }

    public static void setVolume() {
        if (playing >= 0) Audio.send("VOL|m|" + (Mods.MUSIC.n("volume") * 10));
    }

    public static void play(int idx) {
        List<Track> list = results;
        if (idx < 0 || idx >= list.size()) return;
        if (!Audio.windows()) {
            status = "Muzyka działa tylko na Windows.";
            return;
        }
        final int my = ++token;
        playing = idx;
        final Track tr = list.get(idx);
        status = "Pobieram: " + tr.title() + "...";
        Thread t = new Thread(() -> {
            try {
                Path dir = Path.of(System.getProperty("java.io.tmpdir"), "ztn-music");
                Files.createDirectories(dir);
                Path f = dir.resolve(Integer.toHexString(tr.url().hashCode()) + ".mp3");
                if (!Files.exists(f) || Files.size(f) < 2000) {
                    HttpResponse<byte[]> r = HttpClient.newHttpClient().send(
                            HttpRequest.newBuilder(URI.create(tr.url())).timeout(Duration.ofSeconds(15)).build(),
                            HttpResponse.BodyHandlers.ofByteArray());
                    if (r.statusCode() != 200) throw new Exception("http " + r.statusCode());
                    Files.write(f, r.body());
                }
                if (token != my) return;
                Audio.send("PLAY|m|" + (Mods.MUSIC.n("volume") * 10) + "|" + f.toAbsolutePath());
                status = "Gra: " + tr.title() + " - " + tr.artist();
                TIMER.schedule(() -> {
                    if (token == my && playing == idx) {
                        if (idx + 1 < results.size()) play(idx + 1);
                        else playing = -1;
                    }
                }, 31, TimeUnit.SECONDS);
            } catch (Exception e) {
                if (token == my) {
                    status = "Nie udało się pobrać utworu (sprawdź internet).";
                    playing = -1;
                }
            }
        }, "ZTN-Music-Play");
        t.setDaemon(true);
        t.start();
    }
}
