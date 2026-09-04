package dev.zenith.offlineuuid;

public class OfflineUUIDConfig {
    public Mode mode = Mode.BYNAME;
    public String prefix = "OfflinePlayer:";

    public enum Mode {
        ORIGINAL,
        RANDOM,
        BYNAME
    }
}
