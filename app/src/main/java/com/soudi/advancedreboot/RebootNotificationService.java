package com.soudi.advancedreboot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class RebootNotificationService extends Service {
    private static final String CHANNEL_ID = "advanced_reboot_controls";
    private static final int NOTIFICATION_ID = 12701;

    @Override
    public void onCreate() {
        super.onCreate();
        createChannel();
        startForeground(NOTIFICATION_ID, buildNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, buildNotification());
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Advanced Reboot controls",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Persistent reboot controls");
            channel.setShowBadge(false);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        Intent openApp = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,
                openApp,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification.Action normal = new Notification.Action.Builder(
                null,
                "Restart",
                rebootPendingIntent(RebootActionReceiver.ACTION_NORMAL, 1)
        ).build();

        Notification.Action recovery = new Notification.Action.Builder(
                null,
                "Recovery",
                rebootPendingIntent(RebootActionReceiver.ACTION_RECOVERY, 2)
        ).build();

        Notification.Action download = new Notification.Action.Builder(
                null,
                "Download",
                rebootPendingIntent(RebootActionReceiver.ACTION_DOWNLOAD, 3)
        ).build();

        Notification.Builder builder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? new Notification.Builder(this, CHANNEL_ID)
                : new Notification.Builder(this);

        return builder
                .setSmallIcon(R.drawable.ic_recovery)
                .setContentTitle("Advanced Reboot")
                .setContentText("Restart controls are always available")
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setCategory(Notification.CATEGORY_SYSTEM)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .addAction(normal)
                .addAction(recovery)
                .addAction(download)
                .build();
    }

    private PendingIntent rebootPendingIntent(String action, int requestCode) {
        Intent intent = new Intent(this, RebootActionReceiver.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }
}
