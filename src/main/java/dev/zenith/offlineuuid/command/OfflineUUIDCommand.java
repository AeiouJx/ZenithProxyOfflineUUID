package dev.zenith.offlineuuid.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.command.api.Command;
import com.zenith.command.api.CommandCategory;
import com.zenith.command.api.CommandContext;
import com.zenith.command.api.CommandUsage;
import com.zenith.discord.Embed;
import dev.zenith.offlineuuid.module.OfflineUUID;

import java.util.UUID;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.zenith.Globals.MODULE;
import static com.zenith.command.brigadier.ToggleArgumentType.getToggle;
import static com.zenith.command.brigadier.ToggleArgumentType.toggle;
import static dev.zenith.offlineuuid.OfflineUUIDPlugin.PLUGIN_CONFIG;

public class OfflineUUIDCommand extends Command {
    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
            .name("OfflineUUID")
            .category(CommandCategory.MODULE)
            .description("""
                Manage offline UUID behavior for login.
                Uses either a fixed UUID or a deterministic UUID generated from prefix + username.
                """)
            .usageLines(
                "on/off",
                "<prefix>",
                "set <uuid>",
                "clear"
            )
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("offlineUUID")
            .then(argument("toggle", toggle()).executes(c -> {
                PLUGIN_CONFIG.enabled = getToggle(c, "toggle");
                // make sure to sync so the module is actually toggled
                MODULE.get(OfflineUUID.class).syncEnabledFromConfig();
                c.getSource().getEmbed()
                    .title("OfflineUUID Plugin " + toggleStrCaps(PLUGIN_CONFIG.enabled));
                return OK;
            }))
            .then(argument("prefix", string()).executes(c -> {
                PLUGIN_CONFIG.prefix = getString(c, "prefix");
                c.getSource().getEmbed()
                    .title("OfflineUUID Prefix Set")
                    .description(PLUGIN_CONFIG.prefix);
                return OK;
            }))
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
                PLUGIN_CONFIG.offlineUuid = uuid.toString();
                c.getSource().getEmbed()
                    .title("Fixed Offline UUID Set")
                    .description(uuid.toString());
                return OK;
            })))
            .then(literal("clear").executes(c -> {
                PLUGIN_CONFIG.offlineUuid = null;
                c.getSource().getEmbed()
                    .title("Fixed Offline UUID Cleared");
                return OK;
            }));
    }

    @Override
    public void defaultEmbed(Embed embed) {
        embed
            .primaryColor()
            .addField("Enabled", toggleStr(PLUGIN_CONFIG.enabled))
            .addField("Prefix", PLUGIN_CONFIG.prefix)
            .addField("Fixed UUID", String.valueOf(PLUGIN_CONFIG.offlineUuid));
    }
}
