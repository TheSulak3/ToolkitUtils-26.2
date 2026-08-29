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

    private record Btn(String label, Runnable action) {}

    @Override
    protected void init() {
        LOG.debug("CheatMenuScreen init ({}x{})", width, height);

        int cols = 5;
        int bw = 62, bh = 18, gap = 3;
        Btn[] btns = new Btn[] {
            new Btn("Diamonds",   ToolkitProcedures::diamonds),
            new Btn("Netherite",  ToolkitProcedures::netherite),
            new Btn("Upgrade",    ToolkitProcedures::upgrade),
            new Btn("Gapples",    ToolkitProcedures::gapples),
            new Btn("E Gapps",    ToolkitProcedures::egapps),

            new Btn("Food",       ToolkitProcedures::food),
            new Btn("XP",         ToolkitProcedures::experience),
            new Btn("God Kit",    ToolkitProcedures::godKit),
            new Btn("Heal",       ToolkitProcedures::heal),
            new Btn("Feed",       ToolkitProcedures::feed),

            new Btn("Flight",     ToolkitProcedures::flight),
            new Btn("Invincible", ToolkitProcedures::invincible),
            new Btn("Combat",     ToolkitProcedures::combat),
            new Btn("Speed",      ToolkitProcedures::speed),
            new Btn("Jump",       ToolkitProcedures::jumpBoost),

            new Btn("Night Vis",  ToolkitProcedures::nightVision),
            new Btn("Water Br",   ToolkitProcedures::waterBreath),
            new Btn("Haste",      ToolkitProcedures::haste),
            new Btn("Strength",   ToolkitProcedures::strength),
            new Btn("Clr Fx",     ToolkitProcedures::clearEffects),

            new Btn("Day",        ToolkitProcedures::day),
            new Btn("Night",      ToolkitProcedures::night),
            new Btn("Clear WX",   ToolkitProcedures::weatherClear),
            new Btn("Rain",       ToolkitProcedures::weatherRain),
            new Btn("Thunder",    ToolkitProcedures::weatherThunder),

            new Btn("Creative",   ToolkitProcedures::gmCreative),
            new Btn("Survival",   ToolkitProcedures::gmSurvival),
            new Btn("Spectator",  ToolkitProcedures::gmSpectator),
            new Btn("Kill Mobs",  ToolkitProcedures::killHostiles),
            new Btn("Dupe",       ToolkitProcedures::dupe),

            new Btn("Clear Inv",  ToolkitProcedures::clear),
            new Btn("Vanish",     () -> { ToolkitProcedures.vanish(invisible); invisible = !invisible; }),
            new Btn("Items…",     () -> minecraft.setScreen(new ItemPickerScreen())),
            new Btn("",           () -> {}),
            new Btn("",           () -> {})
        };

        int rows = (btns.length + cols - 1) / cols;
        int gridW = cols * bw + (cols - 1) * gap;
        int gridH = rows * bh + (rows - 1) * gap + bh + gap; // + prompt row
        int x0 = (width - gridW) / 2;
        int y0 = (height - gridH) / 2;

        for (int i = 0; i < btns.length; i++) {
            int r = i / cols, c = i % cols;
            int bx = x0 + c * (bw + gap);
            int by = y0 + r * (bh + gap);
            if (btns[i].label.isEmpty()) continue;
            final Btn b = btns[i];
            addRenderableWidget(Button.builder(Component.literal(b.label), w -> {
                LOG.debug("button: {}", b.label);
                try { b.action.run(); }
                catch (Exception e) { LOG.error("button {} threw", b.label, e); }
            }).bounds(bx, by, bw, bh).build());
        }

        int promptRow = y0 + rows * (bh + gap);
        int runW = 50;
        prompt = new EditBox(font, x0, promptRow, gridW - runW - gap, bh, Component.literal("cmd"));
        prompt.setHint(Component.literal("/command…"));
        prompt.setMaxLength(32767);
        addRenderableWidget(prompt);
        addRenderableWidget(Button.builder(Component.literal("Run"), b -> {
            String v = prompt.getValue();
            LOG.debug("prompt Run: '{}'", v);
            try { ToolkitProcedures.commandPrompt(v); }
            catch (Exception e) { LOG.error("prompt threw for '{}'", v, e); }
        }).bounds(x0 + gridW - runW, promptRow, runW, bh).build());
    }
}
