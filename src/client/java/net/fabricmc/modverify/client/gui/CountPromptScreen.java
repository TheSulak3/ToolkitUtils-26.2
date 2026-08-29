package net.fabricmc.modverify.client.gui;

import net.fabricmc.modverify.client.gui.theme.CheatBaseScreen;
import net.fabricmc.modverify.client.gui.theme.CheatWidget;
import net.fabricmc.modverify.client.gui.theme.Theme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.IntConsumer;

public final class CountPromptScreen extends CheatBaseScreen {
    private final String targetLabel;
    private final IntConsumer onSubmit;
    private final Screen returnTo;
    private EditBox items, stacks;

    public CountPromptScreen(String label, IntConsumer onSubmit, Screen returnTo) {
        super("give: " + label);
        this.targetLabel = label;
        this.onSubmit = onSubmit;
        this.returnTo = returnTo;
    }

    @Override protected int panelWidth()  { return 260; }
    @Override protected int panelHeight() { return 170; }

    @Override
    protected void init() {
        int bh = 18, gap = 8;
        int fw = 90;
        int rowW = panelWidth() - 40;
        int x = panelX() + 20;
        int y = contentY() + 10;

        items = new EditBox(font, x + rowW - fw, y, fw, bh, Component.literal("items"));
        items.setHint(Component.literal("1"));
        items.setValue("1");
        addRenderableWidget(items);
        addRenderableWidget(CheatWidget.of(x, y + bh + 4, rowW, bh, "GIVE ITEMS",
                () -> submit(parse(items.getValue(), 1))));

        int y2 = y + 2 * (bh + gap) + 6;
        stacks = new EditBox(font, x + rowW - fw, y2, fw, bh, Component.literal("stacks"));
        stacks.setHint(Component.literal("1"));
        stacks.setValue("1");
        addRenderableWidget(stacks);
        addRenderableWidget(CheatWidget.of(x, y2 + bh + 4, rowW, bh, "GIVE STACKS × 64",
                () -> submit(parse(stacks.getValue(), 1) * 64)));

        addRenderableWidget(CheatWidget.of(x, panelY() + panelHeight() - bh - 8, rowW, bh, "CANCEL",
                () -> minecraft.gui.setScreen(returnTo)));

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

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float dt) {
        super.extractRenderState(g, mx, my, dt);
        int x = panelX() + 20;
        int y = contentY();
        g.text(font, Component.literal("> " + targetLabel.toUpperCase()), x, y, Theme.textAccent(), false);
        int bh = 18;
        g.text(font, Component.literal("items"),  x, y + bh + 15, Theme.TEXT_DIM, false);
        int y2 = y + 3 * bh + 30;
        g.text(font, Component.literal("stacks"), x, y2, Theme.TEXT_DIM, false);
    }
}
