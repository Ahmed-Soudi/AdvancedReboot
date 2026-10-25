package com.soudi.advancedreboot;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int REQUEST_NOTIFICATIONS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView status = findViewById(R.id.status);
        updateStatus(status);

        requestNotificationPermissionIfNeeded();
        startPersistentNotification();

        bind(R.id.reboot_normal, null);
        bind(R.id.reboot_recovery, "recovery");
        bind(R.id.reboot_download, "download");
    }

    private void updateStatus(TextView status) {
        boolean reboot = Rebooter.hasRebootPermission(this);
        boolean recovery = Rebooter.hasRecoveryPermission(this);
        status.setText(
                "REBOOT permission: " + (reboot ? "granted" : "NOT granted") +
                "\nRECOVERY permission: " + (recovery ? "granted" : "NOT granted") +
                "\nPersistent controls: enabled"
        );
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATIONS);
        }
    }

    private void startPersistentNotification() {
        Intent service = new Intent(this, RebootNotificationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(service);
        } else {
            startService(service);
        }
    }

    private void bind(int id, String reason) {
        Button button = findViewById(id);
        button.setOnClickListener(v -> perform(reason));
    }

    private void perform(String reason) {
        if (!Rebooter.hasRebootPermission(this)) {
            Toast.makeText(this, "REBOOT permission not granted", Toast.LENGTH_LONG).show();
            return;
        }
        if ("recovery".equals(reason) && !Rebooter.hasRecoveryPermission(this)) {
            Toast.makeText(this, "RECOVERY permission not granted", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            Rebooter.reboot(this, reason);
        } catch (SecurityException e) {
            Toast.makeText(this, "Permission denied: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (RuntimeException e) {
            Toast.makeText(this, "Reboot failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
