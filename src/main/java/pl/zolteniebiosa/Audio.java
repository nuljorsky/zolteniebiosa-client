package pl.zolteniebiosa;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

// Jeden stale dzialajacy proces PowerShell + MCI (winmm) do odtwarzania MP3 (glos i muzyka).
public class Audio {
    private static Process proc;
    private static OutputStream in;

    private static final String SCRIPT =
            "[Console]::InputEncoding=[System.Text.Encoding]::UTF8\n"
            + "$sig='[DllImport(\"winmm.dll\",CharSet=CharSet.Unicode)] public static extern int mciSendString(string c, System.Text.StringBuilder r, int l, IntPtr h);'\n"
            + "Add-Type -MemberDefinition $sig -Name W -Namespace Z\n"
            + "function M($c){ $r=[Z.W]::mciSendString($c,$null,0,[IntPtr]::Zero); if($r -ne 0){ Write-Output ('ERR '+$r+' '+$c) } }\n"
            + "Write-Output 'READY'\n"
            + "while($true){\n"
            + " $l=[Console]::In.ReadLine()\n"
            + " if($l -eq $null){ break }\n"
            + " $p=$l.Split('|')\n"
            + " if($p[0] -eq 'PLAY'){ M ('close '+$p[1]); M ('open \"'+$p[3]+'\" type mpegvideo alias '+$p[1]); M ('setaudio '+$p[1]+' volume to '+$p[2]); M ('play '+$p[1]) }\n"
            + " elseif($p[0] -eq 'STOP'){ M ('close '+$p[1]) }\n"
            + " elseif($p[0] -eq 'VOL'){ M ('setaudio '+$p[1]+' volume to '+$p[2]) }\n"
            + "}\n";

    public static boolean windows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    private static synchronized boolean ensure() {
        if (!windows()) return false;
        if (proc != null && proc.isAlive() && in != null) return true;
        try {
            String b64 = Base64.getEncoder().encodeToString(SCRIPT.getBytes(StandardCharsets.UTF_16LE));
            File log = FabricLoader.getInstance().getConfigDir().resolve("ztn-audio.log").toFile();
            proc = new ProcessBuilder("powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-WindowStyle", "Hidden",
                    "-EncodedCommand", b64)
                    .redirectErrorStream(true)
                    .redirectOutput(ProcessBuilder.Redirect.to(log))
                    .start();
            in = proc.getOutputStream();
            return true;
        } catch (Exception e) {
            proc = null;
            in = null;
            return false;
        }
    }

    public static synchronized void send(String line) {
        if (!ensure()) return;
        try {
            in.write((line + "\n").getBytes(StandardCharsets.UTF_8));
            in.flush();
        } catch (Exception e) {
            try {
                proc.destroyForcibly();
            } catch (Exception ignored) { }
            proc = null;
            in = null;
        }
    }

    public static void warmUp() {
        Thread t = new Thread(Audio::ensure, "ZTN-Audio-Warmup");
        t.setDaemon(true);
        t.start();
    }

    public static void shutdown() {
        try {
            if (proc != null) proc.destroyForcibly();
        } catch (Exception ignored) { }
    }
}
