package net.fabricmc.modverify.network;

import net.fabricmc.modverify.ModVerify;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SeedResultPayload(long seed, String worldName) implements CustomPacketPayload {
    public static final Type<SeedResultPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ModVerify.MOD_ID, "seed_result"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SeedResultPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG,   SeedResultPayload::seed,
                    ByteBufCodecs.STRING_UTF8, SeedResultPayload::worldName,
                    SeedResultPayload::new);
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
