package net.mcreator.toolkitutils.network;

import net.mcreator.toolkitutils.ToolkitUtilsMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SeedQueryPayload() implements CustomPacketPayload {
    public static final SeedQueryPayload INSTANCE = new SeedQueryPayload();
    public static final Type<SeedQueryPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ToolkitUtilsMod.MOD_ID, "seed_query"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SeedQueryPayload> CODEC =
            StreamCodec.unit(INSTANCE);
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
