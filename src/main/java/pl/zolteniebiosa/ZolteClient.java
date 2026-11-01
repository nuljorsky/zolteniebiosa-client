package pl.zolteniebiosa;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.ActionResult;
import org.lwjgl.glfw.GLFW;

public class ZolteClient implements ClientModInitializer {
    public static KeyBinding MENU;
    public static KeyBinding RESTORE;
    public static KeyBinding ZOOM;
    private static int tickCount = 0;

    @Override
    public void onInitializeClient() {
        Config.load();

        MENU = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zolteniebiosa.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N, "key.categories.zolteniebiosa"));
        RESTORE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zolteniebiosa.restore", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "key.categories.zolteniebiosa"));
        ZOOM = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zolteniebiosa.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "key.categories.zolteniebiosa"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (MENU.wasPressed()) {
                client.setScreen(new MenuScreen());
            }
            while (RESTORE.wasPressed()) {
                Modules.restoreInventory(client);
            }
            Modules.tick(client);
            PvpHud.tick(client);
            tickCount++;
            if (tickCount % 20 == 0) updateDiscord(client);
            if (tickCount % 600 == 0) Config.saveIfChanged();
            if (tickCount % 200 == 1) {
                try {
                    client.getWindow().setTitle(Branding.NAME + " 1.21.4");
                } catch (Throwable ignored) { }
            }
        });

        // zapis ustawien przy wyjsciu z gry / z serwera / zamknieciu okna
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            Music.stop();
            Config.save();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> Config.save());
        Runtime.getRuntime().addShutdownHook(new Thread(Config::save, "ZTN-SaveConfig"));

        DiscordRpc.init();

        Esp.init();
        WorldRenderEvents.AFTER_ENTITIES.register(Pets::render);
        HudRenderCallback.EVENT.register((ctx, tc) -> {
            Hud.render(ctx, tc);
            NameTags.render(ctx, tc);
            Overlays.render(ctx, tc);
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (world.isClient()) {
                Modules.onAttack(entity);
                PvpHud.onHit(entity);
            }
            return ActionResult.PASS;
        });
        ClientSendMessageEvents.ALLOW_CHAT.register(Modules::chatAlias);
    }

    private static void updateDiscord(MinecraftClient mc) {
        String details = Branding.NAME + " 1.21.4";
        String state;
        if (mc.world == null) {
            state = "W menu głównym";
        } else if (mc.isInSingleplayer()) {
            state = "Gra solo";
        } else {
            ServerInfo si = mc.getCurrentServerEntry();
            state = (si != null && Mods.DISCORD.b("showserver")) ? "Serwer: " + si.address : "Gra na serwerze";
        }
        DiscordRpc.set(Mods.DISCORD.enabled, Mods.DISCORD.txt("appid"), details, state);
    }
}
