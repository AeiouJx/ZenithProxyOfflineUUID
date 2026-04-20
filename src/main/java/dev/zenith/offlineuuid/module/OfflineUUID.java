package dev.zenith.offlineuuid.module;

import com.zenith.module.api.Module;
import com.zenith.network.client.ClientSession;
import com.zenith.network.codec.PacketHandler;
import com.zenith.network.codec.PacketHandlerCodec;
import com.zenith.network.codec.PacketHandlerStateCodec;
import com.zenith.network.server.ServerSession;
import dev.zenith.offlineuuid.OfflineUUIDConfig;
import dev.zenith.offlineuuid.OfflineUUIDPlugin;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;
import org.geysermc.mcprotocollib.protocol.data.ProtocolState;
import org.geysermc.mcprotocollib.protocol.packet.login.serverbound.ServerboundHelloPacket;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.zenith.Globals.CONFIG;
import static com.zenith.util.config.Config.Authentication.AccountType.OFFLINE;

public class OfflineUUID extends Module {
    private static final Field MINECRAFT_PROTOCOL_PROFILE_FIELD;
    private static final Field GAME_PROFILE_ID_FIELD;

    static {
        try {
            MINECRAFT_PROTOCOL_PROFILE_FIELD = MinecraftProtocol.class.getDeclaredField("profile");
            MINECRAFT_PROTOCOL_PROFILE_FIELD.setAccessible(true);
            GAME_PROFILE_ID_FIELD = GameProfile.class.getDeclaredField("id");
            GAME_PROFILE_ID_FIELD.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public boolean enabledSetting() {
        return OfflineUUIDPlugin.PLUGIN_CONFIG.enabled;
    }

    @Override
    public PacketHandlerCodec registerServerPacketHandlerCodec() {
        return PacketHandlerCodec.serverBuilder()
            .setId("uuid-server")
            .setPriority(10000)
            .state(ProtocolState.LOGIN, PacketHandlerStateCodec.serverBuilder()
                .inbound(ServerboundHelloPacket.class, new ServerUuidPacketHandler())
                .build())
            .build();
    }

    @Override
    public PacketHandlerCodec registerClientPacketHandlerCodec() {
        return PacketHandlerCodec.clientBuilder()
            .setId("uuid-client")
            .setPriority(10000)
            .state(ProtocolState.LOGIN, PacketHandlerStateCodec.clientBuilder()
                .outbound(ServerboundHelloPacket.class, new ClientUuidPacketHandler())
                .build())
            .build();
    }

    public void syncEnabledFromConfig() {
        if (enabledSetting()) enable();
        else disable();
    }

    private static UUID resolveUuid(final OfflineUUIDConfig.SideConfig config, final String username) {
        return switch (config.mode) {
            case ORIGINAL -> null;
            case FIXED -> parseConfiguredUuid(config.fixedUuid);
            case RANDOM -> UUID.randomUUID();
            case GENERATED -> UUID.nameUUIDFromBytes(buildGenerationSource(config, username).getBytes(StandardCharsets.UTF_8));
        };
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

    private static String buildGenerationSource(final OfflineUUIDConfig.SideConfig config, final String username) {
        if (!config.addPrefix) {
            return username;
        }
        final String prefix = config.prefix == null ? "" : config.prefix;
        return prefix + username;
    }

    private static ServerboundHelloPacket applyForcedUuid(final ServerboundHelloPacket packet, final UUID forcedUuid) {
        if (forcedUuid == null || forcedUuid.equals(packet.getProfileId())) {
            return packet;
        }
        return packet.withProfileId(forcedUuid);
    }

    private static void syncClientProtocolProfile(final ClientSession session, final UUID forcedUuid) {
        if (forcedUuid == null) {
            return;
        }
        try {
            final MinecraftProtocol protocol = session.getPacketProtocol();
            final GameProfile profile = protocol.getProfile();
            if (profile == null || forcedUuid.equals(profile.getId())) {
                return;
            }
            GAME_PROFILE_ID_FIELD.set(profile, forcedUuid);
            MINECRAFT_PROTOCOL_PROFILE_FIELD.set(protocol, profile);
        } catch (ReflectiveOperationException e) {
            OfflineUUIDPlugin.LOG.warn("Failed to override client UUID in MinecraftProtocol", e);
        }
    }

    private static final class ServerUuidPacketHandler implements PacketHandler<ServerboundHelloPacket, ServerSession> {
        @Override
        public ServerboundHelloPacket apply(final ServerboundHelloPacket packet, final ServerSession session) {
            final OfflineUUIDConfig pluginConfig = OfflineUUIDPlugin.PLUGIN_CONFIG;
            if (!pluginConfig.enabled || !pluginConfig.server.enabled) {
                return packet;
            }
            final UUID uuid = resolveUuid(pluginConfig.server, packet.getUsername());
            return applyForcedUuid(packet, uuid);
        }
    }

    private static final class ClientUuidPacketHandler implements PacketHandler<ServerboundHelloPacket, ClientSession> {
        @Override
        public ServerboundHelloPacket apply(final ServerboundHelloPacket packet, final ClientSession session) {
            final OfflineUUIDConfig pluginConfig = OfflineUUIDPlugin.PLUGIN_CONFIG;
            if (!pluginConfig.enabled || !pluginConfig.client.enabled) {
                return packet;
            }
            if (CONFIG.authentication.accountType != OFFLINE) {
                return packet;
            }
            final UUID uuid = resolveUuid(pluginConfig.client, packet.getUsername());
            syncClientProtocolProfile(session, uuid);
            return applyForcedUuid(packet, uuid);
        }
    }
}
