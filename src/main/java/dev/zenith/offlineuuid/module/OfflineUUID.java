package dev.zenith.offlineuuid.module;

import com.github.rfresh2.EventConsumer;
import com.zenith.event.client.ClientStartConnectEvent;
import dev.zenith.offlineuuid.OfflineUUIDConfig;
import dev.zenith.offlineuuid.OfflineUUIDPlugin;
import com.zenith.module.api.Module;

import java.util.List;
import java.util.UUID;

import static com.github.rfresh2.EventConsumer.of;
import static com.zenith.Globals.CONFIG;
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
            case ORIGINAL -> CONFIG.authentication.offlineUUID = null;
            case RANDOM -> CONFIG.authentication.offlineUUID = UUID.randomUUID();
            case BYNAME -> {
                String source = buildGenerationSource(PLUGIN_CONFIG);
                CONFIG.authentication.offlineUUID = UUID.nameUUIDFromBytes(source.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }
        }
    }

    public static void clearUuid() {
        CONFIG.authentication.offlineUUID = null;
    }

    private static String buildGenerationSource(final OfflineUUIDConfig config) {
        if (config.prefix == null) {
            return CONFIG.authentication.username;
        }
        return config.prefix + CONFIG.authentication.username;
    }
}
