package net.mcreator.toolkitutils.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class UIConfig {
    private static final Logger LOG = LoggerFactory.getLogger("toolkit_utils/ui");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("toolkit_utils-ui.json");
    private static UIConfig instance;

    // 0..100
    public int opacity     = 88;
    // hex without alpha, e.g. "#00E5FF" or "#FF3366"
    public String accent   = "#00E5FF";
    // -1 = auto-center; otherwise remembered panel top-left
    public int panelX      = -1;
    public int panelY      = -1;
    // click sfx
    public boolean clickSound       = true;

    public static synchronized UIConfig get() {
        if (instance == null) instance = load();
        return instance;
    }

    /** Argb for panel background, opacity applied to a near-black base. */
    public int bgOverlayArgb() {
        int a = clamp(opacity, 0, 100) * 255 / 100;
        return (a << 24);
    }
    public int panelBgArgb() {
        int a = clamp(opacity + 8, 0, 100) * 255 / 100;
        return (a << 24) | 0x1A1A20;
    }
    public int headerBgArgb() {
        int a = clamp(opacity + 8, 0, 100) * 255 / 100;
        return (a << 24) | 0x0E0E12;
    }

    /** Accent argb (fully opaque). */
    public int accentArgb() { return 0xFF000000 | parseHex(accent, 0x00E5FF); }
    /** Accent argb dimmed. */
    public int accentDimArgb() { return 0x8000_0000 | parseHex(accent, 0x00E5FF); }
    /** Active-toggle fill (accent semi-transparent). */
    public int activeArgb() { return 0x9000_0000 | parseHex(accent, 0x00E5FF); }

    private static int parseHex(String s, int fallback) {
        if (s == null) return fallback;
        String t = s.startsWith("#") ? s.substring(1) : s;
        try { return Integer.parseInt(t, 16) & 0xFFFFFF; }
        catch (NumberFormatException e) { return fallback; }
    }
    private static int clamp(int v, int lo, int hi) { return Math.max(lo, Math.min(hi, v)); }

    private static UIConfig load() {
        try {
            if (Files.exists(FILE)) {
                UIConfig loaded = GSON.fromJson(Files.readString(FILE), UIConfig.class);
                if (loaded != null) return loaded;
            }
        } catch (IOException | RuntimeException e) {
            LOG.error("failed reading ui config, using defaults", e);
        }
        UIConfig fresh = new UIConfig();
        fresh.save();
        return fresh;
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(this));
        } catch (IOException e) {
            LOG.error("failed writing ui config", e);
        }
    }
}
