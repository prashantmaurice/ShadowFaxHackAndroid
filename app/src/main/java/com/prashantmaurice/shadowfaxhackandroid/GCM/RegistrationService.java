package com.prashantmaurice.shadowfaxhackandroid.GCM;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.prashantmaurice.shadowfaxhackandroid.R;

import java.io.IOException;

/**
 * Created by maurice on 12/06/16.
 */
public class RegistrationService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RegistrationService(String name) {
        super(name);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        // ...
        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            String token = instanceID.getToken(getString(R.string.google_app_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i("AsyncTask", "GCM id is " + token);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    // ...
}
