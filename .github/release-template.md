## ZenithProxyOfflineUUID __VERSION__

This release includes the latest updates to ZenithProxyOfflineUUID.

#### Highlights
- Lightweight config-only approach for controlling offline UUID
- Supports `fixed`, `random`, and `generated` UUID modes
- Generated mode supports optional `prefix + username` UUID generation
- Sets `CONFIG.authentication.offlineUUID` directly (no packet interception)
- Added `get` subcommand to show current configuration

#### Recommended usage
```text
offlineuuid on
offlineuuid mode generated
offlineuuid prefix OfflinePlayer:
offlineuuid usePrefix on
```

#### Notes
- This plugin is only for UUID control, not authentication bypass
- Does not bypass Mojang online-mode authentication

#### Artifact
- `__JAR_NAME__`
