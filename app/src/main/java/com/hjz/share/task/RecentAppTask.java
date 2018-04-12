package com.hjz.share.task;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.hjz.share.R;
import com.hjz.share.ShareKeyFragment;
import com.hjz.share.model.DataModel;
import com.hjz.share.model.FileInfo;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecentAppTask extends AsyncTask<Void, Integer, List<UsageStats>> {

    public static final String TAG = "RecentAppTask";
    private List<FileInfo> fileInfoList;
    private final List<UsageStats> usageStatsList;
    private final List<DataModel> dataModels;
    private final Context context;
    private ShareKeyFragment shareKeyFragment;

    public RecentAppTask(ShareKeyFragment shareKeyFragment,List<DataModel> dataModels,List<UsageStats> usageStatsList,List<FileInfo> fileInfoList) {
        this.fileInfoList = fileInfoList;
        this.usageStatsList = usageStatsList;
        this.dataModels = dataModels;
        this.shareKeyFragment = shareKeyFragment;
        this.context = shareKeyFragment.getContext();
    }

    @Override
    protected List<UsageStats> doInBackground(Void... voids) {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);

        UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = manager
                .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, cal.getTimeInMillis(),
                        System.currentTimeMillis());

        /**filter system app*/
        String[] system_app = null;
        List system_list = Arrays.asList(system_app);//system app white list
        for (UsageStats u : queryUsageStats) {
            //get ApplicationInfo by packageName
            String packageName = u.getPackageName();
            ApplicationInfo appInfo = null;
            try {
                appInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                Log.i(TAG, packageName + " not found!");
                continue;
            }
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                //system app
                if (system_list.contains(appInfo.packageName)) {
                    usageStatsList.add(u);
                }
            } else {
                usageStatsList.add(u);
            }
        }

        /**sort*/
        Collections.sort(queryUsageStats, new Comparator<UsageStats>() {
            @Override
            public int compare(UsageStats o1, UsageStats o2) {
                return  ((Long) o2.getLastTimeUsed()).compareTo(o1.getLastTimeUsed());
            }
        });
        return usageStatsList;
    }

    @Override
    protected void onPostExecute(List<UsageStats> usages) {
        //new FileInfoTask(shareKeyFragment, dataModels, usageStatsList, fileInfoList).execute();
    }
}