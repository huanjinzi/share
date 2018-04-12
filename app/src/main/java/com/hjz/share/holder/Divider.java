package com.hjz.share.holder;

/**
 * Created by hjz on 18-2-1.
 * for:
 */

public class Divider implements IViewType {

    private final int mHeight;

    public int getHeight() {
        return mHeight;
    }

    private final int mColor;

    public int getColor() {
        return mColor;
    }

    public Divider(int mHeight, int mColor) {
        this.mHeight = mHeight;
        this.mColor = mColor;
    }

    @Override
    public int getViewType() {
        return BaseHolder.VIEW_TYPE_DIVIDER;
    }
}
