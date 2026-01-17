### Version 1.4.1
* **Fixed:** **Strict Lag Protection**. The scheduler now enforces a strict minimum delay (1s) between messages to prevent overlapping. Controlled via `"enableLagProtection"` (default: `true`).
* **New:** **Timestamps**. Added `"showTimestamp": true` option to `config.json` to prefix messages with `[HH:mm]`.
* **Improved:** Updated to latest stable build.

### Version 1.4.0
* **New:** Added **Simple Mode**. You can now use simple durations (e.g., `"10s"`, `"5m"`, `"1h"`) in `config.json` instead of complex Cron expressions. Enable it by setting `"simpleMode": true`.
* **Fixed:** Implemented **Lag Protection**. The scheduler now uses recursive delays, ensuring that if the server lags or freezes, messages do not accumulate and spam the chat upon recovery.
* **Fixed:** Improved error logging. Invalid duration formats now show a friendly warning instead of a full stack trace.
* **Internal:** Dependency updates and code cleanup.

### Version 1.3.3
* **Fixed:** Definitive fix for message duplication bug. Implemented a volatile `active` state flag to rigorously eliminate "zombie" tasks that survived reloads due to race conditions (lag).

### Version 1.3.2
* **New:** Added support for clickable links (`http`/`https`) in broadcast messages. Now URLs are automatically detected and made clickable.
* **Fixed:** Critical bug where broadcast messages were duplicated after using `/hyannounces reload` (Zombie Schedulers).
* **Fixed:** Potential server crash when reloading the plugin if scheduled messages were previously disabled.
* **Improved:** Internal scheduler lifecycle management is now more robust.
