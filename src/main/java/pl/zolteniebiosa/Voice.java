package pl.zolteniebiosa;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Voice {
    private static Process cur;
    private static final String[] CULT = {"pl-PL", "en-US", "de-DE", "ru-RU"};

    public static synchronized void say(String text) {
        if (!Mods.VOICE.enabled) return;
        if (!System.getProperty("os.name", "").toLowerCase().contains("win")) return;
        String safe = text.replaceAll("[^\\p{L}\\p{N} .,!?-]", "").trim();
        if (safe.isEmpty()) return;
        int vol = Mods.VOICE.n("volume");
        int pm = Mods.VOICE.mode("pitch");
        String lang = CULT[Lang.cur];
        String pitch = pm == 0 ? "medium" : pm == 1 ? "+25%" : "+45%";
        String rate = pm == 0 ? "0%" : "+8%";
        String ssml = "<speak version=\"1.0\" xmlns=\"http://www.w3.org/2001/10/synthesis\" xml:lang=\"" + lang + "\">"
                + "<prosody pitch=\"" + pitch + "\" rate=\"" + rate + "\">" + safe + "</prosody></speak>";
        String ps = "Add-Type -AssemblyName System.Speech;"
                + "$s=New-Object System.Speech.Synthesis.SpeechSynthesizer;"
                + "$v=$s.GetInstalledVoices()|Where-Object{$_.Enabled -and $_.VoiceInfo.Culture.Name -eq '" + lang + "'}"
                + "|Sort-Object{$_.VoiceInfo.Gender -ne 'Female'}|Select-Object -First 1;"
                + "if(-not $v){exit};"
                + "$s.SelectVoice($v.VoiceInfo.Name);$s.Volume=" + vol + ";"
                + "$s.SpeakSsml('" + ssml.replace("'", "''") + "')";
        try {
            if (cur != null && cur.isAlive()) cur.destroyForcibly();
            String b64 = Base64.getEncoder().encodeToString(ps.getBytes(StandardCharsets.UTF_16LE));
            cur = new ProcessBuilder("powershell", "-NoProfile", "-NonInteractive", "-ExecutionPolicy", "Bypass", "-EncodedCommand", b64)
                    .redirectErrorStream(true)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .start();
        } catch (Exception ignored) { }
    }
}
