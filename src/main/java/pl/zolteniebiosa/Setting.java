package pl.zolteniebiosa;

import java.util.LinkedHashSet;

public class Setting {
    public static final int NUM = 0, MODE = 1, COLOR = 2, TEXT = 3, ACTION = 4, BLOCKS = 5, BOOL = 6, INFO = 7;
    public final int kind;
    public final String key, label;
    public double num, min, max;
    public int idx;
    public String[] opts = new String[0];
    public String text = "", hint = "";
    public boolean flag;
    public Runnable action, onCommit;
    public java.util.function.Supplier<String> dyn;
    public final LinkedHashSet<String> blocks = new LinkedHashSet<>();

    private Setting(int kind, String key, String label) {
        this.kind = kind;
        this.key = key;
        this.label = label;
    }

    public static Setting num(String key, String label, double min, double max, double def) {
        Setting s = new Setting(NUM, key, label);
        s.min = min; s.max = max; s.num = def;
        return s;
    }

    public static Setting mode(String key, String label, int def, String... opts) {
        Setting s = new Setting(MODE, key, label);
        s.opts = opts; s.idx = def;
        return s;
    }

    public static Setting color(String key, String label, int def) {
        Setting s = new Setting(COLOR, key, label);
        s.idx = def;
        return s;
    }

    public static Setting bool(String key, String label, boolean def) {
        Setting s = new Setting(BOOL, key, label);
        s.flag = def;
        return s;
    }

    public static Setting text(String key, String label, String def, String hint) {
        Setting s = new Setting(TEXT, key, label);
        s.text = def; s.hint = hint;
        return s;
    }

    public static Setting action(String key, String label, Runnable r) {
        Setting s = new Setting(ACTION, key, label);
        s.action = r;
        return s;
    }

    public static Setting info(String key, String textKey) {
        return new Setting(INFO, key, textKey);
    }

    public static Setting dynInfo(String key, java.util.function.Supplier<String> sup) {
        Setting s = new Setting(INFO, key, key);
        s.dyn = sup;
        return s;
    }

    public static Setting blocks(String key, String label, String... defs) {
        Setting s = new Setting(BLOCKS, key, label);
        s.hint = "s.addblock";
        for (String d : defs) s.blocks.add(d);
        return s;
    }

    public String save() {
        return switch (kind) {
            case NUM -> String.valueOf((int) Math.round(num));
            case MODE, COLOR -> String.valueOf(idx);
            case TEXT -> text;
            case BOOL -> String.valueOf(flag);
            case BLOCKS -> String.join(",", blocks);
            default -> null;
        };
    }

    public void load(String v) {
        try {
            switch (kind) {
                case NUM -> num = Math.max(min, Math.min(max, Integer.parseInt(v)));
                case MODE -> idx = Math.max(0, Math.min(opts.length - 1, Integer.parseInt(v)));
                case COLOR -> idx = Math.max(0, Math.min(Theme.PAL.length - 1, Integer.parseInt(v)));
                case TEXT -> text = v;
                case BOOL -> flag = Boolean.parseBoolean(v);
                case BLOCKS -> {
                    blocks.clear();
                    for (String p : v.split(",")) if (!p.isBlank()) blocks.add(p.trim());
                }
                default -> { }
            }
        } catch (Exception ignored) { }
    }
}
