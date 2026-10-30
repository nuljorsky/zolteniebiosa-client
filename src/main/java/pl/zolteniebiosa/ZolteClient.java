package pl.zolteniebiosa;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ZolteClient implements ClientModInitializer {
    public static KeyBinding MENU;
    public static KeyBinding RESTORE;

    @Override
    public void onInitializeClient() {
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
    }
}
