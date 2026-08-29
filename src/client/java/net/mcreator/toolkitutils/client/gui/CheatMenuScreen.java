package net.mcreator.toolkitutils.client.gui;

import net.mcreator.toolkitutils.client.Effects;
import net.mcreator.toolkitutils.client.UIConfig;
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
import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;

public final class CheatMenuScreen extends CheatBaseScreen {
    private static final Logger LOG = LoggerFactory.getLogger("toolkit_utils/gui");
    private static final int DEFAULT_GIVE = 10;

    public enum Category { ITEMS, BUFFS, WORLD, MODES, UTILITY, SETTINGS }
    private static Category activeCategory = Category.ITEMS;

    private EditBox prompt;

    private record CountedZone(int x, int y, int w, int h, String label, IntConsumer giver, int defaultCount) {}
    private final List<CountedZone> countedZones = new ArrayList<>();

    public CheatMenuScreen() { super("v1.0 — " + activeCategory.name().toLowerCase()); }

    @Override protected int panelWidth()  { return 400; }
    @Override protected int panelHeight() { return 260; }

    private sealed interface Entry permits Plain, Counted, Toggle {}
    private record Plain(String label, Runnable action) implements Entry {}
    private record Counted(String label, IntConsumer giver, int defaultCount) implements Entry {}
    private record Toggle(String label, Runnable action, BooleanSupplier isActive) implements Entry {}

    private int contentTop() { return contentY() + 22; } // room for tab bar

    @Override
    protected void init() {
        LOG.debug("CheatMenuScreen init tab={} ({}x{})", activeCategory, width, height);
        countedZones.clear();

        buildTabs();
        buildCategory(activeCategory);
        buildPromptRow();
    }

    // --- tabs ---
    private void buildTabs() {
        Category[] cats = Category.values();
        int tabH = 18;
        int totalW = panelWidth() - 16;
        int tabW = (totalW - (cats.length - 1) * 2) / cats.length;
        int x0 = panelX() + 8;
        int y  = contentY();
        for (int i = 0; i < cats.length; i++) {
            final Category cat = cats[i];
            int tx = x0 + i * (tabW + 2);
            boolean isActive = cat == activeCategory;
            CheatWidget w = CheatWidget.toggle(tx, y, tabW, tabH, cat.name(),
                    () -> {
                        if (cat != activeCategory) {
                            activeCategory = cat;
                            rebuildWidgets();
                        }
                    },
                    () -> cat == activeCategory);
            addRenderableWidget(w);
            // subtitle already shows category via constructor snapshot; update if user opened it after switching
        }
    }

    private void rebuildWidgets() {
        this.clearWidgets();
        countedZones.clear();
        buildTabs();
        buildCategory(activeCategory);
        buildPromptRow();
    }

    // --- category content ---
    private void buildCategory(Category cat) {
        Entry[] entries = switch (cat) {
            case ITEMS    -> itemsEntries();
            case BUFFS    -> buffsEntries();
            case WORLD    -> worldEntries();
            case MODES    -> modesEntries();
            case UTILITY  -> utilityEntries();
            case SETTINGS -> new Entry[0]; // handled specially
        };

        if (cat == Category.SETTINGS) { buildSettings(); return; }

        int cols = 5;
        int bw = 72, bh = 18, gap = 4;
        int gridW = cols * bw + (cols - 1) * gap;
        int x0 = panelX() + (panelWidth() - gridW) / 2;
        int y0 = contentTop() + 4;

        for (int i = 0; i < entries.length; i++) {
            Entry e = entries[i];
            if (e == null) continue;
            int r = i / cols, c = i % cols;
            int bx = x0 + c * (bw + gap);
            int by = y0 + r * (bh + gap);
            addEntry(e, bx, by, bw, bh);
        }
    }

    private void addEntry(Entry e, int bx, int by, int bw, int bh) {
        if (e instanceof Plain p) {
            addRenderableWidget(CheatWidget.of(bx, by, bw, bh, p.label, wrap(p.label, p.action)));
        } else if (e instanceof Counted cnt) {
            addRenderableWidget(CheatWidget.of(bx, by, bw, bh, cnt.label, wrap(cnt.label, () -> cnt.giver.accept(cnt.defaultCount))));
            countedZones.add(new CountedZone(bx, by, bw, bh, cnt.label, cnt.giver, cnt.defaultCount));
        } else if (e instanceof Toggle t) {
            addRenderableWidget(CheatWidget.toggle(bx, by, bw, bh, t.label, wrap(t.label, t.action), t.isActive));
        }
    }

