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

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.zenith.Globals.CONFIG;
import static com.zenith.command.brigadier.EnumStringArgumentType.enumStrings;
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
                "mode <original/random/byName>",
                "prefix <value>",
                "prefix clear"
            )
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("offlineUUID")
            .executes(c -> {
                defaultEmbed(c.getSource().getEmbed());
                return OK;
            })
            .then(literal("mode").then(argument("mode", enumStrings("original", "random", "byName")).executes(c -> {
                PLUGIN_CONFIG.mode = OfflineUUIDConfig.Mode.valueOf(getString(c, "mode").toUpperCase(Locale.ROOT));
                OfflineUUID.applyUuid();
                c.getSource().getEmbed()
                    .title("OfflineUUID Mode Set")
                    .description(PLUGIN_CONFIG.mode.name().toLowerCase(Locale.ROOT));
                return OK;
            })))
            .then(literal("prefix").then(argument("prefix", greedyString()).executes(c -> {
                PLUGIN_CONFIG.prefix = getString(c, "prefix").trim();
                if (PLUGIN_CONFIG.mode == OfflineUUIDConfig.Mode.BY_NAME) {
                    OfflineUUID.applyUuid();
                }
                c.getSource().getEmbed()
                    .title("OfflineUUID Prefix Set")
                    .description(PLUGIN_CONFIG.prefix);
                return OK;
            })))
            .then(literal("prefix").then(literal("clear").executes(c -> {
                PLUGIN_CONFIG.prefix = null;
                if (PLUGIN_CONFIG.mode == OfflineUUIDConfig.Mode.BY_NAME) {
                    OfflineUUID.applyUuid();
                }
                c.getSource().getEmbed()
                    .title("OfflineUUID Prefix Cleared");
                return OK;
            })));
    }

    @Override
    public void defaultEmbed(Embed embed) {
        embed
            .primaryColor()
            .addField("Enabled", "on")
            .addField("Mode", PLUGIN_CONFIG.mode.name().toLowerCase(Locale.ROOT))
            .addField("Prefix", PLUGIN_CONFIG.prefix != null ? PLUGIN_CONFIG.prefix : "null")
            .addField("Current offlineUUID", CONFIG.authentication.offlineUUID != null
                ? CONFIG.authentication.offlineUUID.toString()
                : "(random)");
    }
}
