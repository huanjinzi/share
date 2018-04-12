package com.hjz.share.model;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjz on 17-12-2.
 * for:
 */

public class PackagePath {

    /**
     * packageName : com.tencent.mm
     * paths : [{"name":"/tencent/MicroMsg"}]
     */

    private String packageName;
    private List<Path> paths;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<Path> getPath() {
        return paths;
    }

    public List<Path> setPaths(List<Path> paths) {
        this.paths = paths;
        return paths;
    }

    public List<File> getFiles(){
        List<File> files = new ArrayList<>();
        for (Path path:paths
             ) {
            String p = Environment.getExternalStorageDirectory().getPath() + path.getName();
            File file = new File(p);
            if (file.exists()) {
                files.add(file);
            }
        }
        return files;
    }

    public static class Path {
        /**
         * name : /tencent/MicroMsg
         */

        private String name;

        public String getName() {
            return name;
        }

        public Path setName(String name) {
            this.name = name;
            return this;
        }
    }
}



