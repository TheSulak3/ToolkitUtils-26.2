package net.mcreator.toolkitutils.client.gui.theme;

import net.mcreator.toolkitutils.client.UIConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Input;
import org.lwjgl.glfw.GLFW;

public abstract class CheatBaseScreen extends Screen {
    protected final String subtitle;

    // Draggable panel offset from center. Persisted in UIConfig on release.
    protected int panelOriginX = Integer.MIN_VALUE;
    protected int panelOriginY = Integer.MIN_VALUE;
    private boolean dragging;
    private int dragOffX, dragOffY;

    protected CheatBaseScreen(String subtitle) {
        super(Component.literal(""));
        this.subtitle = subtitle;
    }

    @Override public boolean isPauseScreen() { return false; }

    protected int panelWidth()   { return 380; }
    protected int panelHeight()  { return 240; }
    protected int headerHeight() { return 22; }

    protected int panelX() {
        if (panelOriginX != Integer.MIN_VALUE) return panelOriginX;
        UIConfig c = UIConfig.get();
        return c.panelX < 0 ? (width - panelWidth()) / 2 : c.panelX;
    }
    protected int panelY() {
        if (panelOriginY != Integer.MIN_VALUE) return panelOriginY;
        UIConfig c = UIConfig.get();
        return c.panelY < 0 ? (height - panelHeight()) / 2 : c.panelY;
    }
    protected int contentY() { return panelY() + headerHeight() + 8; }

    protected boolean styledBackdrop() { return true; }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float dt) {
        if (!styledBackdrop()) { super.extractBackground(g, mouseX, mouseY, dt); return; }

        g.fill(0, 0, width, height, Theme.bgOverlay());

        int x1 = panelX(), y1 = panelY();
        int x2 = x1 + panelWidth(), y2 = y1 + panelHeight();

        Theme.panel(g, x1, y1, x2, y2, Theme.panelBg(), Theme.accent());

        int hy = y1 + headerHeight();
        g.fill(x1, y1, x2, hy, Theme.panelHead());
        g.fill(x1, hy, x2, hy + 1, Theme.PANEL_SEP);

        // Corner accent ticks
        int acc = Theme.accent();
        g.fill(x1, y1, x1 + 8, y1 + 1, acc);
        g.fill(x1, y1, x1 + 1, y1 + 8, acc);
        g.fill(x2 - 8, y2 - 1, x2, y2, acc);
        g.fill(x2 - 1, y2 - 8, x2, y2, acc);

        // "Drag handle" dots to hint the header is grabbable
        int cy = y1 + headerHeight() / 2 - 1;
        int startX = x1 + panelWidth() / 2 - 8;
        for (int i = 0; i < 5; i++) g.fill(startX + i * 4, cy, startX + i * 4 + 2, cy + 2, Theme.TEXT_MUTED);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float dt) {
        if (styledBackdrop()) {
            int x1 = panelX(), y1 = panelY();
            g.text(font, Component.literal(Theme.TITLE), x1 + 8, y1 + 7, Theme.textAccent(), false);
            if (subtitle != null && !subtitle.isEmpty()) {
                int w = font.width(subtitle);
                g.text(font, Component.literal(subtitle), panelX() + panelWidth() - w - 8, y1 + 7, Theme.TEXT_DIM, false);
            }
        }
        super.extractRenderState(g, mouseX, mouseY, dt);
    }

    // --- Draggable panel ---

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        int mx = (int) event.x(), my = (int) event.y();
        if (event.button() == 0 && styledBackdrop() && isOverHeader(mx, my) && !isOverWidget(event)) {
            dragging = true;
            dragOffX = mx - panelX();
            dragOffY = my - panelY();
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (dragging && event.button() == 0) {
            dragging = false;
            UIConfig c = UIConfig.get();
            c.panelX = panelOriginX;
            c.panelY = panelOriginY;
            c.save();
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (dragging) {
            int newX = clamp((int) event.x() - dragOffX, 0, Math.max(0, width - panelWidth()));
            int newY = clamp((int) event.y() - dragOffY, 0, Math.max(0, height - panelHeight()));
            panelOriginX = newX;
            panelOriginY = newY;
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    private boolean isOverHeader(int mx, int my) {
        int x1 = panelX(), y1 = panelY();
        return mx >= x1 && mx < x1 + panelWidth() && my >= y1 && my < y1 + headerHeight();
    }
    private boolean isOverWidget(MouseButtonEvent event) {
        for (var child : this.children()) {
            if (child.isMouseOver(event.x(), event.y())) return true;
        }
        return false;
    }
    private static int clamp(int v, int lo, int hi) { return Math.max(lo, Math.min(hi, v)); }

    // --- Movement pass-through ---
    @Override
    public void tick() {
        super.tick();
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer p = mc.player;
        if (p == null) return;
        if (getFocused() instanceof net.minecraft.client.gui.components.EditBox eb && eb.isFocused()) {
            p.input.keyPresses = Input.EMPTY;
            return;
        }
        long win = mc.getWindow().handle();
        p.input.keyPresses = new Input(
                key(win, GLFW.GLFW_KEY_W),
                key(win, GLFW.GLFW_KEY_S),
                key(win, GLFW.GLFW_KEY_A),
                key(win, GLFW.GLFW_KEY_D),
                key(win, GLFW.GLFW_KEY_SPACE),
                key(win, GLFW.GLFW_KEY_LEFT_SHIFT) || key(win, GLFW.GLFW_KEY_RIGHT_SHIFT),
                key(win, GLFW.GLFW_KEY_LEFT_CONTROL) || key(win, GLFW.GLFW_KEY_RIGHT_CONTROL));
        p.input.tick();
    }
    private static boolean key(long window, int keyCode) { return GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS; }

    @Override
    public void removed() {
        LocalPlayer p = Minecraft.getInstance().player;
        if (p != null) p.input.keyPresses = Input.EMPTY;
        super.removed();
    }
}
