package com.soudi.advancedreboot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class RebootActionReceiver extends BroadcastReceiver {
    public static final String ACTION_NORMAL = "com.soudi.advancedreboot.action.NORMAL";
    public static final String ACTION_RECOVERY = "com.soudi.advancedreboot.action.RECOVERY";
    public static final String ACTION_DOWNLOAD = "com.soudi.advancedreboot.action.DOWNLOAD";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String reason;

        if (ACTION_NORMAL.equals(action)) {
            reason = null;
        } else if (ACTION_RECOVERY.equals(action)) {
            reason = "recovery";
        } else if (ACTION_DOWNLOAD.equals(action)) {
            reason = "download";
        } else {
            return;
        }

        try {
            Rebooter.reboot(context, reason);
        } catch (SecurityException e) {
            Toast.makeText(context, "Permission denied: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (RuntimeException e) {
            Toast.makeText(context, "Reboot failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
