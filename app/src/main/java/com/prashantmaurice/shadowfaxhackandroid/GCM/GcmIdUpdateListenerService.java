package com.prashantmaurice.shadowfaxhackandroid.GCM;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;
import com.prashantmaurice.shadowfaxhackandroid.R;

import java.io.IOException;

public class GcmIdUpdateListenerService extends InstanceIDListenerService {

    private static final String TAG = "IdUpdateListenerSvc";

    public GcmIdUpdateListenerService() {
    }


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {

        final AsyncTask asyncTask = new AsyncTask() {

            private final String[] TOPICS = {"global"};

            @Override
            protected Object doInBackground(Object[] params) {
                Log.i("AsyncTask", "Inside async task, trying to generate GCM id");
                InstanceID instanceID = InstanceID.getInstance(GcmIdUpdateListenerService.this);
                String token = "";
                try {
                    token = instanceID.getToken(getString(R.string.google_app_id),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    Log.i("AsyncTask", "GCM id is " + token);



                    for (String topic : TOPICS) {
                        GcmPubSub pubSub = GcmPubSub.getInstance(GcmIdUpdateListenerService.this);
                        pubSub.subscribe(token, "/topics/" + topic, null);
                    }

                } catch (IOException e) {
                    Log.e("AsyncTask", "Failed to generate GCM id");
                    e.printStackTrace();
                }
                return null;
            }
        };

        asyncTask.execute(this);
    }
    // [END refresh_token]
}
