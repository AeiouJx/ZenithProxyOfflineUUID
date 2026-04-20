# ZenithProxyOfflineUUID

[![Build](https://github.com/AeiouJx/ZenithProxyOfflineUUID/actions/workflows/build.yml/badge.svg)](https://github.com/AeiouJx/ZenithProxyOfflineUUID/actions/workflows/build.yml)
[![Release](https://img.shields.io/github/v/release/AeiouJx/ZenithProxyOfflineUUID)](https://github.com/AeiouJx/ZenithProxyOfflineUUID/releases)
[![License](https://img.shields.io/github/license/AeiouJx/ZenithProxyOfflineUUID)](LICENSE)

A ZenithProxy plugin for controlling UUID rewriting separately on the proxy `server` side and `client` side.

In practice, the useful part is usually the `client` side:

- `server`: affects local Minecraft clients connecting into ZenithProxy
- `client`: affects ZenithProxy connecting outward to the target Minecraft server

## Current Design

This plugin now supports split behavior:

- separate `server` and `client` configuration
- `original`, `fixed`, `random`, and `generated` modes
- optional `prefix + username` generation
- client-side protocol profile synchronization

Recommended usage:

- keep `server` UUID rewriting disabled
- use `client` UUID rewriting only when ZenithProxy is logging in with an offline account and the target server expects a specific UUID pattern

## Security Note

`server` UUID rewriting is not recommended as a security mechanism.

ZenithProxy whitelist checks are effectively UUID-based, so if you rewrite the UUID of a local player connection to match a whitelisted UUID, that can bypass the intended trust boundary.

Recommended safe setup:

```text
uuid on
uuid server off
uuid client on
```

## Features

- Rewrites login `profileId` values in `ServerboundHelloPacket`
- Splits UUID control between ZenithProxy inbound and outbound login flow
- Supports `original`, `fixed`, `random`, and `generated` UUID modes
- Supports generated UUIDs with or without a prefix
- Only applies client-side UUID rewriting when ZenithProxy is using offline authentication
- Keeps the client-side `MinecraftProtocol` profile UUID synchronized with outbound UUID changes

## Compatibility

- ZenithProxy `1.21.4-SNAPSHOT`
- Java plugin release channel
- Java 21+ for users

## Installation

### Option 1: Download a release

1. Open the [Releases](https://github.com/AeiouJx/ZenithProxyOfflineUUID/releases) page.
2. Download the latest `ZenithProxyUUID-<version>.jar` or the published plugin jar.
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
uuid
```

Examples:

```text
uuid on
uuid server off
uuid client on
uuid client mode generated
uuid client prefix OfflinePlayer:
uuid client usePrefix on
uuid client mode fixed
uuid client set 123e4567-e89b-12d3-a456-426614174000
uuid client mode random
```

Available side commands:

- `uuid server on|off`
- `uuid client on|off`
- `uuid server mode <original/fixed/random/generated>`
- `uuid client mode <original/fixed/random/generated>`
- `uuid server prefix <value>`
- `uuid client prefix <value>`
- `uuid server usePrefix on|off`
- `uuid client usePrefix on|off`
- `uuid server set <uuid>`
- `uuid client set <uuid>`
- `uuid server clear`
- `uuid client clear`

Mode meanings:

- `original`: do not rewrite the UUID
- `fixed`: always use the configured UUID
- `random`: generate a random UUID for the login
- `generated`: generate a deterministic UUID from either `username` or `prefix + username`

## Typical Usage

### Safe default

```text
uuid on
uuid server off
uuid client on
uuid client mode generated
uuid client prefix OfflinePlayer:
uuid client usePrefix on
```

### Fixed outbound UUID

```text
uuid on
uuid server off
uuid client on
uuid client mode fixed
uuid client set 70542937-7f25-32a5-8a47-600e13eb5b68
```

### Random outbound UUID

```text
uuid on
uuid server off
uuid client on
uuid client mode random
```

## How It Works

When enabled, the plugin intercepts login packets and can replace the UUID before the login continues.

- `server` side changes inbound player UUIDs before ZenithProxy processes the local login
- `client` side changes outbound UUIDs before ZenithProxy connects to the target server

The client-side rewrite also updates the internal `MinecraftProtocol` profile UUID so session state remains consistent.

## Limitation

This plugin does not bypass Mojang online-mode authentication.

If a target server requires authenticated online-mode login, changing UUID alone will not make an offline-auth ZenithProxy session valid there. This plugin is only for UUID control, not account authentication bypass.

## Versioning

Plugin versions are manual.

To release a new version, update `plugin_version` in [gradle.properties](D:\Downloads\ZenithProxy\ZenithProxyOfflineUUID\gradle.properties), then build and tag the release.

## Release Workflow

This repository includes GitHub Actions for automation:

- every push and pull request runs the build workflow
- pushing a tag like `v1.0.1` creates a GitHub Release and uploads the built jar automatically

## Project Info

- Plugin name: `ZenithProxyUUID`
- Plugin id: `uuid`
- Package: `dev.zenith.offlineuuid`
- Repository name: `ZenithProxyOfflineUUID`

## Changelog

See [CHANGELOG.md](CHANGELOG.md).

## License

This repository is licensed under the [LICENSE](LICENSE) file included in the project.
