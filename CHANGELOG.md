# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

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
