package net.mcreator.toolkitutils.client.gui;

import net.mcreator.toolkitutils.client.ClientInit;
import net.mcreator.toolkitutils.client.ToolkitConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class LoginScreen extends Screen {
    private EditBox idBox, codeBox;

    public LoginScreen() { super(Component.literal("")); }

    @Override public boolean isPauseScreen() { return false; }

    @Override
    protected void init() {
        int w = 160, h = 20, gap = 6;
        int x = (width - w) / 2;
        int y = (height - (3 * h + 2 * gap)) / 2;

        idBox = new EditBox(font, x, y, w, h, Component.literal("id"));
        idBox.setHint(Component.literal("power id"));
        addRenderableWidget(idBox);

        codeBox = new EditBox(font, x, y + h + gap, w, h, Component.literal("code"));
        codeBox.setHint(Component.literal("power code"));
        codeBox.setMaxLength(64);
        addRenderableWidget(codeBox);

        addRenderableWidget(Button.builder(Component.literal("Enter"), b -> check())
                .bounds(x, y + 2 * (h + gap), w, h).build());
        setInitialFocus(idBox);
    }

    private void check() {
        if (ToolkitConfig.get().matches(idBox.getValue(), codeBox.getValue())) {
            ClientInit.AUTHENTICATED = true;
            minecraft.setScreen(new CheatMenuScreen());
        } else {
            codeBox.setValue("");
        }
    }
}
