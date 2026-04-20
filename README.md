# ZenithProxyOfflineUUID

[![Build](https://github.com/AeiouJx/ZenithProxyOfflineUUID/actions/workflows/build.yml/badge.svg)](https://github.com/AeiouJx/ZenithProxyOfflineUUID/actions/workflows/build.yml)
[![Release](https://img.shields.io/github/v/release/AeiouJx/ZenithProxyOfflineUUID)](https://github.com/AeiouJx/ZenithProxyOfflineUUID/releases)
[![License](https://img.shields.io/github/license/AeiouJx/ZenithProxyOfflineUUID)](LICENSE)

A ZenithProxy plugin that overrides the offline UUID used during login.

It supports two modes:

- force a fixed UUID
- generate a deterministic UUID from `prefix + username`

This is useful when you want stable offline identity behavior across reconnects, proxies, or custom account setups.

## Features

- Rewrites the login `profileId` in `ServerboundHelloPacket`
- Supports a fixed configured UUID
- Supports deterministic UUID generation from `prefix + username`
- Keeps the client-side `MinecraftProtocol` profile UUID in sync with the modified login UUID
- Includes a simple in-proxy command for enabling, disabling, and updating settings
- Builds as a normal ZenithProxy Java plugin jar

## Compatibility

- ZenithProxy `1.21.4-SNAPSHOT`
- Java plugin release channel
- Java 21+ for users

## Installation

### Option 1: Download a release

1. Open the [Releases](https://github.com/AeiouJx/ZenithProxyOfflineUUID/releases) page.
2. Download the latest `ZenithProxyOfflineUUID-<version>.jar`.
3. Put the jar in the `plugins` folder next to your ZenithProxy launcher.
4. Restart ZenithProxy.

### Option 2: Build from source

```powershell
.\gradlew.bat build
```

The built jar will be placed in `build/libs`.

## Configuration

The plugin registers its own config file through ZenithProxy.

Example config:

```json
{
  "enabled": false,
  "prefix": "OfflinePlayer",
  "offlineUuid": null
}
```

Fields:

- `enabled`: turns the module on or off
- `prefix`: used when generating a deterministic UUID from `prefix + username`
- `offlineUuid`: if set, this UUID is forced directly during login

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
- `offlineUUID <prefix>`: set the deterministic UUID prefix
- `offlineUUID set <uuid>`: force a specific offline UUID
- `offlineUUID clear`: clear the fixed UUID and return to prefix-based UUID generation

## How It Works

When enabled, the plugin intercepts the login packet and replaces the UUID with either:

- the configured fixed UUID, or
- a deterministic UUID generated from `prefix + username`

It also updates the local protocol profile UUID so the client session state matches the modified login packet.

## Building

```powershell
.\gradlew.bat build
```

Output:

- `build/libs/ZenithProxyOfflineUUID-<version>.jar`

## Release Workflow

This repository includes GitHub Actions for automation:

- every push and pull request runs the build workflow
- pushing a tag like `v1.0.0` creates a GitHub Release and uploads the built jar automatically

## Project Info

- Plugin name: `ZenithProxyOfflineUUID`
- Plugin id: `offline-uuid`
- Package: `dev.zenith.offlineuuid`

## Changelog

See [CHANGELOG.md](CHANGELOG.md).

## Disclaimer

This plugin changes offline UUID behavior inside ZenithProxy. Make sure you understand how your target server handles offline-mode identities before using it in production.

## License

This repository is licensed under the [LICENSE](LICENSE) file included in the project.
