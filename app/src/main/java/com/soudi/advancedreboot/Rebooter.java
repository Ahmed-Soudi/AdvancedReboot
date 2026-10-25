package com.soudi.advancedreboot;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.PowerManager;

final class Rebooter {
    private Rebooter() {}

    static boolean hasRebootPermission(Context context) {
        return context.checkSelfPermission(android.Manifest.permission.REBOOT)
                == PackageManager.PERMISSION_GRANTED;
    }

    static boolean hasRecoveryPermission(Context context) {
        return context.checkSelfPermission("android.permission.RECOVERY")
                == PackageManager.PERMISSION_GRANTED;
    }

    static void reboot(Context context, String reason) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm == null) throw new IllegalStateException("PowerManager unavailable");
        pm.reboot(reason);
    }
}
