# Advanced Reboot (privileged ROM app)

Package: `com.soudi.advancedreboot`

Features:
- Normal restart
- Restart to recovery
- Restart to Samsung Download Mode using reboot reason `download`
- Recovery Quick Settings tile
- Confirmation dialogs
- Displays whether `android.permission.REBOOT` was actually granted

## Critical installation requirement
Installing the APK normally will NOT grant reboot power. Put it in the ROM as:

- `/system_ext/priv-app/AdvancedReboot/AdvancedReboot.apk`
- `/system_ext/etc/permissions/privapp-permissions-advancedreboot.xml`

Both APK and allowlist must be on the same partition. Recommended file mode: `0644`, owner `root:root`.

Check after boot:

```sh
cmd package check-permission android.permission.REBOOT com.soudi.advancedreboot 0
dumpsys package com.soudi.advancedreboot | grep -A5 -B5 REBOOT
```

Expected result: `granted`.

## Build
Open in Android Studio, or run:

```sh
gradle wrapper --gradle-version 8.11.1
./gradlew assembleRelease
```

Output:
`app/build/outputs/apk/release/app-release.apk`

The release build is debug-signed intentionally. Samsung's platform key should not be required when `REBOOT` remains `signature|privileged` and the app is correctly allowlisted.

## Warning
Test `reboot download` on the exact firmware before relying on it. Recovery should use the standard `recovery` reboot reason. Keep the bootloader unlocked while modified partitions are installed.

## v1.1 persistent boot notification

Version 1.1 adds an ongoing foreground-service notification that is recreated at device boot and exposes three actions without opening the app:

- Restart
- Recovery
- Samsung Download Mode

Android 13 / targetSdk 33 requires the user to allow notification permission once. The app requests `POST_NOTIFICATIONS` when opened. The reboot controls themselves still rely on the ROM privileged permissions `REBOOT` and `RECOVERY`.

New manifest permissions:

- `android.permission.RECEIVE_BOOT_COMPLETED`
- `android.permission.FOREGROUND_SERVICE`
- `android.permission.POST_NOTIFICATIONS`

After updating the APK in `/system_ext/priv-app/AdvancedReboot/`, reboot once and open Advanced Reboot once if Android asks for notification permission. Subsequent boots recreate the persistent controls automatically.


## v1.2
- Removed all in-app reboot confirmation dialogs.
- Normal restart, Recovery, and Download Mode now execute immediately when tapped in the app.
- Persistent notification actions remain immediate.
