package dev.zenith.offlineuuid;

public class OfflineUUIDConfig {
    public Mode mode = Mode.BY_NAME;
    public String prefix = "OfflinePlayer:";

    public enum Mode {
        ORIGINAL,
        RANDOM,
        BY_NAME
    }
}
