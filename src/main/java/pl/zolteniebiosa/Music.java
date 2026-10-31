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
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

// Odtwarzacz w launcherze: 30-sekundowe podglady z iTunes Search API (Windows).
public class Music {
    public record Track(String title, String artist, String url) { }

    public static volatile List<Track> results = List.of();
    public static volatile String status = "";
    public static volatile int playing = -1;
    private static Process proc;

    public static void search(String q) {
        final String query = q == null ? "" : q.trim();
        if (query.isEmpty()) return;
        status = "Szukam...";
        Thread t = new Thread(() -> {
            try {
                String url = "https://itunes.apple.com/search?media=music&entity=song&limit=15&term="
                        + URLEncoder.encode(query, StandardCharsets.UTF_8);
                HttpResponse<String> r = HttpClient.newHttpClient().send(
                        HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(10)).build(),
                        HttpResponse.BodyHandlers.ofString());
                JsonArray arr = JsonParser.parseString(r.body()).getAsJsonObject().getAsJsonArray("results");
                List<Track> out = new ArrayList<>();
                for (JsonElement e : arr) {
                    JsonObject o = e.getAsJsonObject();
                    if (!o.has("previewUrl") || !o.has("trackName")) continue;
                    String pu = o.get("previewUrl").getAsString();
                    if (!pu.startsWith("https://") || pu.contains("'")) continue;
                    out.add(new Track(o.get("trackName").getAsString(),
                            o.has("artistName") ? o.get("artistName").getAsString() : "", pu));
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

    public static synchronized void stop() {
        playing = -1;
        killProc();
    }

    private static void killProc() {
        try {
            if (proc != null && proc.isAlive()) proc.destroyForcibly();
        } catch (Exception ignored) { }
        proc = null;
    }

    public static void next() {
        int n = playing + 1;
        if (n < results.size()) play(n);
    }

    public static synchronized void play(int idx) {
        List<Track> list = results;
        if (idx < 0 || idx >= list.size()) return;
        if (!System.getProperty("os.name", "").toLowerCase().contains("win")) {
            status = "Muzyka działa tylko na Windows.";
            return;
        }
        killProc();
        playing = idx;
        final Track tr = list.get(idx);
        double vol = Mods.MUSIC.n("volume") / 100.0;
        String ps = "Add-Type -AssemblyName PresentationCore;"
                + "$p=New-Object System.Windows.Media.MediaPlayer;"
                + "$p.Volume=" + String.format(java.util.Locale.ROOT, "%.2f", vol) + ";"
                + "$p.Open([Uri]'" + tr.url() + "');$p.Play();"
                + "$t=0;while($t -lt 45){Start-Sleep -Milliseconds 500;$t+=0.5;"
                + "if($p.NaturalDuration.HasTimeSpan -and $p.Position -ge $p.NaturalDuration.TimeSpan){break}}";
        try {
            String b64 = Base64.getEncoder().encodeToString(ps.getBytes(StandardCharsets.UTF_16LE));
            final Process pr = new ProcessBuilder("powershell", "-NoProfile", "-NonInteractive", "-ExecutionPolicy", "Bypass",
                    "-WindowStyle", "Hidden", "-EncodedCommand", b64)
                    .redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
            proc = pr;
            Thread w = new Thread(() -> {
                try {
                    pr.waitFor();
                } catch (InterruptedException ignored) { }
                if (playing == idx && proc == pr) {
                    if (idx + 1 < results.size()) play(idx + 1);
                    else playing = -1;
                }
            }, "ZTN-Music-Wait");
            w.setDaemon(true);
            w.start();
        } catch (Exception e) {
            status = "Nie udało się odtworzyć.";
            playing = -1;
        }
    }
}
