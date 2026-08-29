package net.fabricmc.modverify.client.gui;

import net.fabricmc.modverify.client.ClientInit;
import net.fabricmc.modverify.client.ClientConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Disguised as a vanilla-styled "mod integrity check" dialog. Looks like the
 * kind of boring prompt every modded MC install throws up at some point — a
 * shoulder-surfer glances at it and dismisses. Uses vanilla widget styling
 * deliberately (no cheat theme) to blend in.
 */
public final class LoginScreen extends Screen {
    private EditBox idBox, codeBox;
    private boolean failed;
    private int attempts;

    public LoginScreen() { super(Component.literal("Mod Integrity Check")); }

    @Override public boolean isPauseScreen() { return false; }

    @Override
    protected void init() {
        int w = 220, h = 20, gap = 8;
        int x = (width - w) / 2;
        int y = height / 2 - 60;

        idBox = new EditBox(font, x, y, w, h, Component.literal("session id"));
        idBox.setHint(Component.literal("session id"));
        idBox.setMaxLength(64);
        addRenderableWidget(idBox);

        codeBox = new EditBox(font, x, y + h + gap, w, h, Component.literal("verification hash"));
        codeBox.setHint(Component.literal("verification hash"));
        codeBox.setMaxLength(128);
        addRenderableWidget(codeBox);

        addRenderableWidget(Button.builder(Component.literal("Verify"), b -> check())
                .bounds(x, y + 2 * (h + gap), w / 2 - 4, h).build());
        addRenderableWidget(Button.builder(Component.literal("Dismiss"), b -> onClose())
                .bounds(x + w / 2 + 4, y + 2 * (h + gap), w / 2 - 4, h).build());

        setInitialFocus(idBox);
    }

    private void check() {
        if (ClientConfig.get().matches(idBox.getValue(), codeBox.getValue())) {
            ClientInit.AUTHENTICATED = true;
            minecraft.gui.setScreen(new CheatMenuScreen());
        } else {
            failed = true;
            attempts++;
            codeBox.setValue("");
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float dt) {
        super.extractRenderState(g, mx, my, dt);
        String title = "Mod Integrity Check";
        int tw = font.width(title);
        g.text(font, Component.literal(title), (width - tw) / 2, height / 2 - 100, 0xFFBFBFBF, true);

        String line1 = "One or more installed mods require a signed session";
        String line2 = "verification. Enter the credentials provided by your";
        String line3 = "server or mod administrator.";
        int y = height / 2 - 84;
        g.text(font, Component.literal(line1), (width - font.width(line1)) / 2, y, 0xFF808080, false);
        g.text(font, Component.literal(line2), (width - font.width(line2)) / 2, y + 10, 0xFF808080, false);
        g.text(font, Component.literal(line3), (width - font.width(line3)) / 2, y + 20, 0xFF808080, false);

        if (failed) {
            String msg = "verification failed (" + attempts + ")";
            int mw = font.width(msg);
            g.text(font, Component.literal(msg), (width - mw) / 2, height / 2 + 24, 0xFF9E4444, false);
        }
    }
}
