package net.mcreator.toolkitutils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public final class ServerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("toolkit_utils-server.json");
    private static ServerConfig instance;

    public List<String> allowed_uuids = List.of();

    public static synchronized ServerConfig get() {
        if (instance == null) instance = load();
        return instance;
    }

    public boolean isAllowed(UUID uuid) {
        String uuidStr = uuid.toString().toLowerCase();
        for (String allowed : allowed_uuids) {
            if (allowed.replace("-", "").equalsIgnoreCase(uuidStr.replace("-", ""))) return true;
        }
        return false;
    }

    private static ServerConfig load() {
        try {
            if (Files.exists(FILE)) {
                ServerConfig loaded = GSON.fromJson(Files.readString(FILE), ServerConfig.class);
                if (loaded != null && loaded.allowed_uuids != null) return loaded;
            }
        } catch (IOException | RuntimeException ignored) {}

        ServerConfig fresh = new ServerConfig();
        fresh.allowed_uuids = List.of("CHANGE_ME");
        fresh.save();
        return fresh;
    }

    private void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(this));
        } catch (IOException ignored) {}
    }
}
