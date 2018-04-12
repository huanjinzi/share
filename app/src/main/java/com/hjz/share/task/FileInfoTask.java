package com.hjz.share.task;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjz.share.R;
import com.hjz.share.command.BaseInvoker;
import com.hjz.share.command.Command;
import com.hjz.share.command.Invoker;
import com.hjz.share.command.NotifyCommand;
import com.hjz.share.command.Receiver;
import com.hjz.share.holder.BaseHolder;
import com.hjz.share.holder.Grid;
import com.hjz.share.holder.IViewType;
import com.hjz.share.holder.ListItem;
import com.hjz.share.holder.Title;
import com.hjz.share.holder.Divider;
import com.hjz.share.model.FileInfo;
import com.hjz.share.model.PackagePath;
import com.hjz.share.model.SortableKey;
import com.hjz.share.model.TypeFileInfoList;
import com.hjz.share.utils.FileInfoTool;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileInfoTask extends AsyncTask<Object, Integer, Receiver> {

    private static final String PACKAGE_PATH = "package.json";
    private static final String TAG = "FileInfoTask";
    private final static String VOLUME_NAME_EXTERNAL = "external";
    private final static String ORDER = "(date_modified) DESC";
    private final static String MM_DD = "MM/dd";
    private static final long DAYS_MILLI_SECOND = 24 * 60 * 60 * 1001;
    private static final String[] COLUMNS = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_MODIFIED
    };

    private final Context context;
    private final PackageManager pm;

    public FileInfoTask(Context context) {
        this.context = context;
        pm = context.getPackageManager();
    }

    @Override
    protected Receiver doInBackground(Object ...voids) {
        long start = System.currentTimeMillis();
        List<IViewType> dataModels = (List<IViewType>) voids[0];
        dataModels.clear();
        List<FileInfo> fileInfoList = new ArrayList<>();
        String select_opt =
                //" date_modified >" + lastTime +
                " mime_type<>'null'" +
                        " and _data not like '%/.%' " +
///Android/data
                        " and _data not like '%/Android/data%' " +
                        " and (" +
                        //video
                        "mime_type='video/mp4' or " +
                        "mime_type='video/3gpp' or " +

                        //image
                        "(mime_type='image/jpeg' and _size>5000) or " +
                        "(mime_type='image/jpg' and _size>5000) or " +
                        "(mime_type='image/png'  and _size>5000) or " +
                        "(mime_type='image/gif'  and _size>5000) or " +

                        //audio
                        "(mime_type='" + FileInfo.MIME_MP3 + "' and _size>500000) or " +
                        "(mime_type='" + FileInfo.MIME_3GPP + "' and _size>500000) or " +
                        "(mime_type='" + FileInfo.MIME_WAV + "' and _size>500000) or " +

                        //doc
                        "(mime_type='" + FileInfo.MIME_TXT + "' and _size>1000) or " +
                        "mime_type='" + FileInfo.MIME_PPTX + "' or " +
                        "mime_type='" + FileInfo.MIME_XLSX + "' or " +
                        "mime_type='" + FileInfo.MIME_ZIP + "' or " +
                        "mime_type='" + FileInfo.MIME_RAR + "' or " +

                        "mime_type like 'application/%' or " +

                        "mime_type='application/pdf')";

        ContentResolver resolver;
        resolver = context.getContentResolver();

        Cursor cursor;
        cursor = resolver.query(MediaStore.Files.getContentUri(VOLUME_NAME_EXTERNAL), COLUMNS, select_opt, null, ORDER);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                FileInfo fileInfo = new FileInfo(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMNS[0])),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMNS[1])),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMNS[2])),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMNS[3])),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMNS[4])),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMNS[5]))
                );
                fileInfo.setThumbnail(null);
                fileInfoList.add(fileInfo);
            }
            cursor.close();
        }
        /**2.读取应用与路径的对应关系，其中key为包名，value为List<File>*/
        InputStream in;
        List<PackagePath> packagePathList = null;
        try {
            in = context.getAssets().open(PACKAGE_PATH);
            Gson gson = new Gson();
            packagePathList = gson.fromJson(new InputStreamReader(in), new TypeToken<List<PackagePath>>() {
            }.getType());
        } catch (Exception e) {
            Log.e(TAG, "can't open or find:package.json", e);
        }
        finally {
            //cancel(true);
        }
        HashMap<String,String> hashMap = FileInfoTool.transferPackagePathToMap(packagePathList);

        /**3.为每一个FileInfo设置包名*/
        FileInfoTool.setDefaultPackageNameForFileInfo(fileInfoList);
        FileInfoTool.setPackageNameForFileInfo(fileInfoList, hashMap);

        /**4.对文件以天为间隔进行分组*/
        Map<LocalDate, List<FileInfo>> mapByDay = FileInfoTool.groupByDay(fileInfoList);
        Set<LocalDate> dayKeys = mapByDay.keySet();

        /**5.对文件以类型和应用分组*/
        dayKeys.stream().sorted((ld1, ld2) -> ld1.isAfter(ld2) ? -1 : 1).forEach(day -> {
            //groupByMimeType
            List<TypeFileInfoList> listByType = FileInfoTool.groupByMimeType(mapByDay.get(day));

            listByType.stream().forEach(type -> {
                //groupByPackageName会返回一个key为SortableKey的HashMap，其中SortableKey的KEY为PackageName
                HashMap<SortableKey, List<FileInfo>> mapByPackageName = FileInfoTool.groupByPackageName(type.FILE_INFO_LIST);
                Set<SortableKey> packageKeys = mapByPackageName.keySet();

                packageKeys.stream().sorted().forEach(key -> {
                    //SortableKey的KEY为PackageName,ORDER为文件修改的时间
                    String packageName = key.KEY;
                    ApplicationInfo appInfo = null;
                    try {
                        appInfo = pm.getApplicationInfo(packageName, 0);
                    } catch (PackageManager.NameNotFoundException ignored) {
                    }
                    List<FileInfo> items = mapByPackageName.get(key);
                    FileInfo item;
                    dataModels.add(new Title((String) pm.getApplicationLabel(appInfo),
                            day.format(DateTimeFormatter.ofPattern(MM_DD)),pm.getApplicationIcon(appInfo)));
                    for (int i = 0,size=items.size(); i < size; i++) {
                        item = items.get(i);
                        switch (item.getViewType()) {
                            case BaseHolder.VIEW_TYPE_LIST:
                                ListItem listItem = new ListItem(item.getDisplayName(), item.getDisplaySize(), item.getIconId(), item.getPath());
                                if (i != 0) listItem.showTopDivider(false);
                                dataModels.add(listItem);
                                break;
                                case BaseHolder.VIEW_TYPE_GRID:
                                    dataModels.add(new Grid(item.getPath(), item.getType() == FileInfo.VIDEO));
                                    if (i % 3 == 2) dataModels.add(new Divider(12, R.color.white));
                                    break;
                        }
                    }
                    dataModels.add(new Divider(42, R.drawable.group_divider_background));
                });
            });
        });
        Log.d(TAG, "doInBackground end");
        Log.w(TAG, System.currentTimeMillis() - start + "--spend time!");
        return (Receiver) voids[1];
    }

    @Override
    protected void onPostExecute(Receiver receiver) {
        Log.d(TAG, "onPostExecute()");
        Command command = new NotifyCommand(receiver,-1);
        Invoker invoker = new BaseInvoker(command);
        invoker.runCommand();
    }
}