package net.mcreator.toolkitutils.client.gui;

import net.mcreator.toolkitutils.client.VanishClient;
import net.mcreator.toolkitutils.procedures.ToolkitProcedures;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public final class CheatMenuScreen extends Screen {
    private static final Logger LOG = LoggerFactory.getLogger("toolkit_utils/gui");
    private static final int DEFAULT_GIVE = 10;
    private EditBox prompt;

    // Track counted-button hitboxes so middle-click can open a count prompt.
    private record CountedZone(int x, int y, int w, int h, String label, IntConsumer giver, int defaultCount) {}
    private final List<CountedZone> countedZones = new ArrayList<>();

    public CheatMenuScreen() { super(Component.literal("")); }

    @Override public boolean isPauseScreen() { return false; }

    private sealed interface Entry permits Plain, Counted {}
    private record Plain(String label, Runnable action) implements Entry {}
    private record Counted(String label, IntConsumer giver, int defaultCount) implements Entry {}

    @Override
    protected void init() {
        LOG.debug("CheatMenuScreen init ({}x{})", width, height);
        countedZones.clear();

        int cols = 5;
        int bw = 62, bh = 18, gap = 3;

        Entry[] entries = new Entry[] {
            // row 1 — materials + consumables (all counted, default 10)
            new Counted("Diamonds",  ToolkitProcedures::diamonds,  DEFAULT_GIVE),
            new Counted("Netherite", ToolkitProcedures::netherite, DEFAULT_GIVE),
            new Counted("Upgrade",   ToolkitProcedures::upgrade,   DEFAULT_GIVE),
            new Counted("Gapples",   ToolkitProcedures::gapples,   DEFAULT_GIVE),
            new Counted("E Gapps",   ToolkitProcedures::egapps,    DEFAULT_GIVE),

            // row 2
            new Counted("Food",      ToolkitProcedures::food,      DEFAULT_GIVE),
            new Counted("XP (lvl)",  ToolkitProcedures::experience, 30),
            new Plain  ("God Kit",   ToolkitProcedures::godKit),
            new Plain  ("Heal",      ToolkitProcedures::heal),
            new Plain  ("Feed",      ToolkitProcedures::feed),

            // row 3
            new Plain("Flight",     ToolkitProcedures::flight),
            new Plain("Invincible", ToolkitProcedures::invincible),
            new Plain("Combat",     ToolkitProcedures::combat),
            new Plain("Speed",      ToolkitProcedures::speed),
            new Plain("Jump",       ToolkitProcedures::jumpBoost),

            // row 4
            new Plain("Night Vis",  ToolkitProcedures::nightVision),
            new Plain("Water Br",   ToolkitProcedures::waterBreath),
            new Plain("Haste",      ToolkitProcedures::haste),
            new Plain("Strength",   ToolkitProcedures::strength),
            new Plain("Clr Fx",     ToolkitProcedures::clearEffects),

            // row 5
            new Plain("Day",        ToolkitProcedures::day),
            new Plain("Night",      ToolkitProcedures::night),
            new Plain("Clear WX",   ToolkitProcedures::weatherClear),
            new Plain("Rain",       ToolkitProcedures::weatherRain),
            new Plain("Thunder",    ToolkitProcedures::weatherThunder),

            // row 6
            new Plain("Creative",   ToolkitProcedures::gmCreative),
            new Plain("Survival",   ToolkitProcedures::gmSurvival),
            new Plain("Spectator",  ToolkitProcedures::gmSpectator),
            new Plain("Kill Mobs",  ToolkitProcedures::killHostiles),
            new Plain("Dupe",       ToolkitProcedures::dupe),

            // row 7
            new Plain("Clear Inv",  ToolkitProcedures::clear),
            new Plain("Vanish",     VanishClient::toggle),
            new Plain("Items…",     () -> minecraft.gui.setScreen(new ItemPickerScreen())),
            new Plain("Tools…",     () -> minecraft.gui.setScreen(new ToolsScreen())),
            new Plain("",           () -> {})
        };

        int rows = (entries.length + cols - 1) / cols;
        int gridW = cols * bw + (cols - 1) * gap;
        int gridH = rows * bh + (rows - 1) * gap + bh + gap;
        int x0 = (width - gridW) / 2;
        int y0 = (height - gridH) / 2;

        for (int i = 0; i < entries.length; i++) {
            int r = i / cols, c = i % cols;
            int bx = x0 + c * (bw + gap);
            int by = y0 + r * (bh + gap);
            Entry e = entries[i];
            if (e instanceof Plain p) {
                if (p.label.isEmpty()) continue;
                addRenderableWidget(Button.builder(Component.literal(p.label), w -> {
                    LOG.debug("button: {}", p.label);
                    try { p.action.run(); }
                    catch (Exception ex) { LOG.error("button {} threw", p.label, ex); }
                }).bounds(bx, by, bw, bh).build());
            } else if (e instanceof Counted cnt) {
                final int fx = bx, fy = by;
                addRenderableWidget(Button.builder(Component.literal(cnt.label), w -> {
                    LOG.debug("button: {} (default {})", cnt.label, cnt.defaultCount);
                    try { cnt.giver.accept(cnt.defaultCount); }
                    catch (Exception ex) { LOG.error("button {} threw", cnt.label, ex); }
                }).bounds(fx, fy, bw, bh).build());
                countedZones.add(new CountedZone(fx, fy, bw, bh, cnt.label, cnt.giver, cnt.defaultCount));
            }
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
            catch (Exception ex) { LOG.error("prompt threw for '{}'", v, ex); }
        }).bounds(x0 + gridW - runW, promptRow, runW, bh).build());
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 2) { // middle click
            int mx = (int) event.x(), my = (int) event.y();
            for (CountedZone z : countedZones) {
                if (mx >= z.x && mx < z.x + z.w && my >= z.y && my < z.y + z.h) {
                    LOG.debug("middle-click on {}, opening count prompt", z.label);
                    minecraft.gui.setScreen(new CountPromptScreen(z.label, z.giver::accept, this));
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }
}
