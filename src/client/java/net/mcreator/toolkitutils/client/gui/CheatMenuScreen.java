package net.mcreator.toolkitutils.client.gui;

import net.mcreator.toolkitutils.procedures.ToolkitProcedures;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class CheatMenuScreen extends Screen {
    private EditBox prompt;
    private static boolean invisible;

    public CheatMenuScreen() { super(Component.literal("")); }

    @Override public boolean isPauseScreen() { return false; }

    @Override
    protected void init() {
        int cols = 3;
        int bw = 70, bh = 20, gap = 4;
        int gridW = cols * bw + (cols - 1) * gap;
        int gridH = 6 * bh + 5 * gap;
        int x0 = (width - gridW) / 2;
        int y0 = (height - gridH) / 2;

        String[] labels = {
            "Diamonds", "Netherite", "Upgrade",
            "Gapples",  "E Gapps",   "Food",
            "XP",       "Clear",     "Day",
            "Flight",   "Invincible","Combat",
            "Creative", "Dupe",      "Vanish"
        };
        Runnable[] actions = {
            ToolkitProcedures::diamonds, ToolkitProcedures::netherite, ToolkitProcedures::upgrade,
            ToolkitProcedures::gapples,  ToolkitProcedures::egapps,    ToolkitProcedures::food,
            ToolkitProcedures::experience,ToolkitProcedures::clear,    ToolkitProcedures::day,
            ToolkitProcedures::flight,   ToolkitProcedures::invincible,ToolkitProcedures::combat,
            () -> ToolkitProcedures.gamemode(minecraft.player),
            ToolkitProcedures::dupe,
            () -> { ToolkitProcedures.vanish(invisible); invisible = !invisible; }
        };

        for (int i = 0; i < labels.length; i++) {
            int r = i / cols, c = i % cols;
            int bx = x0 + c * (bw + gap);
            int by = y0 + r * (bh + gap);
            Runnable a = actions[i];
            addRenderableWidget(Button.builder(Component.literal(labels[i]), b -> a.run()).bounds(bx, by, bw, bh).build());
        }

        int promptRow = y0 + 5 * (bh + gap);
        prompt = new EditBox(font, x0, promptRow, gridW - bw - gap, bh, Component.literal("cmd"));
        prompt.setMaxLength(32767);
        addRenderableWidget(prompt);
        addRenderableWidget(Button.builder(Component.literal("Run"),
                b -> ToolkitProcedures.commandPrompt(prompt.getValue()))
                .bounds(x0 + gridW - bw, promptRow, bw, bh).build());
    }
}
