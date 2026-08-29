package net.mcreator.toolkitutils.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

/** Snapshots of the local player's effect state, used for toggle-highlighting buttons. */
public final class Effects {
    private Effects() {}

    private static boolean has(Holder<MobEffect> effect) {
        var p = Minecraft.getInstance().player;
        return p != null && p.hasEffect(effect);
    }

    public static boolean flight()      { return has(MobEffects.SLOW_FALLING); }
    public static boolean invincible()  { return has(MobEffects.RESISTANCE); }
    public static boolean speed()       { return has(MobEffects.SPEED); }
    public static boolean jump()        { return has(MobEffects.JUMP_BOOST); }
    public static boolean nightVis()    { return has(MobEffects.NIGHT_VISION); }
    public static boolean waterBr()     { return has(MobEffects.WATER_BREATHING); }
    public static boolean haste()       { return has(MobEffects.HASTE); }
    public static boolean strength()    { return has(MobEffects.STRENGTH); }
    public static boolean fireRes()     { return has(MobEffects.FIRE_RESISTANCE); }
    public static boolean regen()       { return has(MobEffects.REGENERATION); }
    public static boolean invisibility(){ return has(MobEffects.INVISIBILITY); }
    public static boolean combat()      { return fireRes() && regen(); }
}
