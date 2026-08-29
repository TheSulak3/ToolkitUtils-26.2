package net.mcreator.toolkitutils.client.gui;

import net.mcreator.toolkitutils.client.SpyClient;
import net.mcreator.toolkitutils.client.gui.theme.CheatBaseScreen;
import net.mcreator.toolkitutils.client.gui.theme.CheatWidget;
import net.mcreator.toolkitutils.client.gui.theme.Theme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public final class IpRevealScreen extends CheatBaseScreen {
    private final String target;

    public IpRevealScreen(String target) {
        super(target.isEmpty() ? "server ip" : ("ip: " + target));
        this.target = target;
        SpyClient.queryIp(target);
    }

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
                () -> SpyClient.queryIp(target)));
        addRenderableWidget(CheatWidget.of(x + 2 * (third + gap), panelY() + panelHeight() - bh - 8, third, bh, "COPY",
                () -> {
                    var r = SpyClient.lastIp;
                    if (r != null && r.targetName().equals(target)) {
                        net.minecraft.client.Minecraft.getInstance().keyboardHandler.setClipboard(r.ip() + ":" + r.port());
                    }
                }));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float dt) {
        super.extractRenderState(g, mx, my, dt);
        int x = panelX() + 14;
        int y = contentY() + 4;
        int lh = font.lineHeight + 2;
        var r = SpyClient.lastIp;
        if (r == null || !r.targetName().equals(target)) {
            g.text(font, Component.literal("querying…"), x, y, Theme.TEXT_DIM, false);
            return;
        }
        g.text(font, Component.literal("target"), x, y, Theme.TEXT_DIM, false);
        g.text(font, Component.literal(target.isEmpty() ? "[SERVER]" : target), x + 60, y, Theme.TEXT, false);
        g.text(font, Component.literal("ip"),   x, y + lh, Theme.TEXT_DIM, false);
        g.text(font, Component.literal(r.ip()), x + 60, y + lh, Theme.textAccent(), false);
        g.text(font, Component.literal("port"), x, y + 2 * lh, Theme.TEXT_DIM, false);
        g.text(font, Component.literal(String.valueOf(r.port())), x + 60, y + 2 * lh, Theme.TEXT, false);
    }
}
