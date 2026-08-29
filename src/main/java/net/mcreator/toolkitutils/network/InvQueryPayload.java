package net.mcreator.toolkitutils.network;

import net.mcreator.toolkitutils.ToolkitUtilsMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record InvQueryPayload(String targetName, boolean ender) implements CustomPacketPayload {
    public static final Type<InvQueryPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ToolkitUtilsMod.MOD_ID, "inv_query"));
    public static final StreamCodec<RegistryFriendlyByteBuf, InvQueryPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, InvQueryPayload::targetName,
                    ByteBufCodecs.BOOL,        InvQueryPayload::ender,
                    InvQueryPayload::new);
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
