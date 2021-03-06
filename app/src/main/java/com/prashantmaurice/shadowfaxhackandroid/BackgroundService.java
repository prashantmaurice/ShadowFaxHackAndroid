package com.prashantmaurice.shadowfaxhackandroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.prashantmaurice.shadowfaxhackandroid.MainActivity.MainActivity;

public class BackgroundService extends Service {
    String TAG = "BACKGROUNDSERVICE";
    private NotificationManager mNM;
    private int NOTIFICATION = 1001;
    private static final String ACTION1="android.intent.action.NEW_OUTGOING_CALL";
    private static final String ACTION2="android.intent.action.PHONE_STATE";
    private BroadcastReceiver yourReceiver;

    public BackgroundService() {}

    @Override
    public void onCreate() {
        Log.d(TAG,"onCreate");
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification();
        attachBroadcast();

//        //turn on speaker
//        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        audioManager.setSpeakerphoneOn(true);
//        //increase Volume
//        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.about_close_button);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        new Notification();

//        Notification notification = new Notification.Builder(this)
//                .setSmallIcon(R.drawable.ic_about)  // the status icon
//                .setTicker(text)  // the status text
//                .setWhen(System.currentTimeMillis())  // the time stamp
//                .setContentTitle(getText(R.string.about_close_button))  // the label of the entry
//                .setContentText(text)  // the contents of the entry
//                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
//                .build();

        // Send the notification.
        Notification notification = new Notification(R.drawable.ic_about,text,System.currentTimeMillis());
        notification.contentIntent = contentIntent;
        mNM.notify(NOTIFICATION, notification);
    }

    void attachBroadcast(){
        final IntentFilter theFilter = new IntentFilter();
        theFilter.addAction(ACTION1);
        theFilter.addAction(ACTION2);
        this.yourReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // Do whatever you need it to do when it receives the broadcast
                // Example show a Toast message...
                String extraState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                Log.d(TAG,"onReceive : "+extraState+" : "+intent.getExtras().toString());
                String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                if(phoneNumber==null) phoneNumber = intent.getStringExtra("incoming_number");
                if(extraState==null) return;
                if(extraState.equals("OFFHOOK")){//call started

                }
                if(extraState.equals("IDLE")){//call ended

                }

            }
        };
        // Registers the receiver so that your service will listen for
        // broadcasts
        this.registerReceiver(this.yourReceiver, theFilter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Do not forget to unregister the receiver!!!
        this.unregisterReceiver(this.yourReceiver);
    }

}
