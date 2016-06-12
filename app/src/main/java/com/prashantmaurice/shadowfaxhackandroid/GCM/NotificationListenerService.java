package com.prashantmaurice.shadowfaxhackandroid.GCM;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by maurice on 12/06/16.
 */
public class NotificationListenerService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data){
        Toast.makeText(this,"GCM Message received",Toast.LENGTH_SHORT).show();
        Log.d("GCM","Received GCM");
    }
}
