package dev.zenith.offlineuuid.module;

import com.github.rfresh2.EventConsumer;
import com.zenith.event.client.ClientStartConnectEvent;
import dev.zenith.offlineuuid.OfflineUUIDPlugin;
import com.zenith.module.api.Module;

import java.util.List;

import static com.github.rfresh2.EventConsumer.of;
import static dev.zenith.offlineuuid.OfflineUUIDPlugin.PLUGIN_CONFIG;

public class OfflineUUID extends Module {

    @Override
    public boolean enabledSetting() {
        return true;
    }

    @Override
    public List<EventConsumer<?>> registerEvents() {
        return List.of(
            of(ClientStartConnectEvent.class, this::onStartConnect)
        );
    }

    private void onStartConnect(ClientStartConnectEvent event) {
        applyUuid();
    }

    public static void applyUuid() {
        switch (PLUGIN_CONFIG.mode) {
            case FIXED -> {
                java.util.UUID uuid = parseConfiguredUuid(PLUGIN_CONFIG.fixedUuid);
                com.zenith.Globals.CONFIG.authentication.offlineUUID = uuid;
            }
            case GENERATED -> {
                String source = buildGenerationSource(PLUGIN_CONFIG);
                com.zenith.Globals.CONFIG.authentication.offlineUUID = java.util.UUID.nameUUIDFromBytes(source.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }
        }
    }

    public static void clearUuid() {
        com.zenith.Globals.CONFIG.authentication.offlineUUID = null;
    }

    private static java.util.UUID parseConfiguredUuid(final String uuidString) {
        if (uuidString == null || uuidString.isBlank()) {
            return null;
        }
        try {
            return java.util.UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            OfflineUUIDPlugin.LOG.warn("Ignoring invalid configured UUID: {}", uuidString);
            return null;
        }
    }

    private static String buildGenerationSource(final dev.zenith.offlineuuid.OfflineUUIDConfig config) {
        if (config.prefix == null) {
            return com.zenith.Globals.CONFIG.authentication.username;
        }
        return config.prefix + com.zenith.Globals.CONFIG.authentication.username;
    }
}
