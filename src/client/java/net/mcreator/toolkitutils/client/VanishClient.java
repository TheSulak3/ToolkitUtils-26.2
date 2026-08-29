package net.mcreator.toolkitutils.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mcreator.toolkitutils.network.VanishPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VanishClient {
    private static final Logger LOG = LoggerFactory.getLogger("mod_verify/net");
    private static boolean hidden;

    private VanishClient() {}

    public static void toggle() {
        boolean next = !hidden;
        if (!ClientPlayNetworking.canSend(VanishPayload.TYPE)) {
            LOG.error("vanish canSend=false");
            return;
        }
        LOG.debug("-> server: vanish {}", next);
        try {
            ClientPlayNetworking.send(new VanishPayload(next));
            hidden = next;
        } catch (Exception e) {
            LOG.error("vanish send() threw", e);
        }
    }

    public static boolean hidden() { return hidden; }
}
