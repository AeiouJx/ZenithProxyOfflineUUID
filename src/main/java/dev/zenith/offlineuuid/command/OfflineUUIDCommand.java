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
import static com.mojang.brigadier.arguments.StringArgumentType.string;
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
                "get",
                "mode <fixed/random/generated>",
                "set <uuid>",
                "clear",
                "prefix <value>",
                "prefix clear"
            )
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("offlineUUID")
            .then(literal("get").executes(c -> {
                c.getSource().getEmbed()
                    .title("OfflineUUID Status");
                return OK;
            }))
            .then(literal("mode").then(argument("mode", enumStrings("fixed", "random", "generated")).executes(c -> {
                PLUGIN_CONFIG.mode = OfflineUUIDConfig.Mode.valueOf(getString(c, "mode").toUpperCase(Locale.ROOT));
                OfflineUUID.applyUuid();
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
                if (PLUGIN_CONFIG.mode == OfflineUUIDConfig.Mode.FIXED) {
                    OfflineUUID.applyUuid();
                }
                c.getSource().getEmbed()
                    .title("OfflineUUID Fixed UUID Set")
                    .description(PLUGIN_CONFIG.fixedUuid);
                return OK;
            })))
            .then(literal("clear").executes(c -> {
                PLUGIN_CONFIG.fixedUuid = null;
                OfflineUUID.clearUuid();
                c.getSource().getEmbed()
                    .title("OfflineUUID Fixed UUID Cleared");
                return OK;
            }))
            .then(literal("prefix").then(argument("prefix", string()).executes(c -> {
                PLUGIN_CONFIG.prefix = getString(c, "prefix");
                if (PLUGIN_CONFIG.mode == OfflineUUIDConfig.Mode.GENERATED) {
                    OfflineUUID.applyUuid();
                }
                c.getSource().getEmbed()
                    .title("OfflineUUID Prefix Set")
                    .description(PLUGIN_CONFIG.prefix);
                return OK;
            })))
            .then(literal("prefix").then(literal("clear").executes(c -> {
                PLUGIN_CONFIG.prefix = null;
                if (PLUGIN_CONFIG.mode == OfflineUUIDConfig.Mode.GENERATED) {
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
            .addField("Prefix", String.valueOf(PLUGIN_CONFIG.prefix))
            .addField("Fixed UUID", String.valueOf(PLUGIN_CONFIG.fixedUuid))
            .addField("Current offlineUUID", CONFIG.authentication.offlineUUID != null
                ? CONFIG.authentication.offlineUUID.toString()
                : "(random)");
    }
}
