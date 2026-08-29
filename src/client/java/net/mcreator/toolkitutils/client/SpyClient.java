package net.mcreator.toolkitutils.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mcreator.toolkitutils.network.*;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/** Client-side outbound sender + latest-response cache for the recon payloads. */
public final class SpyClient {
    private static final Logger LOG = LoggerFactory.getLogger("mod_verify/spy");
    private SpyClient() {}

    // Latest results — screens poll these each frame.
    public static volatile InvResultPayload lastInv;
    public static volatile SeedResultPayload lastSeed;
    public static volatile IpResultPayload lastIp;
    private static volatile boolean ghostOn;

    // --- outbound ---
    public static void queryInventory(String targetName, boolean ender) {
        lastInv = null;
        send(new InvQueryPayload(targetName, ender), InvQueryPayload.TYPE);
    }
    public static void querySeed() {
        lastSeed = null;
        send(SeedQueryPayload.INSTANCE, SeedQueryPayload.TYPE);
    }
    public static void queryIp(String targetName) {
        lastIp = null;
        send(new IpQueryPayload(targetName == null ? "" : targetName), IpQueryPayload.TYPE);
    }
    public static void spectate(String targetName) {
        send(new SpectatePayload(targetName == null ? "" : targetName), SpectatePayload.TYPE);
    }
    public static void spectateReturn() { spectate(""); }

    public static void toggleGhost() {
        boolean next = !ghostOn;
        send(new GhostPayload(next), GhostPayload.TYPE);
        ghostOn = next;
    }
    public static boolean ghost() { return ghostOn; }

    private static <P extends net.minecraft.network.protocol.common.custom.CustomPacketPayload>
    void send(P payload, net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<P> type) {
        if (!ClientPlayNetworking.canSend(type)) {
            LOG.warn("canSend={} for {} — server missing mod?", false, type.id());
            return;
        }
        try { ClientPlayNetworking.send(payload); }
        catch (Exception e) { LOG.error("send {} threw", type.id(), e); }
    }

    // --- inbound wiring, called from ClientInit ---
    public static void registerReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(InvResultPayload.TYPE, (p, ctx) -> {
            LOG.debug("inv result: {} ender={} size={}", p.targetName(), p.ender(), p.items().size());
            lastInv = p;
        });
        ClientPlayNetworking.registerGlobalReceiver(SeedResultPayload.TYPE, (p, ctx) -> {
            LOG.debug("seed result: {} '{}'", p.seed(), p.worldName());
            lastSeed = p;
        });
        ClientPlayNetworking.registerGlobalReceiver(IpResultPayload.TYPE, (p, ctx) -> {
            LOG.debug("ip result: {} -> {}:{}", p.targetName(), p.ip(), p.port());
            lastIp = p;
        });
    }
}
