package pl.zolteniebiosa;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class Modules {
    private static int tickCounter = 0;

    public static void tick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;
        tickCounter++;

        // NOC (tylko wizualnie po stronie klienta)
        if (Config.night) {
            mc.world.getLevelProperties().setTimeOfDay(18000L);
        }

        // AUTO LEAVE - tylko gracze
        if (Config.autoLeave && mc.getNetworkHandler() != null) {
            for (PlayerEntity p : mc.world.getPlayers()) {
                if (p == mc.player) continue;
                if (p.distanceTo(mc.player) <= Config.leaveDist) {
                    mc.getNetworkHandler().getConnection().disconnect(
                            Text.literal("ZolteNiebiosa CLIENT: AutoLeave - gracz " + p.getName().getString() + " w poblizu"));
                    Config.autoLeave = false;
                    return;
                }
            }
        }

        // BLOCK ESP - skan co sekunde
        if (Config.blockEsp && tickCounter % 20 == 0) {
            Esp.scan(mc);
        }
    }

    // ===== ANTY KOSTKA =====
    public static void saveInventory(MinecraftClient mc) {
        if (mc.player == null) return;
        Item[] arr = new Item[46];
        for (int id = 9; id <= 44; id++) {
            arr[id] = mc.player.playerScreenHandler.getSlot(id).getStack().getItem();
        }
        Config.savedInv = arr;
        mc.player.sendMessage(Text.literal("§e[ZolteNiebiosa] §aZapisano uklad EQ!"), false);
    }

    public static void restoreInventory(MinecraftClient mc) {
        if (mc.player == null || mc.interactionManager == null) return;
        if (Config.savedInv == null) {
            mc.player.sendMessage(Text.literal("§e[ZolteNiebiosa] §cNajpierw zapisz uklad EQ w menu (N)!"), false);
            return;
        }
        var handler = mc.player.playerScreenHandler;
        Item[] saved = Config.savedInv;
        for (int id = 9; id <= 44; id++) {
            Item target = saved[id];
            if (handler.getSlot(id).getStack().getItem() == target) continue;
            if (target == net.minecraft.item.Items.AIR) continue;
            for (int j = 9; j <= 44; j++) {
                if (j == id) continue;
                if (handler.getSlot(j).getStack().getItem() != target) continue;
                if (saved[j] == target) continue; // ten juz stoi dobrze
                mc.interactionManager.clickSlot(0, j, 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(0, id, 0, SlotActionType.PICKUP, mc.player);
                if (!handler.getCursorStack().isEmpty()) {
                    mc.interactionManager.clickSlot(0, j, 0, SlotActionType.PICKUP, mc.player);
                }
                break;
            }
        }
        mc.player.sendMessage(Text.literal("§e[ZolteNiebiosa] §aPrzywrocono EQ!"), false);
    }
}
