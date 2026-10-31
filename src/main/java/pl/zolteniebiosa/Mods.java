package pl.zolteniebiosa;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

public class Mods {
    public static final List<Mod> ALL = new ArrayList<>();

    private static Mod reg(Mod m) {
        ALL.add(m);
        return m;
    }

    // ===== WYGLAD (0) =====
    public static final Mod THEME = reg(new Mod("theme", Items.NETHER_STAR, 0, false)
            .add(Setting.color("color", "s.color", 0)));

    public static final Mod VIEWMODEL = reg(new Mod("viewmodel", Items.DIAMOND_SWORD, 0, true)
            .add(Setting.num("x", "s.x", -100, 100, 0))
            .add(Setting.num("y", "s.y", -100, 100, 0))
            .add(Setting.num("z", "s.z", -100, 100, 0))
            .add(Setting.num("scale", "s.scale", 10, 200, 100))
            .add(Setting.num("rx", "s.rx", -180, 180, 0))
            .add(Setting.num("ry", "s.ry", -180, 180, 0))
            .add(Setting.num("rz", "s.rz", -180, 180, 0)));

    public static final Mod NIGHT = reg(new Mod("night", Items.CLOCK, 0, true));

    public static final Mod FULLBRIGHT = reg(new Mod("fullbright", Items.SEA_LANTERN, 0, true));

    public static final Mod WEATHER = reg(new Mod("weather", Items.WATER_BUCKET, 0, true)
            .add(Setting.mode("type", "s.type", 1, "o.rain", "o.snow"))
            .add(Setting.num("density", "s.density", 1, 20, 6)));

    public static final Mod HITSOUND = reg(new Mod("hitsound", Items.NOTE_BLOCK, 0, true)
            .add(Setting.mode("sound", "s.sound", 0, "Kawaii Ding", "Crystal Hit", "Neko Pop"))
            .add(Setting.num("volume", "s.volume", 0, 100, 70))
            .add(Setting.mode("target", "s.target", 2, "o.players", "o.mobs", "o.both")));

    public static final Mod HUD = reg(new Mod("hud", Items.COMPASS, 0, true)
            .add(Setting.bool("watermark", "s.watermark", true))
            .add(Setting.bool("arraylist", "s.arraylist", true))
            .add(Setting.bool("glow", "s.glow", true)));

    public static final Mod VOICE = reg(new Mod("voice", Items.JUKEBOX, 0, true)
            .add(Setting.num("volume", "s.volume", 0, 100, 90))
            .add(Setting.mode("pitch", "s.pitch", 1, "o.normal", "o.cute", "o.high")));

    // ===== PETY (1) =====
    public static final Mod PET = reg(new Mod("pet", Items.BONE, 1, true)
            .add(Setting.mode("pet", "s.pet", 0, "o.parrot", "o.cat", "o.dog", "o.panda"))
            .add(Setting.mode("pos", "s.pos", 0, "o.shoulder", "o.side", "o.head"))
            .add(Setting.color("color", "s.color", 6))
            .add(Setting.num("size", "s.size", 10, 100, 45)));

    // ===== hacksyZOLTEGO (2) =====
    public static final Mod TRACERS = reg(new Mod("tracers", Items.ENDER_EYE, 2, true)
            .add(Setting.num("dist", "s.dist", 1, 1000, 300))
            .add(Setting.mode("target", "s.target", 0, "o.players", "o.mobs", "o.both"))
            .add(Setting.num("width", "s.width", 1, 8, 3))
            .add(Setting.color("color", "s.color", 1)));

    public static final Mod NAMETAGS = reg(new Mod("nametags", Items.NAME_TAG, 2, true)
            .add(Setting.mode("target", "s.target", 0, "o.players", "o.mobs", "o.both"))
            .add(Setting.bool("armor", "s.armor", true))
            .add(Setting.bool("items", "s.items", true))
            .add(Setting.bool("hearts", "s.hearts", true))
            .add(Setting.bool("showdist", "s.showdist", true))
            .add(Setting.num("scale", "s.scale", 50, 200, 100))
            .add(Setting.num("maxdist", "s.dist", 10, 400, 120)));

    public static final Mod NAMEPROTECT = reg(new Mod("nameprotect", Items.WRITABLE_BOOK, 2, true)
            .add(Setting.text("name", "s.nick", "", "s.nick"))
            .add(Setting.info("info", "i.np")));

    public static final Mod SKIN = reg(new Mod("skin", Items.PLAYER_HEAD, 2, true)
            .add(Setting.text("name", "s.skinname", "", "s.skinname"))
            .add(Setting.info("info", "i.skin")));

    // ===== ZDOBYCZ (3) =====
    private static final String[] ORES = {"coal_ore", "deepslate_coal_ore", "iron_ore", "deepslate_iron_ore",
            "copper_ore", "deepslate_copper_ore", "gold_ore", "deepslate_gold_ore", "nether_gold_ore",
            "redstone_ore", "deepslate_redstone_ore", "lapis_ore", "deepslate_lapis_ore", "diamond_ore",
            "deepslate_diamond_ore", "emerald_ore", "deepslate_emerald_ore", "nether_quartz_ore", "ancient_debris"};

    public static final Mod BLOCKESP = reg(new Mod("blockesp", Items.DIAMOND_ORE, 3, true)
            .add(Setting.num("dist", "s.dist", 1, 48, 32))
            .add(Setting.mode("colormode", "s.colormode", 0, "o.auto", "o.custom"))
            .add(Setting.color("color", "s.color", 6))
            .add(Setting.bool("fill", "s.fill", false))
            .add(Setting.blocks("blocks", "s.blocks", "diamond_ore", "deepslate_diamond_ore")));

    public static final Mod XRAY = reg(new Mod("xray", Items.SPYGLASS, 3, true)
            .add(Setting.num("dist", "s.dist", 1, 48, 32))
            .add(Setting.mode("colormode", "s.colormode", 0, "o.auto", "o.custom"))
            .add(Setting.color("color", "s.color", 6))
            .add(Setting.bool("fill", "s.fill", true))
            .add(Setting.blocks("blocks", "s.blocks", ORES))
            .add(Setting.info("info", "i.xray")));

    public static final Mod BARITONE = reg(new Mod("baritone", Items.IRON_PICKAXE, 3, true)
            .add(Setting.text("block", "s.block", "diamond_ore", "s.block"))
            .add(Setting.info("info", "i.baritone")));

    // ===== BEZPIECZENSTWO (4) =====
    public static final Mod ANTIKOSTKA = reg(new Mod("antikostka", Items.CHEST, 4, false)
            .add(Setting.action("save", "s.save", () -> Modules.saveInventory(MinecraftClient.getInstance())))
            .add(Setting.action("restore", "s.restore", () -> Modules.restoreInventory(MinecraftClient.getInstance())))
            .add(Setting.info("info", "i.anti")));

    public static final Mod AUTOLEAVE = reg(new Mod("autoleave", Items.OAK_DOOR, 4, true)
            .add(Setting.num("dist", "s.leavedist", 1, 100, 20))
            .add(Setting.info("info", "i.leave")));

    static {
        HUD.enabled = true;
        VOICE.enabled = true;
        BARITONE.onChange = () -> Modules.baritone(BARITONE.enabled);
        SKIN.get("name").onCommit = () -> Skin.apply(SKIN.enabled ? SKIN.txt("name") : "");
        SKIN.onChange = () -> Skin.apply(SKIN.enabled ? SKIN.txt("name") : "");
    }
}
