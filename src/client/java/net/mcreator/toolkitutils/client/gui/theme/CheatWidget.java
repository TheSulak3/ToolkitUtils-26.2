package net.mcreator.toolkitutils.client.gui.theme;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;

public final class CheatWidget extends AbstractWidget {
    private final Runnable action;
    private final BooleanSupplier isActive; // for toggle-style highlight, may be null

    public CheatWidget(int x, int y, int w, int h, Component msg, Runnable action) {
        this(x, y, w, h, msg, action, null);
    }

    public CheatWidget(int x, int y, int w, int h, Component msg, Runnable action, BooleanSupplier isActive) {
        super(x, y, w, h, msg);
        this.action = action;
        this.isActive = isActive;
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        if (this.active && action != null) {
            action.run();
            AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
        }
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor g, int mx, int my, float dt) {
        int x1 = getX(), y1 = getY();
        int x2 = x1 + getWidth(), y2 = y1 + getHeight();
        boolean hov = isHoveredOrFocused();
        boolean act = isActive != null && isActive.getAsBoolean();

        int bg     = !this.active ? Theme.BTN_INACTIVE
                    : act ? Theme.BTN_BG_ACT
                    : hov ? Theme.BTN_BG_HOV
                    : Theme.BTN_BG;
        int border = !this.active ? Theme.BTN_BORDER
                    : (hov || act) ? Theme.BTN_BORDER_H
                    : Theme.BTN_BORDER;
        int text   = !this.active ? Theme.TEXT_INACTIVE
                    : act ? 0xFF000000
                    : hov ? Theme.TEXT_ACCENT
                    : Theme.TEXT;

        g.fill(x1, y1, x2, y2, bg);
        // top accent line on hover
        if (hov && !act) g.fill(x1, y1, x2, y1 + 1, Theme.ACCENT);
        Theme.border(g, x1, y1, x2, y2, border);

        Font font = Minecraft.getInstance().font;
        g.centeredText(font, getMessage(),
                x1 + getWidth() / 2,
                y1 + (getHeight() - font.lineHeight) / 2 + 1,
                text);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput out) {
        defaultButtonNarrationText(out);
    }

    public static CheatWidget of(int x, int y, int w, int h, String label, Runnable action) {
        return new CheatWidget(x, y, w, h, Component.literal(label), action);
    }

    public static CheatWidget toggle(int x, int y, int w, int h, String label, Runnable action, BooleanSupplier isActive) {
        return new CheatWidget(x, y, w, h, Component.literal(label), action, isActive);
    }
}
