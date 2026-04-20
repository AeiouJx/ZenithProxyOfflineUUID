package dev.zenith.offlineuuid.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.command.api.Command;
import com.zenith.command.api.CommandCategory;
import com.zenith.command.api.CommandContext;
import com.zenith.command.api.CommandUsage;
import com.zenith.discord.Embed;
import dev.zenith.offlineuuid.OfflineUUIDConfig;
import dev.zenith.offlineuuid.module.OfflineUUID;

import java.util.Locale;
import java.util.UUID;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.zenith.Globals.MODULE;
import static com.zenith.command.brigadier.EnumStringArgumentType.enumStrings;
import static com.zenith.command.brigadier.ToggleArgumentType.getToggle;
import static com.zenith.command.brigadier.ToggleArgumentType.toggle;
import static dev.zenith.offlineuuid.OfflineUUIDPlugin.PLUGIN_CONFIG;

public class OfflineUUIDCommand extends Command {
    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
            .name("UUID")
            .category(CommandCategory.MODULE)
            .description("""
                Manage server-side and client-side UUID rewrite behavior.
                Server affects players connecting to ZenithProxy.
                Client affects ZenithProxy connecting outward while using offline auth.
                """)
            .usageLines(
                "on/off",
                "server on/off",
                "client on/off",
                "server mode <original/fixed/random/generated>",
                "client mode <original/fixed/random/generated>",
                "server prefix <value>",
                "client prefix <value>",
                "server usePrefix on/off",
                "client usePrefix on/off",
                "server set <uuid>",
                "client set <uuid>",
                "server clear",
                "client clear"
            )
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("uuid")
            .then(argument("toggle", toggle()).executes(c -> {
                PLUGIN_CONFIG.enabled = getToggle(c, "toggle");
                MODULE.get(OfflineUUID.class).syncEnabledFromConfig();
                c.getSource().getEmbed()
                    .title("UUID Plugin " + toggleStrCaps(PLUGIN_CONFIG.enabled));
                return OK;
            }))
            .then(sideCommand("server", PLUGIN_CONFIG.server))
            .then(sideCommand("client", PLUGIN_CONFIG.client));
    }

    private LiteralArgumentBuilder<CommandContext> sideCommand(final String sideName, final OfflineUUIDConfig.SideConfig config) {
        return literal(sideName)
            .then(argument("toggle", toggle()).executes(c -> {
                config.enabled = getToggle(c, "toggle");
                c.getSource().getEmbed()
                    .title(titlePrefix(sideName) + " " + toggleStrCaps(config.enabled));
                return OK;
            }))
            .then(literal("mode").then(argument("mode", enumStrings("original", "fixed", "random", "generated")).executes(c -> {
                config.mode = OfflineUUIDConfig.Mode.valueOf(getString(c, "mode").toUpperCase(Locale.ROOT));
                c.getSource().getEmbed()
                    .title(titlePrefix(sideName) + " Mode Set")
                    .description(config.mode.name().toLowerCase(Locale.ROOT));
                return OK;
            })))
            .then(literal("prefix").then(argument("prefix", string()).executes(c -> {
                config.prefix = getString(c, "prefix");
                c.getSource().getEmbed()
                    .title(titlePrefix(sideName) + " Prefix Set")
                    .description(config.prefix);
                return OK;
            })))
            .then(literal("usePrefix").then(argument("toggle", toggle()).executes(c -> {
                config.addPrefix = getToggle(c, "toggle");
                c.getSource().getEmbed()
                    .title(titlePrefix(sideName) + " Prefix Usage " + toggleStrCaps(config.addPrefix));
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
                config.fixedUuid = uuid.toString();
                c.getSource().getEmbed()
                    .title(titlePrefix(sideName) + " Fixed UUID Set")
                    .description(config.fixedUuid);
                return OK;
            })))
            .then(literal("clear").executes(c -> {
                config.fixedUuid = null;
                c.getSource().getEmbed()
                    .title(titlePrefix(sideName) + " Fixed UUID Cleared");
                return OK;
            }));
    }

    private String titlePrefix(final String sideName) {
        return Character.toUpperCase(sideName.charAt(0)) + sideName.substring(1) + " UUID";
    }

    @Override
    public void defaultEmbed(Embed embed) {
        embed
            .primaryColor()
            .addField("Enabled", toggleStr(PLUGIN_CONFIG.enabled))
            .addField("Server Enabled", toggleStr(PLUGIN_CONFIG.server.enabled))
            .addField("Server Mode", PLUGIN_CONFIG.server.mode.name().toLowerCase(Locale.ROOT))
            .addField("Server Prefix", PLUGIN_CONFIG.server.prefix)
            .addField("Server Use Prefix", toggleStr(PLUGIN_CONFIG.server.addPrefix))
            .addField("Server Fixed UUID", String.valueOf(PLUGIN_CONFIG.server.fixedUuid))
            .addField("Client Enabled", toggleStr(PLUGIN_CONFIG.client.enabled))
            .addField("Client Mode", PLUGIN_CONFIG.client.mode.name().toLowerCase(Locale.ROOT))
            .addField("Client Prefix", PLUGIN_CONFIG.client.prefix)
            .addField("Client Use Prefix", toggleStr(PLUGIN_CONFIG.client.addPrefix))
            .addField("Client Fixed UUID", String.valueOf(PLUGIN_CONFIG.client.fixedUuid));
    }
}
