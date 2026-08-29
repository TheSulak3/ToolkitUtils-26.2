package net.mcreator.toolkitutils.client.gui;

import net.mcreator.toolkitutils.procedures.ToolkitProcedures;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ItemPickerScreen extends Screen {
    private EditBox search;
    private EditBox amount;
    private final List<Button> resultButtons = new ArrayList<>();
    private int scroll = 0;
    private static final int ROWS = 10;
    private static final int PAGE = ROWS;

    // Cached: (display name lowercased, id string, item) sorted by display name.
    private record Entry(String nameLower, String id, String display) {}
    private static List<Entry> ALL_ITEMS;

    public ItemPickerScreen() { super(Component.literal("")); }

    @Override public boolean isPauseScreen() { return false; }

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
        int w = 260, bh = 18, gap = 2;
        int x = (width - w) / 2;
        int y = 20;

        search = new EditBox(font, x, y, w - 60, bh, Component.literal("search"));
        search.setHint(Component.literal("search items…"));
        search.setResponder(s -> { scroll = 0; rebuild(); });
        addRenderableWidget(search);

        amount = new EditBox(font, x + w - 58, y, 58, bh, Component.literal("count"));
        amount.setHint(Component.literal("64"));
        amount.setValue("64");
        addRenderableWidget(amount);

        addRenderableWidget(Button.builder(Component.literal("Back"),
                b -> minecraft.gui.setScreen(new CheatMenuScreen()))
                .bounds(x, y + (ROWS + 1) * (bh + gap) + 4, 60, bh).build());

        addRenderableWidget(Button.builder(Component.literal("Prev"),
                b -> { scroll = Math.max(0, scroll - PAGE); rebuild(); })
                .bounds(x + 70, y + (ROWS + 1) * (bh + gap) + 4, 60, bh).build());

        addRenderableWidget(Button.builder(Component.literal("Next"),
                b -> { scroll += PAGE; rebuild(); })
                .bounds(x + 140, y + (ROWS + 1) * (bh + gap) + 4, 60, bh).build());

        rebuild();
        setInitialFocus(search);
    }

    private void rebuild() {
        for (Button b : resultButtons) removeWidget(b);
        resultButtons.clear();

        String q = search.getValue().toLowerCase().trim();
        int w = 260, bh = 18, gap = 2;
        int x = (width - w) / 2;
        int y0 = 20 + bh + gap;

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
            Button b = Button.builder(Component.literal(e.display),
                    btn -> ToolkitProcedures.giveById(e.id, parseAmount()))
                    .bounds(x, by, w, bh).build();
            resultButtons.add(b);
            addRenderableWidget(b);
        }
    }

    private int parseAmount() {
        try {
            int n = Integer.parseInt(amount.getValue());
            return Math.max(1, Math.min(n, 6400));
        } catch (NumberFormatException e) { return 64; }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dx, double dy) {
        if (dy > 0) scroll = Math.max(0, scroll - 3);
        else scroll += 3;
        rebuild();
        return true;
    }
}
