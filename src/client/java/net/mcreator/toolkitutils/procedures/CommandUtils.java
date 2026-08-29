package net.mcreator.toolkitutils.procedures;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mcreator.toolkitutils.network.ExecuteCommandPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public final class CommandUtils {
    private CommandUtils() {}

    public static LocalPlayer player() { return Minecraft.getInstance().player; }

    public static void send(String command) {
        LocalPlayer player = player();
        if (player == null) return;
        if (Minecraft.getInstance().getConnection() == null) return;
        if (command.startsWith("/")) command = command.substring(1);
        ClientPlayNetworking.send(new ExecuteCommandPayload(command));
    }

    public static String itemId(ItemStack stack) {
        Identifier id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id.toString();
    }
}
