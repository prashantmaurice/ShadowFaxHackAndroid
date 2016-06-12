package com.prashantmaurice.shadowfaxhackandroid;

/**
 * This contains all the routes to our backend server. This is similar to API CONTRACT given to backend team.
 * Any changes with backend API routes will only be reflected by changes in this FIle.
 */
public class Router {
    public static class Uploads{
        private static String base = Settings.BASE_URL+"sfdata";
        public static String base(){return base; }
        public static String postAudioFile(){return base+"/postAudio"; }
    }
}


