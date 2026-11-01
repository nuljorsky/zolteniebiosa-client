package pl.zolteniebiosa;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * Auto-wystawianie trzymanego przedmiotu na rynku serwera (np. /ah).
 * UWAGA: to NIE skanuje prawdziwych cen innych graczy (kazdy serwer ma inny GUI rynku,
 * wiec nie da sie tego zrobic ogolnie dla kazdego serwera). Ty ustawiasz cene bazowa
 * (Cena bazowa / Domyslna) i o ile % lub ile monet ma byc nizsza - to jest "podbijanie" ceny.
 * Dopasuj komende "listcmd" do SWOJEGO serwera (np. "ah sell {cena}", "rynek wystaw {cena}").
 */
public final class AutoMarket {
    private static String status = "";
    private static long cooldownUntil = 0;

    private AutoMarket() { }

    public static String status() {
        return status.isEmpty() ? Lang.t("am.idle") : status;
    }

    /** Oblicza cene na podstawie trybu (% / stala kwota), ilosci sztuk i ceny minimalnej. */
    private static long computePrice(int stackCount) {
        Mod m = Mods.AUTOMARKET;
        long base;
        try {
            base = Long.parseLong(m.txt("fallback").trim());
        } catch (Exception e) {
            base = 0;
        }
        if (base <= 0) {
            status = Lang.t("am.nofallback");
            return -1;
        }
        long price;
        if (m.mode("mode") == 0) { // procent
            double pct = m.n("amount") / 100.0;
            price = Math.round(base * (1.0 - pct));
        } else { // stala kwota
            price = base - m.n("amount");
        }
        long floor;
        try {
            floor = Long.parseLong(m.txt("floor").trim());
        } catch (Exception e) {
            floor = 1;
        }
        if (price < floor) price = floor;
        if (m.b("perunit")) price = price * Math.max(1, stackCount);
        return price;
    }

    /** Usuwa ewentualny poczatkowy "/" - sendChatCommand sam go dokleja na poziomie pakietu. */
    private static String stripSlash(String cmd) {
        cmd = cmd.trim();
        return cmd.startsWith("/") ? cmd.substring(1) : cmd;
    }

    /** Wystawia przedmiot trzymany aktualnie w rece. Wolaj przyciskiem "Start" w menu. */
    public static void start() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (System.currentTimeMillis() < cooldownUntil) {
            status = Lang.t("am.wait");
            return;
        }
        ItemStack held = mc.player.getMainHandStack();
        if (held.isEmpty()) {
            status = Lang.t("am.empty");
            return;
        }
        long price = computePrice(held.getCount());
        if (price < 0) return;

        String openCmd = Mods.AUTOMARKET.txt("opencmd");
        String listCmd = Mods.AUTOMARKET.txt("listcmd")
                .replace("{cena}", String.valueOf(price))
                .replace("{price}", String.valueOf(price))
                .replace("{item}", held.getItem().toString())
                .replace("{ilosc}", String.valueOf(held.getCount()))
                .replace("{count}", String.valueOf(held.getCount()));

        if (!openCmd.isBlank()) {
            mc.player.networkHandler.sendChatCommand(stripSlash(openCmd));
        }
        mc.player.networkHandler.sendChatCommand(stripSlash(listCmd));

        cooldownUntil = System.currentTimeMillis() + 1500L;
        status = Lang.t("am.listed") + " " + held.getItem().toString().replace("minecraft:", "") + " -> " + price;
        mc.player.sendMessage(Text.literal("§e[ZolteNiebiosa] §7" + status), false);
    }
}
