package net.fabricmc.modverify.client.gui;

import net.fabricmc.modverify.client.SpyClient;
import net.fabricmc.modverify.client.gui.theme.CheatBaseScreen;
import net.fabricmc.modverify.client.gui.theme.CheatWidget;
import net.fabricmc.modverify.client.gui.theme.Theme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public final class SeedRevealScreen extends CheatBaseScreen {
    public SeedRevealScreen() { super("world seed"); SpyClient.querySeed(); }

    @Override protected int panelWidth()  { return 320; }
    @Override protected int panelHeight() { return 150; }

    @Override
    protected void init() {
        int bh = 18, rowW = panelWidth() - 20, gap = 4;
        int x = panelX() + 10;
        int third = (rowW - 2 * gap) / 3;
        addRenderableWidget(CheatWidget.of(x, panelY() + panelHeight() - bh - 8, third, bh, "BACK",
                () -> minecraft.gui.setScreen(new CheatMenuScreen())));
        addRenderableWidget(CheatWidget.of(x + third + gap, panelY() + panelHeight() - bh - 8, third, bh, "REFRESH",
                () -> SpyClient.querySeed()));
        addRenderableWidget(CheatWidget.of(x + 2 * (third + gap), panelY() + panelHeight() - bh - 8, third, bh, "COPY",
                () -> {
                    var r = SpyClient.lastSeed;
                    if (r != null) net.minecraft.client.Minecraft.getInstance().keyboardHandler.setClipboard(String.valueOf(r.seed()));
                }));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float dt) {
        super.extractRenderState(g, mx, my, dt);
        int x = panelX() + 14;
        int y = contentY() + 4;
        int lh = font.lineHeight + 2;
        var r = SpyClient.lastSeed;
        if (r == null) {
            g.text(font, Component.literal("querying…"), x, y, Theme.TEXT_DIM, false);
            return;
        }
        g.text(font, Component.literal("motd"), x, y, Theme.TEXT_DIM, false);
        g.text(font, Component.literal(r.worldName()), x + 60, y, Theme.TEXT, false);
        g.text(font, Component.literal("seed"), x, y + lh, Theme.TEXT_DIM, false);
        g.text(font, Component.literal(String.valueOf(r.seed())), x + 60, y + lh, Theme.textAccent(), false);
    }
}
