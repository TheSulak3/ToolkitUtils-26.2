package net.mcreator.toolkitutils.client.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.IntConsumer;

public final class CountPromptScreen extends Screen {
    private final IntConsumer onSubmit;
    private final Screen returnTo;
    private EditBox items, stacks;

    public CountPromptScreen(String label, IntConsumer onSubmit, Screen returnTo) {
        super(Component.literal(label));
        this.onSubmit = onSubmit;
        this.returnTo = returnTo;
    }

    @Override public boolean isPauseScreen() { return false; }

    @Override
    protected void init() {
        int w = 180, bh = 18, gap = 6;
        int fw = 90;
        int x = (width - w) / 2;
        int y = (height - (5 * bh + 4 * gap)) / 2;

        items = new EditBox(font, x + w - fw, y + bh + gap, fw, bh, Component.literal("items"));
        items.setHint(Component.literal("64"));
        items.setValue("64");
        addRenderableWidget(items);
        addRenderableWidget(Button.builder(Component.literal("Give items"),
                b -> submit(parse(items.getValue(), 64)))
                .bounds(x, y + 2 * (bh + gap), w, bh).build());

        stacks = new EditBox(font, x + w - fw, y + 3 * (bh + gap), fw, bh, Component.literal("stacks"));
        stacks.setHint(Component.literal("1"));
        stacks.setValue("1");
        addRenderableWidget(stacks);
        addRenderableWidget(Button.builder(Component.literal("Give stacks (x64)"),
                b -> submit(parse(stacks.getValue(), 1) * 64))
                .bounds(x, y + 4 * (bh + gap), w, bh).build());

        addRenderableWidget(Button.builder(Component.literal("Cancel"),
                b -> minecraft.gui.setScreen(returnTo))
                .bounds(x, y + 5 * (bh + gap) + 6, w, bh).build());

        setInitialFocus(items);
    }

    private int parse(String s, int def) {
        try { return Math.max(1, Math.min(Integer.parseInt(s), 65536)); }
        catch (NumberFormatException e) { return def; }
    }

    private void submit(int count) {
        onSubmit.accept(count);
        minecraft.gui.setScreen(returnTo);
    }
}
