## ZenithProxyOfflineUUID __VERSION__

This release includes the latest updates to ZenithProxyOfflineUUID.

#### Highlights
- Packet interception approach for controlling offline UUID
- Client and server UUID behavior can be configured separately
- Supports `original`, `fixed`, `random`, and `generated` modes
- Supports generated UUIDs with or without a prefix
- Client-side UUID rewriting only applies while ZenithProxy is using offline authentication
- Added `get` subcommand to show current configuration

#### Recommended usage
```text
offlineuuid on
offlineuuid server off
offlineuuid client on
```

#### Notes
- `server` UUID rewriting is not recommended for security-sensitive use
- This plugin is primarily intended for client-side UUID control when ZenithProxy connects outward with offline authentication
- Does not bypass Mojang online-mode authentication

#### Artifact
- `__JAR_NAME__`
