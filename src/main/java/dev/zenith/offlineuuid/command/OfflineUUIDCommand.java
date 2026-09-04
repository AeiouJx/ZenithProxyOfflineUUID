package dev.zenith.offlineuuid.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.command.api.Command;
import com.zenith.command.api.CommandCategory;
import com.zenith.command.api.CommandContext;
import com.zenith.command.api.CommandUsage;
import com.zenith.discord.Embed;
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
                    .title("OfflineUUID " + toggleStrCaps(PLUGIN_CONFIG.enabled));
            }))
            .then(argument("uuid", greedyString()).executes(c -> {
                try {
                    UUID uuid = UUID.fromString(getString(c, "uuid").trim());
                    CONFIG.authentication.offlineUUID = uuid;
                    c.getSource().getEmbed()
                        .title("OfflineUUID Set");
                } catch (IllegalArgumentException e) {
                    c.getSource().getEmbed()
                        .title("Invalid UUID")
                        .description("Use the standard 8-4-4-4-12 UUID format.");
                }
            }))
            .then(literal("clear").executes(c -> {
                CONFIG.authentication.offlineUUID = null;
                c.getSource().getEmbed()
                    .title("OfflineUUID Cleared");
            }))
            .then(literal("mode").then(argument("mode", enumStrings("original", "random", "byName")).executes(c -> {
                PLUGIN_CONFIG.mode = OfflineUUIDConfig.Mode.valueOf(getString(c, "mode").toUpperCase(Locale.ROOT));
                if (PLUGIN_CONFIG.enabled) {
                    OfflineUUID.applyUuid();
                }
                c.getSource().getEmbed()
                    .title("OfflineUUID Mode Set");
            })))
            .then(literal("prefix").executes(c -> {
                c.getSource().getEmbed()
                    .title("OfflineUUID Prefix")
                    .description(PLUGIN_CONFIG.prefix != null ? PLUGIN_CONFIG.prefix : "null");
            }))
            .then(literal("prefix").then(argument("prefix", greedyString()).executes(c -> {
                PLUGIN_CONFIG.prefix = getString(c, "prefix").trim();
                if (PLUGIN_CONFIG.enabled && PLUGIN_CONFIG.mode == OfflineUUIDConfig.Mode.BYNAME) {
                    OfflineUUID.applyUuid();
                }
                c.getSource().getEmbed()
                    .title("OfflineUUID Prefix Set");
            })))
            .then(literal("prefix").then(literal("clear").executes(c -> {
                PLUGIN_CONFIG.prefix = null;
                if (PLUGIN_CONFIG.enabled && PLUGIN_CONFIG.mode == OfflineUUIDConfig.Mode.BYNAME) {
                    OfflineUUID.applyUuid();
                }
                c.getSource().getEmbed()
                    .title("OfflineUUID Prefix Cleared");
            })));
    }

    @Override
    public void defaultEmbed(Embed embed) {
        embed
            .primaryColor()
            .addField("Enabled", toggleStr(PLUGIN_CONFIG.enabled))
            .addField("Mode", PLUGIN_CONFIG.mode.name().toLowerCase(Locale.ROOT))
            .addField("Prefix", PLUGIN_CONFIG.prefix != null ? PLUGIN_CONFIG.prefix : "null")
            .addField("Current offlineUUID", CONFIG.authentication.offlineUUID != null
                ? CONFIG.authentication.offlineUUID.toString()
                : "(random)");
    }
}
