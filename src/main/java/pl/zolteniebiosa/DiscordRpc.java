package pl.zolteniebiosa;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Discord Rich Presence bez zadnych bibliotek - rozmawia z dzialajacym Discordem przez lokalny pipe/socket.
 * Znajomi widza "Gra w ZolteNiebiosa CLIENT" + serwer. Nie trzeba sie nigdzie logowac - uzywa konta z aplikacji Discord.
 * Potrzebny jest tylko Application ID (https://discord.com/developers/applications) - patrz README.
 */
public final class DiscordRpc {
    private static final ScheduledExecutorService EX = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "ZTN-Discord");
        t.setDaemon(true);
        return t;
    });

    // ustawiane z glownego watku
    private static volatile boolean wanted;
    private static volatile String appId = "";
    private static volatile String details = "";
    private static volatile String state = "";
    private static volatile boolean started;

    // widoczne dla menu
    private static volatile String status = "";
    private static volatile String user = "";

    // uzywane tylko w watku EX
    private static Link link;
    private static String sentKey = "";
    private static String linkAppId = "";
    private static long nextTry = 0;
    private static long lastSend = 0;
    private static final long START = System.currentTimeMillis() / 1000L;

    private DiscordRpc() { }

    /** Wywolaj raz przy starcie. */
    public static void init() {
        if (started) return;
        started = true;
        EX.scheduleWithFixedDelay(DiscordRpc::pump, 2, 3, TimeUnit.SECONDS);
    }

    public static void set(boolean enabled, String applicationId, String detailsLine, String stateLine) {
        wanted = enabled;
        appId = applicationId == null ? "" : applicationId.trim();
        details = detailsLine == null ? "" : detailsLine;
        state = stateLine == null ? "" : stateLine;
    }

    public static String status() {
        return status;
    }

    public static String user() {
        return user;
    }

    /** Wymusza ponowne polaczenie z Discordem. */
    public static void reconnect() {
        EX.execute(() -> {
            close();
            nextTry = 0;
            sentKey = "";
        });
    }

    // ===================== petla =====================
    private static void pump() {
        try {
            String id = appId;
            if (!wanted) {
                if (link != null) close();
                status = "Wyłączone";
                return;
            }
            if (!id.matches("\\d{15,25}")) {
                if (link != null) close();
                status = "Brak Application ID - wklej je niżej (przycisk pomoże je założyć)";
                return;
            }
            if (link != null && !id.equals(linkAppId)) close();
            long now = System.currentTimeMillis();
            if (link == null) {
                if (now < nextTry) return;
                connect(id);
                if (link == null) {
                    nextTry = now + 15_000L;
                    return;
                }
            }
            String key = details + "\n" + state;
            if (!key.equals(sentKey) && now - lastSend >= 5_000L) {
                sendActivity();
                sentKey = key;
                lastSend = now;
            }
        } catch (Exception e) {
            close();
            status = "Błąd połączenia z Discordem - ponawiam";
            nextTry = System.currentTimeMillis() + 15_000L;
        }
    }

    private static void connect(String id) {
        status = "Łączenie z Discordem...";
        for (int i = 0; i < 10; i++) {
            Link l = null;
            try {
                l = open(i);
                if (l == null) continue;
                send(l.out, 0, "{\"v\":1,\"client_id\":\"" + esc(id) + "\"}");
                Frame f = recv(l.in);
                Matcher m = Pattern.compile("\"username\"\\s*:\\s*\"([^\"]*)\"").matcher(f.json);
                if (f.json.contains("\"READY\"") || f.json.contains("\"user\"")) {
                    user = m.find() ? m.group(1) : "?";
                    link = l;
                    linkAppId = id;
                    status = "Połączono jako " + user;
                    return;
                }
                if (f.json.contains("\"code\"")) {
                    status = "Discord odrzucił to Application ID - sprawdź, czy skopiowałeś poprawny numer z discord.com/developers";
                }
                l.close();
            } catch (Exception e) {
                if (l != null) l.close();
            }
        }
        if (link == null && !status.startsWith("Discord odrzucił")) {
            status = "Nie widzę aplikacji Discord - musi być otwarta na TYM komputerze (nie telefon, nie przeglądarka)";
        }
    }

    private static void sendActivity() throws IOException {
        StringBuilder a = new StringBuilder("{");
        a.append("\"details\":\"").append(esc(details)).append("\"");
        if (!state.isEmpty()) a.append(",\"state\":\"").append(esc(state)).append("\"");
        a.append(",\"timestamps\":{\"start\":").append(START).append("}");
        a.append(",\"assets\":{\"large_image\":\"logo\",\"large_text\":\"").append(esc(Branding.NAME)).append("\"}");
        a.append(",\"buttons\":[{\"label\":\"YouTube ZolteNiebiosa\",\"url\":\"").append(esc(Branding.YT_URL)).append("\"}]");
        a.append("}");
        String json = "{\"cmd\":\"SET_ACTIVITY\",\"args\":{\"pid\":" + ProcessHandle.current().pid()
                + ",\"activity\":" + a + "},\"nonce\":\"" + UUID.randomUUID() + "\"}";
        send(link.out, 1, json);
        recv(link.in); // Discord odpowiada na kazde zapytanie - odczytujemy zeby pipe sie nie zapchal
    }

    private static void close() {
        if (link != null) {
            link.close();
            link = null;
        }
        user = "";
        sentKey = "";
    }

    // ===================== transport =====================
    private static final class Link {
        InputStream in;
        OutputStream out;
        Closeable_ c;

        void close() {
            try {
                if (c != null) c.close();
            } catch (Exception ignored) { }
        }
    }

    private interface Closeable_ {
        void close() throws IOException;
    }

    private record Frame(int op, String json) { }

    private static Link open(int n) throws IOException {
        String os = System.getProperty("os.name", "").toLowerCase();
        Link l = new Link();
        if (os.contains("win")) {
            String path = "\\\\.\\pipe\\discord-ipc-" + n;
            File f = new File(path);
            if (!f.exists()) return null;
            RandomAccessFile raf = new RandomAccessFile(path, "rw");
            l.in = new InputStream() {
                @Override
                public int read() throws IOException {
                    return raf.read();
                }

                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    return raf.read(b, off, len);
                }
            };
            l.out = new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    raf.write(b);
                }

                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    raf.write(b, off, len);
                }
            };
            l.c = raf::close;
            return l;
        }
        String[] bases = {
                System.getenv("XDG_RUNTIME_DIR"), System.getenv("TMPDIR"), System.getenv("TMP"), System.getenv("TEMP"), "/tmp"
        };
        for (String b : bases) {
            if (b == null || b.isEmpty()) continue;
            String[] subs = {"", "/app/com.discordapp.Discord", "/snap.discord"};
            for (String s : subs) {
                File f = new File(b + s + "/discord-ipc-" + n);
                if (!f.exists()) continue;
                SocketChannel ch = SocketChannel.open(StandardProtocolFamily.UNIX);
                ch.connect(UnixDomainSocketAddress.of(f.toPath()));
                l.in = Channels.newInputStream(ch);
                l.out = Channels.newOutputStream(ch);
                l.c = ch::close;
                return l;
            }
        }
        return null;
    }

    private static void send(OutputStream o, int op, String json) throws IOException {
        byte[] d = json.getBytes(StandardCharsets.UTF_8);
        ByteBuffer b = ByteBuffer.allocate(8 + d.length).order(ByteOrder.LITTLE_ENDIAN);
        b.putInt(op).putInt(d.length).put(d);
        o.write(b.array());
        o.flush();
    }

    private static Frame recv(InputStream in) throws IOException {
        byte[] h = in.readNBytes(8);
        if (h.length < 8) throw new EOFException("Discord zamknal polaczenie");
        ByteBuffer hb = ByteBuffer.wrap(h).order(ByteOrder.LITTLE_ENDIAN);
        int op = hb.getInt();
        int len = hb.getInt();
        if (len < 0 || len > 1 << 20) throw new IOException("zly rozmiar ramki");
        byte[] body = in.readNBytes(len);
        if (body.length < len) throw new EOFException("ucieta ramka");
        return new Frame(op, new String(body, StandardCharsets.UTF_8));
    }

    private static String esc(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 32) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        return sb.toString();
    }
}
