package net.mcreator.toolkitutils.client;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.mcreator.toolkitutils.client.gui.theme.Theme;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class ToolkitHud implements HudElement {

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, DeltaTracker delta) {
        UIConfig c = UIConfig.get();
        if (!c.hudEnabled) return;
        if (Minecraft.getInstance().options.hideGui) return;
        if (!ClientInit.AUTHENTICATED) return;

        Minecraft mc = Minecraft.getInstance();
        var font = mc.font;

        List<String> lines = new ArrayList<>();
        lines.add("§l" + Theme.TITLE); // bold header

        if (c.hudShowActive) {
            if (Effects.invisibility()) lines.add("· vanish");
            if (Effects.flight())       lines.add("· flight");
            if (Effects.invincible())   lines.add("· god");
            if (Effects.combat())       lines.add("· combat");
            if (Effects.speed())        lines.add("· speed");
            if (Effects.jump())         lines.add("· jump");
            if (Effects.nightVis())     lines.add("· nvg");
            if (Effects.waterBr())      lines.add("· aqua");
            if (Effects.haste())        lines.add("· haste");
            if (Effects.strength())     lines.add("· str");
        }

        int padX = 6, padY = 4, gap = 2;
        int width = 0;
        for (String s : lines) width = Math.max(width, font.width(s));
        int height = lines.size() * (font.lineHeight + 1) + padY * 2;
        int boxW = width + padX * 2;

        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();
        int x, y;
        switch (c.hudAnchor == null ? "top_left" : c.hudAnchor.toLowerCase()) {
            case "top_right"    -> { x = sw - boxW - 4; y = 4; }
            case "bottom_left"  -> { x = 4;              y = sh - height - 4; }
            case "bottom_right" -> { x = sw - boxW - 4;  y = sh - height - 4; }
            default             -> { x = 4;              y = 4; }
        }

        // background panel
        int bg = (Math.max(0, Math.min(100, c.opacity)) * 255 / 100) << 24;
        g.fill(x, y, x + boxW, y + height, bg | 0x141420);
        // left accent bar
        g.fill(x, y, x + 2, y + height, Theme.accent());
        Theme.border(g, x, y, x + boxW, y + height, Theme.accent());

        int ty = y + padY;
        int i = 0;
        for (String s : lines) {
            int color = i == 0 ? Theme.textAccent() : Theme.TEXT_DIM;
            g.text(font, Component.literal(s), x + padX, ty, color, false);
            ty += font.lineHeight + gap;
            i++;
        }
    }
}
