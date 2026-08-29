package net.mcreator.toolkitutils.client.gui;

import net.mcreator.toolkitutils.client.ClientInit;
import net.mcreator.toolkitutils.client.ToolkitConfig;
import net.mcreator.toolkitutils.client.gui.theme.CheatBaseScreen;
import net.mcreator.toolkitutils.client.gui.theme.CheatWidget;
import net.mcreator.toolkitutils.client.gui.theme.Theme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public final class LoginScreen extends CheatBaseScreen {
    private EditBox idBox, codeBox;
    private boolean failed;

    public LoginScreen() { super("auth"); }

    @Override protected int panelWidth()  { return 260; }
    @Override protected int panelHeight() { return 170; }

    @Override
    protected void init() {
        int w = panelWidth() - 40, h = 18, gap = 8;
        int x = panelX() + 20;
        int y = contentY() + 12;

        addRenderableWidget(labelledEdit(x, y, w, h, "power id", false, e -> idBox = e));
        addRenderableWidget(labelledEdit(x, y + (h + gap) + 10, w, h, "power code", true, e -> codeBox = e));

        addRenderableWidget(CheatWidget.of(x, y + 2 * (h + gap) + 28, w, h, "AUTHENTICATE", this::check));
        addRenderableWidget(CheatWidget.of(x, y + 3 * (h + gap) + 30, w, h, "CANCEL", this::onClose));

        setInitialFocus(idBox);
    }

    private EditBox labelledEdit(int x, int y, int w, int h, String hint, boolean secret, java.util.function.Consumer<EditBox> capture) {
        EditBox e = new EditBox(font, x, y, w, h, Component.literal(hint));
        e.setHint(Component.literal(hint));
        if (secret) e.setMaxLength(128);
        capture.accept(e);
        return e;
    }

    private void check() {
        if (ToolkitConfig.get().matches(idBox.getValue(), codeBox.getValue())) {
            ClientInit.AUTHENTICATED = true;
            minecraft.gui.setScreen(new CheatMenuScreen());
        } else {
            failed = true;
            codeBox.setValue("");
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float dt) {
        super.extractRenderState(g, mx, my, dt);
        int x = panelX() + 20;
        int y = contentY();
        g.text(font, Component.literal("> ACCESS REQUIRED"), x, y, Theme.TEXT_ACCENT, false);
        if (failed) {
            g.text(font, Component.literal("invalid credentials"), x, panelY() + panelHeight() - 14, 0xFFFF4466, false);
        }
    }
}
