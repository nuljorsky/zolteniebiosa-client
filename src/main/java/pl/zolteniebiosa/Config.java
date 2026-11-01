package pl.zolteniebiosa;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** Zapis ustawien clienta do config/zolteniebiosa.json. Zapisuje sie przy zmianach, co chwile i przy wyjsciu z gry. */
public class Config {
    private static String lastSaved = "";

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("zolteniebiosa.json");
    }

    public static void load() {
        try {
            Path p = path();
            if (!Files.exists(p)) return;
            String raw = Files.readString(p);
            Map<String, String> m = new Gson().fromJson(raw, new TypeToken<Map<String, String>>() { }.getType());
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
            lastSaved = snapshot();
        } catch (Exception ignored) { }
    }

    private static String snapshot() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("lang", String.valueOf(Lang.cur));
        for (Mod mod : Mods.ALL) {
            if (mod.hasSwitch) m.put(mod.id + ".enabled", String.valueOf(mod.enabled));
            for (Setting s : mod.settings) {
                String v = s.save();
                if (v != null) m.put(mod.id + "." + s.key, v);
            }
        }
        return new Gson().toJson(m);
    }

    public static synchronized void save() {
        try {
            String json = snapshot();
            Files.writeString(path(), json);
            lastSaved = json;
        } catch (Exception ignored) { }
    }

    /** Zapisuje tylko gdy cos sie zmienilo (np. suwak przeciagniety, tekst wpisany). */
    public static void saveIfChanged() {
        try {
            if (!snapshot().equals(lastSaved)) save();
        } catch (Exception ignored) { }
    }
}
