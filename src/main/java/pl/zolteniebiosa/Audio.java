package pl.zolteniebiosa;

import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

// Jeden stale dzialajacy proces PowerShell: MCI (winmm) + zapasowo Windows Media Player (COM).
public class Audio {
    private static Process proc;
    private static OutputStream in;
    public static volatile String status = "Odtwarzacz jeszcze nie uruchomiony";

    private static final String SCRIPT =
            "[Console]::InputEncoding=[System.Text.Encoding]::UTF8\n"
            + "[Console]::OutputEncoding=[System.Text.Encoding]::UTF8\n"
            + "$sig='[DllImport(\"winmm.dll\",CharSet=CharSet.Unicode)] public static extern int mciSendString(string c, System.Text.StringBuilder r, int l, IntPtr h);'\n"
            + "Add-Type -MemberDefinition $sig -Name W -Namespace Z\n"
            + "$sb=New-Object System.Text.StringBuilder 256\n"
            + "$wmp=@{}\n"
            + "function M($c){ $sb.Length=0; $r=[Z.W]::mciSendString($c,$sb,256,[IntPtr]::Zero); if($r -ne 0){ Write-Host ('ERR '+$r+' '+$c) }; return $r }\n"
            + "Write-Host 'READY'\n"
            + "while($true){\n"
            + " $l=[Console]::In.ReadLine()\n"
            + " if($l -eq $null){ break }\n"
            + " $p=$l.Split('|')\n"
            + " if($p[0] -eq 'PLAY'){\n"
            + "  $a=$p[1]; [void](M ('close '+$a))\n"
            + "  if($wmp.ContainsKey($a)){ try{$wmp[$a].controls.stop()}catch{}; $wmp.Remove($a) }\n"
            + "  $r=M ('open \"'+$p[3]+'\" type mpegvideo alias '+$a)\n"
            + "  if($r -eq 0){ [void](M ('setaudio '+$a+' volume to '+$p[2])); $r=M ('play '+$a) }\n"
            + "  if($r -ne 0){\n"
            + "   try{ $w=New-Object -ComObject WMPlayer.OCX; $w.settings.volume=[int]([int]$p[2]/10); $w.URL=$p[3]; $w.controls.play(); $wmp[$a]=$w; Write-Host ('WMP '+$a) }catch{ Write-Host ('FAIL '+$_) }\n"
            + "  } else { Write-Host ('MCI '+$a) }\n"
            + " }\n"
            + " elseif($p[0] -eq 'STOP'){ [void](M ('close '+$p[1])); if($wmp.ContainsKey($p[1])){ try{$wmp[$p[1]].controls.stop()}catch{}; $wmp.Remove($p[1]) } }\n"
            + " elseif($p[0] -eq 'VOL'){ [void](M ('setaudio '+$p[1]+' volume to '+$p[2])); if($wmp.ContainsKey($p[1])){ $wmp[$p[1]].settings.volume=[int]([int]$p[2]/10) } }\n"
            + " elseif($p[0] -eq 'PAUSE'){ if($wmp.ContainsKey($p[1])){ if($p[2] -eq '1'){ $wmp[$p[1]].controls.pause() } else { $wmp[$p[1]].controls.play() } } else { if($p[2] -eq '1'){ [void](M ('pause '+$p[1])) } else { [void](M ('resume '+$p[1])) } } }\n"
            + " elseif($p[0] -eq 'Q'){ $a=$p[1]; if($wmp.ContainsKey($a)){ $st=$wmp[$a].playState; if($st -eq 1 -or $st -eq 8){ Write-Host ('ST '+$a+' stopped') } else { Write-Host ('ST '+$a+' playing') } } else { [void](M ('status '+$a+' mode')); Write-Host ('ST '+$a+' '+$sb.ToString()) } }\n"
            + " elseif($p[0] -eq 'KEY'){ try{ (New-Object -ComObject WScript.Shell).SendKeys([string][char][int]$p[1]) }catch{} }\n"
            + "}\n";

    public static boolean windows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    private static synchronized boolean ensure() {
        if (!windows()) {
            status = "Muzyka i głos działają tylko na Windows";
            return false;
        }
        if (proc != null && proc.isAlive() && in != null) return true;
        try {
            String b64 = Base64.getEncoder().encodeToString(SCRIPT.getBytes(StandardCharsets.UTF_16LE));
            proc = new ProcessBuilder("powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-WindowStyle", "Hidden",
                    "-EncodedCommand", b64)
                    .redirectErrorStream(true)
                    .start();
            in = proc.getOutputStream();
            final Process pr = proc;
            Thread rd = new Thread(() -> reader(pr), "ZTN-Audio-Reader");
            rd.setDaemon(true);
            rd.start();
            status = "Uruchamiam odtwarzacz...";
            return true;
        } catch (Exception e) {
            proc = null;
            in = null;
            status = "Nie udało się uruchomić PowerShell: " + e.getMessage();
            return false;
        }
    }

    private static void reader(Process pr) {
        Path log = FabricLoader.getInstance().getConfigDir().resolve("ztn-audio.log");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    Files.writeString(log, line + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                } catch (Exception ignored) { }
                if (line.startsWith("READY")) status = "Odtwarzacz gotowy";
                else if (line.startsWith("MCI")) status = "Silnik: MCI (OK)";
                else if (line.startsWith("WMP")) status = "Silnik: Windows Media Player (zapasowy)";
                else if (line.startsWith("ERR") || line.startsWith("FAIL")) status = "Błąd odtwarzacza: " + line;
                else if (line.startsWith("ST m") && line.contains("stopped")) Music.onStopped();
            }
        } catch (Exception ignored) { }
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
            status = "Odtwarzacz przestał działać, spróbuj ponownie";
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
