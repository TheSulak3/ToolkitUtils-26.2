package net.mcreator.toolkitutils.client.gui;

import net.mcreator.toolkitutils.procedures.ToolkitProcedures;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class ItemPickerScreen extends Screen {
    private EditBox search;
    private EditBox amount;
    private final List<Button> resultButtons = new ArrayList<>();
    private int scroll = 0;
    private static final int ROWS = 10;
    private static final int PAGE = ROWS;
    private String lastQuery = "";

    public ItemPickerScreen() { super(Component.literal("")); }

    @Override public boolean isPauseScreen() { return false; }

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

        List<Identifier> matches = new ArrayList<>();
        for (Identifier id : BuiltInRegistries.ITEM.keySet()) {
            String s = id.toString();
            if (q.isEmpty() || s.contains(q) || id.getPath().contains(q)) matches.add(id);
            if (matches.size() > scroll + PAGE) break;
        }
        if (scroll >= matches.size()) scroll = Math.max(0, matches.size() - PAGE);
        int end = Math.min(matches.size(), scroll + PAGE);
        for (int i = scroll; i < end; i++) {
            Identifier id = matches.get(i);
            int row = i - scroll;
            int by = y0 + row * (bh + gap);
            Button b = Button.builder(Component.literal(id.toString()),
                    btn -> ToolkitProcedures.giveById(id.toString(), parseAmount()))
                    .bounds(x, by, w, bh).build();
            resultButtons.add(b);
            addRenderableWidget(b);
        }
        lastQuery = q;
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
