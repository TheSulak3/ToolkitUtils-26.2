package net.mcreator.toolkitutils.procedures;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

public final class ToolkitProcedures {
    private ToolkitProcedures() {}

    // --- gives ---
    public static void diamonds()  { CommandUtils.send("give @s minecraft:diamond 64"); }
    public static void netherite() { CommandUtils.send("give @s minecraft:netherite_ingot 64"); }
    public static void upgrade()   { CommandUtils.send("give @s minecraft:netherite_upgrade_smithing_template 64"); }
    public static void gapples()   { CommandUtils.send("give @s minecraft:enchanted_golden_apple 64"); }
    public static void egapps()    { CommandUtils.send("give @s minecraft:golden_apple 64"); }
    public static void food() {
        CommandUtils.send("give @s minecraft:cooked_beef 64");
        CommandUtils.send("give @s minecraft:bread 64");
    }
    public static void experience(){ CommandUtils.send("experience add @s 100 points"); }

    // --- kits ---
    public static void godKit() {
        String enchArmor = "[minecraft:enchantments={levels:{\"minecraft:protection\":4,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:thorns\":3}}]";
        String enchSword = "[minecraft:enchantments={levels:{\"minecraft:sharpness\":5,\"minecraft:looting\":3,\"minecraft:sweeping_edge\":3,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:fire_aspect\":2}}]";
        String enchPick  = "[minecraft:enchantments={levels:{\"minecraft:efficiency\":5,\"minecraft:fortune\":3,\"minecraft:unbreaking\":3,\"minecraft:mending\":1}}]";
        String enchAxe   = "[minecraft:enchantments={levels:{\"minecraft:efficiency\":5,\"minecraft:sharpness\":5,\"minecraft:unbreaking\":3,\"minecraft:mending\":1}}]";
        String enchShov  = "[minecraft:enchantments={levels:{\"minecraft:efficiency\":5,\"minecraft:unbreaking\":3,\"minecraft:mending\":1}}]";
        String enchBow   = "[minecraft:enchantments={levels:{\"minecraft:power\":5,\"minecraft:infinity\":1,\"minecraft:unbreaking\":3,\"minecraft:flame\":1}}]";
        CommandUtils.send("item replace entity @s armor.head with minecraft:netherite_helmet" + enchArmor);
        CommandUtils.send("item replace entity @s armor.chest with minecraft:netherite_chestplate" + enchArmor);
        CommandUtils.send("item replace entity @s armor.legs with minecraft:netherite_leggings" + enchArmor);
        CommandUtils.send("item replace entity @s armor.feet with minecraft:netherite_boots" + enchArmor);
        CommandUtils.send("give @s minecraft:netherite_sword" + enchSword);
        CommandUtils.send("give @s minecraft:netherite_pickaxe" + enchPick);
        CommandUtils.send("give @s minecraft:netherite_axe" + enchAxe);
        CommandUtils.send("give @s minecraft:netherite_shovel" + enchShov);
        CommandUtils.send("give @s minecraft:bow" + enchBow);
        CommandUtils.send("give @s minecraft:arrow 1");
        CommandUtils.send("give @s minecraft:enchanted_golden_apple 32");
    }

    // --- health/state ---
    public static void heal()      { CommandUtils.send("effect give @s minecraft:instant_health 1 100 true"); }
    public static void feed()      { CommandUtils.send("effect give @s minecraft:saturation 1 100 true"); }
    public static void clear()     { CommandUtils.send("clear @s"); }

    // --- buffs ---
    public static void flight()      { CommandUtils.send("effect give @s minecraft:slow_falling 100000 0 true"); }
    public static void invincible()  { CommandUtils.send("effect give @s minecraft:resistance 100000 4 true"); }
    public static void combat() {
        CommandUtils.send("effect give @s minecraft:absorption 480 4 true");
        CommandUtils.send("effect give @s minecraft:fire_resistance 480 0 true");
        CommandUtils.send("effect give @s minecraft:regeneration 480 2 true");
    }
    public static void speed()        { CommandUtils.send("effect give @s minecraft:speed 100000 4 true"); }
    public static void jumpBoost()    { CommandUtils.send("effect give @s minecraft:jump_boost 100000 4 true"); }
    public static void nightVision()  { CommandUtils.send("effect give @s minecraft:night_vision 100000 0 true"); }
    public static void waterBreath()  { CommandUtils.send("effect give @s minecraft:water_breathing 100000 0 true"); }
    public static void haste()        { CommandUtils.send("effect give @s minecraft:haste 100000 4 true"); }
    public static void strength()     { CommandUtils.send("effect give @s minecraft:strength 100000 4 true"); }
    public static void clearEffects() { CommandUtils.send("effect clear @s"); }

    // --- world/time ---
    public static void day()          { CommandUtils.send("time set day"); }
    public static void night()        { CommandUtils.send("time set night"); }
    public static void weatherClear() { CommandUtils.send("weather clear"); }
    public static void weatherRain()  { CommandUtils.send("weather rain"); }
    public static void weatherThunder(){ CommandUtils.send("weather thunder"); }
    public static void killHostiles() { CommandUtils.send("kill @e[type=#minecraft:monster,distance=..50]"); }

    // --- gamemode ---
    public static void gmCreative()   { CommandUtils.send("gamemode creative @s"); }
    public static void gmSurvival()   { CommandUtils.send("gamemode survival @s"); }
    public static void gmSpectator()  { CommandUtils.send("gamemode spectator @s"); }
    public static void gamemode(LocalPlayer player) {
        if (player == null) return;
        CommandUtils.send(player.isCreative() ? "gamemode survival @s" : "gamemode creative @s");
    }

    // --- vanish ---
    public static void vanish(boolean currentlyInvisible) {
        if (currentlyInvisible) CommandUtils.send("effect clear @s minecraft:invisibility");
        else CommandUtils.send("effect give @s minecraft:invisibility 100000 0 true");
    }

    // --- dupe / prompt ---
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

    // --- item picker helper ---
    public static void giveById(String id, int count) {
        if (id == null || id.isBlank()) return;
        CommandUtils.send("give @s " + id + " " + Math.max(1, count));
    }
}
