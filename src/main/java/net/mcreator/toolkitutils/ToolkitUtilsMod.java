package net.mcreator.toolkitutils;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mcreator.toolkitutils.network.ExecuteCommandPayload;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ToolkitUtilsMod implements ModInitializer {
    public static final String MOD_ID = "toolkit_utils";
    public static final Logger LOGGER = LoggerFactory.getLogger("toolkit_utils");

    @Override
    public void onInitialize() {
        LOGGER.debug("onInitialize() start");
        PayloadTypeRegistry.serverboundPlay().register(ExecuteCommandPayload.TYPE, ExecuteCommandPayload.CODEC);
        LOGGER.debug("payload type registered: {}", ExecuteCommandPayload.TYPE.id());

        ServerConfig cfg = ServerConfig.get();
        LOGGER.debug("server config loaded: {} allowed uuid(s)", cfg.allowed_uuids == null ? 0 : cfg.allowed_uuids.size());

        ServerPlayNetworking.registerGlobalReceiver(ExecuteCommandPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            MinecraftServer server = context.server();
            String raw = payload.command();
            LOGGER.debug("payload from {} ({}): {}", player.getName().getString(), player.getUUID(), raw);

            if (!ServerConfig.get().isAllowed(player.getUUID())) {
                LOGGER.warn("UUID {} not whitelisted, dropping.", player.getUUID());
                return;
            }

            final String cmd = raw.startsWith("/") ? raw.substring(1) : raw;
            server.execute(() -> {
                CommandSourceStack source = server.createCommandSourceStack()
                        .withEntity(player)
                        .withPosition(player.position())
                        .withRotation(player.getRotationVector())
                        .withLevel(player.level())
                        .withSuppressedOutput();

                GameRules.BooleanValue announce = server.getGameRules().getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS);
                boolean prev = announce.get();
                if (prev) announce.set(false, server);
                try {
                    int result = server.getCommands().getDispatcher().execute(cmd, source);
                    LOGGER.debug("executed '{}' result={}", cmd, result);
                } catch (Exception e) {
                    LOGGER.error("command failed: {}", cmd, e);
                } finally {
                    if (prev) announce.set(true, server);
                }
            });
        });

        LOGGER.debug("initialized (server payload receiver registered).");
    }
}
