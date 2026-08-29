package net.mcreator.toolkitutils.network;

import net.mcreator.toolkitutils.ToolkitUtilsMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ExecuteCommandPayload(String command) implements CustomPacketPayload {
    public static final Type<ExecuteCommandPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ToolkitUtilsMod.MOD_ID, "exec_cmd"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ExecuteCommandPayload> CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ExecuteCommandPayload::command, ExecuteCommandPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