    private Runnable wrap(String label, Runnable r) {
        return () -> {
            LOG.debug("button: {}", label);
            try { r.run(); } catch (Exception ex) { LOG.error("button {} threw", label, ex); }
        };
    }

    private Entry[] itemsEntries() {
        return new Entry[] {
            new Counted("Diamonds",  ToolkitProcedures::diamonds,  DEFAULT_GIVE),
            new Counted("Netherite", ToolkitProcedures::netherite, DEFAULT_GIVE),
            new Counted("Upgrade",   ToolkitProcedures::upgrade,   DEFAULT_GIVE),
            new Counted("Gapples",   ToolkitProcedures::gapples,   DEFAULT_GIVE),
            new Counted("E Gapps",   ToolkitProcedures::egapps,    DEFAULT_GIVE),
            new Counted("Food",      ToolkitProcedures::food,      DEFAULT_GIVE),
            new Counted("XP (lvl)",  ToolkitProcedures::experience, 30),
            new Plain  ("God Kit",   ToolkitProcedures::godKit),
            new Plain  ("Tools",     () -> minecraft.gui.setScreen(new ToolsScreen())),
            new Plain  ("Items",     () -> minecraft.gui.setScreen(new ItemPickerScreen())),
        };
    }
    private Entry[] buffsEntries() {
        return new Entry[] {
            new Toggle("Flight",     ToolkitProcedures::flight,      Effects::flight),
            new Toggle("Invincible", ToolkitProcedures::invincible,  Effects::invincible),
            new Toggle("Combat",     ToolkitProcedures::combat,      Effects::combat),
            new Toggle("Speed",      ToolkitProcedures::speed,       Effects::speed),
            new Toggle("Jump",       ToolkitProcedures::jumpBoost,   Effects::jump),
            new Toggle("Night Vis",  ToolkitProcedures::nightVision, Effects::nightVis),
            new Toggle("Water Br",   ToolkitProcedures::waterBreath, Effects::waterBr),
            new Toggle("Haste",      ToolkitProcedures::haste,       Effects::haste),
            new Toggle("Strength",   ToolkitProcedures::strength,    Effects::strength),
            new Toggle("Vanish",     VanishClient::toggle,            VanishClient::hidden),
            new Plain ("Heal",       ToolkitProcedures::heal),
            new Plain ("Feed",       ToolkitProcedures::feed),
            new Plain ("Clear Fx",   ToolkitProcedures::clearEffects),
        };
    }
    private Entry[] worldEntries() {
        return new Entry[] {
            new Plain("Day",        ToolkitProcedures::day),
            new Plain("Night",      ToolkitProcedures::night),
            new Plain("Clear WX",   ToolkitProcedures::weatherClear),
            new Plain("Rain",       ToolkitProcedures::weatherRain),
            new Plain("Thunder",    ToolkitProcedures::weatherThunder),
            new Plain("Kill Mobs",  ToolkitProcedures::killHostiles),
        };
    }
    private Entry[] modesEntries() {
        return new Entry[] {
            new Plain("Creative",   ToolkitProcedures::gmCreative),
            new Plain("Survival",   ToolkitProcedures::gmSurvival),
            new Plain("Spectator",  ToolkitProcedures::gmSpectator),
        };
    }
    private Entry[] utilityEntries() {
        return new Entry[] {
            new Plain("Clear Inv",  ToolkitProcedures::clear),
            new Plain("Dupe",       ToolkitProcedures::dupe),
        };
    }

    // --- prompt row ---
    private void buildPromptRow() {
        int bh = 18, gap = 4;
        int runW = 60;
        int rowW = panelWidth() - 16;
        int x0 = panelX() + 8;
        int py = panelY() + panelHeight() - bh - 8;
        prompt = new EditBox(font, x0, py, rowW - runW - gap, bh, Component.literal("cmd"));
        prompt.setHint(Component.literal("/command  (run as op)"));
        prompt.setMaxLength(32767);
        addRenderableWidget(prompt);
        addRenderableWidget(CheatWidget.of(x0 + rowW - runW, py, runW, bh, "RUN", () -> {
            String v = prompt.getValue();
            LOG.debug("prompt Run: '{}'", v);
            try { ToolkitProcedures.commandPrompt(v); } catch (Exception ex) { LOG.error("prompt threw", ex); }
        }));
    }

