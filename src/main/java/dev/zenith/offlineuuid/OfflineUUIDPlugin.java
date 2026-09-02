package dev.zenith.offlineuuid;

import com.zenith.plugin.api.Plugin;
import com.zenith.plugin.api.PluginAPI;
import com.zenith.plugin.api.ZenithProxyPlugin;
import dev.zenith.offlineuuid.command.OfflineUUIDCommand;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

@Plugin(
    id = BuildConstants.PLUGIN_ID,
    version = BuildConstants.VERSION,
    description = "ZenithProxy offline UUID control plugin",
    url = "https://github.com/aeioujx/ZenithProxyOfflineUUID",
    authors = {"aeioujx"},
    mcVersions = {BuildConstants.MC_VERSION}
)
public class OfflineUUIDPlugin implements ZenithProxyPlugin {
    public static OfflineUUIDConfig PLUGIN_CONFIG;
    public static ComponentLogger LOG;

    @Override
    public void onLoad(PluginAPI pluginAPI) {
        LOG = pluginAPI.getLogger();
        LOG.info("OfflineUUID Plugin loading...");
        PLUGIN_CONFIG = pluginAPI.registerConfig(BuildConstants.PLUGIN_ID, OfflineUUIDConfig.class);
        pluginAPI.registerCommand(new OfflineUUIDCommand());
        LOG.info("OfflineUUID Plugin loaded!");
    }
}
