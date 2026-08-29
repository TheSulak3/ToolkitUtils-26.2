package net.mcreator.toolkitutils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public final class ServerConfig {
    private static final Logger LOG = LoggerFactory.getLogger("toolkit_utils/cfg");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("toolkit_utils-server.json");
    private static ServerConfig instance;

    public List<String> allowed_uuids = List.of();

    public static synchronized ServerConfig get() {
        if (instance == null) instance = load();
        return instance;
    }

    public boolean isAllowed(UUID uuid) {
        String uuidStr = uuid.toString().toLowerCase().replace("-", "");
        for (String allowed : allowed_uuids) {
            if (allowed == null) continue;
            if (allowed.replace("-", "").equalsIgnoreCase(uuidStr)) {
                LOG.info("[toolkit_utils] whitelist hit: {} matches entry '{}'", uuid, allowed);
                return true;
            }
        }
        LOG.warn("[toolkit_utils] whitelist miss for {} (checked {} entries)", uuid, allowed_uuids.size());
        return false;
    }

    private static ServerConfig load() {
        LOG.info("[toolkit_utils] loading server config from {}", FILE);
        try {
            if (Files.exists(FILE)) {
                ServerConfig loaded = GSON.fromJson(Files.readString(FILE), ServerConfig.class);
                if (loaded != null && loaded.allowed_uuids != null) {
                    LOG.info("[toolkit_utils] server config loaded ok ({} entries)", loaded.allowed_uuids.size());
                    return loaded;
                }
                LOG.warn("[toolkit_utils] server config parsed but was null/invalid, using defaults");
            } else {
                LOG.warn("[toolkit_utils] server config not found, creating default");
            }
        } catch (IOException | RuntimeException e) {
            LOG.error("[toolkit_utils] failed to read server config, using defaults", e);
        }

        ServerConfig fresh = new ServerConfig();
        fresh.allowed_uuids = List.of("CHANGE_ME");
        fresh.save();
        return fresh;
    }

    private void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(this));
            LOG.info("[toolkit_utils] wrote default server config to {}", FILE);
        } catch (IOException e) {
            LOG.error("[toolkit_utils] failed to save server config", e);
        }
    }
}
