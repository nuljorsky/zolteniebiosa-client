package pl.zolteniebiosa;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedInputStream;
import java.io.InputStream;
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
import java.util.Locale;
import java.util.stream.Stream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Muzyka do PvP wewnatrz gry (bez przegladarki).
 * Zrodla: (1) wyszukiwarka Deezer - darmowe 30-sekundowe fragmenty utworow, (2) wlasne pliki MP3 z folderu
 * config/zolteniebiosa/music (pelne utwory, ktore sam posiadasz).
 */
public final class Music {
    public record Track(String title, String artist, String url, int seconds, boolean local) {
        public String label() {
            return artist.isEmpty() ? title : artist + " - " + title;
        }
    }

    public static volatile List<Track> results = List.of();
    public static volatile String status = "";
    public static volatile Track now = null;
    public static volatile boolean playing = false;
    public static volatile int posSec = 0;

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)).followRedirects(HttpClient.Redirect.NORMAL).build();
    private static volatile int gen = 0;
    private static volatile int lastVol = -1;

    private Music() { }

    public static Path folder() {
        return FabricLoader.getInstance().getConfigDir().resolve("zolteniebiosa").resolve("music");
    }

    // ===================== szukanie =====================
    public static void search(String q) {
        q = q == null ? "" : q.trim();
        if (q.isEmpty()) return;
        final String query = q;
        status = Lang.t("mu.searching");
        new Thread(() -> {
            try {
                String url = "https://api.deezer.com/search?limit=25&q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
                HttpResponse<String> r = null;
                Exception last = null;
                for (int attempt = 0; attempt < 3; attempt++) {
                    try {
                        r = HTTP.send(HttpRequest.newBuilder(URI.create(url))
                                .timeout(Duration.ofSeconds(12)).GET().build(), HttpResponse.BodyHandlers.ofString());
                        if (r.statusCode() == 200) break;
                    } catch (Exception ex) {
                        last = ex;
                    }
                    Thread.sleep(500L * (attempt + 1));
                }
                if (r == null || r.statusCode() != 200) {
                    status = Lang.t("mu.err");
                    return;
                }
                JsonObject root = JsonParser.parseString(r.body()).getAsJsonObject();
                JsonArray data = root.has("data") ? root.getAsJsonArray("data") : new JsonArray();
                List<Track> out = new ArrayList<>();
                for (JsonElement el : data) {
                    JsonObject o = el.getAsJsonObject();
                    String prev = o.has("preview") && !o.get("preview").isJsonNull() ? o.get("preview").getAsString() : "";
                    if (prev.isEmpty()) continue;
                    String artist = o.has("artist") && o.get("artist").isJsonObject()
                            ? o.getAsJsonObject("artist").get("name").getAsString() : "";
                    out.add(new Track(o.get("title").getAsString(), artist, prev,
                            o.has("duration") ? o.get("duration").getAsInt() : 30, false));
                }
                results = out;
                status = out.isEmpty() ? Lang.t("mu.none") : "";
            } catch (Exception e) {
                status = Lang.t("mu.err");
            }
        }, "ZTN-MusicSearch").start();
    }

    /** Pokazuje pliki .mp3 z folderu config/zolteniebiosa/music. */
    public static void listLocal() {
        List<Track> out = new ArrayList<>();
        try {
            Path dir = folder();
            Files.createDirectories(dir);
            try (Stream<Path> s = Files.list(dir)) {
                s.filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".mp3"))
                        .sorted().forEach(p -> {
                            String n = p.getFileName().toString();
                            out.add(new Track(n.substring(0, n.length() - 4), "", p.toString(), 0, true));
                        });
            }
        } catch (Exception ignored) { }
        results = out;
        status = out.isEmpty() ? Lang.t("mu.nolocal") + " " + folder() : "";
    }

    // ===================== odtwarzanie =====================
    public static void play(Track t) {
        final int myGen = ++gen;
        now = t;
        playing = true;
        posSec = 0;
        new Thread(() -> run(t, myGen), "ZTN-Music").start();
    }

    public static void stop() {
        gen++;
        playing = false;
        now = null;
        posSec = 0;
    }

    public static void next() {
        List<Track> l = results;
        if (l.isEmpty()) return;
        int i = -1;
        for (int k = 0; k < l.size(); k++) if (l.get(k) == now) i = k;
        play(l.get((i + 1) % l.size()));
    }

    public static String nowLine() {
        Track t = now;
        return t == null ? "" : "\u266A " + t.label();
    }

    private static void run(Track t, int myGen) {
        SourceDataLine line = null;
        try (InputStream raw = t.local()
                ? Files.newInputStream(Path.of(t.url()))
                : HTTP.send(HttpRequest.newBuilder(URI.create(t.url())).timeout(Duration.ofSeconds(15)).GET().build(),
                HttpResponse.BodyHandlers.ofInputStream()).body()) {
            Bitstream bs = new Bitstream(new BufferedInputStream(raw, 1 << 16));
            Decoder dec = new Decoder();
            long shorts = 0;
            lastVol = -1;
            Header h;
            while (myGen == gen && (h = bs.readFrame()) != null) {
                SampleBuffer buf = (SampleBuffer) dec.decodeFrame(h, bs);
                if (line == null) {
                    AudioFormat fmt = new AudioFormat(buf.getSampleFrequency(), 16, buf.getChannelCount(), true, false);
                    line = AudioSystem.getSourceDataLine(fmt);
                    line.open(fmt);
                    line.start();
                }
                setVolume(line);
                short[] s = buf.getBuffer();
                int n = buf.getBufferLength();
                byte[] out = new byte[n * 2];
                for (int i = 0; i < n; i++) {
                    out[i * 2] = (byte) s[i];
                    out[i * 2 + 1] = (byte) (s[i] >> 8);
                }
                line.write(out, 0, out.length);
                shorts += n;
                posSec = (int) (shorts / Math.max(1, buf.getChannelCount()) / Math.max(1, buf.getSampleFrequency()));
                bs.closeFrame();
            }
            if (line != null && myGen == gen) line.drain();
        } catch (Throwable e) {
            if (myGen == gen) status = Lang.t("mu.err") + " (" + e.getClass().getSimpleName() + ")";
        } finally {
            if (line != null) {
                try {
                    line.stop();
                    line.close();
                } catch (Exception ignored) { }
            }
        }
        if (myGen == gen) {
            playing = false;
            if (Mods.MUSIC.b("autoplay")) next();
        }
    }

    private static void setVolume(SourceDataLine line) {
        int v = Mods.MUSIC.n("volume");
        if (v == lastVol) return;
        lastVol = v;
        try {
            if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl c = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                float db = v <= 0 ? c.getMinimum() : (float) (20.0 * Math.log10(v / 100.0));
                c.setValue(Math.max(c.getMinimum(), Math.min(c.getMaximum(), db)));
            }
        } catch (Exception ignored) { }
    }
}
