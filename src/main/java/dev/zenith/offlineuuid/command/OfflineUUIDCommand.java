package dev.zenith.offlineuuid.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.command.api.Command;
import com.zenith.command.api.CommandCategory;
import com.zenith.command.api.CommandContext;
import com.zenith.command.api.CommandUsage;
import com.zenith.discord.Embed;
import dev.zenith.offlineuuid.OfflineUUIDConfig;
import dev.zenith.offlineuuid.OfflineUUIDPlugin;

import java.util.Locale;
import java.util.UUID;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.zenith.Globals.CONFIG;
import static com.zenith.command.brigadier.EnumStringArgumentType.enumStrings;
import static com.zenith.command.brigadier.ToggleArgumentType.getToggle;
import static com.zenith.command.brigadier.ToggleArgumentType.toggle;
import static dev.zenith.offlineuuid.OfflineUUIDPlugin.PLUGIN_CONFIG;

public class OfflineUUIDCommand extends Command {
    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
            .name("OFFLINEUUID")
            .category(CommandCategory.MODULE)
            .description("""
                Manage offline UUID behavior.
                Sets the UUID used when ZenithProxy connects outward with offline auth.
                """)
            .usageLines(
                "on/off",
                "mode <fixed/random/generated>",
                "set <uuid>",
                "clear",
                "prefix <value>",
                "usePrefix on/off"
            )
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("offlineuuid")
            .then(argument("toggle", toggle()).executes(c -> {
                PLUGIN_CONFIG.enabled = getToggle(c, "toggle");
                if (PLUGIN_CONFIG.enabled) {
                    applyUuid();
                }
                c.getSource().getEmbed()
                    .title("OfflineUUID Plugin " + toggleStrCaps(PLUGIN_CONFIG.enabled));
                return OK;
            }))
            .then(literal("mode").then(argument("mode", enumStrings("fixed", "random", "generated")).executes(c -> {
                PLUGIN_CONFIG.mode = OfflineUUIDConfig.Mode.valueOf(getString(c, "mode").toUpperCase(Locale.ROOT));
                if (PLUGIN_CONFIG.enabled) {
                    applyUuid();
                }
                c.getSource().getEmbed()
                    .title("OfflineUUID Mode Set")
                    .description(PLUGIN_CONFIG.mode.name().toLowerCase(Locale.ROOT));
                return OK;
            })))
            .then(literal("set").then(argument("uuid", string()).executes(c -> {
                final UUID uuid;
                try {
                    uuid = UUID.fromString(getString(c, "uuid"));
                } catch (IllegalArgumentException e) {
                    c.getSource().getEmbed()
                        .title("Invalid UUID")
                        .description("Use the standard 8-4-4-4-12 UUID format.");
                    return ERROR;
                }
                PLUGIN_CONFIG.fixedUuid = uuid.toString();
                if (PLUGIN_CONFIG.enabled && PLUGIN_CONFIG.mode == OfflineUUIDConfig.Mode.FIXED) {
                    applyUuid();
                }
                c.getSource().getEmbed()
                    .title("OfflineUUID Fixed UUID Set")
                    .description(PLUGIN_CONFIG.fixedUuid);
                return OK;
            })))
            .then(literal("clear").executes(c -> {
                PLUGIN_CONFIG.fixedUuid = null;
                CONFIG.authentication.offlineUUID = null;
                c.getSource().getEmbed()
                    .title("OfflineUUID Fixed UUID Cleared");
                return OK;
            }))
            .then(literal("prefix").then(argument("prefix", string()).executes(c -> {
                PLUGIN_CONFIG.prefix = getString(c, "prefix");
                if (PLUGIN_CONFIG.enabled && PLUGIN_CONFIG.mode == OfflineUUIDConfig.Mode.GENERATED) {
                    applyUuid();
                }
                c.getSource().getEmbed()
                    .title("OfflineUUID Prefix Set")
                    .description(PLUGIN_CONFIG.prefix);
                return OK;
            })))
            .then(literal("usePrefix").then(argument("toggle", toggle()).executes(c -> {
                PLUGIN_CONFIG.addPrefix = getToggle(c, "toggle");
                if (PLUGIN_CONFIG.enabled && PLUGIN_CONFIG.mode == OfflineUUIDConfig.Mode.GENERATED) {
                    applyUuid();
                }
                c.getSource().getEmbed()
                    .title("OfflineUUID Prefix Usage " + toggleStrCaps(PLUGIN_CONFIG.addPrefix));
                return OK;
            })));
    }

    private void applyUuid() {
        switch (PLUGIN_CONFIG.mode) {
            case FIXED -> {
                UUID uuid = parseConfiguredUuid(PLUGIN_CONFIG.fixedUuid);
                CONFIG.authentication.offlineUUID = uuid;
            }
            case RANDOM -> CONFIG.authentication.offlineUUID = null;
            case GENERATED -> {
                String source = buildGenerationSource();
                CONFIG.authentication.offlineUUID = UUID.nameUUIDFromBytes(source.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }
        }
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

    private String buildGenerationSource() {
        if (!PLUGIN_CONFIG.addPrefix) {
            return CONFIG.authentication.username;
        }
        final String prefix = PLUGIN_CONFIG.prefix == null ? "" : PLUGIN_CONFIG.prefix;
        return prefix + CONFIG.authentication.username;
    }

    @Override
    public void defaultEmbed(Embed embed) {
        embed
            .primaryColor()
            .addField("Enabled", toggleStr(PLUGIN_CONFIG.enabled))
            .addField("Mode", PLUGIN_CONFIG.mode.name().toLowerCase(Locale.ROOT))
            .addField("Prefix", PLUGIN_CONFIG.prefix)
            .addField("Use Prefix", toggleStr(PLUGIN_CONFIG.addPrefix))
            .addField("Fixed UUID", String.valueOf(PLUGIN_CONFIG.fixedUuid))
            .addField("Current offlineUUID", CONFIG.authentication.offlineUUID != null
                ? CONFIG.authentication.offlineUUID.toString()
                : "(random)");
    }
}