    // --- settings tab ---
    private void buildSettings() {
        UIConfig c = UIConfig.get();
        int bh = 18, gap = 6;
        int rowW = panelWidth() - 40;
        int x = panelX() + 20;
        int y = contentTop() + 8;

        // Opacity: [-] N [+] with 5% steps
        int labelW = 90, valW = 44, btnW = 22;
        addRenderableWidget(CheatWidget.of(x + labelW + valW + 4,           y, btnW, bh, "-", () -> { c.opacity = Math.max(0, c.opacity - 5); c.save(); rebuildWidgets(); }));
        addRenderableWidget(CheatWidget.of(x + labelW + valW + btnW + 8,    y, btnW, bh, "+", () -> { c.opacity = Math.min(100, c.opacity + 5); c.save(); rebuildWidgets(); }));

        // Accent color presets
        y += bh + gap;
        String[][] presets = { {"Cyan","#00E5FF"}, {"Purple","#B266FF"}, {"Pink","#FF3EA5"}, {"Green","#00E676"}, {"Red","#FF3355"}, {"Amber","#FFAB40"}, {"White","#EAEAF2"} };
        int pW = (rowW - (presets.length - 1) * 3) / presets.length;
        for (int i = 0; i < presets.length; i++) {
            final String hex = presets[i][1];
            addRenderableWidget(CheatWidget.of(x + i * (pW + 3), y + bh + 2, pW, bh, presets[i][0], () -> { c.accent = hex; c.save(); rebuildWidgets(); }));
        }

        // HUD toggle + anchor
        y += 3 * bh + 12;
        addRenderableWidget(CheatWidget.toggle(x, y, rowW / 2 - 4, bh, "HUD Enabled", () -> { c.hudEnabled = !c.hudEnabled; c.save(); rebuildWidgets(); }, () -> c.hudEnabled));
        addRenderableWidget(CheatWidget.toggle(x + rowW / 2 + 4, y, rowW / 2 - 4, bh, "Show Active", () -> { c.hudShowActive = !c.hudShowActive; c.save(); rebuildWidgets(); }, () -> c.hudShowActive));

        y += bh + gap;
        String[] anchors = { "top_left","top_right","bottom_left","bottom_right" };
        int aW = (rowW - 9) / 4;
        for (int i = 0; i < 4; i++) {
            final String anc = anchors[i];
            addRenderableWidget(CheatWidget.toggle(x + i * (aW + 3), y, aW, bh, anc.replace('_', ' '), () -> { c.hudAnchor = anc; c.save(); rebuildWidgets(); }, () -> anc.equals(c.hudAnchor)));
        }

        // Click sound + Reset
        y += bh + gap;
        addRenderableWidget(CheatWidget.toggle(x, y, rowW / 2 - 4, bh, "Click Sound", () -> { c.clickSound = !c.clickSound; c.save(); rebuildWidgets(); }, () -> c.clickSound));
        addRenderableWidget(CheatWidget.of(x + rowW / 2 + 4, y, rowW / 2 - 4, bh, "Reset Position", () -> {
            c.panelX = -1; c.panelY = -1; c.save();
            this.panelOriginX = Integer.MIN_VALUE; this.panelOriginY = Integer.MIN_VALUE;
            rebuildWidgets();
        }));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 2) {
            int mx = (int) event.x(), my = (int) event.y();
            for (CountedZone z : countedZones) {
                if (mx >= z.x && mx < z.x + z.w && my >= z.y && my < z.y + z.h) {
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
        if (activeCategory == Category.SETTINGS) {
            UIConfig c = UIConfig.get();
            int x = panelX() + 20;
            int y = contentTop() + 8;
            g.text(font, Component.literal("Opacity"), x, y + 5, Theme.TEXT_DIM, false);
            g.text(font, Component.literal(c.opacity + "%"), x + 90, y + 5, Theme.textAccent(), false);
            g.text(font, Component.literal("Accent"),  x, y + 24 + 6, Theme.TEXT_DIM, false);
            g.fill(x + 60, y + 26, x + 76, y + 42, c.accentArgb());
            g.text(font, Component.literal(c.accent), x + 82, y + 30, Theme.TEXT_DIM, false);
        } else {
            int hy = panelY() + headerHeight() + 2;
            g.text(font, Component.literal("left click = default  ·  middle click = amount prompt  ·  drag header to move"),
                    panelX() + 8, hy - 12, Theme.TEXT_MUTED, false);
        }
    }
}
