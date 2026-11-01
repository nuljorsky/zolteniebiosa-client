package pl.zolteniebiosa;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// "Zolty Rynek": automatyczne kupowanie z menu rynku serwera (uniwersalne, ustawiasz pod swoj serwer).
public class Market {
    private static long lastClick = 0, lastRefresh = 0, lastOpen = 0;
    public static String status = "";
    private static final Pattern NUM = Pattern.compile("(\\d[\\d .,]*)\\s*([kKmM]?)");

    private static String loreOf(ItemStack st) {
        StringBuilder sb = new StringBuilder();
        LoreComponent lore = st.get(DataComponentTypes.LORE);
        if (lore != null) {
            for (Text t : lore.lines()) sb.append(t.getString()).append(' ');
        }
        return sb.toString();
    }

    private static double parse(String s) {
        Matcher m = NUM.matcher(s);
        if (!m.find()) return -1;
        String n = m.group(1).replace(" ", "");
        if (n.matches("\\d+[.,]\\d{3}")) n = n.replace(".", "").replace(",", "");
        else n = n.replace(',', '.');
        try {
            double v = Double.parseDouble(n);
            String suf = m.group(2).toLowerCase();
            if (suf.equals("k")) v *= 1_000;
            if (suf.equals("m")) v *= 1_000_000;
            return v;
        } catch (Exception e) {
            return -1;
        }
    }

    private static double priceOf(String lore) {
        String l = lore.toLowerCase();
        int idx = -1;
        for (String k : new String[]{"cena", "price", "koszt", "$", "zł", "pln"}) {
            int i = l.indexOf(k);
            if (i >= 0 && (idx < 0 || i < idx)) idx = i;
        }
        return parse(idx >= 0 ? l.substring(idx) : l);
    }

    public static void tick(MinecraftClient mc) {
        Mod m = Mods.MARKET;
        if (!m.enabled || mc.player == null || mc.interactionManager == null) return;
        long now = System.currentTimeMillis();

        if (mc.currentScreen instanceof GenericContainerScreen gs) {
            GenericContainerScreenHandler h = gs.getScreenHandler();
            int size = h.getRows() * 9;
            int delay = m.n("delay");
            if (now - lastClick < delay) return;

            String[] confirm = m.txt("confirm").toLowerCase().split(",");
            for (int i = 0; i < size; i++) {
                ItemStack st = h.slots.get(i).getStack();
                if (st.isEmpty()) continue;
                String name = st.getName().getString().toLowerCase();
                for (String c : confirm) {
                    c = c.trim();
                    if (!c.isEmpty() && name.contains(c)) {
                        mc.interactionManager.clickSlot(h.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                        lastClick = now;
                        status = "Potwierdzam: " + st.getName().getString();
                        return;
                    }
                }
            }

            String filter = m.txt("filter").trim().toLowerCase();
            double max = parse(m.txt("maxprice").trim().isEmpty() ? "0" : m.txt("maxprice"));
            for (int i = 0; i < size; i++) {
                ItemStack st = h.slots.get(i).getStack();
                if (st.isEmpty()) continue;
                String lore = loreOf(st);
                String hay = (st.getName().getString() + " " + lore).toLowerCase();
                if (!filter.isEmpty() && !hay.contains(filter)) continue;
                if (max > 0) {
                    double pr = priceOf(lore);
                    if (pr <= 0 || pr > max) continue;
                }
                mc.interactionManager.clickSlot(h.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                lastClick = now;
                status = "Kupuję: " + st.getName().getString();
                return;
            }

            int rs = m.n("refresh");
            if (rs >= 0 && rs < size && now - lastRefresh >= Math.max(100, delay)) {
                mc.interactionManager.clickSlot(h.syncId, rs, 0, SlotActionType.PICKUP, mc.player);
                lastRefresh = now;
                lastClick = now;
                status = "Odświeżam rynek";
            }
        } else if (m.b("autoopen") && mc.currentScreen == null && now - lastOpen > m.n("reopen") * 1000L) {
            String cmd = m.txt("command").trim();
            if (cmd.startsWith("/")) cmd = cmd.substring(1);
            if (!cmd.isEmpty() && mc.player.networkHandler != null) {
                mc.player.networkHandler.sendChatCommand(cmd);
                lastOpen = now;
                status = "Otwieram /" + cmd;
            }
        }
    }
}
