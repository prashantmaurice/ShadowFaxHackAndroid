package com.prashantmaurice.shadowfaxhackandroid;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.prashantmaurice.shadowfaxhackandroid.MainActivity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by maurice on 11/06/16.
 */
public class NetworkController {
    static final String TAG = "NETWORKCONTROLLER";
    static NetworkController instance;
    private final Context mContext;
    RequestQueue queue;
    ProgressDialog progress;

    private NetworkController(Context context) {
        this.mContext = context;
        queue = Volley.newRequestQueue(context);
    }

    public static NetworkController getInstance(Context context){
        if(instance==null) instance = new NetworkController(context);
        return instance;
    }

    public void fetchDataFromServer(UploadTask uploadTask, boolean showInDialog, String filepath, String fileName){
        UploadChatImageTask task = new UploadChatImageTask(mContext,showInDialog,filepath,fileName);
        task.execute(uploadTask);
        if(showInDialog) showProgressBar();
    }



    protected <T> void uploadFile(final File file, String tag,
                                         final Response.Listener<JSONObject> resultDelivery,
                                         final Response.ErrorListener errorListener,
                                         MultipartRequest.MultipartProgressListener progListener) {
//        AZNetworkRetryPolicy retryPolicy = new AZNetworkRetryPolicy();
        //TODO: add checks for image
        HashMap<String,String> headers = new HashMap<String,String>();
        headers.put("FILENAME",file.getName());
        HashMap<String,String> headers2 = new HashMap<String,String>();
        headers.put("FILENAME",file.getName());
        MultipartRequest mr = new MultipartRequest(Router.Uploads.postAudioFile(), errorListener,
                resultDelivery, file, file.length(), headers2, headers,
                "testing", progListener);

//        mr.setRetryPolicy(retryPolicy);
//        mr.setTag(tag);

        mr.setRetryPolicy(new DefaultRetryPolicy(
                30 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Log.e(TAG, "Volley current timeout is: " + mr.getRetryPolicy().getCurrentTimeout());

        mr.setTag(tag);
        queue.add(mr);
        //Volley.newRequestQueue(activity).add(mr);
    }

    public static class UploadTask{
        File file;
        public UploadTask(File file) {
            this.file = file;
        }
    }


    private class UploadChatImageTask extends AsyncTask<UploadTask, Void, Void> {

        final Long startTime = new Date().getTime();
        private final Context mContext;
        private final boolean showInDialog;
        private final String filepath;
        private final String fileName;

        private UploadChatImageTask(Context context, boolean showInDialog, String filepath, String fileName) {
            this.showInDialog = showInDialog;
            this.mContext = context;
            this.filepath = filepath;
            this.fileName = fileName;
        }

        protected Void doInBackground(UploadTask... tasks) {
            final UploadTask task = tasks[0];
            final Long startTime = new Date().getTime();


            uploadFile(task.file, task.file.getAbsolutePath(),new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    Log.d("DEBUGAWS", "Upload : " + jsonObject.toString());
                    dismissProgress();

                    try {
                        JSONObject obj = new JSONObject(jsonObject.getString("response"));
                        if (obj.getString("status").equals("success")) {
                            Result result = Result.parseJSON(obj);
                            if(showInDialog){
                                showResultInDialog(result,filepath,fileName);
                            }else{
                                showInNotification(result,filepath,fileName);
                            }
                            Log.d(TAG, "Uploaded in " + ((new Date().getTime() - startTime)) + " msecs");
                        } else {
                            Toast.makeText(mContext,"Could not upload",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    dismissProgress();
                    volleyError.printStackTrace();
                    if(volleyError.networkResponse!=null)Toast.makeText(mContext,"Could not upload : "+volleyError.networkResponse.statusCode+" ErrorCode",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error in "+((new Date().getTime() - startTime)) + " msecs");
                }
            }, new MultipartRequest.MultipartProgressListener() {
                @Override
                public void transferred(long transfered, final int progress) {
                    Log.d("DEBUGAWS", "Uploaded : " + transfered + " : " + progress);
                }
            });




            return null;
        }

        protected void onPostExecute(Void result) {}
    }


    private void showResultInDialog(final Result result, final String filepath, final String fileName) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        String text = "Raw \n "+result.notificationtext+ "\n\n Address Discussed\n"+result.getAddress();
        builder.setMessage(text)
                .setPositiveButton("PLAY AUDIO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startPlayExternal(filepath,fileName);
                    }
                })
                .setNegativeButton("MAPS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Search for restaurants nearby
//                        Uri gmmIntentUri = Uri.parse("geo:12.928217,77.6145885?q="+result.getAddress());
                        Uri gmmIntentUri = Uri.parse("geo:0,0?q="+result.getAddress());
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        mContext.startActivity(mapIntent);
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    static class Result{

        static List<String> keywords = Arrays.asList(
                "koramangala", "ejipura", "sony","world", "signal","block",
                "1th","2th","3th","4th","5th","6th","7th","7th","8th","10th","1th",
                "near","bus","stop",
                "bangalore","bengaluru",
                "jyothi","nivas","college","road","rd","bhima","jewellery","right","left"
        );
        String text = "text";
        String notificationtext = "notificationtext";
        Result(){}
        static Result parseJSON(JSONObject obj){
            Result result = new Result();
            try {
                result.text = obj.getString("text");
                result.notificationtext = ""+obj.getString("notificationtext");
            } catch (JSONException e) {e.printStackTrace();}
            return result;
        }
        String getAddress(){
            String[] words = text.split(" ");
            String coreAddress = "";
            for(int i=0;i<words.length;i++){
                String word = words[i];
                if(keywords.contains(word.toLowerCase())){
                    coreAddress += " "+word;
                }
            }
            return coreAddress;
        }
    }

    private void showInNotification(Result obj, String filepath, String fileName) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Conversation")
                        .setContentText(obj.notificationtext);
        if(obj.getAddress().isEmpty()){
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().
                    bigText(obj.notificationtext)
                    .setSummaryText("No Address Found"));
        }else{
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().
                    bigText(obj.getAddress()));
//                    .setSummaryText("Address discussed"));


            //action 1
            Uri gmmIntentUri = Uri.parse("geo:12.928217,77.6145885?q="+obj.getAddress());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, mapIntent, 0);
            mBuilder.addAction(android.R.drawable.ic_menu_always_landscape_portrait, "REPLAY", pendingIntent);

            //action 2
            File file = new File(filepath, fileName);
            Uri intentUri;
            if (file.exists()) intentUri = Uri.parse("file://" + FileHelper.getFilePath() + "/"
                        + Constants.FILE_DIRECTORY + "/" + fileName);
            else intentUri = Uri.parse("file://"
                        + mContext.getFilesDir().getAbsolutePath() + "/"
                        + Constants.FILE_DIRECTORY + "/" + fileName);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(intentUri, "audio/3gpp");
            PendingIntent pendingIntent1 = PendingIntent.getActivity(mContext, 0, intent, 0);
            mBuilder.addAction(android.R.drawable.ic_menu_always_landscape_portrait, "Map", pendingIntent1);
        }


        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(2001, mBuilder.build());
        Toast.makeText(mContext,"Successfully uploaded"+obj.toString(),Toast.LENGTH_SHORT).show();
    }

    void startPlayExternal(String filepath, String fileName) {
        File file = new File(filepath, fileName);
        Uri intentUri;

        if (file.exists())
            intentUri = Uri.parse("file://" + FileHelper.getFilePath() + "/"
                    + Constants.FILE_DIRECTORY + "/" + fileName);
        else
            intentUri = Uri.parse("file://"
                    + mContext.getFilesDir().getAbsolutePath() + "/"
                    + Constants.FILE_DIRECTORY + "/" + fileName);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(intentUri, "audio/3gpp");
        mContext.startActivity(intent);
    }

    void showProgressBar(){
        progress = new ProgressDialog(mContext);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();
    }
    private void dismissProgress() {
        if(progress!=null && progress.isShowing())progress.dismiss();
    }


}
