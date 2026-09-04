# ZenithProxyOfflineUUID

[![Build](https://github.com/AeiouJx/ZenithProxyOfflineUUID/actions/workflows/build.yml/badge.svg)](https://github.com/AeiouJx/ZenithProxyOfflineUUID/actions/workflows/build.yml)
[![Release](https://img.shields.io/github/v/release/AeiouJx/ZenithProxyOfflineUUID)](https://github.com/AeiouJx/ZenithProxyOfflineUUID/releases)
[![License](https://img.shields.io/github/license/AeiouJx/ZenithProxyOfflineUUID)](LICENSE)

A ZenithProxy plugin for controlling the offline UUID used when ZenithProxy connects outward to a target Minecraft server with offline authentication.

## Features

- Sets `CONFIG.authentication.offlineUUID` directly (no packet interception)
- Supports `fixed`, `random`, and `generated` UUID modes
- Generated mode supports optional `prefix + username` UUID generation
- Lightweight config-only approach

## Compatibility

- ZenithProxy `1.21.4`
- Java 21+

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

## Commands

Base command:

```text
offlineUUID
```

Examples:

```text
offlineUUID mode fixed
offlineUUID set 123e4567-e89b-12d3-a456-426614174000
offlineUUID mode generated
offlineUUID prefix OfflinePlayer:
offlineUUID prefix clear
offlineUUID clear
```

Available subcommands:

- `offlineUUID` - show current configuration and active offlineUUID value
- `offlineUUID mode <fixed/generated>` - set UUID generation mode
- `offlineUUID set <uuid>` - set a fixed UUID
- `offlineUUID clear` - clear the fixed UUID
- `offlineUUID prefix <value>` - set the prefix for generated mode
- `offlineUUID prefix clear` - clear the prefix

Mode meanings:

- `fixed`: always use the configured UUID
- `generated`: generate a deterministic UUID from `prefix + username`

## Typical Usage

### Fixed outbound UUID

```text
offlineUUID mode fixed
offlineUUID set 70542937-7f25-32a5-8a47-600e13eb5b68
```

### Generated UUID with prefix

```text
offlineUUID mode generated
offlineUUID prefix OfflinePlayer:
```

## How It Works

The plugin sets `CONFIG.authentication.offlineUUID` before ZenithProxy connects to the target server. In GENERATED mode, the UUID is auto-regenerated on each connection based on the current username. This field is read by the built-in `Authenticator.login()` method in ZenithProxy's outbound connection flow.

## Limitation

This plugin does not bypass Mojang online-mode authentication.

If a target server requires authenticated online-mode login, changing UUID alone will not make an offline-auth ZenithProxy session valid there. This plugin is only for UUID control, not account authentication bypass.

## Versioning

Plugin versions are manual.

To release a new version, update `plugin_version` in [gradle.properties](gradle.properties), then build and tag the release.

## Release Workflow

This repository includes GitHub Actions for automation:

- every push and pull request runs the build workflow
- pushing a tag like `v2.0.0` creates a GitHub Release and uploads the built jar automatically

## Project Info

- Plugin name: `ZenithProxyOfflineUUID`
- Plugin id: `offlineUUID`
- Package: `dev.zenith.offlineuuid`
- Repository name: `ZenithProxyOfflineUUID`

## Branches

| Branch     | ZenithProxy Version | Approach            |
|------------|--------------------|---------------------|
| `1.21.4`   | 1.21.4             | Config (no Module)  |
| `1.21.11`  | 1.21.11            | Packet interception  |

## Changelog

See [CHANGELOG.md](CHANGELOG.md).

## License

This repository is licensed under the [LICENSE](LICENSE) file included in the project.
