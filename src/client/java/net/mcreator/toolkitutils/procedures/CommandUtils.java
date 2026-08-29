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
    private static final Logger LOG = LoggerFactory.getLogger("toolkit_utils/net");
    private CommandUtils() {}

    public static LocalPlayer player() { return Minecraft.getInstance().player; }

    public static void send(String command) {
        LocalPlayer p = player();
        if (p == null) { LOG.warn("[toolkit_utils] send() aborted: no local player"); return; }
        if (Minecraft.getInstance().getConnection() == null) {
            LOG.warn("[toolkit_utils] send() aborted: no ClientPacketListener (not connected)"); return;
        }
        if (command == null || command.isBlank()) {
            LOG.warn("[toolkit_utils] send() aborted: empty command"); return;
        }
        if (command.startsWith("/")) command = command.substring(1);

        boolean ok = ClientPlayNetworking.canSend(ExecuteCommandPayload.TYPE);
        if (!ok) {
            LOG.error("[toolkit_utils] canSend=false — server didn't register payload. Mod missing/mismatched server-side.");
            return;
        }
        LOG.info("[toolkit_utils] -> server: {}", command);
        try {
            ClientPlayNetworking.send(new ExecuteCommandPayload(command));
        } catch (Exception e) {
            LOG.error("[toolkit_utils] send() threw for '{}'", command, e);
        }
    }

    public static String itemId(ItemStack stack) {
        Identifier id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id == null ? "minecraft:air" : id.toString();
    }
}
