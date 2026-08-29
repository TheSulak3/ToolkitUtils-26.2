package net.mcreator.toolkitutils.client.gui.theme;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class Theme {
    private Theme() {}

    // Backdrop / panel
    public static final int BG_OVERLAY   = 0xE0000000;
    public static final int PANEL_BG     = 0xF01A1A20;
    public static final int PANEL_HEAD   = 0xF00E0E12;
    public static final int PANEL_SEP    = 0xFF262632;

    // Accent (cyan)
    public static final int ACCENT       = 0xFF00E5FF;
    public static final int ACCENT_DIM   = 0x6000E5FF;

    // Buttons
    public static final int BTN_BG       = 0xFF1E1E28;
    public static final int BTN_BG_HOV   = 0xFF2A2A38;
    public static final int BTN_BG_ACT   = 0x9000E5FF;
    public static final int BTN_BORDER   = 0xFF3A3A48;
    public static final int BTN_BORDER_H = 0xFF00E5FF;
    public static final int BTN_INACTIVE = 0xFF141420;

    // Text
    public static final int TEXT         = 0xFFEEEEEE;
    public static final int TEXT_DIM     = 0xFF9098A4;
    public static final int TEXT_MUTED   = 0xFF5C6470;
    public static final int TEXT_ACCENT  = 0xFF00E5FF;
    public static final int TEXT_INACTIVE= 0xFF3A3A48;

    // EditBox
    public static final int EDIT_BG      = 0xFF0F0F16;
    public static final int EDIT_BORDER  = 0xFF3A3A48;
    public static final int EDIT_BORDER_F= 0xFF00E5FF;

    public static final String TITLE     = "TOOLKIT UTILS";

    // 1-px stroke rectangle
    public static void border(GuiGraphicsExtractor g, int x1, int y1, int x2, int y2, int color) {
        g.fill(x1,     y1,     x2,     y1 + 1, color);
        g.fill(x1,     y2 - 1, x2,     y2,     color);
        g.fill(x1,     y1,     x1 + 1, y2,     color);
        g.fill(x2 - 1, y1,     x2,     y2,     color);
    }

    // Filled rect + 1-px border
    public static void panel(GuiGraphicsExtractor g, int x1, int y1, int x2, int y2, int fill, int border) {
        g.fill(x1, y1, x2, y2, fill);
        border(g, x1, y1, x2, y2, border);
    }
}
