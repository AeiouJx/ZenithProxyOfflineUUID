package dev.zenith.offlineuuid;

public class OfflineUUIDConfig {
    public boolean enabled = false;
    public Mode mode = Mode.RANDOM;
    public String fixedUuid = null;
    public boolean addPrefix = true;
    public String prefix = "OfflinePlayer:";

    public enum Mode {
        FIXED,
        RANDOM,
        GENERATED
    }
}
