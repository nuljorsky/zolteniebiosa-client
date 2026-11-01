package pl.zolteniebiosa;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;

public class Mod {
    public final String id;
    public final Item icon;
    public final int cat;
    public final boolean hasSwitch;
    public boolean enabled, open = true;
    public Runnable onChange;
    public final List<Setting> settings = new ArrayList<>();

    public Mod(String id, Item icon, int cat, boolean hasSwitch) {
        this.id = id;
        this.icon = icon;
        this.cat = cat;
        this.hasSwitch = hasSwitch;
    }

    public Mod add(Setting s) {
        settings.add(s);
        return this;
    }

    public String name() { return Lang.t("m." + id); }

    public String desc() { return Lang.t("d." + id); }

    public Setting get(String key) {
        for (Setting s : settings) if (s.key.equals(key)) return s;
        throw new IllegalArgumentException("Brak ustawienia " + key + " w " + id);
    }

    public int n(String k) { return (int) Math.round(get(k).num); }

    public int mode(String k) { return get(k).idx; }

    public boolean b(String k) { return get(k).flag; }

    public String txt(String k) { return get(k).text; }

    public void toggle() {
        enabled = !enabled;
        if (onChange != null) onChange.run();
    }
}
