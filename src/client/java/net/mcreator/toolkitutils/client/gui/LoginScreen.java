package net.mcreator.toolkitutils.client.gui;

import net.mcreator.toolkitutils.client.ToolkitConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class LoginScreen extends Screen {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("toolkit_utils", "textures/screens/login.png");
    private EditBox idBox;
    private EditBox passBox;

    public LoginScreen(Screen ignored) { super(Component.translatable("block.toolkit_utils.toolbox")); }

    @Override protected void init() {
        int x = (this.width - 176) / 2;
        int y = (this.height - 166) / 2;
        idBox = new EditBox(this.font, x + 24, y + 34, 120, 20, Component.translatable("gui.toolkit_utils.login.powersid"));
        passBox = new EditBox(this.font, x + 24, y + 58, 120, 20, Component.translatable("gui.toolkit_utils.login.powerscode"));
        passBox.setMaxLength(32767);
        addRenderableWidget(idBox);
        addRenderableWidget(passBox);
        addRenderableWidget(Button.builder(Component.translatable("gui.toolkit_utils.login.button_check"), b -> check()).bounds(x + 24, y + 86, 120, 20).build());
        setInitialFocus(idBox);
    }

    private void check() {
        ToolkitConfig config = ToolkitConfig.get();
        if (config.matches(idBox.getValue(), passBox.getValue())) {
            net.mcreator.toolkitutils.client.ClientInit.AUTHENTICATED = true;
            minecraft.gui.setScreen(new CommandScreenScreen());
        } else {
            passBox.setValue("");
        }
    }

    @Override public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);
        int x=(width-176)/2,y=(height-166)/2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, 176, 166, 176, 166);
    }

    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        int x=(width-176)/2,y=(height-166)/2;
        graphics.text(font, Component.translatable("gui.toolkit_utils.login.label_error_page_could_not_load"), x+10, y+4, 0xFF3C3333, false);
        graphics.text(font, Component.translatable("gui.toolkit_utils.login.label_details"), x+10, y+110, 0xFF3C3333, false);
        graphics.text(font, Component.translatable("gui.toolkit_utils.login.label_missing_texture_root_file"), x+10, y+122, 0xFF3C3333, false);
    }

    @Override public boolean shouldCloseOnEsc() { return true; }
    @Override public boolean isPauseScreen() { return false; }
}
