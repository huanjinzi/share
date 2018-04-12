package com.hjz.share.model;

import java.util.List;

/**
 * Created by hjz on 18-1-31.
 * for:
 */

public class TypeFileInfoList {
    public final String MIME_TYPE;
    public final List<FileInfo> FILE_INFO_LIST;

    public TypeFileInfoList(String mime_type, List<FileInfo> fileInfoList) {
        MIME_TYPE = mime_type;
        FILE_INFO_LIST = fileInfoList;
    }
}
