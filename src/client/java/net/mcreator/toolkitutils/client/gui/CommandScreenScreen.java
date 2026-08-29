package net.mcreator.toolkitutils.client.gui;

import net.mcreator.toolkitutils.procedures.ToolkitProcedures;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class CommandScreenScreen extends Screen {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("toolkit_utils", "textures/screens/command_screen.png");
    private EditBox prompt;
    private static boolean invisible;
    public CommandScreenScreen() { super(Component.literal("Command Screen")); }

    @Override protected void init() {
        int x=(width-176)/2, y=(height-166)/2;
        int w=58,h=20;
        addRenderableWidget(Button.builder(Component.literal("Diamonds"), b->ToolkitProcedures.diamonds()).bounds(x+6,y+12,w,h).build());
        addRenderableWidget(Button.builder(Component.literal("Gapples"), b->ToolkitProcedures.gapples()).bounds(x+66,y+12,w,h).build());
        addRenderableWidget(Button.builder(Component.literal("XP"), b->ToolkitProcedures.experience()).bounds(x+126,y+12,w,h).build());
        addRenderableWidget(Button.builder(Component.literal("Netherite"), b->ToolkitProcedures.netherite()).bounds(x+6,y+36,w,h).build());
        addRenderableWidget(Button.builder(Component.literal("Clear"), b->ToolkitProcedures.clear()).bounds(x+66,y+36,w,h).build());
        addRenderableWidget(Button.builder(Component.literal("Combat"), b->ToolkitProcedures.combat()).bounds(x+126,y+36,w,h).build());
        addRenderableWidget(Button.builder(Component.literal("Flight"), b->ToolkitProcedures.flight()).bounds(x+6,y+60,w,h).build());
        addRenderableWidget(Button.builder(Component.literal("Invicible"), b->ToolkitProcedures.invincible()).bounds(x+66,y+60,w,h).build());
        addRenderableWidget(Button.builder(Component.literal("Creative"), b->ToolkitProcedures.gamemode(minecraft.player)).bounds(x+126,y+60,w,h).build());
        addRenderableWidget(Button.builder(Component.literal("Upgrade"), b->ToolkitProcedures.upgrade()).bounds(x+6,y+84,w,h).build());
        addRenderableWidget(Button.builder(Component.literal("Food"), b->ToolkitProcedures.food()).bounds(x+66,y+84,w,h).build());
        addRenderableWidget(Button.builder(Component.literal("E Gapps"), b->ToolkitProcedures.egapps()).bounds(x+126,y+84,w,h).build());
        addRenderableWidget(Button.builder(Component.literal("Day"), b->ToolkitProcedures.day()).bounds(x+6,y+108,w,h).build());
        addRenderableWidget(Button.builder(Component.literal("Dupe"), b->ToolkitProcedures.dupe()).bounds(x+66,y+108,w,h).build());
        addRenderableWidget(Button.builder(Component.literal("Vanish"), b->{ ToolkitProcedures.vanish(invisible); invisible=!invisible; }).bounds(x+126,y+108,w,h).build());
        prompt = new EditBox(font, x+6, y+132, 116, 20, Component.literal("Command Prompt"));
        addRenderableWidget(prompt);
        addRenderableWidget(Button.builder(Component.literal("Enter"), b->ToolkitProcedures.commandPrompt(prompt.getValue())).bounds(x+124,y+132,46,20).build());
        addRenderableWidget(Button.builder(Component.literal(">"), b->minecraft.gui.setScreen(new CommandScreen1Screen())).bounds(x+148,y+2,22,20).build());
    }
    @Override public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);
        int x=(width-176)/2,y=(height-166)/2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, 176, 166, 176, 166);
    }
    @Override public boolean isPauseScreen() { return false; }
}
