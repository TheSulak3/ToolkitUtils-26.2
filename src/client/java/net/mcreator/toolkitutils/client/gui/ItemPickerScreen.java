package net.mcreator.toolkitutils.client.gui;

import net.mcreator.toolkitutils.client.gui.theme.CheatBaseScreen;
import net.mcreator.toolkitutils.client.gui.theme.CheatWidget;
import net.mcreator.toolkitutils.client.gui.theme.Theme;
import net.mcreator.toolkitutils.procedures.ToolkitProcedures;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ItemPickerScreen extends CheatBaseScreen {
    private EditBox search;
    private EditBox amount;
    private final List<CheatWidget> resultButtons = new ArrayList<>();
    private int scroll = 0;
    private static final int PAGE = 12;

    private record Entry(String nameLower, String id, String display) {}
    private static List<Entry> ALL_ITEMS;

    public ItemPickerScreen() { super("item picker"); }

    @Override protected int panelWidth()  { return 340; }
    @Override protected int panelHeight() { return 320; }

    private static List<Entry> allItems() {
        if (ALL_ITEMS == null) {
            List<Entry> list = new ArrayList<>();
            for (Item item : BuiltInRegistries.ITEM) {
                Identifier id = BuiltInRegistries.ITEM.getKey(item);
                if (id == null) continue;
                String display;
                try { display = new ItemStack(item).getHoverName().getString(); }
                catch (Exception e) { display = id.getPath(); }
                list.add(new Entry(display.toLowerCase(), id.toString(), display));
            }
            list.sort(Comparator.comparing(Entry::nameLower));
            ALL_ITEMS = list;
        }
        return ALL_ITEMS;
    }

    @Override
    protected void init() {
        int rowW = panelWidth() - 20;
        int bh = 18, gap = 3;
        int x = panelX() + 10;
        int y = contentY() + 4;

        int amountW = 60;
        search = new EditBox(font, x, y, rowW - amountW - gap, bh, Component.literal("search"));
        search.setHint(Component.literal("search items…"));
        search.setResponder(s -> { scroll = 0; rebuild(); });
        addRenderableWidget(search);

        amount = new EditBox(font, x + rowW - amountW, y, amountW, bh, Component.literal("count"));
        amount.setHint(Component.literal("1"));
        amount.setValue("1");
        addRenderableWidget(amount);

        int navY = panelY() + panelHeight() - bh - 8;
        int navW = (rowW - 2 * gap) / 3;
        addRenderableWidget(CheatWidget.of(x,                        navY, navW, bh, "BACK",
                () -> minecraft.gui.setScreen(new CheatMenuScreen())));
        addRenderableWidget(CheatWidget.of(x + navW + gap,           navY, navW, bh, "PREV",
                () -> { scroll = Math.max(0, scroll - PAGE); rebuild(); }));
        addRenderableWidget(CheatWidget.of(x + 2 * (navW + gap),     navY, navW, bh, "NEXT",
                () -> { scroll += PAGE; rebuild(); }));

        rebuild();
        setInitialFocus(search);
    }

    private void rebuild() {
        for (CheatWidget b : resultButtons) removeWidget(b);
        resultButtons.clear();

        String q = search.getValue().toLowerCase().trim();
        int rowW = panelWidth() - 20;
        int bh = 18, gap = 2;
        int x = panelX() + 10;
        int y0 = contentY() + bh + 8;

        List<Entry> matches = new ArrayList<>();
        for (Entry e : allItems()) {
            if (q.isEmpty() || e.nameLower.contains(q) || e.id.contains(q)) matches.add(e);
            if (matches.size() > scroll + PAGE) break;
        }
        if (scroll >= matches.size()) scroll = Math.max(0, matches.size() - PAGE);
        int end = Math.min(matches.size(), scroll + PAGE);
        for (int i = scroll; i < end; i++) {
            Entry e = matches.get(i);
            int row = i - scroll;
            int by = y0 + row * (bh + gap);
            CheatWidget b = CheatWidget.of(x, by, rowW, bh, e.display,
                    () -> ToolkitProcedures.giveById(e.id, parseAmount()));
            resultButtons.add(b);
            addRenderableWidget(b);
        }
    }

    private int parseAmount() {
        try {
            int n = Integer.parseInt(amount.getValue());
            return Math.max(1, Math.min(n, 6400));
        } catch (NumberFormatException e) { return 1; }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dx, double dy) {
        if (dy > 0) scroll = Math.max(0, scroll - 3);
        else scroll += 3;
        rebuild();
        return true;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float dt) {
        super.extractRenderState(g, mx, my, dt);
        // page indicator top-right of header
        String pos = "row " + (scroll + 1);
        int w = font.width(pos);
        g.text(font, Component.literal(pos), panelX() + panelWidth() - w - 8, contentY(), Theme.TEXT_DIM, false);
    }
}
