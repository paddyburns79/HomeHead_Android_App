package com.paddy.homehead;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 17/07/2017
 */
public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FirebaseMessagingServic";
    // initialise instance of Firebase Firestore
    FirebaseFirestore dBase = FirebaseFirestore.getInstance();

    // inititalise instance of DatabaseHelper
    DatabaseHelper notificationsDB;

    public MyFirebaseMessagingService(FirebaseFirestore dBase) {
        this.dBase = dBase;
    }

    /**
     * Constructor
     */
    public MyFirebaseMessagingService() {
    }

    /**
     * Handles messages received by the client app
     * Includes control of notifications if headphones are connected
     * @param remoteMessage
     */
     @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

         // Cloud Firestore instance settings
         FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                 .setTimestampsInSnapshotsEnabled(true)
                 .build();
         dBase.setFirestoreSettings(settings);

         /*// retrieve message data
         Map<String, String> data = remoteMessage.getData();
         String title = data.get("title");
         String messageBody = data.get("body");*/

         // forward notification data to sendNotification method if areHeadphonesConnected value = true
         if (areHeadphonesConnected()) {
             // retrieve message data
             Map<String, String> data = remoteMessage.getData();
             String title = data.get("title");
             String messageBody = data.get("body");

            sendNotification(title, messageBody);
            // add message data to SQLite database
            notificationsDB = new DatabaseHelper(this);
            addMsgDataToDB(messageBody);
         }
    }


    /**
     * Create and show a simple notification containing the received FCM message.
     * (Source: Firebase Quickstart Guide, Github)
     *
     */
    private void sendNotification(String title, String messageBody) {

        Intent intent = new Intent(this, ViewNotificationsReceivedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.homehead_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                                R.mipmap.homehead_launcher))
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(2)
                        .setVibrate(new long[1000])
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }

    @Override
    public void onDeletedMessages() {

    }

    /**
     * Method to get current Device Token
     * Calls sendRegistrationToServer method to post device token to Firestore Database
     * @param newToken
     */
    @Override
    public void onNewToken(String newToken) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken",newToken);
                sendRegistrationToServer(newToken);
            }
        });
    }

    /**
     * Method to post Device Token to Google Firestore Database
     * @param token
     */
    public void sendRegistrationToServer(String token) {

        // obtain the device name/model
        String deviceMod = Build.MANUFACTURER + " " + Build.MODEL;

        // add token and device id data to a hashmap
        Map<String, Object> deviceData = new HashMap<>();
        deviceData.put("tokenID", token);
        deviceData.put("deviceName", deviceMod);
        // areHeadphonesConnected method check
        //deviceData.put("headphonesConnected", areHeadphonesConnected());

        // send data to Firestore DB
        dBase.collection("devices").document()
                .set(deviceData);
    }

    /**
     * Method returns true or false to indicate whether wired headphones are connected to the device
     * @return
     */
    private boolean areHeadphonesConnected(){

        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo[] audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
        for(AudioDeviceInfo deviceInfo : audioDevices)
            if (deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                    || deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET
                        || deviceInfo.getType() == AudioDeviceInfo.TYPE_USB_HEADSET
                            || deviceInfo.getType() == AudioDeviceInfo.TYPE_USB_ACCESSORY
                                || deviceInfo.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                                    || deviceInfo.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO)  {
                return true;
            }
        return false;
    }

    public void addMsgDataToDB(String msgBodyToDB) {

        boolean insertData = notificationsDB.addData(msgBodyToDB);

    }
}
