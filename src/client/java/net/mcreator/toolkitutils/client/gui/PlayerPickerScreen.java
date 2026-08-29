package net.mcreator.toolkitutils.client.gui;

import net.mcreator.toolkitutils.client.gui.theme.CheatBaseScreen;
import net.mcreator.toolkitutils.client.gui.theme.CheatWidget;
import net.mcreator.toolkitutils.client.gui.theme.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/** Lists online players (from client's tab-list snapshot). Click sends the name back. */
public final class PlayerPickerScreen extends CheatBaseScreen {
    private final String title;
    private final Consumer<String> onPick;
    private final Screen returnTo;
    private final boolean includeSelfOption; // adds a "[SERVER]" pseudo-entry at top

    public PlayerPickerScreen(String title, Consumer<String> onPick, Screen returnTo) {
        this(title, onPick, returnTo, false);
    }
    public PlayerPickerScreen(String title, Consumer<String> onPick, Screen returnTo, boolean includeServer) {
        super(title);
        this.title = title;
        this.onPick = onPick;
        this.returnTo = returnTo;
        this.includeSelfOption = includeServer;
    }

    @Override protected int panelWidth()  { return 320; }
    @Override protected int panelHeight() { return 260; }

    @Override
    protected void init() {
        int bh = 18, gap = 3;
        int rowW = panelWidth() - 20;
        int x = panelX() + 10;
        int y = contentY() + 4;

        List<String> names = new ArrayList<>();
        var conn = Minecraft.getInstance().getConnection();
        if (conn != null) {
            for (var pi : conn.getListedOnlinePlayers()) names.add(pi.getProfile().getName());
        }
        names.sort(Comparator.naturalOrder());
        if (includeSelfOption) names.add(0, "[SERVER]");

        int rowsPerCol = 10;
        int cols = Math.max(1, (names.size() + rowsPerCol - 1) / rowsPerCol);
        int bw = (rowW - (cols - 1) * gap) / cols;

        for (int i = 0; i < names.size(); i++) {
            int r = i % rowsPerCol, c = i / rowsPerCol;
            final String n = names.get(i);
            addRenderableWidget(CheatWidget.of(
                    x + c * (bw + gap), y + r * (bh + gap), bw, bh, n,
                    () -> { onPick.accept(n.equals("[SERVER]") ? "" : n); minecraft.gui.setScreen(returnTo); }));
        }

        addRenderableWidget(CheatWidget.of(x, panelY() + panelHeight() - bh - 8, rowW, bh, "BACK",
                () -> minecraft.gui.setScreen(returnTo)));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float dt) {
        super.extractRenderState(g, mx, my, dt);
        g.text(font, Component.literal("> pick a target"), panelX() + 10, contentY() - 12, Theme.textAccent(), false);
    }
}
