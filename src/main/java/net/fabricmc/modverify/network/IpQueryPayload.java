package net.fabricmc.modverify.network;

import net.fabricmc.modverify.ModVerify;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Empty targetName == server info. */
public record IpQueryPayload(String targetName) implements CustomPacketPayload {
    public static final Type<IpQueryPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ModVerify.MOD_ID, "ip_query"));
    public static final StreamCodec<RegistryFriendlyByteBuf, IpQueryPayload> CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8, IpQueryPayload::targetName, IpQueryPayload::new);
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
