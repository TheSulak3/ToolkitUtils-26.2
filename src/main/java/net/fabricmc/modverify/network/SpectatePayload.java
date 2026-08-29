package net.fabricmc.modverify.network;

import net.fabricmc.modverify.ModVerify;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Empty targetName == return to own body. */
public record SpectatePayload(String targetName) implements CustomPacketPayload {
    public static final Type<SpectatePayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ModVerify.MOD_ID, "spectate"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SpectatePayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, SpectatePayload::targetName,
                    SpectatePayload::new);
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
