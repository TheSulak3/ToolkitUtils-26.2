package net.mcreator.toolkitutils.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class CommandScreen1Screen extends Screen {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("toolkit_utils", "textures/screens/command_screen_1.png");
    public CommandScreen1Screen() { super(Component.literal("Command Screen")); }
    @Override protected void init(){
        int x=(width-176)/2,y=(height-166)/2;
        addRenderableWidget(Button.builder(Component.literal("<"), b->minecraft.gui.setScreen(new CommandScreenScreen())).bounds(x+6,y+4,22,20).build());
    }
    @Override public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);
        int x=(width-176)/2,y=(height-166)/2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, 176, 166, 176, 166);
    }
    @Override public boolean isPauseScreen() { return false; }
}
