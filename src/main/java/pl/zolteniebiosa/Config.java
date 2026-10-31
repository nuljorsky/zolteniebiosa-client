package pl.zolteniebiosa;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class Config {
    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("zolteniebiosa.json");
    }

    public static void load() {
        try {
            Path p = path();
            if (!Files.exists(p)) return;
            Map<String, String> m = new Gson().fromJson(Files.readString(p), new TypeToken<Map<String, String>>() { }.getType());
            if (m == null) return;
            String l = m.get("lang");
            if (l != null) Lang.cur = Math.max(0, Math.min(3, Integer.parseInt(l)));
            for (Mod mod : Mods.ALL) {
                String e = m.get(mod.id + ".enabled");
                if (e != null && mod.hasSwitch && !mod.id.equals("baritone")) mod.enabled = Boolean.parseBoolean(e);
                for (Setting s : mod.settings) {
                    String v = m.get(mod.id + "." + s.key);
                    if (v != null) s.load(v);
                }
            }
        } catch (Exception ignored) { }
    }

    public static void save() {
        try {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("lang", String.valueOf(Lang.cur));
            for (Mod mod : Mods.ALL) {
                if (mod.hasSwitch) m.put(mod.id + ".enabled", String.valueOf(mod.enabled));
                for (Setting s : mod.settings) {
                    String v = s.save();
                    if (v != null) m.put(mod.id + "." + s.key, v);
                }
            }
            Files.writeString(path(), new Gson().toJson(m));
        } catch (Exception ignored) { }
    }
}
