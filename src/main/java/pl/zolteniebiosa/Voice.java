package pl.zolteniebiosa;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Base64;

public class Voice {
    private static Process cur;
    private static final String[] CULT = {"pl-PL", "en-US", "de-DE", "ru-RU"};
    private static final String[] TL = {"pl", "en", "de", "ru"};

    public static void say(String text) {
        if (!Mods.VOICE.enabled || !Audio.windows()) return;
        final String t = text.replaceAll("[^\\p{L}\\p{N} .,!?-]", "").trim();
        if (t.isEmpty()) return;
        final int lang = Lang.cur;
        Thread th = new Thread(() -> {
            try {
                if (Mods.VOICE.mode("engine") == 0) {
                    Path f = tts(t, lang);
                    if (f != null) {
                        Audio.send("PLAY|v|" + (Mods.VOICE.n("volume") * 10) + "|" + f.toAbsolutePath());
                        return;
                    }
                }
            } catch (Exception ignored) { }
            local(t, lang);
        }, "ZTN-Voice");
        th.setDaemon(true);
        th.start();
    }

    // glos online (naturalniejszy niz systemowy), z cache w katalogu tymczasowym
    private static Path tts(String text, int lang) throws Exception {
        Path dir = Path.of(System.getProperty("java.io.tmpdir"), "ztn-voice");
        Files.createDirectories(dir);
        Path f = dir.resolve(TL[lang] + "_" + Integer.toHexString(text.hashCode()) + ".mp3");
        if (Files.exists(f) && Files.size(f) > 800) return f;
        String url = "https://translate.google.com/translate_tts?ie=UTF-8&client=tw-ob&tl=" + TL[lang]
                + "&q=" + URLEncoder.encode(text, StandardCharsets.UTF_8);
        HttpResponse<byte[]> r = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(8))
                        .header("User-Agent", "Mozilla/5.0").build(),
                HttpResponse.BodyHandlers.ofByteArray());
        if (r.statusCode() != 200 || r.body().length < 800) return null;
        Files.write(f, r.body());
        return f;
    }

    // glos systemowy Windows (zapasowy)
    private static synchronized void local(String text, int lang) {
        int pm = Mods.VOICE.mode("pitch");
        String pitch = pm == 0 ? "medium" : pm == 1 ? "+25%" : "+45%";
        String rate = pm == 0 ? "0%" : "+8%";
        String l = CULT[lang];
        String ssml = "<speak version=\"1.0\" xmlns=\"http://www.w3.org/2001/10/synthesis\" xml:lang=\"" + l + "\">"
                + "<prosody pitch=\"" + pitch + "\" rate=\"" + rate + "\">" + text + "</prosody></speak>";
        String ps = "Add-Type -AssemblyName System.Speech;"
                + "$s=New-Object System.Speech.Synthesis.SpeechSynthesizer;"
                + "$v=$s.GetInstalledVoices()|Where-Object{$_.Enabled -and $_.VoiceInfo.Culture.Name -eq '" + l + "'}"
                + "|Sort-Object{$_.VoiceInfo.Gender -ne 'Female'}|Select-Object -First 1;"
                + "if(-not $v){exit};$s.SelectVoice($v.VoiceInfo.Name);$s.Volume=" + Mods.VOICE.n("volume") + ";"
                + "$s.SpeakSsml('" + ssml.replace("'", "''") + "')";
        try {
            if (cur != null && cur.isAlive()) cur.destroyForcibly();
            String b64 = Base64.getEncoder().encodeToString(ps.getBytes(StandardCharsets.UTF_16LE));
            cur = new ProcessBuilder("powershell", "-NoProfile", "-NonInteractive", "-ExecutionPolicy", "Bypass",
                    "-WindowStyle", "Hidden", "-EncodedCommand", b64)
                    .redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
        } catch (Exception ignored) { }
    }
}
