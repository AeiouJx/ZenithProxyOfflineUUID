package dev.zenith.offlineuuid.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.command.api.Command;
import com.zenith.command.api.CommandCategory;
import com.zenith.command.api.CommandContext;
import com.zenith.command.api.CommandUsage;
import dev.zenith.offlineuuid.OfflineUUIDConfig;
import dev.zenith.offlineuuid.OfflineUUIDPlugin;
import dev.zenith.offlineuuid.module.OfflineUUID;

import java.util.Locale;
import java.util.UUID;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.zenith.Globals.CONFIG;
import static com.zenith.command.brigadier.EnumStringArgumentType.enumStrings;
import static com.zenith.command.brigadier.ToggleArgumentType.getToggle;
import static com.zenith.command.brigadier.ToggleArgumentType.toggle;
import static dev.zenith.offlineuuid.OfflineUUIDPlugin.PLUGIN_CONFIG;

public class OfflineUUIDCommand extends Command {
    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
            .name("offlineUUID")
            .category(CommandCategory.MODULE)
            .description("""
                Manage offline UUID behavior.
                Sets the UUID used when ZenithProxy connects outward with offline auth.
                """)
            .usageLines(
                "on/off",
                "<uuid>",
                "clear",
                "mode <original/random/byName>",
                "prefix <value>",
                "prefix clear"
            )
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("offlineUUID")
            .then(argument("toggle", toggle()).executes(c -> {
                PLUGIN_CONFIG.enabled = getToggle(c, "toggle");
                if (PLUGIN_CONFIG.enabled) {
                    OfflineUUID.applyUuid();
                }
                c.getSource().getEmbed()
                    .title("OfflineUUID " + (PLUGIN_CONFIG.enabled ? "Enabled" : "Disabled"));
                return OK;
            }))
            .then(argument("uuid", greedyString()).executes(c -> {
                final UUID uuid;
                try {
                    uuid = UUID.fromString(getString(c, "uuid").trim());
                } catch (IllegalArgumentException e) {
                    c.getSource().getEmbed()
                        .title("Invalid UUID")
                        .description("Use the standard 8-4-4-4-12 UUID format.");
                    return ERROR;
                }
                CONFIG.authentication.offlineUUID = uuid;
                c.getSource().getEmbed()
                    .title("OfflineUUID Set")
                    .description(uuid.toString());
                return OK;
            }))
            .then(literal("clear").executes(c -> {
                CONFIG.authentication.offlineUUID = null;
                c.getSource().getEmbed()
                    .title("OfflineUUID Cleared");
                return OK;
            }))
            .then(literal("mode").then(argument("mode", enumStrings("original", "random", "byName")).executes(c -> {
                PLUGIN_CONFIG.mode = OfflineUUIDConfig.Mode.valueOf(getString(c, "mode").toUpperCase(Locale.ROOT));
                OfflineUUID.applyUuid();
                c.getSource().getEmbed()
                    .title("OfflineUUID Mode Set")
                    .description(PLUGIN_CONFIG.mode.name().toLowerCase(Locale.ROOT));
                return OK;
            })))
            .then(literal("prefix").executes(c -> {
                c.getSource().getEmbed()
                    .title("OfflineUUID Prefix")
                    .description(PLUGIN_CONFIG.prefix != null ? PLUGIN_CONFIG.prefix : "null");
                return OK;
            }))
            .then(literal("prefix").then(argument("prefix", greedyString()).executes(c -> {
                PLUGIN_CONFIG.prefix = getString(c, "prefix").trim();
                if (PLUGIN_CONFIG.mode == OfflineUUIDConfig.Mode.BYNAME) {
                    OfflineUUID.applyUuid();
                }
                c.getSource().getEmbed()
                    .title("OfflineUUID Prefix Set")
                    .description(PLUGIN_CONFIG.prefix);
                return OK;
            })))
            .then(literal("prefix").then(literal("clear").executes(c -> {
                PLUGIN_CONFIG.prefix = null;
                if (PLUGIN_CONFIG.mode == OfflineUUIDConfig.Mode.BYNAME) {
                    OfflineUUID.applyUuid();
                }
                c.getSource().getEmbed()
                    .title("OfflineUUID Prefix Cleared");
                return OK;
            })));
    }
}
