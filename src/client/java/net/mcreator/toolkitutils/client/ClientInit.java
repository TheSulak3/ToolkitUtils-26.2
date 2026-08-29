package net.mcreator.toolkitutils.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.mcreator.toolkitutils.client.gui.CheatMenuScreen;
import net.mcreator.toolkitutils.client.gui.LoginScreen;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClientInit implements ClientModInitializer {
    private static final Logger LOG = LoggerFactory.getLogger("toolkit_utils/client");
    public static boolean AUTHENTICATED = false;
    private static boolean comboWasDown;

    @Override
    public void onInitializeClient() {
        LOG.debug("client init");
        SpyClient.registerReceivers();

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
                LOG.debug("combo triggered, auth={}", AUTHENTICATED);
                client.gui.setScreen(AUTHENTICATED ? new CheatMenuScreen() : new LoginScreen());
            }
            comboWasDown = combo;
        });
    }
}
