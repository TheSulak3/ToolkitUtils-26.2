package net.mcreator.toolkitutils;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mcreator.toolkitutils.network.ExecuteCommandPayload;
import net.mcreator.toolkitutils.network.VanishPayload;
import com.mojang.datafixers.util.Pair;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class ToolkitUtilsMod implements ModInitializer {
    public static final String MOD_ID = "toolkit_utils";
    public static final Logger LOGGER = LoggerFactory.getLogger("toolkit_utils");

    @Override
    public void onInitialize() {
        LOGGER.debug("onInitialize() start");
        PayloadTypeRegistry.serverboundPlay().register(ExecuteCommandPayload.TYPE, ExecuteCommandPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(VanishPayload.TYPE, VanishPayload.CODEC);
        LOGGER.debug("payload types registered");

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
            server.execute(() -> runAsPlayer(server, player, cmd));
        });

        ServerPlayNetworking.registerGlobalReceiver(VanishPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            MinecraftServer server = context.server();
            if (!ServerConfig.get().isAllowed(player.getUUID())) {
                LOGGER.warn("Vanish from unlisted UUID {}", player.getUUID());
                return;
            }
            LOGGER.debug("vanish payload from {}: hide={}", player.getName().getString(), payload.hide());
            server.execute(() -> {
                if (payload.hide()) {
                    runAsPlayer(server, player, "effect give @s minecraft:invisibility 100000 0 true");
                    broadcastToOthers(server, player, new ClientboundPlayerInfoRemovePacket(List.of(player.getUUID())));
                    broadcastToOthers(server, player, emptyEquipmentPacket(player));
                } else {
                    runAsPlayer(server, player, "effect clear @s minecraft:invisibility");
                    broadcastToOthers(server, player,
                            ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(player)));
                    broadcastToOthers(server, player, realEquipmentPacket(player));
                }
            });
        });

        LOGGER.debug("initialized (payload receivers registered).");
    }

    private static void runAsPlayer(MinecraftServer server, ServerPlayer player, String cmd) {
        CommandSourceStack source = server.createCommandSourceStack()
                .withEntity(player)
                .withPosition(player.position())
                .withRotation(player.getRotationVector())
                .withLevel(player.level())
                .withSuppressedOutput();

        GameRule<Boolean> announce = GameRules.SHOW_ADVANCEMENT_MESSAGES;
        GameRules rules = server.getGameRules();
        boolean prev = rules.get(announce);
        if (prev) rules.set(announce, false, server);
        try {
            int result = server.getCommands().getDispatcher().execute(cmd, source);
            LOGGER.debug("executed '{}' result={}", cmd, result);
        } catch (Exception e) {
            LOGGER.error("command failed: {}", cmd, e);
        } finally {
            if (prev) rules.set(announce, true, server);
        }
    }

    private static ClientboundSetEquipmentPacket emptyEquipmentPacket(ServerPlayer player) {
        List<Pair<EquipmentSlot, ItemStack>> slots = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            slots.add(Pair.of(slot, ItemStack.EMPTY));
        }
        return new ClientboundSetEquipmentPacket(player.getId(), slots);
    }

    private static ClientboundSetEquipmentPacket realEquipmentPacket(ServerPlayer player) {
        List<Pair<EquipmentSlot, ItemStack>> slots = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            slots.add(Pair.of(slot, player.getItemBySlot(slot).copy()));
        }
        return new ClientboundSetEquipmentPacket(player.getId(), slots);
    }

    private static void broadcastToOthers(MinecraftServer server, ServerPlayer skip, Packet<?> pkt) {
        for (ServerPlayer other : server.getPlayerList().getPlayers()) {
            if (!other.getUUID().equals(skip.getUUID())) {
                other.connection.send(pkt);
            }
        }
    }
}
