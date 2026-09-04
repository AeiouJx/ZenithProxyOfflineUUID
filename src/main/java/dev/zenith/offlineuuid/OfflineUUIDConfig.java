package dev.zenith.offlineuuid;

public class OfflineUUIDConfig {
    public boolean enabled = true;
    public Mode mode = Mode.BYNAME;
    public String prefix = "OfflinePlayer:";

    public enum Mode {
        ORIGINAL,
        RANDOM,
        BYNAME
    }
}
