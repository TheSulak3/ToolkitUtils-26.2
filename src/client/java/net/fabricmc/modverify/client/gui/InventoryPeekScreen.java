package net.fabricmc.modverify.client.gui;

import net.fabricmc.modverify.client.SpyClient;
import net.fabricmc.modverify.client.gui.theme.CheatBaseScreen;
import net.fabricmc.modverify.client.gui.theme.CheatWidget;
import net.fabricmc.modverify.client.gui.theme.Theme;
import net.fabricmc.modverify.network.InvResultPayload;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class InventoryPeekScreen extends CheatBaseScreen {
    private final String targetName;
    private final boolean ender;
    private static final int SLOT = 18;
    private static final int COLS = 9;

    public InventoryPeekScreen(String targetName, boolean ender) {
        super((ender ? "ender: " : "inv: ") + targetName);
        this.targetName = targetName;
        this.ender = ender;
        SpyClient.queryInventory(targetName, ender);
    }

    @Override protected int panelWidth()  { return 220; }
    @Override protected int panelHeight() { return ender ? 190 : 220; }

    @Override
    protected void init() {
        int bh = 18;
        int rowW = panelWidth() - 20;
        int x = panelX() + 10;
        addRenderableWidget(CheatWidget.of(x, panelY() + panelHeight() - bh - 8, rowW, bh, "BACK",
                () -> minecraft.gui.setScreen(new CheatMenuScreen())));
        addRenderableWidget(CheatWidget.of(x, panelY() + panelHeight() - 2 * bh - 12, rowW, bh, "REFRESH",
                () -> SpyClient.queryInventory(targetName, ender)));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float dt) {
        super.extractRenderState(g, mx, my, dt);
        int x0 = panelX() + 10;
        int y0 = contentY() + 4;

        InvResultPayload res = SpyClient.lastInv;
        if (res == null || !res.targetName().equals(targetName) || res.ender() != ender) {
            g.text(font, Component.literal("querying…"), x0, y0, Theme.TEXT_DIM, false);
            return;
        }
        if (res.items().isEmpty()) {
            g.text(font, Component.literal("no data (offline?)"), x0, y0, 0xFFFF6688, false);
            return;
        }

        int rows = (res.items().size() + COLS - 1) / COLS;
        int gridW = COLS * SLOT;
        int gx = panelX() + (panelWidth() - gridW) / 2;
        int gy = y0;

        // slot cells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < COLS; c++) {
                int idx = r * COLS + c;
                if (idx >= res.items().size()) break;
                int sx = gx + c * SLOT, sy = gy + r * SLOT;
                g.fill(sx, sy, sx + SLOT - 1, sy + SLOT - 1, 0xFF141420);
                Theme.border(g, sx, sy, sx + SLOT - 1, sy + SLOT - 1, Theme.BTN_BORDER);
                ItemStack it = res.items().get(idx);
                if (!it.isEmpty()) {
                    g.item(it, sx + 1, sy + 1);
                    g.itemDecorations(font, it, sx + 1, sy + 1);
                }
            }
        }

        // subtitle: which section
        String sub = ender ? "ender chest — 27 slots" : "inventory — main 0-35, armor 36-39, offhand 40";
        g.text(font, Component.literal(sub), x0, gy + rows * SLOT + 4, Theme.TEXT_MUTED, false);
    }
}
