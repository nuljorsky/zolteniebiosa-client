package pl.zolteniebiosa;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.List;

public class Modules {
    private static int ticks = 0;
    private static boolean fbApplied = false;
    private static boolean zooming = false;
    private static int oldFov = 70;
    public static Item[] saved = null;
    public static volatile boolean dirty = false;

    public static void tick(MinecraftClient mc) {
        Rpc.tick(mc);
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

        // zoom (klawisz C)
        boolean zk = ZolteClient.ZOOM != null && ZolteClient.ZOOM.isPressed() && mc.currentScreen == null;
        if (Mods.ZOOM.enabled && zk) {
            if (!zooming) {
                oldFov = mc.options.getFov().getValue();
                zooming = true;
            }
            mc.options.getFov().setValue(Mods.ZOOM.n("fov"));
        } else if (zooming) {
            mc.options.getFov().setValue(oldFov);
            zooming = false;
        }

        // autosprint
        if (Mods.AUTOSPRINT.enabled && mc.currentScreen == null && mc.options.forwardKey.isPressed()
                && !mc.player.isSneaking() && !mc.player.horizontalCollision
                && mc.player.getHungerManager().getFoodLevel() > 6) {
            mc.player.setSprinting(true);
        }

        Miner.tick(mc);

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
            Skin.apply(Mods.SKIN.txt("name"));
        }
        if (ticks % 1200 == 0) Config.save();
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
        if (s == 0) play(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, vol, 1.7f);
        else if (s == 1) play(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, vol, 1.3f);
        else play(SoundEvents.ENTITY_CAT_PURREOW, vol, 1.6f);
    }

    private static void play(Object snd, float vol, float pitch) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        SoundEvent ev = null;
        if (snd instanceof SoundEvent se) ev = se;
        else if (snd instanceof RegistryEntry<?> re && re.value() instanceof SoundEvent se2) ev = se2;
        if (ev != null) mc.player.playSound(ev, vol, pitch);
    }

    // ===== wbudowany kopacz (Baritone) =====
    public static void baritone(boolean on) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) {
            Mods.BARITONE.enabled = false;
            return;
        }
        if (on) {
            if (Mods.BARITONE.txt("block").trim().isEmpty()) {
                mc.player.sendMessage(Text.literal("§e[ZolteNiebiosa] §cWpisz nazwę bloku w ustawieniach Baritone."), false);
                Mods.BARITONE.enabled = false;
                return;
            }
            Miner.start();
        } else {
            Miner.stop();
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
