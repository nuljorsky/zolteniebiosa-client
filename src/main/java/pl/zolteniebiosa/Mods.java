package pl.zolteniebiosa;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public class Mods {
    public static final List<Mod> ALL = new ArrayList<>();

    /** Zakladki menu: 0 WYGLAD, 1 PETY, 2 hacksyZOLTEGO, 3 ZDOBYCZ, 4 BEZPIECZENSTWO, 5 UDOGODNIENIA, 6 MODY PvP, 7 MUZYKA, 8 DISCORD, 9 #INFO */
    public static final int TAB_MUSIC = 7, TAB_INFO = 9;

    private static Mod reg(Mod m) {
        ALL.add(m);
        return m;
    }

    // ===== WYGLAD (0) =====
    public static final Mod THEME = reg(new Mod("theme", Items.PAINTING, 0, false)
            .add(Setting.color("color", "s.color", 0))
            .add(Setting.num("opacity", "s.opacity", 15, 100, 55))
            .add(Setting.bool("bootanim", "s.bootanim", true))
            .add(Setting.bool("blur", "s.blur", false))
            .add(Setting.bool("parallax", "s.parallax", true))
            .add(Setting.bool("titlescreen", "s.titlescreen", true)));

    public static final Mod CROSSHAIR = reg(new Mod("crosshair", Items.SPYGLASS, 0, false)
            .add(Setting.xhair("style", "s.style", 0, "o.cr0", "o.cr1", "o.cr2", "o.cr3", "o.cr4", "o.cr5"))
            .add(Setting.color("color", "s.color", 1))
            .add(Setting.num("size", "s.size2", 2, 20, 6))
            .add(Setting.num("thick", "s.thick", 1, 4, 2))
            .add(Setting.num("gap", "s.gap", 0, 12, 3))
            .add(Setting.bool("outline", "s.outline", true)));

    public static final Mod SKIN = reg(new Mod("skin", Items.PLAYER_HEAD, 0, true)
            .add(Setting.text("name", "s.skinname", "", "s.skinname"))
            .add(Setting.dyn("status", () -> Skin.status(Mods.SKIN.txt("name"))))
            .add(Setting.info("info", "i.skin")));

    public static final Mod CAPE = reg(new Mod("cape", Items.WHITE_BANNER, 0, true)
            .add(Setting.mode("cape", "s.cape", 0, Capes.names()))
            .add(Setting.info("info", "i.cape")));

    public static final Mod VIEWMODEL = reg(new Mod("viewmodel", Items.DIAMOND_SWORD, 0, true)
            .add(Setting.num("x", "s.x", -100, 100, 0))
            .add(Setting.num("y", "s.y", -100, 100, 0))
            .add(Setting.num("z", "s.z", -100, 100, 0))
            .add(Setting.num("scale", "s.scale", 10, 200, 100))
            .add(Setting.num("rx", "s.rx", -180, 180, 0))
            .add(Setting.num("ry", "s.ry", -180, 180, 0))
            .add(Setting.num("rz", "s.rz", -180, 180, 0)));

    public static final Mod NIGHT = reg(new Mod("night", Items.CLOCK, 0, true));

    public static final Mod FULLBRIGHT = reg(new Mod("fullbright", Items.GLOWSTONE_DUST, 0, true));

    public static final Mod WEATHER = reg(new Mod("weather", Items.SNOWBALL, 0, true)
            .add(Setting.mode("type", "s.type", 1, "o.rain", "o.snow"))
            .add(Setting.num("density", "s.density", 1, 20, 8))
            .add(Setting.bool("frost", "s.frost", true))
            .add(Setting.bool("lightning", "s.lightning", true)));

    public static final Mod HITSOUND = reg(new Mod("hitsound", Items.NOTE_BLOCK, 0, true)
            .add(Setting.mode("sound", "s.sound", 0, "Kawaii Ding", "Crystal Hit", "Neko Pop"))
            .add(Setting.num("volume", "s.volume", 0, 100, 70))
            .add(Setting.mode("target", "s.target", 2, "o.players", "o.mobs", "o.both")));

    public static final Mod HUD = reg(new Mod("hud", Items.COMPASS, 0, true)
            .add(Setting.bool("watermark", "s.watermark", true))
            .add(Setting.bool("arraylist", "s.arraylist", true))
            .add(Setting.bool("glow", "s.glow", true)));

    public static final Mod VOICE = reg(new Mod("voice", Items.BELL, 0, true)
            .add(Setting.mode("engine", "s.engine", 0, "o.vg", "o.vw"))
            .add(Setting.num("volume", "s.volume", 0, 100, 85))
            .add(Setting.mode("pitch", "s.pitch", 0, "o.normal", "o.cute", "o.high")));

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

    public static final Mod NAMEPROTECT = reg(new Mod("nameprotect", Items.BARRIER, 2, true)
            .add(Setting.text("name", "s.nick", "", "s.nick"))
            .add(Setting.info("info", "i.np")));

    // ===== ZDOBYCZ (3) =====
    private static final String[] ORES = {"coal_ore", "deepslate_coal_ore", "iron_ore", "deepslate_iron_ore",
            "copper_ore", "deepslate_copper_ore", "gold_ore", "deepslate_gold_ore", "nether_gold_ore",
            "redstone_ore", "deepslate_redstone_ore", "lapis_ore", "deepslate_lapis_ore", "diamond_ore",
            "deepslate_diamond_ore", "emerald_ore", "deepslate_emerald_ore", "nether_quartz_ore", "ancient_debris"};

    public static final Mod BLOCKESP = reg(new Mod("blockesp", Items.SPYGLASS, 3, true)
            .add(Setting.num("dist", "s.dist", 1, 48, 32))
            .add(Setting.mode("colormode", "s.colormode", 0, "o.auto", "o.custom"))
            .add(Setting.color("color", "s.color", 6))
            .add(Setting.bool("fill", "s.fill", false))
            .add(Setting.blocks("blocks", "s.blocks", "diamond_ore", "deepslate_diamond_ore")));

    public static final Mod XRAY = reg(new Mod("xray", Items.GLASS, 3, true)
            .add(Setting.num("dist", "s.dist", 1, 48, 32))
            .add(Setting.mode("colormode", "s.colormode", 0, "o.auto", "o.custom"))
            .add(Setting.color("color", "s.color", 6))
            .add(Setting.bool("fill", "s.fill", true))
            .add(Setting.blocks("blocks", "s.blocks", ORES))
            .add(Setting.info("info", "i.xray")));

    public static final Mod BARITONE = reg(new Mod("baritone", Items.DIAMOND_PICKAXE, 3, true)
            .add(Setting.text("block", "s.block", "diamond_ore", "s.block"))
            .add(Setting.info("info", "i.baritone")));

    // ===== BEZPIECZENSTWO (4) =====
    public static final Mod ANTIKOSTKA = reg(new Mod("antikostka", Items.CHEST, 4, false)
            .add(Setting.action("save", "s.save", () -> Modules.saveInventory(MinecraftClient.getInstance())))
            .add(Setting.action("restore", "s.restore", () -> Modules.restoreInventory(MinecraftClient.getInstance())))
            .add(Setting.info("info", "i.anti")));

    public static final Mod AUTOLEAVE = reg(new Mod("autoleave", Items.ENDER_PEARL, 4, true)
            .add(Setting.num("dist", "s.leavedist", 1, 100, 20))
            .add(Setting.info("info", "i.leave")));

    // ===== UDOGODNIENIA (5) =====
    public static final Mod NOPUSH = reg(new Mod("nopush", Items.ENDER_PEARL, 5, true)
            .add(Setting.bool("players", "s.np.players", true))
            .add(Setting.bool("blocks", "s.np.blocks", true))
            .add(Setting.bool("water", "s.np.water", true))
            .add(Setting.info("info", "i.nopush")));

    public static final Mod AUTOMARKET = reg(new Mod("automarket", Items.EMERALD, 5, false)
            .add(Setting.dyn("status", AutoMarket::status))
            .add(Setting.text("opencmd", "s.am.open", "ah", "s.am.open"))
            .add(Setting.text("listcmd", "s.am.list", "ah sell {cena}", "s.am.list"))
            .add(Setting.text("fallback", "s.am.fallback", "", "s.am.fallback"))
            .add(Setting.mode("mode", "s.am.mode", 0, "o.pct", "o.flat"))
            .add(Setting.num("amount", "s.am.amount", 1, 50, 5))
            .add(Setting.bool("perunit", "s.am.perunit", false))
            .add(Setting.text("floor", "s.am.floor", "1", "s.am.floor"))
            .add(Setting.action("start", "s.am.start", AutoMarket::start))
            .add(Setting.info("info", "i.automarket")));

    public static final Mod SPRINT = reg(new Mod("sprint", Items.LEATHER_BOOTS, 5, true));

    public static final Mod ZOOM = reg(new Mod("zoom", Items.SPYGLASS, 5, true)
            .add(Setting.num("factor", "s.zoomf", 2, 10, 4))
            .add(Setting.info("info", "i.zoom")));

    // ===== MODY PvP (6) =====
    public static final Mod TARGETHUD = reg(new Mod("targethud", Items.TOTEM_OF_UNDYING, 6, true));

    public static final Mod COMBO = reg(new Mod("combo", Items.IRON_SWORD, 6, true)
            .add(Setting.bool("reach", "s.reach", true)));

    public static final Mod DMGPOP = reg(new Mod("dmgpop", Items.FIRE_CHARGE, 6, true));

    public static final Mod LOWHP = reg(new Mod("lowhp", Items.GOLDEN_APPLE, 6, true)
            .add(Setting.num("hp", "s.lowhp", 2, 12, 6)));

    public static final Mod COMPASS = reg(new Mod("compass", Items.COMPASS, 6, true)
            .add(Setting.bool("speed", "s.speed", true)));

    public static final Mod KEYSTROKES = reg(new Mod("keystrokes", Items.STICK, 6, true)
            .add(Setting.bool("cps", "s.cps", true)));

    public static final Mod ARMORHUD = reg(new Mod("armorhud", Items.IRON_CHESTPLATE, 6, true));

    // ===== MUZYKA (7) =====
    public static final Mod MUSIC = reg(new Mod("music", Items.JUKEBOX, 7, false)
            .add(Setting.num("volume", "s.volume", 0, 100, 60))
            .add(Setting.bool("autoplay", "s.autoplay", true))
            .add(Setting.bool("shownow", "s.shownow", true)));

    // ===== DISCORD (8) =====
    public static final Mod DISCORD = reg(new Mod("discord", Items.ENDER_CHEST, 8, true)
            .add(Setting.dyn("status", DiscordRpc::status))
            .add(Setting.action("create", "s.createapp", () ->
                    net.minecraft.util.Util.getOperatingSystem().open(java.net.URI.create("https://discord.com/developers/applications"))))
            .add(Setting.text("appid", "s.appid", "", "s.appid"))
            .add(Setting.bool("showserver", "s.showserver", true))
            .add(Setting.action("reconnect", "s.reconnect", DiscordRpc::reconnect))
            .add(Setting.info("info", "i.discord")));

    public static final Mod ZTNSYNC = reg(new Mod("ztnsync", Items.ENDER_EYE, 8, true)
            .add(Setting.dyn("status", Net::status))
            .add(Setting.text("url", "s.syncurl", "", "s.syncurl"))
            .add(Setting.info("info", "i.sync")));

    static {
        HUD.enabled = true;
        VOICE.enabled = true;
        DISCORD.enabled = true;
        ZTNSYNC.enabled = true;
        BARITONE.onChange = () -> Modules.baritone(BARITONE.enabled);
        SKIN.get("name").onCommit = () -> Skin.apply(SKIN.txt("name"));
        SKIN.onChange = () -> Skin.apply(SKIN.txt("name"));
    }
}
