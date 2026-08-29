package net.mcreator.toolkitutils.client.gui;

import net.mcreator.toolkitutils.procedures.CommandUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ToolsScreen extends Screen {
    public ToolsScreen() { super(Component.literal("")); }

    @Override public boolean isPauseScreen() { return false; }

    private static final String E_ARMOR = "[minecraft:enchantments={levels:{\"minecraft:protection\":4,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:thorns\":3}}]";
    private static final String E_SWORD = "[minecraft:enchantments={levels:{\"minecraft:sharpness\":5,\"minecraft:looting\":3,\"minecraft:sweeping_edge\":3,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:fire_aspect\":2}}]";
    private static final String E_PICK  = "[minecraft:enchantments={levels:{\"minecraft:efficiency\":5,\"minecraft:fortune\":3,\"minecraft:unbreaking\":3,\"minecraft:mending\":1}}]";
    private static final String E_AXE   = "[minecraft:enchantments={levels:{\"minecraft:efficiency\":5,\"minecraft:sharpness\":5,\"minecraft:unbreaking\":3,\"minecraft:mending\":1}}]";
    private static final String E_SHOV  = "[minecraft:enchantments={levels:{\"minecraft:efficiency\":5,\"minecraft:unbreaking\":3,\"minecraft:mending\":1}}]";
    private static final String E_HOE   = "[minecraft:enchantments={levels:{\"minecraft:efficiency\":5,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:fortune\":3}}]";
    private static final String E_BOW   = "[minecraft:enchantments={levels:{\"minecraft:power\":5,\"minecraft:infinity\":1,\"minecraft:unbreaking\":3,\"minecraft:flame\":1,\"minecraft:punch\":2}}]";
    private static final String E_XBOW  = "[minecraft:enchantments={levels:{\"minecraft:quick_charge\":3,\"minecraft:multishot\":1,\"minecraft:unbreaking\":3,\"minecraft:mending\":1}}]";
    private static final String E_TRIDENT="[minecraft:enchantments={levels:{\"minecraft:loyalty\":3,\"minecraft:channeling\":1,\"minecraft:impaling\":5,\"minecraft:unbreaking\":3,\"minecraft:mending\":1}}]";
    private static final String E_ELYTRA= "[minecraft:enchantments={levels:{\"minecraft:unbreaking\":3,\"minecraft:mending\":1}}]";
    private static final String E_SHIELD= "[minecraft:enchantments={levels:{\"minecraft:unbreaking\":3,\"minecraft:mending\":1}}]";
    private static final String E_ROD   = "[minecraft:enchantments={levels:{\"minecraft:luck_of_the_sea\":3,\"minecraft:lure\":3,\"minecraft:unbreaking\":3,\"minecraft:mending\":1}}]";

    private record T(String label, String cmd) {}

    @Override
    protected void init() {
        T[] entries = new T[] {
            new T("N Sword",   "give @s minecraft:netherite_sword" + E_SWORD),
            new T("N Pick",    "give @s minecraft:netherite_pickaxe" + E_PICK),
            new T("N Axe",     "give @s minecraft:netherite_axe" + E_AXE),
            new T("N Shovel",  "give @s minecraft:netherite_shovel" + E_SHOV),
            new T("N Hoe",     "give @s minecraft:netherite_hoe" + E_HOE),

            new T("D Sword",   "give @s minecraft:diamond_sword" + E_SWORD),
            new T("D Pick",    "give @s minecraft:diamond_pickaxe" + E_PICK),
            new T("D Axe",     "give @s minecraft:diamond_axe" + E_AXE),
            new T("D Shovel",  "give @s minecraft:diamond_shovel" + E_SHOV),
            new T("D Hoe",     "give @s minecraft:diamond_hoe" + E_HOE),

            new T("Bow",       "give @s minecraft:bow" + E_BOW),
            new T("Crossbow",  "give @s minecraft:crossbow" + E_XBOW),
            new T("Trident",   "give @s minecraft:trident" + E_TRIDENT),
            new T("Elytra",    "give @s minecraft:elytra" + E_ELYTRA),
            new T("Shield",    "give @s minecraft:shield" + E_SHIELD),

            new T("Fish Rod",  "give @s minecraft:fishing_rod" + E_ROD),
            new T("F&Steel",   "give @s minecraft:flint_and_steel 1"),
            new T("Shears",    "give @s minecraft:shears 1"),
            new T("Arrows",    "give @s minecraft:arrow 64"),
            new T("TNT",       "give @s minecraft:tnt 64"),

            new T("N Helmet",  "give @s minecraft:netherite_helmet" + E_ARMOR),
            new T("N Chest",   "give @s minecraft:netherite_chestplate" + E_ARMOR),
            new T("N Legs",    "give @s minecraft:netherite_leggings" + E_ARMOR),
            new T("N Boots",   "give @s minecraft:netherite_boots" + E_ARMOR),
            new T("Totem",     "give @s minecraft:totem_of_undying 1")
        };

        int cols = 5;
        int bw = 62, bh = 18, gap = 3;
        int rows = (entries.length + cols - 1) / cols;
        int gridW = cols * bw + (cols - 1) * gap;
        int gridH = rows * bh + (rows - 1) * gap;
        int x0 = (width - gridW) / 2;
        int y0 = (height - gridH - bh - 8) / 2;

        for (int i = 0; i < entries.length; i++) {
            int r = i / cols, c = i % cols;
            final T t = entries[i];
            addRenderableWidget(Button.builder(Component.literal(t.label),
                    b -> CommandUtils.send(t.cmd))
                    .bounds(x0 + c * (bw + gap), y0 + r * (bh + gap), bw, bh).build());
        }

        addRenderableWidget(Button.builder(Component.literal("Back"),
                b -> minecraft.setScreen(new CheatMenuScreen()))
                .bounds(x0, y0 + rows * (bh + gap) + 6, gridW, bh).build());
    }
}
