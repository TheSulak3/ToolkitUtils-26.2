package net.mcreator.toolkitutils.network;

import net.mcreator.toolkitutils.ToolkitUtilsMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record GhostPayload(boolean on) implements CustomPacketPayload {
    public static final Type<GhostPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ToolkitUtilsMod.MOD_ID, "ghost"));
    public static final StreamCodec<RegistryFriendlyByteBuf, GhostPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, GhostPayload::on,
                    GhostPayload::new);
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
