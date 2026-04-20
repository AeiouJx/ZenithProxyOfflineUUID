package dev.zenith.offlineuuid;

import com.zenith.plugin.api.Plugin;
import com.zenith.plugin.api.PluginAPI;
import com.zenith.plugin.api.ZenithProxyPlugin;
import dev.zenith.offlineuuid.command.OfflineUUIDCommand;
import dev.zenith.offlineuuid.module.OfflineUUID;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;


@Plugin(
    id = BuildConstants.PLUGIN_ID,
    version = BuildConstants.VERSION,
    description = "ZenithProxy UUID control plugin",
    url = "https://github.com/aeioujx/ZenithProxyOfflineUUID",
    authors = {"aeioujx"},
    mcVersions = {BuildConstants.MC_VERSION} // to indicate any MC version: @Plugin(mcVersions = "*")
)
public class OfflineUUIDPlugin implements ZenithProxyPlugin {
    // public static for simple access from modules and commands
    // or alternatively, you could pass these around in constructors
    public static OfflineUUIDConfig PLUGIN_CONFIG;
    public static ComponentLogger LOG;

    @Override
    public void onLoad(PluginAPI pluginAPI) {
        LOG = pluginAPI.getLogger();
        LOG.info("UUID Plugin loading...");
        // initialize any configurations before modules or commands might need to read them
        PLUGIN_CONFIG = pluginAPI.registerConfig(BuildConstants.PLUGIN_ID, OfflineUUIDConfig.class);
        pluginAPI.registerModule(new OfflineUUID());
        pluginAPI.registerCommand(new OfflineUUIDCommand());
        LOG.info("UUID Plugin loaded!");
    }
}
