### Version 1.3.3
* **Fixed:** Definitive fix for message duplication bug. Implemented a volatile `active` state flag to rigorously eliminate "zombie" tasks that survived reloads due to race conditions (lag).

### Version 1.3.2
* **New:** Added support for clickable links (`http`/`https`) in broadcast messages. Now URLs are automatically detected and made clickable.
* **Fixed:** Critical bug where broadcast messages were duplicated after using `/hyannounces reload` (Zombie Schedulers).
* **Fixed:** Potential server crash when reloading the plugin if scheduled messages were previously disabled.
* **Improved:** Internal scheduler lifecycle management is now more robust.
