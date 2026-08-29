package net.mcreator.toolkitutils.client.gui.theme;

import net.mcreator.toolkitutils.client.UIConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class Theme {
    private Theme() {}

    // Static neutral colors (not user-tunable)
    public static final int PANEL_SEP     = 0xFF262632;
    public static final int BTN_BG        = 0xFF1E1E28;
    public static final int BTN_BG_HOV    = 0xFF2A2A38;
    public static final int BTN_INACTIVE  = 0xFF141420;
    public static final int BTN_BORDER    = 0xFF3A3A48;
    public static final int EDIT_BG       = 0xFF0F0F16;
    public static final int EDIT_BORDER   = 0xFF3A3A48;
    public static final int TEXT          = 0xFFEEEEEE;
    public static final int TEXT_DIM      = 0xFF9098A4;
    public static final int TEXT_MUTED    = 0xFF5C6470;
    public static final int TEXT_INACTIVE = 0xFF3A3A48;
    public static final int TAB_BG        = 0xFF14141C;
    public static final int TAB_BG_ACTIVE = 0xFF1E1E2A;

    public static final String TITLE = "TOOLKIT UTILS";

    // Dynamic colors driven by UIConfig
    public static int bgOverlay()   { return UIConfig.get().bgOverlayArgb(); }
    public static int panelBg()     { return UIConfig.get().panelBgArgb(); }
    public static int panelHead()   { return UIConfig.get().headerBgArgb(); }
    public static int accent()      { return UIConfig.get().accentArgb(); }
    public static int accentDim()   { return UIConfig.get().accentDimArgb(); }
    public static int active()      { return UIConfig.get().activeArgb(); }
    public static int textAccent()  { return accent(); }
    public static int btnBorderH()  { return accent(); }

    public static void border(GuiGraphicsExtractor g, int x1, int y1, int x2, int y2, int color) {
        g.fill(x1,     y1,     x2,     y1 + 1, color);
        g.fill(x1,     y2 - 1, x2,     y2,     color);
        g.fill(x1,     y1,     x1 + 1, y2,     color);
        g.fill(x2 - 1, y1,     x2,     y2,     color);
    }
    public static void panel(GuiGraphicsExtractor g, int x1, int y1, int x2, int y2, int fill, int border) {
        g.fill(x1, y1, x2, y2, fill);
        border(g, x1, y1, x2, y2, border);
    }
}
