package net.mcreator.toolkitutils.client.gui;

import net.mcreator.toolkitutils.procedures.ToolkitProcedures;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CheatMenuScreen extends Screen {
    private static final Logger LOG = LoggerFactory.getLogger("toolkit_utils/gui");
    private EditBox prompt;
    private static boolean invisible;

    public CheatMenuScreen() { super(Component.literal("")); }

    @Override public boolean isPauseScreen() { return false; }

    @Override
    protected void init() {
        LOG.info("[toolkit_utils] CheatMenuScreen init ({}x{})", width, height);
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
            final String label = labels[i];
            final Runnable a = actions[i];
            addRenderableWidget(Button.builder(Component.literal(label), b -> {
                LOG.info("[toolkit_utils] button clicked: {}", label);
                try { a.run(); }
                catch (Exception e) { LOG.error("[toolkit_utils] button {} threw", label, e); }
            }).bounds(bx, by, bw, bh).build());
        }

        int promptRow = y0 + 5 * (bh + gap);
        prompt = new EditBox(font, x0, promptRow, gridW - bw - gap, bh, Component.literal("cmd"));
        prompt.setMaxLength(32767);
        addRenderableWidget(prompt);
        addRenderableWidget(Button.builder(Component.literal("Run"), b -> {
            String v = prompt.getValue();
            LOG.info("[toolkit_utils] prompt Run: '{}'", v);
            try { ToolkitProcedures.commandPrompt(v); }
            catch (Exception e) { LOG.error("[toolkit_utils] prompt threw for '{}'", v, e); }
        }).bounds(x0 + gridW - bw, promptRow, bw, bh).build());
    }
}
