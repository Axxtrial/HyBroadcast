# Changelog

## [2.0.0] - 2026-01-17
### Added
- **Sound System Support**: Added `--sound <name>` flag to manual commands and `sound` field in `config.json`.
- **Hytale Native Argument System**: Refactored commands to leverage Hytale's native parser for flags.
- **Improved Thread-Safety**: All broadcasts and sound playbacks now use `world.execute()` for maximum stability and thread-independence.
- **Smart Lag Protection**: Schedulers now intelligently skip missed intervals during heavy server lag to prevent "message storms".
- **Enhanced Wiki**: Added interactive visual guide and responsive command documentation.

### Fixed
- **Greedy Parser Conflict**: Resolved critical engine-level parsing error where multi-word messages conflicted with optional flags.
- **Semver Compliance**: Fixed version string to `2.0.0` to satisfy Hytale's alphanumeric requirements.
- **Clickable Links**: Detection and registration of URLs in broadcast messages.

---

## [1.6.0]
### Added
- **Architecture Overhaul**: Complete rewrite of the scheduling engine for Total Thread Independence.
- **Rigid No-Drift Timing**: Mathematical target-time calculation ensures perfect rhythm regardless of server TPS.
- **Async Broadcasting**: All broadcasts moved to `CompletableFuture` to prevent main-thread blockage.

---

## [1.5.0]
### Added
- **Anti-Flood System**: Minimum gap of 1.5 seconds enforced between any two announcements.
- **Startup Auto-Correction**: Automatic staggering of messages scheduled for the same second.

---

## [1.4.2]
### Fixed
- **Zombie Schedulers**: Optimized shutdown logic to prevent task duplication during rapid reloads.
- **Volatile State Flag**: Added rigorous task tracking to ensure only one active loop exists per instance.

---

## [1.4.1]
### Added
- **Simple Mode**: Support for human-readable durations like `10s`, `5m`, `1h`.
- **Timestamps**: Optional `showTimestamp` flag to prefix messages with `[HH:mm]`.
- **Lag Protection**: Recursive delay logic to prevent message accumulation.
