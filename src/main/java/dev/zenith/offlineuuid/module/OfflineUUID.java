package dev.zenith.offlineuuid.module;

import com.zenith.module.api.Module;
import com.zenith.network.client.ClientSession;
import com.zenith.network.codec.PacketHandler;
import com.zenith.network.codec.PacketHandlerCodec;
import com.zenith.network.codec.PacketHandlerStateCodec;
import com.zenith.network.server.ServerSession;
import dev.zenith.offlineuuid.OfflineUUIDPlugin;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;
import org.geysermc.mcprotocollib.protocol.data.ProtocolState;
import org.geysermc.mcprotocollib.protocol.packet.login.serverbound.ServerboundHelloPacket;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

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
            .setId("offline-uuid-server")
            .setPriority(10000)
            .state(ProtocolState.LOGIN, PacketHandlerStateCodec.serverBuilder()
                .inbound(ServerboundHelloPacket.class, new ServerUuidPacketHandler())
                .build())
            .build();
    }

    @Override
    public PacketHandlerCodec registerClientPacketHandlerCodec() {
        return PacketHandlerCodec.clientBuilder()
            .setId("offline-uuid-client")
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

    private static UUID generateOfflineUUID(final String username) {
        final String configuredUuid = OfflineUUIDPlugin.PLUGIN_CONFIG.offlineUuid;
        if (configuredUuid != null && !configuredUuid.isBlank()) {
            try {
                return UUID.fromString(configuredUuid);
            } catch (IllegalArgumentException e) {
                OfflineUUIDPlugin.LOG.warn("Ignoring invalid offlineUuid in plugin config: {}", configuredUuid);
            }
        }
        final String prefix = OfflineUUIDPlugin.PLUGIN_CONFIG.prefix == null
            ? "OfflinePlayer:"
            : OfflineUUIDPlugin.PLUGIN_CONFIG.prefix;
        return UUID.nameUUIDFromBytes((prefix + username).getBytes(StandardCharsets.UTF_8));
    }

    private static ServerboundHelloPacket applyForcedUuid(final ServerboundHelloPacket packet, final UUID forcedUuid) {
        if (forcedUuid.equals(packet.getProfileId())) {
            return packet;
        }
        return packet.withProfileId(forcedUuid);
    }

    private static void syncClientProtocolProfile(final ClientSession session, final UUID forcedUuid) {
        try {
            final MinecraftProtocol protocol = session.getPacketProtocol();
            final GameProfile profile = protocol.getProfile();
            if (profile == null || forcedUuid.equals(profile.getId())) {
                return;
            }
            GAME_PROFILE_ID_FIELD.set(profile, forcedUuid);
            MINECRAFT_PROTOCOL_PROFILE_FIELD.set(protocol, profile);
        } catch (ReflectiveOperationException e) {
            OfflineUUIDPlugin.LOG.warn("Failed to override client offline UUID in MinecraftProtocol", e);
        }
    }

    private static final class ServerUuidPacketHandler implements PacketHandler<ServerboundHelloPacket, ServerSession> {
        @Override
        public ServerboundHelloPacket apply(final ServerboundHelloPacket packet, final ServerSession session) {
            return applyForcedUuid(packet, generateOfflineUUID(packet.getUsername()));
        }
    }

    private static final class ClientUuidPacketHandler implements PacketHandler<ServerboundHelloPacket, ClientSession> {
        @Override
        public ServerboundHelloPacket apply(final ServerboundHelloPacket packet, final ClientSession session) {
            final UUID forcedUuid = generateOfflineUUID(packet.getUsername());
            syncClientProtocolProfile(session, forcedUuid);
            return applyForcedUuid(packet, forcedUuid);
        }
    }
}
