# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

## [2.0.0] - 2026-09-02

### Changed

- Renamed command from `uuid` to `offlineuuid`
- Added `get` subcommand to show current UUID configuration and active offlineUUID value
- Renamed plugin id from `uuid` to `offlineuuid`
- Renamed plugin from `ZenithProxyUUID` to `ZenithProxyOfflineUUID`

## [1.0.1] - 2026-04-21

### Changed

- Renamed the in-proxy command to `uuid`
- Split UUID behavior into separate `server` and `client` settings
- Added `original`, `fixed`, `random`, and `generated` modes for each side
- Added optional prefix usage for generated UUID mode
- Limited client-side rewriting to offline-auth sessions
- Clarified that server-side UUID rewriting is not recommended for security-sensitive use
- Updated documentation to reflect the split client/server design

## [1.0.0] - 2026-04-20

### Added

- Initial public release
- Offline UUID override during login
- Fixed UUID mode
- Prefix-based deterministic UUID mode
- In-proxy command for configuration updates
- GitHub Actions build and release automation
