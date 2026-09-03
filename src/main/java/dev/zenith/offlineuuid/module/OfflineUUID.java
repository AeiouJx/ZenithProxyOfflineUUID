package dev.zenith.offlineuuid.module;

import dev.zenith.offlineuuid.OfflineUUIDConfig;
import dev.zenith.offlineuuid.OfflineUUIDPlugin;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.zenith.Globals.CONFIG;

public class OfflineUUID {

    public static void applyUuid() {
        final OfflineUUIDConfig config = OfflineUUIDPlugin.PLUGIN_CONFIG;
        switch (config.mode) {
            case FIXED -> {
                UUID uuid = parseConfiguredUuid(config.fixedUuid);
                CONFIG.authentication.offlineUUID = uuid;
            }
            case RANDOM -> CONFIG.authentication.offlineUUID = null;
            case GENERATED -> {
                String source = buildGenerationSource(config);
                CONFIG.authentication.offlineUUID = UUID.nameUUIDFromBytes(source.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    public static void clearUuid() {
        CONFIG.authentication.offlineUUID = null;
    }

    private static UUID parseConfiguredUuid(final String uuidString) {
        if (uuidString == null || uuidString.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            OfflineUUIDPlugin.LOG.warn("Ignoring invalid configured UUID: {}", uuidString);
            return null;
        }
    }

    private static String buildGenerationSource(final OfflineUUIDConfig config) {
        if (config.prefix == null) {
            return CONFIG.authentication.username;
        }
        return config.prefix + CONFIG.authentication.username;
    }
}
