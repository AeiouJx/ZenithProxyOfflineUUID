# ZenithProxyOfflineUUID

A simple [ZenithProxy](https://github.com/rfresh2/ZenithProxy) plugin that overrides the offline UUID used during login.

This is useful when you want a stable offline UUID for a specific account, or when you want ZenithProxy to generate a predictable UUID from a custom prefix plus the username.

## Features

- Overrides the `profileId` in the login `ServerboundHelloPacket`
- Supports a fixed offline UUID from config or command
- Supports deterministic UUID generation from `prefix + username`
- Syncs the client-side `MinecraftProtocol` profile UUID to match the overridden login UUID
- Includes an in-proxy command for enabling, disabling, and updating settings

## Compatibility

- ZenithProxy `1.21.4-SNAPSHOT`
- Java plugin release channel for ZenithProxy

## Installation

1. Build the plugin:

```powershell
.\gradlew.bat build
```

2. Find the built jar in `build/libs`.
3. Copy the jar into the `plugins` folder next to your ZenithProxy launcher.
4. Restart ZenithProxy.

## Build

```powershell
.\gradlew.bat build
```

The output jar will be placed in `build/libs`.

## Configuration

The plugin registers its own config file through ZenithProxy.

Current config fields:

```json
{
  "enabled": false,
  "prefix": "OfflinePlayer",
  "offlineUuid": null
}
```

Field meanings:

- `enabled`: turns the module on or off
- `prefix`: used to generate a deterministic UUID from `prefix + username` when `offlineUuid` is not set
- `offlineUuid`: if set, this UUID is forced directly for login

## Commands

Base command:

```text
offlineUUID
```

Examples:

```text
offlineUUID on
offlineUUID off
offlineUUID MyPrefix:
offlineUUID set 123e4567-e89b-12d3-a456-426614174000
offlineUUID clear
```

Behavior:

- `offlineUUID on|off`: enable or disable the module
- `offlineUUID <prefix>`: update the UUID generation prefix
- `offlineUUID set <uuid>`: force a specific offline UUID
- `offlineUUID clear`: clear the fixed UUID and fall back to generated UUID mode

## How It Works

When enabled, the plugin intercepts the login packet sent by the client and replaces the UUID with either:

- the configured fixed UUID, or
- a deterministic UUID generated from `prefix + username`

It also updates the client protocol profile UUID so the session state stays consistent with the modified login packet.

## Project Info

- Plugin name: `ZenithProxyOfflineUUID`
- Plugin id: `offline-uuid`
- Package: `dev.zenith.offlineuuid`

## Disclaimer

This plugin is intended for offline UUID behavior customization inside ZenithProxy. Make sure you understand how your target server handles offline-mode UUIDs before using it in production.

## License

This repository is licensed under the [LICENSE](LICENSE) file included in the project.
