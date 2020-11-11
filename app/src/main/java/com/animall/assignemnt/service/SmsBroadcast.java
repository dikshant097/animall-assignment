package com.animall.assignemnt.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import androidx.core.app.NotificationCompat;

import com.animall.assignemnt.R;
import com.animall.assignemnt.views.activitites.MainActivity;

import static com.animall.assignemnt.utils.ConstantsKt.ANDROID_PROVIDER_SMS_RECEIVED;

public class SmsBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!ANDROID_PROVIDER_SMS_RECEIVED.equals(intent.getAction())) {
            return;
        }

        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String msg_from;

        try {
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];

            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                msg_from = msgs[i].getOriginatingAddress();
                String msgBody = msgs[i].getMessageBody();

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");
                builder.setContentTitle("New Message From: " + msg_from);
                builder.setContentText(msgBody);
                builder.setSmallIcon(R.drawable.ic_launcher_foreground);
                builder.setAutoCancel(true);
                builder.setChannelId("default");

                Intent customIntent = new Intent(context, MainActivity.class);
                customIntent.putExtra("sender_id", msg_from);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, customIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);

                Notification notification = builder.build();
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel notificationChannel = null;
                    notificationChannel = new NotificationChannel("default", "Dikshant's Channel", importance);
                    notificationManager.createNotificationChannel(notificationChannel);
                }

                notificationManager.notify(1000, notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
