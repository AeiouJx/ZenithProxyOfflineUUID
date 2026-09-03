package dev.zenith.offlineuuid;

public class OfflineUUIDConfig {
    public Mode mode = Mode.GENERATED;
    public String fixedUuid = null;
    public String prefix = "OfflinePlayer:";

    public enum Mode {
        FIXED,
        GENERATED
    }
}
