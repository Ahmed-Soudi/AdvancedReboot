package com.soudi.advancedreboot;

import android.service.quicksettings.TileService;
import android.widget.Toast;

public class RecoveryTileService extends TileService {
    @Override
    public void onClick() {
        super.onClick();

        if (!Rebooter.hasRebootPermission(this)) {
            Toast.makeText(this, "REBOOT permission not granted", Toast.LENGTH_LONG).show();
            return;
        }
        if (!Rebooter.hasRecoveryPermission(this)) {
            Toast.makeText(this, "RECOVERY permission not granted", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Rebooter.reboot(this, "recovery");
        } catch (SecurityException e) {
            Toast.makeText(this, "Permission denied: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (RuntimeException e) {
            Toast.makeText(this, "Reboot failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
