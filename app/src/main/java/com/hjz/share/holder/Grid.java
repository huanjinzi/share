package com.hjz.share.holder;

import android.graphics.drawable.Drawable;

import com.hjz.share.model.IFileInfo;

/**
 * Created by hjz on 18-2-1.
 * for:
 */

public class Grid implements IViewType,IFileInfo {

    private final String mFilePath;
    private Drawable thumbnail;
    private final boolean video;
    private boolean checked;

    public Grid(String mFilePath, boolean video) {
        this.mFilePath = mFilePath;
        this.video = video;
    }


    @Override
    public int getViewType() {
        return BaseHolder.VIEW_TYPE_GRID;
    }

    public boolean isVideo() {
        return video;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String getFilePath() {
        return mFilePath;
    }

    public Drawable getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Drawable thumbnail) {
        this.thumbnail = thumbnail;
    }
}
