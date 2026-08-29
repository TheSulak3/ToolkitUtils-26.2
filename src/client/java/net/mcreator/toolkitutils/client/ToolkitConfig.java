package net.mcreator.toolkitutils.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ToolkitConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("toolkit_utils-client.json");
    private static ToolkitConfig instance;

    public String id = "";
    public String code = "";
    public String trigger = "";

    public static synchronized ToolkitConfig get() {
        if (instance == null) instance = load();
        return instance;
    }

    public boolean matches(String enteredId, String enteredCode) {
        return id.equals(enteredId) && code.equals(enteredCode);
    }

    public boolean matchesTrigger(String message) {
        return !trigger.isBlank() && trigger.equals(message);
    }

    private static ToolkitConfig load() {
        try {
            if (Files.exists(FILE)) {
                ToolkitConfig loaded = GSON.fromJson(Files.readString(FILE), ToolkitConfig.class);
                if (loaded != null && loaded.id != null && loaded.code != null) {
                    if (loaded.trigger == null) loaded.trigger = "";
                    return loaded;
                }
            }
        } catch (IOException | RuntimeException ignored) {}

        ToolkitConfig fresh = new ToolkitConfig();
        fresh.id = "CHANGE_ME";
        fresh.code = "CHANGE_ME";
        fresh.trigger = "CHANGE_ME";
        fresh.save();
        return fresh;
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(this));
        } catch (IOException ignored) {}
    }
}
