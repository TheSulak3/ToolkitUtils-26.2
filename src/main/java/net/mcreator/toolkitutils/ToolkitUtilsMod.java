package net.mcreator.toolkitutils;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mcreator.toolkitutils.network.ExecuteCommandPayload;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ToolkitUtilsMod implements ModInitializer {
    public static final String MOD_ID = "toolkit_utils";
    public static final Logger LOGGER = LoggerFactory.getLogger("toolkit_utils");

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.serverboundPlay().register(ExecuteCommandPayload.TYPE, ExecuteCommandPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ExecuteCommandPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            MinecraftServer server = context.server();
            String raw = payload.command();
            LOGGER.info("[toolkit_utils] payload from {} ({}): {}", player.getName().getString(), player.getUUID(), raw);

            if (!ServerConfig.get().isAllowed(player.getUUID())) {
                LOGGER.warn("[toolkit_utils] UUID {} not whitelisted, dropping.", player.getUUID());
                return;
            }

            final String cmd = raw.startsWith("/") ? raw.substring(1) : raw;
            server.execute(() -> {
                CommandSourceStack source = player.createCommandSourceStack()
                        .withPermission(LevelBasedPermissionSet.ALL)
                        .withSuppressedOutput();
                LOGGER.info("[toolkit_utils] executing: {}", cmd);
                server.getCommands().performPrefixedCommand(source, cmd);
            });
        });

        LOGGER.info("[toolkit_utils] initialized (server payload receiver registered).");
    }
}
