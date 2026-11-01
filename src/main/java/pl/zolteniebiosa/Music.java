package pl.zolteniebiosa;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

// Odtwarzacz muzyki: wlasne pliki (cale utwory), Jamendo (cale, darmowe), Deezer (30 s).
public class Music {
    public record Track(String title, String artist, String url) { }

    public static volatile List<Track> results = List.of();
    public static volatile String status = "";
    public static volatile int playing = -1;
    private static volatile int token = 0;
    private static volatile long startedAt = 0;
    private static boolean paused = false;

    private static final ScheduledExecutorService TIMER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "ZTN-Music-Timer");
        t.setDaemon(true);
        return t;
    });

    static {
        TIMER.scheduleAtFixedRate(() -> {
            if (playing >= 0 && !paused) Audio.send("Q|m");
        }, 3, 2, TimeUnit.SECONDS);
    }

    public static Path dir() {
        Path d = FabricLoader.getInstance().getConfigDir().resolve("ztn-music");
        try {
            Files.createDirectories(d);
        } catch (Exception ignored) { }
        return d;
    }

    public static void search(String q) {
        int src = Mods.MUSIC.mode("source");
        final String query = q == null ? "" : q.trim();
        status = "Szukam...";
        Thread t = new Thread(() -> {
            try {
                List<Track> out;
                if (src == 0) out = local(query);
                else if (src == 1) out = jamendo(query);
                else out = deezer(query);
                results = out;
                playing = -1;
                if (out.isEmpty()) {
                    status = src == 0 ? "Brak plików MP3 w folderze (przycisk: Otwórz folder z muzyką)." : "Nic nie znaleziono.";
                } else {
                    status = "Znaleziono: " + out.size() + " - kliknij utwór, żeby odtworzyć.";
                }
            } catch (Exception ex) {
                status = "Błąd wyszukiwania: " + ex.getMessage();
            }
        }, "ZTN-Music-Search");
        t.setDaemon(true);
        t.start();
    }

    private static List<Track> local(String q) throws Exception {
        List<Track> out = new ArrayList<>();
        try (Stream<Path> s = Files.list(dir())) {
            s.filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".mp3"))
                    .sorted(Comparator.comparing(p -> p.getFileName().toString().toLowerCase(Locale.ROOT)))
                    .forEach(p -> {
                        String n = p.getFileName().toString();
                        String title = n.substring(0, n.length() - 4);
                        if (q.isEmpty() || title.toLowerCase(Locale.ROOT).contains(q.toLowerCase(Locale.ROOT))) {
                            out.add(new Track(title, "Moja muzyka", p.toAbsolutePath().toString()));
                        }
                    });
        }
        return out;
    }

    private static List<Track> jamendo(String q) throws Exception {
        String id = Mods.MUSIC.txt("jamendo").trim();
        if (id.isEmpty()) throw new Exception("wklej Jamendo Client ID (instrukcja w menu)");
        String url = "https://api.jamendo.com/v3.0/tracks/?format=json&limit=25&order=popularity_total&client_id="
                + URLEncoder.encode(id, StandardCharsets.UTF_8) + "&search=" + URLEncoder.encode(q, StandardCharsets.UTF_8);
        HttpResponse<String> r = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(10)).build(),
                HttpResponse.BodyHandlers.ofString());
        JsonArray arr = JsonParser.parseString(r.body()).getAsJsonObject().getAsJsonArray("results");
        List<Track> out = new ArrayList<>();
        for (JsonElement e : arr) {
            JsonObject o = e.getAsJsonObject();
            if (!o.has("audio") || o.get("audio").getAsString().isEmpty() || !o.has("name")) continue;
            out.add(new Track(o.get("name").getAsString(),
                    o.has("artist_name") ? o.get("artist_name").getAsString() : "", o.get("audio").getAsString()));
        }
        return out;
    }

    private static List<Track> deezer(String q) throws Exception {
        if (q.isEmpty()) return List.of();
        String url = "https://api.deezer.com/search?limit=25&q=" + URLEncoder.encode(q, StandardCharsets.UTF_8);
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
        return out;
    }

    public static void stop() {
        token++;
        playing = -1;
        paused = false;
        Audio.send("STOP|m");
        status = "Zatrzymano.";
    }

    public static void pauseToggle() {
        if (playing < 0) return;
        paused = !paused;
        Audio.send("PAUSE|m|" + (paused ? "1" : "0"));
        status = paused ? "Pauza." : "Gram dalej.";
    }

    public static void next() {
        int n = playing + 1;
        if (n < results.size()) play(n);
    }

    public static void setVolume() {
        if (playing >= 0) Audio.send("VOL|m|" + (Mods.MUSIC.n("volume") * 10));
    }

    // wywolywane, gdy odtwarzacz zglosi koniec utworu
    public static void onStopped() {
        if (playing < 0 || paused) return;
        if (System.currentTimeMillis() - startedAt < 5000) return;
        if (!Mods.MUSIC.b("autoplay")) {
            playing = -1;
            return;
        }
        if (playing + 1 < results.size()) play(playing + 1);
        else playing = -1;
    }

    public static void play(int idx) {
        List<Track> list = results;
        if (idx < 0 || idx >= list.size()) return;
        final int my = ++token;
        playing = idx;
        paused = false;
        startedAt = System.currentTimeMillis();
        final Track tr = list.get(idx);
        status = "Przygotowuję: " + tr.title() + "...";
        Thread t = new Thread(() -> {
            try {
                String path = tr.url();
                if (path.startsWith("http")) {
                    Path d = Path.of(System.getProperty("java.io.tmpdir"), "ztn-music");
                    Files.createDirectories(d);
                    Path f = d.resolve(Integer.toHexString(path.hashCode()) + ".mp3");
                    if (!Files.exists(f) || Files.size(f) < 4000) {
                        HttpResponse<byte[]> r = HttpClient.newHttpClient().send(
                                HttpRequest.newBuilder(URI.create(path)).timeout(Duration.ofSeconds(60)).build(),
                                HttpResponse.BodyHandlers.ofByteArray());
                        if (r.statusCode() != 200) throw new Exception("HTTP " + r.statusCode());
                        Files.write(f, r.body());
                    }
                    path = f.toAbsolutePath().toString();
                }
                if (token != my) return;
                startedAt = System.currentTimeMillis();
                Audio.send("PLAY|m|" + (Mods.MUSIC.n("volume") * 10) + "|" + path);
                status = "Gra: " + tr.title() + (tr.artist().isEmpty() ? "" : " - " + tr.artist());
            } catch (Exception e) {
                if (token == my) {
                    status = "Nie udało się odtworzyć: " + e.getMessage();
                    playing = -1;
                }
            }
        }, "ZTN-Music-Play");
        t.setDaemon(true);
        t.start();
    }
}
