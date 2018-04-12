package com.hjz.share.model;

import android.graphics.drawable.Drawable;

import com.hjz.share.holder.IViewType;

/**
 * Created by hjz on 17-12-8.
 * for:
 */

public abstract class DataModel implements IViewType {
    public String Title(){
        return null;}
    public int ViewType(){
        return 0;
    }
    public String Description() {
        return null;
    }

    public String Content() {
        return null;
    }

    public String Date() {
        return null;
    }

    public String Size() {
        return null;
    }

    public Drawable Thumbnail() {
        return null;
    }

    public Drawable Icon() {
        return null;
    }

    public Object Object(){
        return null;
    }


    @Override
    public int getViewType() {
        return ViewType();
    }
}
