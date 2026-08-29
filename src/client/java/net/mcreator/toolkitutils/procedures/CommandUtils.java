package net.mcreator.toolkitutils.procedures;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mcreator.toolkitutils.network.ExecuteCommandPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommandUtils {
    private static final Logger LOG = LoggerFactory.getLogger("toolkit_utils/client");
    private CommandUtils() {}

    public static LocalPlayer player() { return Minecraft.getInstance().player; }

    public static void send(String command) {
        LocalPlayer p = player();
        if (p == null) { LOG.info("send() no player"); return; }
        if (Minecraft.getInstance().getConnection() == null) { LOG.info("send() no connection"); return; }
        if (command.startsWith("/")) command = command.substring(1);

        boolean ok = ClientPlayNetworking.canSend(ExecuteCommandPayload.TYPE);
        LOG.info("send '{}' canSend={}", command, ok);
        if (!ok) {
            LOG.warn("Server didn't register payload — mod missing server-side or handshake failed.");
            return;
        }
        ClientPlayNetworking.send(new ExecuteCommandPayload(command));
    }

    public static String itemId(ItemStack stack) {
        Identifier id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id.toString();
    }
}
