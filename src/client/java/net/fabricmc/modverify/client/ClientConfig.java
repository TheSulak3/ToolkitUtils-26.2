package net.fabricmc.modverify.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ClientConfig {
    private static final Logger LOG = LoggerFactory.getLogger("mod_verify/cfg");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("mod_verify-client.json");
    private static ClientConfig instance;

    public String id = "";
    public String code = "";

    public static synchronized ClientConfig get() {
        if (instance == null) instance = load();
        return instance;
    }

    public boolean matches(String enteredId, String enteredCode) {
        return id != null && code != null && id.equals(enteredId) && code.equals(enteredCode);
    }

    private static ClientConfig load() {
        LOG.debug("loading client config from {}", FILE);
        try {
            if (Files.exists(FILE)) {
                ClientConfig loaded = GSON.fromJson(Files.readString(FILE), ClientConfig.class);
                if (loaded != null && loaded.id != null && loaded.code != null) return loaded;
                LOG.warn("client config parsed but invalid, using defaults");
            }
        } catch (IOException | RuntimeException e) {
            LOG.error("failed to read client config, using defaults", e);
        }
        ClientConfig fresh = new ClientConfig();
        fresh.id = "CHANGE_ME";
        fresh.code = "CHANGE_ME";
        fresh.save();
        return fresh;
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(this));
        } catch (IOException e) {
            LOG.error("failed to save client config", e);
        }
    }
}
