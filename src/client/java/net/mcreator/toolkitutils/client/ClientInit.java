package net.mcreator.toolkitutils.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.mcreator.toolkitutils.client.gui.CheatMenuScreen;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClientInit implements ClientModInitializer {
    private static final Logger LOG = LoggerFactory.getLogger("toolkit_utils/client");
    private static boolean comboWasDown;

    @Override
    public void onInitializeClient() {
        LOG.info("[toolkit_utils] client init: registering combo listener (Ctrl+Alt+Space+MMB)");
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            long win = client.getWindow().handle();
            boolean mouse3 = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS;
            boolean ctrl  = GLFW.glfwGetKey(win, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
                         || GLFW.glfwGetKey(win, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
            boolean alt   = GLFW.glfwGetKey(win, GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS
                         || GLFW.glfwGetKey(win, GLFW.GLFW_KEY_RIGHT_ALT) == GLFW.GLFW_PRESS;
            boolean space = GLFW.glfwGetKey(win, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS;
            boolean combo = mouse3 && ctrl && alt && space;
            if (combo && !comboWasDown && client.gui.screen() == null) {
                LOG.info("[toolkit_utils] combo triggered, opening CheatMenuScreen");
                client.gui.setScreen(new CheatMenuScreen());
            }
            comboWasDown = combo;
        });
    }
}
