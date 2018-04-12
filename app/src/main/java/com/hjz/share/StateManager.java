package com.hjz.share;

import android.util.Log;

import com.hjz.share.model.IFileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjz on 17-12-25.
 * for:
 */

public class StateManager {

    public static final String TAG = "StateManager";

    /**
     * UI is half screen
     */
    public static final int UI_MODE_HALF = 0;
    /**
     * UI is full screen
     */
    public static final int UI_MODE_FULLSCREEN = 1;

    /**
     * send data
     */
    public static final int ACTION_SEND = 3;
    /**
     * enter select mode
     */
    public static final int ACTION_SELECT = 4;
    /**
     * exit select mode
     */
    public static final int ACTION_SELECT_CANCEL = 5;

    /**
     * notify the adapter
     */
    public static final int ACTION_NOTIFY = 6;

    /**
     * the action can run
     */
    public static final int ACTION_RUN = 7;

    /**
     * edit mode
     */
    public static final int MODE_NORMAL = 6;
    public static final int MODE_MULTI_SELECT = 7;

    private static int UiMode = UI_MODE_HALF;
    private static int Mode = MODE_NORMAL;
    private static int SelectedNum = 0;


    private static List<IFileInfo> sendList = new ArrayList<>();

    /**
     * @return return the current UI mode
     */
    public static int getUiMode() {
        return UiMode;
    }

    /**
     * set the current UI mode
     *
     * @param mode ui mode to set
     */
    public static void setUiMode(int mode) {
        UiMode = mode;
    }

    public static int getMode() {
        return Mode;
    }

    public static void setMode(int mode) {
        Mode = mode;
    }

    public static void add(IFileInfo info) {
        sendList.add(info);
        SelectedNum++;
    }


    public static void remove(IFileInfo info) {
        try {
            sendList.remove(info);
            SelectedNum--;
        } catch (Exception e) {
            SelectedNum = 0;
            sendList.clear();
            Log.e(TAG, "" + e.getMessage());
        }

    }

    /**
     * check is exit multi select mode
     */
    public static boolean exit() {

        if (Mode == MODE_MULTI_SELECT && SelectedNum == 0) {
            Mode = MODE_NORMAL;
            return true;
        }
        return false;
    }

    /**
     * check is exit multi select mode
     */
    public static void reset(){
        Mode = MODE_NORMAL;
        SelectedNum = 0;
        for (IFileInfo info : sendList
                ) {
            info.setChecked(false);
        }
        sendList.clear();
    }

    /**
     * return the send list size
     */
    public static int size() {
        return SelectedNum;
    }

    public static List<IFileInfo> getSendList(){
        return sendList;
    }
}
