package pl.zolteniebiosa;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;
import java.util.List;

public class Modules {
    private static int ticks = 0;
    private static boolean fbApplied = false;
    public static Item[] saved = null;
    public static volatile boolean dirty = false;

    public static void tick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;
        ticks++;

        if (Mods.NIGHT.enabled) {
            mc.world.getLevelProperties().setTimeOfDay(18000L);
        }

        if (Mods.FULLBRIGHT.enabled) {
            if (ticks % 100 == 1) {
                mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 400, 0, false, false, false));
                fbApplied = true;
            }
        } else if (fbApplied) {
            mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
            fbApplied = false;
        }

        if (Mods.SPRINT.enabled && mc.options.forwardKey.isPressed() && !mc.player.isSneaking()
                && !mc.player.horizontalCollision && mc.player.getHungerManager().getFoodLevel() > 6) {
            mc.player.setSprinting(true);
        }

        Net.tick(mc);

        if (Mods.AUTOLEAVE.enabled && mc.getNetworkHandler() != null) {
            int dist = Mods.AUTOLEAVE.n("dist");
            for (PlayerEntity p : mc.world.getPlayers()) {
                if (p == mc.player) continue;
                if (p.distanceTo(mc.player) <= dist) {
                    mc.getNetworkHandler().getConnection().disconnect(
                            Text.literal("ZolteNiebiosa CLIENT: AutoLeave - gracz " + p.getName().getString() + " w poblizu"));
                    Mods.AUTOLEAVE.enabled = false;
                    return;
                }
            }
        }

        if (dirty || ticks % 40 == 0) {
            dirty = false;
            Esp.espHits = Mods.BLOCKESP.enabled ? Esp.scan(Mods.BLOCKESP, mc) : List.of();
            Esp.xrayHits = Mods.XRAY.enabled ? Esp.scan(Mods.XRAY, mc) : List.of();
        }

        if (ticks % 40 == 5 && Mods.SKIN.enabled) {
            Skin.get(Mods.SKIN.txt("name"));
        }
    }

    // ===== trafienie =====
    public static void onAttack(Entity e) {
        if (!Mods.HITSOUND.enabled) return;
        int tg = Mods.HITSOUND.mode("target");
        boolean isP = e instanceof PlayerEntity;
        if (tg == 0 && !isP) return;
        if (tg == 1 && isP) return;
        float vol = Mods.HITSOUND.n("volume") / 100f;
        int s = Mods.HITSOUND.mode("sound");
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (s == 0) mc.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, vol, 1.7f);
        else if (s == 1) mc.player.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, vol, 1.3f);
        else mc.player.playSound(SoundEvents.ENTITY_CHICKEN_EGG, vol, 1.6f);
    }

    // ===== zoom =====
    private static double zoomCur = 1.0;

    /** Wspolczynnik przyblizenia (1.0 = brak). Wygladzany, wolany co klatke z mixina GameRenderer. */
    public static double zoomFactor() {
        double target = 1.0;
        if (Mods.ZOOM.enabled && ZolteClient.ZOOM != null && ZolteClient.ZOOM.isPressed()
                && MinecraftClient.getInstance().currentScreen == null) {
            target = Mods.ZOOM.n("factor");
        }
        zoomCur += (target - zoomCur) * 0.35;
        if (Math.abs(zoomCur - target) < 0.01) zoomCur = target;
        return zoomCur;
    }

    // ===== baritone =====
    private static boolean hasBaritone() {
        FabricLoader fl = FabricLoader.getInstance();
        return fl.isModLoaded("baritone") || fl.isModLoaded("baritone-fabric") || fl.isModLoaded("baritone_unoptimized");
    }

    private static boolean baritoneTuned = false;

    /** Ustawienia Baritone pod spokojniejszy, mniej "szarpiący" ruch - wysylane raz, przy pierwszym wlaczeniu. */
    private static void tuneBaritone(MinecraftClient mc) {
        if (baritoneTuned) return;
        baritoneTuned = true;
        String[] cmds = {
                "#set freeLook true",          // Baritone chodzi bez obracania Twojej kamery - to najczesciej powoduje "szarpanie"
                "#set elytraFlightInLoadedOnly true",
                "#set allowSprint true",
                "#set walkOnLava false",
                "#set allowInventory false",
                "#set pauseMiningForFallingBlocks false",
                "#set mineGoalUpdateInterval 5",
                "#set blockReachDistance 4.2",
                "#set randomLooking 0",        // bez losowego, "nerwowego" rozgladania sie po okolicy
                "#set followRadius 0",
        };
        for (String c : cmds) mc.player.networkHandler.sendChatMessage(c);
    }

    public static void baritone(boolean on) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (!hasBaritone()) {
            mc.player.sendMessage(Text.literal("§e[ZolteNiebiosa] §cBaritone nie jest wbudowany w ten build - wrzuć moda Baritone do folderu mods (albo zbuduj przez GitHub Actions, które go dołącza)."), false);
            Mods.BARITONE.enabled = false;
            return;
        }
        String block = Mods.BARITONE.txt("block").trim();
        if (on) {
            if (block.isEmpty()) {
                mc.player.sendMessage(Text.literal("§e[ZolteNiebiosa] §cWpisz nazwę bloku w ustawieniach Baritone."), false);
                Mods.BARITONE.enabled = false;
                return;
            }
            tuneBaritone(mc);
            mc.player.networkHandler.sendChatMessage("#mine " + block);
            mc.player.sendMessage(Text.literal("§e[ZolteNiebiosa] §7Baritone wystartował - zatrzymasz przełącznikiem w menu (N)."), false);
        } else {
            mc.player.networkHandler.sendChatMessage("#stop");
            mc.player.networkHandler.sendChatMessage("#cancel");
        }
    }

    // "#baritone diamond_ore on" / "#baritone off"
    public static boolean chatAlias(String msg) {
        String m = msg.trim();
        if (!m.toLowerCase().startsWith("#baritone")) return true;
        String[] p = m.split("\\s+");
        if (p.length < 2) return false;
        String last = p[p.length - 1].toLowerCase();
        if (last.equals("off")) {
            Mods.BARITONE.enabled = false;
            baritone(false);
        } else if (last.equals("on") && p.length >= 3) {
            Mods.BARITONE.get("block").text = p[1];
            Mods.BARITONE.enabled = true;
            baritone(true);
        }
        return false;
    }

    // ===== anty kostka =====
    public static void saveInventory(MinecraftClient mc) {
        if (mc.player == null) return;
        Item[] arr = new Item[46];
        for (int id = 9; id <= 44; id++) {
            arr[id] = mc.player.playerScreenHandler.getSlot(id).getStack().getItem();
        }
        saved = arr;
        mc.player.sendMessage(Text.literal("§e[ZolteNiebiosa] §aZapisano układ EQ!"), false);
    }

    public static void restoreInventory(MinecraftClient mc) {
        if (mc.player == null || mc.interactionManager == null) return;
        if (saved == null) {
            mc.player.sendMessage(Text.literal("§e[ZolteNiebiosa] §cNajpierw zapisz układ EQ w menu (N)!"), false);
            return;
        }
        var handler = mc.player.playerScreenHandler;
        for (int id = 9; id <= 44; id++) {
            Item target = saved[id];
            if (handler.getSlot(id).getStack().getItem() == target) continue;
            if (target == Items.AIR) continue;
            for (int j = 9; j <= 44; j++) {
                if (j == id) continue;
                if (handler.getSlot(j).getStack().getItem() != target) continue;
                if (saved[j] == target) continue;
                mc.interactionManager.clickSlot(0, j, 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(0, id, 0, SlotActionType.PICKUP, mc.player);
                if (!handler.getCursorStack().isEmpty()) {
                    mc.interactionManager.clickSlot(0, j, 0, SlotActionType.PICKUP, mc.player);
                }
                break;
            }
        }
        mc.player.sendMessage(Text.literal("§e[ZolteNiebiosa] §aPrzywrócono EQ!"), false);
    }
}
