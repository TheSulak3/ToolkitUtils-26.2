package net.mcreator.toolkitutils.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.mcreator.toolkitutils.client.gui.CommandScreenScreen;
import net.mcreator.toolkitutils.client.gui.LoginScreen;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public final class ClientInit implements ClientModInitializer {
    public static boolean AUTHENTICATED = false;
    private static boolean comboWasDown = false;

    @Override
    public void onInitializeClient() {
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if (ToolkitConfig.get().matchesTrigger(message)) {
                openMenu();
                return false;
            }
            return true;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            long window = client.getWindow().handle();
            boolean mouse3Down = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS;
            boolean ctrlDown = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
            boolean altDown = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_ALT) == GLFW.GLFW_PRESS;
            boolean spaceDown = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS;
            boolean combo = mouse3Down && ctrlDown && altDown && spaceDown;
            if (combo && !comboWasDown && client.gui.screen() == null) {
                client.gui.setScreen(new LoginScreen(null));
            }
            comboWasDown = combo;
        });
    }

    private static void openMenu() {
        Minecraft client = Minecraft.getInstance();
        if (client.gui.screen() == null) {
            client.gui.setScreen(AUTHENTICATED ? new CommandScreenScreen() : new LoginScreen(null));
        }
    }
}
