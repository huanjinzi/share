package com.hjz.share.holder;

import android.graphics.drawable.Drawable;

/**
 * Created by hjz on 18-2-1.
 * for:
 */

public class Title implements IViewType {

    private final String mAppName;
    private final String mDate;
    private final Drawable mAppIcon;

    public Title(String mAppName, String mDate, Drawable mIcon) {
        this.mAppName = mAppName;
        this.mDate = mDate;
        this.mAppIcon = mIcon;
    }

    @Override
    public int getViewType() {
        return BaseHolder.VIEW_TYPE_TITLE;
    }

    public String getAppName() {
        return mAppName;
    }

    public String getDate() {
        return mDate;
    }

    public Drawable getAppIcon() {
        return mAppIcon;
    }
}
