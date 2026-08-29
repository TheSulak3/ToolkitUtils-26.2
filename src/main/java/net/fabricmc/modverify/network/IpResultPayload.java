package net.fabricmc.modverify.network;

import net.fabricmc.modverify.ModVerify;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record IpResultPayload(String targetName, String ip, int port) implements CustomPacketPayload {
    public static final Type<IpResultPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ModVerify.MOD_ID, "ip_result"));
    public static final StreamCodec<RegistryFriendlyByteBuf, IpResultPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, IpResultPayload::targetName,
                    ByteBufCodecs.STRING_UTF8, IpResultPayload::ip,
                    ByteBufCodecs.VAR_INT,     IpResultPayload::port,
                    IpResultPayload::new);
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
