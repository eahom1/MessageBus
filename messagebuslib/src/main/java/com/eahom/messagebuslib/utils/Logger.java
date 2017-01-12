package com.eahom.messagebuslib.utils;

import android.util.Log;

/**
 * Created by eahom on 17/1/11.
 */

public class Logger {

    private static final boolean DEBUG = true;
    private static final String TAG = "MessageBus";

    public static void e(String logText) {
        if (DEBUG)
            Log.e(TAG, logText);
    }

}
