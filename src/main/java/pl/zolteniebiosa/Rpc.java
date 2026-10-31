package pl.zolteniebiosa;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

// Discord Rich Presence przez lokalny pipe Discorda (tylko Windows, Discord musi byc uruchomiony)
public class Rpc {
    private static volatile String state = "Minecraft 1.21.4";
    private static RandomAccessFile pipe;
    private static String connectedId = "";
    public static volatile String status = "Wyłączone";
    private static final Object LOCK = new Object();

    public static void connectNow() {
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }

    public static void init() {
        Thread t = new Thread(Rpc::loop, "ZTN-RPC");
        t.setDaemon(true);
        t.start();
    }

    public static void tick(MinecraftClient mc) {
        if (mc.player == null) {
            state = "W menu głównym";
            return;
        }
        ServerInfo s = mc.getCurrentServerEntry();
        if (s != null && Mods.DISCORD.b("showserver")) state = "Gra na " + s.address;
        else state = "Gra w Minecrafta 1.21.4";
    }

    private static void loop() {
        long startTs = System.currentTimeMillis() / 1000;
        while (true) {
            try {
                String id = Mods.DISCORD.txt("appid").trim();
                if (Mods.DISCORD.enabled && !id.isEmpty()) {
                    if (pipe == null || !id.equals(connectedId)) {
                        status = "Łączę z Discordem...";
                        connect(id);
                    }
                    activity(startTs);
                    status = "Połączono - status widoczny na Discordzie";
                } else {
                    if (pipe != null) close();
                    status = !Mods.DISCORD.enabled ? "Wyłączone" : "Wklej Application ID (patrz instrukcja)";
                }
                synchronized (LOCK) {
                    LOCK.wait(15000);
                }
            } catch (InterruptedException e) {
                return;
            } catch (Throwable e) {
                close();
                status = "Błąd: Discord nie jest uruchomiony albo złe Application ID (" + e.getClass().getSimpleName() + ")";
                try {
                    synchronized (LOCK) {
                        LOCK.wait(15000);
                    }
                } catch (InterruptedException ie) {
                    return;
                }
            }
        }
    }

    private static void close() {
        try {
            if (pipe != null) pipe.close();
        } catch (Exception ignored) { }
        pipe = null;
        connectedId = "";
    }

    private static void connect(String id) throws Exception {
        close();
        Exception last = null;
        for (int i = 0; i < 10; i++) {
            try {
                pipe = new RandomAccessFile(new File("\\\\.\\pipe\\discord-ipc-" + i), "rw");
                JsonObject hs = new JsonObject();
                hs.addProperty("v", 1);
                hs.addProperty("client_id", id);
                write(0, hs.toString());
                read();
                connectedId = id;
                return;
            } catch (Exception e) {
                last = e;
                close();
            }
        }
        throw last == null ? new Exception("brak Discorda") : last;
    }

    private static void write(int op, String json) throws Exception {
        byte[] d = json.getBytes(StandardCharsets.UTF_8);
        ByteBuffer bb = ByteBuffer.allocate(8 + d.length).order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(op).putInt(d.length).put(d);
        pipe.write(bb.array());
    }

    private static String read() throws Exception {
        byte[] h = new byte[8];
        pipe.readFully(h);
        int len = ByteBuffer.wrap(h).order(ByteOrder.LITTLE_ENDIAN).getInt(4);
        byte[] body = new byte[Math.max(0, Math.min(len, 1 << 20))];
        pipe.readFully(body);
        return new String(body, StandardCharsets.UTF_8);
    }

    private static void activity(long startTs) throws Exception {
        JsonObject act = new JsonObject();
        act.addProperty("details", "ZolteNiebiosa CLIENT");
        act.addProperty("state", state);
        JsonObject ts = new JsonObject();
        ts.addProperty("start", startTs);
        act.add("timestamps", ts);
        JsonArray buttons = new JsonArray();
        JsonObject b = new JsonObject();
        b.addProperty("label", "YouTube ZolteNiebiosa");
        b.addProperty("url", "https://www.youtube.com/@zolteNiebiosa");
        buttons.add(b);
        act.add("buttons", buttons);
        JsonObject args = new JsonObject();
        args.addProperty("pid", ProcessHandle.current().pid());
        args.add("activity", act);
        JsonObject root = new JsonObject();
        root.addProperty("cmd", "SET_ACTIVITY");
        root.add("args", args);
        root.addProperty("nonce", UUID.randomUUID().toString());
        write(1, root.toString());
        read();
    }
}
