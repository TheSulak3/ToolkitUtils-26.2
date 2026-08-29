package net.mcreator.toolkitutils.client.gui;

import net.mcreator.toolkitutils.client.VanishClient;
import net.mcreator.toolkitutils.client.gui.theme.CheatBaseScreen;
import net.mcreator.toolkitutils.client.gui.theme.CheatWidget;
import net.mcreator.toolkitutils.client.gui.theme.Theme;
import net.mcreator.toolkitutils.procedures.ToolkitProcedures;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public final class CheatMenuScreen extends CheatBaseScreen {
    private static final Logger LOG = LoggerFactory.getLogger("toolkit_utils/gui");
    private static final int DEFAULT_GIVE = 10;
    private EditBox prompt;

    private record CountedZone(int x, int y, int w, int h, String label, IntConsumer giver, int defaultCount) {}
    private final List<CountedZone> countedZones = new ArrayList<>();

    public CheatMenuScreen() { super("cheat menu"); }

    @Override protected int panelWidth()  { return 400; }
    @Override protected int panelHeight() { return 260; }

    private sealed interface Entry permits Plain, Counted {}
    private record Plain(String label, Runnable action) implements Entry {}
    private record Counted(String label, IntConsumer giver, int defaultCount) implements Entry {}

    @Override
    protected void init() {
        LOG.debug("CheatMenuScreen init ({}x{})", width, height);
        countedZones.clear();

        int cols = 5;
        int bw = 70, bh = 18, gap = 4;

        Entry[] entries = new Entry[] {
            new Counted("Diamonds",   ToolkitProcedures::diamonds,  DEFAULT_GIVE),
            new Counted("Netherite",  ToolkitProcedures::netherite, DEFAULT_GIVE),
            new Counted("Upgrade",    ToolkitProcedures::upgrade,   DEFAULT_GIVE),
            new Counted("Gapples",    ToolkitProcedures::gapples,   DEFAULT_GIVE),
            new Counted("E Gapps",    ToolkitProcedures::egapps,    DEFAULT_GIVE),

            new Counted("Food",       ToolkitProcedures::food,      DEFAULT_GIVE),
            new Counted("XP (lvl)",   ToolkitProcedures::experience, 30),
            new Plain  ("God Kit",    ToolkitProcedures::godKit),
            new Plain  ("Heal",       ToolkitProcedures::heal),
            new Plain  ("Feed",       ToolkitProcedures::feed),

            new Plain("Flight",       ToolkitProcedures::flight),
            new Plain("Invincible",   ToolkitProcedures::invincible),
            new Plain("Combat",       ToolkitProcedures::combat),
            new Plain("Speed",        ToolkitProcedures::speed),
            new Plain("Jump",         ToolkitProcedures::jumpBoost),

            new Plain("Night Vis",    ToolkitProcedures::nightVision),
            new Plain("Water Br",     ToolkitProcedures::waterBreath),
            new Plain("Haste",        ToolkitProcedures::haste),
            new Plain("Strength",     ToolkitProcedures::strength),
            new Plain("Clear Fx",     ToolkitProcedures::clearEffects),

            new Plain("Day",          ToolkitProcedures::day),
            new Plain("Night",        ToolkitProcedures::night),
            new Plain("Clear WX",     ToolkitProcedures::weatherClear),
            new Plain("Rain",         ToolkitProcedures::weatherRain),
            new Plain("Thunder",      ToolkitProcedures::weatherThunder),

            new Plain("Creative",     ToolkitProcedures::gmCreative),
            new Plain("Survival",     ToolkitProcedures::gmSurvival),
            new Plain("Spectator",    ToolkitProcedures::gmSpectator),
            new Plain("Kill Mobs",    ToolkitProcedures::killHostiles),
            new Plain("Dupe",         ToolkitProcedures::dupe),

            new Plain("Clear Inv",    ToolkitProcedures::clear),
            new Plain("Items",        () -> minecraft.gui.setScreen(new ItemPickerScreen())),
            new Plain("Tools",        () -> minecraft.gui.setScreen(new ToolsScreen())),
            null, null // filler
        };

        int rows = 7;
        int gridW = cols * bw + (cols - 1) * gap;
        int x0 = panelX() + (panelWidth() - gridW) / 2;
        int y0 = contentY() + 4;

        for (int i = 0; i < entries.length; i++) {
            Entry e = entries[i];
            if (e == null) continue;
            int r = i / cols, c = i % cols;
            int bx = x0 + c * (bw + gap);
            int by = y0 + r * (bh + gap);
            if (e instanceof Plain p) {
                addRenderableWidget(CheatWidget.of(bx, by, bw, bh, p.label, () -> {
                    LOG.debug("button: {}", p.label);
                    try { p.action.run(); } catch (Exception ex) { LOG.error("button {} threw", p.label, ex); }
                }));
            } else if (e instanceof Counted cnt) {
                Runnable click = () -> {
                    LOG.debug("button: {} (default {})", cnt.label, cnt.defaultCount);
                    try { cnt.giver.accept(cnt.defaultCount); } catch (Exception ex) { LOG.error("button {} threw", cnt.label, ex); }
                };
                addRenderableWidget(CheatWidget.of(bx, by, bw, bh, cnt.label, click));
                countedZones.add(new CountedZone(bx, by, bw, bh, cnt.label, cnt.giver, cnt.defaultCount));
            }
        }

        // Vanish button explicitly with toggle highlight
        int lastRow = 6;
        int by = y0 + lastRow * (bh + gap);
        addRenderableWidget(CheatWidget.toggle(x0 + 3 * (bw + gap), by, bw, bh,
                "Vanish", VanishClient::toggle, VanishClient::hidden));

        // Prompt row
        int promptY = panelY() + panelHeight() - bh - 8;
        int runW = 60;
        prompt = new EditBox(font, x0, promptY, gridW - runW - gap, bh, Component.literal("cmd"));
        prompt.setHint(Component.literal("/command…"));
        prompt.setMaxLength(32767);
        addRenderableWidget(prompt);
        addRenderableWidget(CheatWidget.of(x0 + gridW - runW, promptY, runW, bh, "Run", () -> {
            String v = prompt.getValue();
            LOG.debug("prompt Run: '{}'", v);
            try { ToolkitProcedures.commandPrompt(v); } catch (Exception ex) { LOG.error("prompt threw for '{}'", v, ex); }
        }));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 2) {
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

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float dt) {
        super.extractRenderState(g, mx, my, dt);
        // Hint line under the header
        int hy = panelY() + headerHeight() + 2;
        g.text(font, Component.literal("left click = default  ·  middle click = amount prompt"),
                panelX() + 8, hy - 12, Theme.TEXT_MUTED, false);
    }
}
