package net.mcreator.toolkitutils.procedures;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

public final class ToolkitProcedures {
    private ToolkitProcedures() {}

    public static void clear() { CommandUtils.send("clear @s"); }
    public static void day() { CommandUtils.send("time set day"); }
    public static void diamonds() { CommandUtils.send("give @s minecraft:diamond 64"); }
    public static void netherite() { CommandUtils.send("give @s minecraft:netherite_ingot 64"); }
    public static void upgrade() { CommandUtils.send("give @s minecraft:netherite_upgrade_smithing_template 64"); }
    public static void gapples() { CommandUtils.send("give @s minecraft:enchanted_golden_apple 64"); }
    public static void egapps() { CommandUtils.send("give @s minecraft:golden_apple 64"); }
    public static void food() {
        CommandUtils.send("give @s minecraft:bread 32");
        CommandUtils.send("give @s minecraft:cooked_beef 32");
    }
    public static void experience() { CommandUtils.send("experience add @s 100 points"); }
    public static void flight() { CommandUtils.send("effect give @s minecraft:slow_falling 600 0 true"); }
    public static void invincible() { CommandUtils.send("effect give @s minecraft:resistance 1000000 4 true"); }
    public static void combat() {
        CommandUtils.send("effect give @s minecraft:absorption 480 0 true");
        CommandUtils.send("effect give @s minecraft:fire_resistance 480 0 true");
        CommandUtils.send("effect give @s minecraft:regeneration 480 0 true");
    }
    public static void gamemode(LocalPlayer player) {
        if (player == null) return;
        CommandUtils.send(player.isCreative() ? "gamemode survival @s" : "gamemode creative @s");
    }
    public static void vanish(boolean currentlyInvisible) {
        if (currentlyInvisible) CommandUtils.send("effect clear @s minecraft:invisibility");
        else CommandUtils.send("effect give @s minecraft:invisibility 1000000 0 true");
    }
    public static void dupe() {
        LocalPlayer player = CommandUtils.player();
        if (player == null) return;
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) return;
        CommandUtils.send("give @s " + CommandUtils.itemId(held) + " " + held.getCount());
    }
    public static void commandPrompt(String command) {
        if (command == null || command.isBlank()) return;
        CommandUtils.send(command);
    }
}
