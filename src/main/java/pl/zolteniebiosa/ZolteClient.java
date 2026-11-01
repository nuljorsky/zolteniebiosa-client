package pl.zolteniebiosa;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.ActionResult;
import org.lwjgl.glfw.GLFW;

public class ZolteClient implements ClientModInitializer {
    public static KeyBinding MENU;
    public static KeyBinding RESTORE;

    @Override
    public void onInitializeClient() {
        Config.load();

        MENU = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zolteniebiosa.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N, "key.categories.zolteniebiosa"));
        RESTORE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zolteniebiosa.restore", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "key.categories.zolteniebiosa"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (MENU.wasPressed()) {
                client.setScreen(new MenuScreen());
            }
            while (RESTORE.wasPressed()) {
                Modules.restoreInventory(client);
            }
            Modules.tick(client);
        });

        Esp.init();
        WorldRenderEvents.AFTER_ENTITIES.register(Pets::render);
        HudRenderCallback.EVENT.register((ctx, tc) -> {
            Weather.render(ctx);
            Crosshair.render(ctx);
            Hud2.render(ctx);
            Hud.render(ctx, tc);
            NameTags.render(ctx, tc);
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (world.isClient()) Modules.onAttack(entity);
            return ActionResult.PASS;
        });
        ClientSendMessageEvents.ALLOW_CHAT.register(Modules::chatAlias);

        // plynne logo ZTN znikajace po zaladowaniu gry
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            ScreenEvents.afterRender(screen).register((scr, ctx, mx, my, td) -> Splash.fade(ctx));
            if (screen instanceof TitleScreen) {
                Screens.getButtons(screen).add(ButtonWidget.builder(Text.literal("ZTN CLIENT"),
                        b -> client.setScreen(new MenuScreen())).dimensions(4, 4, 96, 20).build());
                Screens.getButtons(screen).add(ButtonWidget.builder(themeLabel(), b -> {
                    Setting st = Mods.THEME.get("title");
                    st.flag = !st.flag;
                    b.setMessage(themeLabel());
                    Config.save();
                }).dimensions(4, 28, 96, 20).build());
                ScreenEvents.afterRender(screen).register((scr, ctx, mx, my, td) -> {
                    if (TitleTheme.active()) TitleTheme.footer(ctx);
                });
            }
        });

        // zapis ustawien przy wyjsciu z gry / swiata
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            Config.save();
            Audio.shutdown();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> Config.save());

        Rpc.init();
        Sync.init();
        Audio.warmUp();
    }

    private static Text themeLabel() {
        return Text.literal("Motyw: " + (Mods.THEME.b("title") ? "ZTN" : "Vanilla"));
    }
}
