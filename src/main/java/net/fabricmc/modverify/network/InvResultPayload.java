package net.fabricmc.modverify.network;

import net.fabricmc.modverify.ModVerify;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record InvResultPayload(String targetName, boolean ender, List<ItemStack> items) implements CustomPacketPayload {
    public static final Type<InvResultPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ModVerify.MOD_ID, "inv_result"));
    public static final StreamCodec<RegistryFriendlyByteBuf, InvResultPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, InvResultPayload::targetName,
                    ByteBufCodecs.BOOL,        InvResultPayload::ender,
                    ItemStack.OPTIONAL_LIST_STREAM_CODEC, InvResultPayload::items,
                    InvResultPayload::new);
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
