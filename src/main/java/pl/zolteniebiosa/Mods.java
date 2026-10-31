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
            .add(Setting.color("color", "s.color", 0))
            .add(Setting.num("opacity", "s.opacity", 10, 100, 35))
            .add(Setting.bool("bganim", "s.bganim", true)));

    public static final Mod VIEWMODEL = reg(new Mod("viewmodel", Items.DIAMOND_SWORD, 0, true)
            .add(Setting.num("x", "s.x", -100, 100, 0))
            .add(Setting.num("y", "s.y", -100, 100, 0))
            .add(Setting.num("z", "s.z", -100, 100, 0))
            .add(Setting.num("scale", "s.scale", 10, 200, 100))
            .add(Setting.num("rx", "s.rx", -180, 180, 0))
            .add(Setting.num("ry", "s.ry", -180, 180, 0))
            .add(Setting.num("rz", "s.rz", -180, 180, 0)));

    public static final Mod CROSSHAIR = reg(new Mod("crosshair", Items.SPECTRAL_ARROW, 0, true)
            .add(Setting.mode("style", "s.style", 0, "Plus", "Kropka", "Okrąg", "X-Strike", "Snajper"))
            .add(Setting.num("size", "s.size2", 1, 10, 3))
            .add(Setting.num("thick", "s.thick", 1, 3, 1))
            .add(Setting.num("gap", "s.gap", 0, 8, 2))
            .add(Setting.color("color", "s.color", 6))
            .add(Setting.bool("dot", "s.dot", false)));

    public static final Mod CAPE = reg(new Mod("cape", Items.ELYTRA, 0, true)
            .add(Setting.mode("cape", "s.cape", 1, Capes.NAMES))
            .add(Setting.info("info", "i.cape")));

    public static final Mod FULLBRIGHT = reg(new Mod("fullbright", Items.SEA_LANTERN, 0, true));

    public static final Mod WEATHER = reg(new Mod("weather", Items.WATER_BUCKET, 0, true)
            .add(Setting.mode("type", "s.type", 1, "o.rain", "o.snow"))
            .add(Setting.num("density", "s.density", 1, 20, 6)));

    public static final Mod HITSOUND = reg(new Mod("hitsound", Items.NOTE_BLOCK, 0, true)
            .add(Setting.mode("sound", "s.sound", 0, "Crit Smash", "Pling Pro", "Bell Hit", "Ding"))
            .add(Setting.num("volume", "s.volume", 0, 300, 200))
            .add(Setting.mode("target", "s.target", 2, "o.players", "o.mobs", "o.both")));

    public static final Mod HUD = reg(new Mod("hud", Items.COMPASS, 0, true)
            .add(Setting.bool("watermark", "s.watermark", true))
            .add(Setting.bool("arraylist", "s.arraylist", true))
            .add(Setting.bool("glow", "s.glow", true)));

    public static final Mod KEYSTROKES = reg(new Mod("keystrokes", Items.OAK_BUTTON, 0, true)
            .add(Setting.bool("cps", "s.cps", true)));

    public static final Mod ARMORHUD = reg(new Mod("armorhud", Items.IRON_CHESTPLATE, 0, true));

    public static final Mod EFFECTS = reg(new Mod("effects", Items.POTION, 0, true));

    public static final Mod VOICE = reg(new Mod("voice", Items.JUKEBOX, 0, true)
            .add(Setting.mode("engine", "s.engine", 0, "o.google", "o.windows"))
            .add(Setting.num("volume", "s.volume", 0, 100, 90))
            .add(Setting.mode("pitch", "s.pitch", 1, "o.normal", "o.cute", "o.high")));

    public static final Mod DISCORD = reg(new Mod("discord", Items.BELL, 0, true)
            .add(Setting.text("appid", "s.appid", "", "s.appid"))
            .add(Setting.bool("showserver", "s.showserver", false))
            .add(Setting.action("connect", "s.connect", () -> Rpc.connectNow()))
            .add(Setting.dynInfo("status", () -> "Status: " + Rpc.status))
            .add(Setting.info("info", "i.discord")));

    public static final Mod SYNC = reg(new Mod("sync", Items.ENDER_PEARL, 0, true)
            .add(Setting.action("refresh", "s.refresh", () -> Sync.refreshNow()))
            .add(Setting.dynInfo("status", () -> "Status: " + Sync.status))
            .add(Setting.info("info", "i.sync")));

    // ===== PETY (1) =====
    public static final Mod PET = reg(new Mod("pet", Items.BONE, 1, true)
            .add(Setting.mode("pet", "s.pet", 0, "o.parrot", "o.cat", "o.dog", "o.panda"))
            .add(Setting.mode("pos", "s.pos", 0, "o.shoulder", "o.side", "o.head"))
            .add(Setting.color("color", "s.color", 6))
            .add(Setting.num("size", "s.size", 10, 100, 45))
            .add(Setting.info("info", "i.pet")));

    // ===== hacksyZOLTEGO (2) =====
    public static final Mod TRACERS = reg(new Mod("tracers", Items.ENDER_EYE, 2, true)
            .add(Setting.num("dist", "s.dist", 1, 1000, 300))
            .add(Setting.mode("target", "s.target", 0, "o.players", "o.mobs", "o.both"))
            .add(Setting.num("width", "s.width", 1, 8, 3))
            .add(Setting.color("color", "s.color", 1)));

    public static final Mod HITBOXES = reg(new Mod("hitboxes", Items.IRON_BARS, 2, true)
            .add(Setting.mode("target", "s.target", 0, "o.players", "o.mobs", "o.both"))
            .add(Setting.num("dist", "s.dist", 1, 200, 80))
            .add(Setting.num("width", "s.width", 1, 6, 2))
            .add(Setting.color("color", "s.color", 2)));

    public static final Mod NAMETAGS = reg(new Mod("nametags", Items.NAME_TAG, 2, true)
            .add(Setting.mode("target", "s.target", 0, "o.players", "o.mobs", "o.both"))
            .add(Setting.bool("armor", "s.armor", true))
            .add(Setting.bool("items", "s.items", true))
            .add(Setting.bool("hearts", "s.hearts", true))
            .add(Setting.bool("showdist", "s.showdist", true))
            .add(Setting.num("scale", "s.scale", 50, 200, 100))
            .add(Setting.num("maxdist", "s.dist", 10, 400, 120)));

    public static final Mod KILLAURA = reg(new Mod("killaura", Items.IRON_SWORD, 2, true)
            .add(Setting.mode("target", "s.target", 1, "o.players", "o.mobs", "o.both"))
            .add(Setting.num("range", "s.range", 10, 60, 35))
            .add(Setting.bool("rotate", "s.rotate", true))
            .add(Setting.info("info", "i.killaura")));

    public static final Mod NOPUSH = reg(new Mod("nopush", Items.PISTON, 2, true)
            .add(Setting.info("info", "i.nopush")));

    public static final Mod AUTOSPRINT = reg(new Mod("autosprint", Items.LEATHER_BOOTS, 2, true));

    public static final Mod AUTOWATER = reg(new Mod("autowater", Items.WATER_BUCKET, 2, true)
            .add(Setting.info("info", "i.autowater")));

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
            .add(Setting.num("dist", "s.dist", 1, 128, 48))
            .add(Setting.mode("colormode", "s.colormode", 0, "o.auto", "o.custom"))
            .add(Setting.color("color", "s.color", 6))
            .add(Setting.bool("fill", "s.fill", false))
            .add(Setting.blocks("blocks", "s.blocks", "diamond_ore", "deepslate_diamond_ore")));

    public static final Mod XRAY = reg(new Mod("xray", Items.SPYGLASS, 3, true)
            .add(Setting.blocks("blocks", "s.blocks", ORES))
            .add(Setting.info("info", "i.xray")));

    public static final Mod BARITONE = reg(new Mod("baritone", Items.IRON_PICKAXE, 3, true)
            .add(Setting.text("block", "s.block", "diamond_ore", "s.block"))
            .add(Setting.bool("unlimited", "s.unlimited", true))
            .add(Setting.num("radius", "s.radius", 4, 128, 64))
            .add(Setting.bool("safe", "s.safe", true))
            .add(Setting.dynInfo("status", () -> "Status: " + Miner.status))
            .add(Setting.info("info", "i.baritone")));

    public static final Mod MARKET = reg(new Mod("market", Items.EMERALD, 3, true)
            .add(Setting.text("filter", "s.mfilter", "", "s.mfilter"))
            .add(Setting.text("maxprice", "s.maxprice", "", "s.maxprice"))
            .add(Setting.num("delay", "s.delay", 0, 2000, 200))
            .add(Setting.num("refresh", "s.refreshslot", -1, 53, -1))
            .add(Setting.text("confirm", "s.confirm", "potwierdź,confirm,kup", "s.confirm"))
            .add(Setting.bool("autoopen", "s.autoopen", false))
            .add(Setting.text("command", "s.command", "rynek", "s.command"))
            .add(Setting.num("reopen", "s.reopen", 1, 60, 5))
            .add(Setting.dynInfo("status", () -> "Status: " + Market.status))
            .add(Setting.info("info", "i.market")));

    // ===== BEZPIECZENSTWO (4) =====
    public static final Mod ANTIKOSTKA = reg(new Mod("antikostka", Items.CHEST, 4, false)
            .add(Setting.action("save", "s.save", () -> Modules.saveInventory(MinecraftClient.getInstance())))
            .add(Setting.action("restore", "s.restore", () -> Modules.restoreInventory(MinecraftClient.getInstance())))
            .add(Setting.info("info", "i.anti")));

    public static final Mod AUTOLEAVE = reg(new Mod("autoleave", Items.OAK_DOOR, 4, true)
            .add(Setting.num("dist", "s.leavedist", 1, 100, 20))
            .add(Setting.info("info", "i.leave")));

    // ===== MUZYKA (5) =====
    public static final Mod MUSIC = reg(new Mod("music", Items.NOTE_BLOCK, 5, false)
            .add(Setting.text("query", "s.query", "", "mu.hint"))
            .add(Setting.num("volume", "s.volume", 0, 100, 60))
            .add(Setting.action("stop", "s.mstop", () -> Music.stop()))
            .add(Setting.action("next", "s.mnext", () -> Music.next())));

    static {
        HUD.enabled = true;
        VOICE.enabled = true;
        SYNC.enabled = true;
        BARITONE.onChange = () -> Modules.baritone(BARITONE.enabled);
        XRAY.onChange = () -> Xray.requestReload();
        SKIN.get("name").onCommit = () -> {
            Skin.apply(SKIN.enabled ? SKIN.txt("name") : "");
            Sync.refreshNow();
        };
        SKIN.onChange = () -> {
            Skin.apply(SKIN.enabled ? SKIN.txt("name") : "");
            Sync.refreshNow();
        };
        CAPE.onChange = () -> Sync.refreshNow();
        MUSIC.get("query").onCommit = () -> Music.search(MUSIC.txt("query"));
    }
}
