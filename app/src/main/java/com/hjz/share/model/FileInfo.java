package com.hjz.share.model;

import android.graphics.drawable.Drawable;

import com.hjz.share.R;
import com.hjz.share.holder.BaseHolder;

import java.io.File;
import java.util.HashMap;

/**
 * Created by hjz on 17-11-30.
 * for:
 */


public class FileInfo extends DataModel{
    public static final int OTHER = -1;
    public static final int IMAGE = 0;
    public static final int AUDIO = 1;
    public static final int VIDEO = 2;

    public static final String MIME_3GPP = "audio/mp4";
    public static final String MIME_MP3 = "audio/mpeg";
    public static final String MIME_WAV = "audio/x-wav";

    public static final String MIME_TXT = "text/plain";
    public static final String MIME_PDF = "application/pdf";
    public static final String MIME_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String MIME_PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    public static final String MIME_ZIP = "application/zip";
    public static final String MIME_RAR = "application/rar";

    public static HashMap<String, Integer> ICON_MAP = new HashMap<String,Integer>(){
        {
            put(MIME_3GPP, R.mipmap.ic_3gpp);
            put(MIME_MP3, R.mipmap.ic_mp3);
            put(MIME_WAV, R.mipmap.ic_wav);
            put(MIME_TXT, R.mipmap.ic_txt);
            put(MIME_PDF, R.mipmap.ic_pdf);
            put(MIME_XLSX, R.mipmap.ic_office);
            put(MIME_PPTX, R.mipmap.ic_office);
            put(MIME_ZIP, R.mipmap.ic_zip);
            put(MIME_RAR, R.mipmap.ic_rar);
        }
    };

    private Drawable thumbnail;
    private String mDisplayName;
    private String mDisplaySize;

    private String mPath;
    private String mThumnailPath;


    public int getViewType() {
        return mViewType = mMimeType.contains("image") || mMimeType.contains("video")?BaseHolder.VIEW_TYPE_GRID: BaseHolder.VIEW_TYPE_LIST;
    }

    public void setViewType(int mViewType) {
        this.mViewType = mViewType;
    }

    private int mViewType = BaseHolder.VIEW_TYPE_LIST;

    public String getMimeType() {
        return mMimeType;
    }

    private String mMimeType;

    public long getId() {
        return mId;
    }

    private long mId;

    public long getSize() {
        return mSize;
    }

    private long mSize;
    private long mDateModified;

    public String getPackageName() {
        return packageName;
    }

    public FileInfo setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    private String packageName;

    private boolean isSelected = false;

    public int getType() {
        if (mMimeType == null) {
            return OTHER;
        }
        if (mMimeType.contains("image/")) {
            return IMAGE;
        } else if (mMimeType.contains("audio/")) {
            return AUDIO;
        } else if (mMimeType.contains("video/")) {
            return VIDEO;
        }
        return type;
    }

    private int type = OTHER;


    public FileInfo(long id,String display_name, String path, String mime_type, long size, long date_modified) {
        mId = id;
        mDisplayName = display_name;
        mPath = path;
        mMimeType = mime_type;
        mSize = size;
        mDateModified = date_modified;
    }

    public String getPath() {
        return mPath;
    }

    public String getDir() {
        return new File(mPath).getParent();
    }

    public String getDisplayName() {
        if (mDisplayName == null) {
            String[] s = mPath.split("[/]");
            return s[s.length - 1];
        }
        return mDisplayName;
    }

    public String getDisplaySize() {
        long per = 1024;
        if (mSize < per) {
            return mSize + "B";
        } else if (mSize < per*per) {
            return mSize / per + "KB";
        } else if (mSize < 10 * per * per) {
            long M = mSize / (per * per);
            long L_KB = mSize%(per * per) / per;
            long M_1 = L_KB/100;
            long M_2 = L_KB%100/10;
            return M+"."+M_1+""+M_2+"MB";
        } else if (mSize < 100 * per * per) {
            long M = mSize / (per * per);
            long L_KB = mSize%(per * per) / per;
            long M_1 = L_KB/100;
            return M+"."+M_1+"MB";
        } else if (mSize < per * per * per) {
            long M = mSize / (per * per);
            return M+"MB";
        }
            return "";
    }

    public long getDateModified() {
        return mDateModified;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Drawable getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Drawable thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getIconId() {
        return ICON_MAP.get(mMimeType) == null?R.mipmap.ic_default:ICON_MAP.get(mMimeType);
    }

    @Override
    public String Title() {
        return getDisplayName();
    }

    @Override
    public String Size() {
        return getDisplaySize();
    }

    @Override
    public Drawable Thumbnail() {
        return getThumbnail();
    }
    @Override
    public int ViewType() {
        return getViewType();
    }

    @Override
    public Object Object() {
        return this;
    }
}

