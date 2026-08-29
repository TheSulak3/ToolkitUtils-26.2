package net.fabricmc.modverify.network;

import net.fabricmc.modverify.ModVerify;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record VanishPayload(boolean hide) implements CustomPacketPayload {
    public static final Type<VanishPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ModVerify.MOD_ID, "vanish"));

    public static final StreamCodec<RegistryFriendlyByteBuf, VanishPayload> CODEC =
            StreamCodec.composite(ByteBufCodecs.BOOL, VanishPayload::hide, VanishPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
