package net.fabricmc.modverify.client.gui;

import net.fabricmc.modverify.client.SpyClient;
import net.fabricmc.modverify.client.gui.theme.CheatBaseScreen;
import net.fabricmc.modverify.client.gui.theme.CheatWidget;
import net.fabricmc.modverify.client.gui.theme.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public final class ServerInfoScreen extends CheatBaseScreen {
    public ServerInfoScreen() { super("server info"); SpyClient.queryIp(""); }

    @Override protected int panelWidth()  { return 360; }
    @Override protected int panelHeight() { return 220; }

    @Override
    protected void init() {
        int bh = 18, rowW = panelWidth() - 20, gap = 4;
        int x = panelX() + 10;
        int half = (rowW - gap) / 2;
        addRenderableWidget(CheatWidget.of(x, panelY() + panelHeight() - bh - 8, half, bh, "BACK",
                () -> minecraft.gui.setScreen(new CheatMenuScreen())));
        addRenderableWidget(CheatWidget.of(x + half + gap, panelY() + panelHeight() - bh - 8, half, bh, "COPY ALL",
                this::copyAll));
        addRenderableWidget(CheatWidget.of(x, panelY() + panelHeight() - 2 * bh - 12, rowW, bh, "REFRESH",
                () -> SpyClient.queryIp("")));
    }

    private void copyAll() {
        Minecraft mc = Minecraft.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("server_addr=").append(addr(mc)).append('\n');
        sb.append("brand=").append(brand(mc)).append('\n');
        sb.append("protocol=").append(protocol(mc)).append('\n');
        sb.append("world=").append(worldName(mc)).append('\n');
        sb.append("gametype=").append(gm(mc)).append('\n');
        sb.append("players=").append(playerCount(mc)).append('\n');
        sb.append("ping=").append(ping(mc)).append('\n');
        sb.append("uuid=").append(uuid(mc)).append('\n');
        sb.append("bind=").append(bindIp()).append('\n');
        mc.keyboardHandler.setClipboard(sb.toString());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float dt) {
        super.extractRenderState(g, mx, my, dt);
        Minecraft mc = Minecraft.getInstance();
        int x = panelX() + 14;
        int y = contentY() + 4;
        int lh = font.lineHeight + 2;

        row(g, x, y +  0 * lh, "server addr", addr(mc));
        row(g, x, y +  1 * lh, "brand",       brand(mc));
        row(g, x, y +  2 * lh, "protocol",    protocol(mc));
        row(g, x, y +  3 * lh, "world",       worldName(mc));
        row(g, x, y +  4 * lh, "gametype",    gm(mc));
        row(g, x, y +  5 * lh, "players",     playerCount(mc));
        row(g, x, y +  6 * lh, "ping",        ping(mc));
        row(g, x, y +  7 * lh, "your uuid",   uuid(mc));
        row(g, x, y +  8 * lh, "bind ip",     bindIp());
    }

    private void row(GuiGraphicsExtractor g, int x, int y, String label, String value) {
        g.text(font, Component.literal(label), x, y, Theme.TEXT_DIM, false);
        g.text(font, Component.literal(value == null ? "?" : value), x + 90, y, Theme.TEXT, false);
    }

    private static String addr(Minecraft mc) {
        var s = mc.getCurrentServer();
        return s == null ? "singleplayer" : s.ip;
    }
    private static String brand(Minecraft mc) {
        var conn = mc.getConnection();
        return conn == null ? "?" : conn.serverBrand();
    }
    private static String protocol(Minecraft mc) {
        return String.valueOf(net.minecraft.SharedConstants.getProtocolVersion());
    }
    private static String worldName(Minecraft mc) {
        return mc.level == null ? "?" : mc.level.dimension().identifier().toString();
    }
    private static String gm(Minecraft mc) {
        var pgm = mc.getConnection() == null ? null : mc.getConnection().getPlayerInfo(mc.player == null ? null : mc.player.getUUID());
        return pgm == null ? "?" : pgm.getGameMode().getName();
    }
    private static String playerCount(Minecraft mc) {
        var conn = mc.getConnection();
        return conn == null ? "?" : String.valueOf(conn.getOnlinePlayers().size());
    }
    private static String ping(Minecraft mc) {
        var conn = mc.getConnection();
        if (conn == null || mc.player == null) return "?";
        var info = conn.getPlayerInfo(mc.player.getUUID());
        return info == null ? "?" : (info.getLatency() + " ms");
    }
    private static String uuid(Minecraft mc) {
        return mc.player == null ? "?" : mc.player.getUUID().toString();
    }
    private static String bindIp() {
        var r = SpyClient.lastIp;
        return (r != null && r.targetName().isEmpty()) ? (r.ip() + ":" + r.port()) : "querying…";
    }
}
