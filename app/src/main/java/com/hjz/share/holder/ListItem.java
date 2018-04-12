package com.hjz.share.holder;

import com.hjz.share.model.IFileInfo;

/**
 * Created by hjz on 18-2-1.
 * for:
 */

public class ListItem implements IViewType,IFileInfo{

    private final String mFileName;
    private final String mFileSize;
    private final int mFileIcon;
    private final String mFilePath;

    private boolean checked;
    private boolean topDivider = true;
    private boolean bottomDivider = true;

    public ListItem(String mFileName, String mFileSize, int mFileIcon, String mFilePath) {
        this.mFileName = mFileName;
        this.mFileSize = mFileSize;
        this.mFileIcon = mFileIcon;
        this.mFilePath = mFilePath;
    }

    @Override
    public int getViewType() {
        return BaseHolder.VIEW_TYPE_LIST;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getFileSize() {
        return mFileSize;
    }

    public int getFileIcon() {
        return mFileIcon;
    }

    public boolean isShowTopDivider() {
        return topDivider;
    }

    public void showTopDivider(boolean topDivider) {
        this.topDivider = topDivider;
    }

    public boolean isShowBottomDivider() {
        return bottomDivider;
    }

    public void showBottomDivider(boolean bottomDivider) {
        this.bottomDivider = bottomDivider;
    }


    @Override
    public String getFilePath() {
        return mFilePath;
    }
}
