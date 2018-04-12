package com.hjz.share.utils;

import android.os.Environment;

import com.hjz.share.model.FileInfo;
import com.hjz.share.model.PackagePath;
import com.hjz.share.model.SortableKey;
import com.hjz.share.model.TypeFileInfoList;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hjz on 17-12-2.
 * for:
 */

public class FileInfoTool {

    public static final String PATH_SEPARATOR = "/";
    public static final String MIME_TYPE_IMAGE = "image/";
    public static final String MIME_TYPE_AUDIO = "audio/";
    public static final String MIME_TYPE_VIDEO = "video/";

    public static final String DEFAULT_PACKAGE_IMAGE = "com.android.gallery3d";
    public static final String DEFAULT_PACKAGE_AUDIO = "com.android.music";
    public static final String DEFAULT_PACKAGE_VIDEO = "com.android.gallery3d";
    public static final String DEFAULT_PACKAGE_OTHER = "com.transsion.filemanager";

    private static final long DAYS = 24 * 60 * 60;

    /**
     * 为FileInfo List中的每一个对象设置默认包名
     * @param fileInfoList FileInfo对象的列表
     * */
    public static List<FileInfo> setDefaultPackageNameForFileInfo(List<FileInfo> fileInfoList) {
        return fileInfoList.parallelStream().filter(info -> info.getMimeType() != null).map(info -> {
            if (info.getMimeType().contains(MIME_TYPE_IMAGE))
                return info.setPackageName(DEFAULT_PACKAGE_IMAGE);

            else if (info.getMimeType().contains(MIME_TYPE_AUDIO))
                return info.setPackageName(DEFAULT_PACKAGE_AUDIO);

            else if (info.getMimeType().contains(MIME_TYPE_VIDEO))
                return info.setPackageName(DEFAULT_PACKAGE_VIDEO);

            return info.setPackageName(DEFAULT_PACKAGE_OTHER);
        }).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }


    /**
     * 将 app/assets/package.json 中的数据转化为HashMap，其中key为文件路径（dir），在转化的过程
     * 中过滤掉不存在的文件夹，避免不必要的运算，转化成HashMap之后为每个FileInfo设置包名（PackageName）的
     * 时候查询速度很快，不需要对PackagePath遍历
     * value为文件所属app的包名（PackageName）,HashMap查找效率比较高，HashMap非线程安全，
     * HashMap将key-value 实体（Entity）存在数组中（通过key的Hash值算出存储位置），当发生冲突时，会调用
     * key.equals()方法来判断key是否相等，不相等则在HEAD Entity的前面插入新的Entity，避免尾递归，当HashMap
     * 超过负载因子（一般0.75时），会对数组进行扩容(bucket)，一般为1倍
     *
     * @param packagePathList PackagePath列表，包含包名与之对应的paths
     * @return 返回key为path，value为PackageName的HashMap
     * */
    public static HashMap<String,String> transferPackagePathToMap(List<PackagePath> packagePathList){
        final File root = Environment.getExternalStorageDirectory();
        HashMap<String, String> map = new HashMap<>();
        packagePathList.stream()
                .forEach(pp -> pp.getPath().stream()
                        .map(path -> {
                            String name = path.getName();
                            if (!name.startsWith(PATH_SEPARATOR)) name = PATH_SEPARATOR.concat(name);
                            if (name.endsWith(PATH_SEPARATOR)) name = name.substring(0, name.length() - 1);
                            return path.setName(root.getAbsolutePath() + name);})
                        .forEach(path -> {
                            File file;
                            if ((file = new File(path.getName())).exists())
                                map.put(file.getAbsolutePath(), pp.getPackageName());})
                );
        return map;
    }


    /**
     * 对于给定的FileInfo ListItem，为每一个FileInfo设置包名
     * @param fileInfoList 只设置了默认包名（PackageName）的FileInfo ListItem
     * @param pathPackageMap 路径（Path）与包名（PackageName）的HashMap，key为路径，value为包名
     * @return 返回设置了包名（PackageName）之后的FileInfo ListItem
     * */
    public static List<FileInfo> setPackageNameForFileInfo(List<FileInfo> fileInfoList,HashMap<String,String> pathPackageMap) {
        fileInfoList.parallelStream()
                .forEach(info -> {
                    if (pathPackageMap.containsKey(info.getDir()))
                        info.setPackageName(pathPackageMap.get(info.getDir()));
                });
        return fileInfoList;
    }

    /**
     * 将文件列表以时间间隔<天>进行分组，日期在同一天的文件都被加入到同一个列表
     * @param fileInfoList {@code com.sagereal.sharekey.model.FileInfo.java}的列表
     * @return 返回一个HashMap，其中key为LocalDate（不可变对象，并且线程安全），value为
     * ListItem<FileInfo>
     * */
    public static Map<LocalDate, List<FileInfo>> groupByDay(List<FileInfo> fileInfoList) {
        return fileInfoList.parallelStream().collect(Collectors.groupingBy(info -> LocalDate.ofEpochDay(info.getDateModified() / DAYS + 1)));
    }

    /**
     * 按天分组之后，需要按类型对每天的文件进行分组（每天的文件都是有序的），并且分组是是以连续类型为一组*/
    public static List<TypeFileInfoList> groupByMimeType(List<FileInfo> fileInfoList){
        fileInfoList.sort((f1, f2) -> f1.getDateModified() < f2.getDateModified() ? 1 : -1);
        List<TypeFileInfoList> list = new ArrayList<>();
        FileInfo last = null;
        List<FileInfo> item = null;
        for (FileInfo info:fileInfoList
             ) {
            if (last == null) {
                //第一个文件进来
                list.add(new TypeFileInfoList(info.getMimeType(), item = new ArrayList<>()));
                item.add(info);
            } else if (last.getMimeType().equals(info.getMimeType())) {
                //这个文件和上个文件属于相同的类型
                item.add(info);
            } else {
                //这个文件和上个文件不是同一个类型
                list.add(new TypeFileInfoList(info.getMimeType(), item = new ArrayList<>()));
                item.add(info);
            }
            last = info;
        }
        return list;
    }

    public static HashMap<SortableKey, List<FileInfo>> groupByPackageName(List<FileInfo> fileInfoList){
        HashMap<SortableKey, List<FileInfo>> map = new HashMap<>();
        fileInfoList.stream().forEach(info ->{
            //这儿没有更新时间
            SortableKey sortableKey = new SortableKey(info.getPackageName(), info.getDateModified());
            if (map.containsKey(sortableKey)) {
                map.get(sortableKey).add(info);
            } else {
                map.put(sortableKey, new ArrayList<>());
                map.get(sortableKey).add(info);
            }
        });
        return map;
    }

}
