package dev.zenith.offlineuuid;

/**
 * Plugin configuration persisted by ZenithProxy.
 */
public class OfflineUUIDConfig {
    public boolean enabled = false;
    public SideConfig server = new SideConfig();
    public SideConfig client = new SideConfig();

    public static final class SideConfig {
        public boolean enabled = false;
        public Mode mode = Mode.GENERATED;
        public String fixedUuid = null;
        public boolean addPrefix = true;
        public String prefix = "OfflinePlayer:";
    }

    public enum Mode {
        ORIGINAL,
        FIXED,
        RANDOM,
        GENERATED
    }
}
