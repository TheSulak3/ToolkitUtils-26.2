package net.mcreator.toolkitutils.client.gui.theme;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Input;
import org.lwjgl.glfw.GLFW;

public abstract class CheatBaseScreen extends Screen {
    protected final String subtitle;

    protected CheatBaseScreen(String subtitle) {
        super(Component.literal(""));
        this.subtitle = subtitle;
    }

    @Override public boolean isPauseScreen() { return false; }

    /**
     * Movement pass-through: while this screen is open, poll WASD/space/shift/ctrl
     * from GLFW every tick and shove them into the player's ClientInput so the
     * character keeps walking, jumping, sneaking, and sprinting. Focused EditBoxes
     * suppress it so typing "wasd" into a text field doesn't launch you across
     * the world.
     */
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
        boolean fwd    = key(win, GLFW.GLFW_KEY_W);
        boolean back   = key(win, GLFW.GLFW_KEY_S);
        boolean left   = key(win, GLFW.GLFW_KEY_A);
        boolean right  = key(win, GLFW.GLFW_KEY_D);
        boolean jump   = key(win, GLFW.GLFW_KEY_SPACE);
        boolean shift  = key(win, GLFW.GLFW_KEY_LEFT_SHIFT) || key(win, GLFW.GLFW_KEY_RIGHT_SHIFT);
        boolean sprint = key(win, GLFW.GLFW_KEY_LEFT_CONTROL) || key(win, GLFW.GLFW_KEY_RIGHT_CONTROL);
        p.input.keyPresses = new Input(fwd, back, left, right, jump, shift, sprint);
        p.input.tick();
    }

    private static boolean key(long window, int keyCode) {
        return GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS;
    }

    @Override
    public void removed() {
        LocalPlayer p = Minecraft.getInstance().player;
        if (p != null) p.input.keyPresses = Input.EMPTY;
        super.removed();
    }

    /** Panel dimensions — override for tighter or wider layouts. */
    protected int panelWidth()  { return 380; }
    protected int panelHeight() { return 240; }
    protected int headerHeight() { return 22; }

    protected int panelX() { return (width  - panelWidth())  / 2; }
    protected int panelY() { return (height - panelHeight()) / 2; }

    /** Y-coordinate for the first row of content inside the panel. */
    protected int contentY() { return panelY() + headerHeight() + 8; }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float dt) {
        g.fill(0, 0, width, height, Theme.BG_OVERLAY);

        int x1 = panelX(), y1 = panelY();
        int x2 = x1 + panelWidth(), y2 = y1 + panelHeight();

        Theme.panel(g, x1, y1, x2, y2, Theme.PANEL_BG, Theme.ACCENT);

        // header strip
        int hy = y1 + headerHeight();
        g.fill(x1, y1, x2, hy, Theme.PANEL_HEAD);
        g.fill(x1, hy, x2, hy + 1, Theme.PANEL_SEP);

        // corner accent ticks
        g.fill(x1, y1, x1 + 8, y1 + 1, Theme.ACCENT);
        g.fill(x1, y1, x1 + 1, y1 + 8, Theme.ACCENT);
        g.fill(x2 - 8, y2 - 1, x2, y2, Theme.ACCENT);
        g.fill(x2 - 1, y2 - 8, x2, y2, Theme.ACCENT);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float dt) {
        int x1 = panelX(), y1 = panelY();

        // title left, subtitle right
        g.text(font, Component.literal(Theme.TITLE), x1 + 8, y1 + 7, Theme.TEXT_ACCENT, false);
        if (subtitle != null && !subtitle.isEmpty()) {
            int w = font.width(subtitle);
            g.text(font, Component.literal(subtitle), panelX() + panelWidth() - w - 8, y1 + 7, Theme.TEXT_DIM, false);
        }

        super.extractRenderState(g, mouseX, mouseY, dt);
    }
}
