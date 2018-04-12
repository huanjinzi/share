package com.hjz.share.task;

import android.util.Log;

/**
 * Created by hjz on 17-12-27.
 * for:
 */

public class BaseTask implements Runnable {

    public static final String TAG = "BaseTask-YH";
    @Override
    public void run() {
        try {
            Log.i(TAG, "BaseTask executing...");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**this will run in the UI thread*/
    public void onPostExecute(){

    }
}
