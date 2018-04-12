package com.hjz.share;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;

import java.util.ArrayList;


public class MainActivity extends Activity {


    public static final String TAG = "MainActivity";
    public static final int QUERY = 101;

    private FragmentManager fm = getFragmentManager();
    private ShareKeyFragment shareKeyFragment = new ShareKeyFragment();

    /**
     * what permissions are needed by this activity
     */
    private static String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check normal permission
        //checkPermission();
        //RequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_activity);
        start(shareKeyFragment);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * check the Permission
     */
    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            ArrayList<String> needRequestPermissions = new ArrayList<>();
            for (String permission : PERMISSIONS) {
                if (checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                    needRequestPermissions.add(permission);
                }
            }
            requestPermissions(needRequestPermissions.toArray(new String[]{}), QUERY);
        }
        else {
            start(shareKeyFragment);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            ArrayList<String> fails = new ArrayList<>();
            boolean allPermissionsGrant = true;
            if (requestCode == QUERY) {
                boolean shouldShowRequestPermissionRationale = false;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        allPermissionsGrant = false;
                        fails.add(permissions[i]);
                        boolean boo;

                        boo = !shouldShowRequestPermissionRationale(permissions[i]);

                        shouldShowRequestPermissionRationale = (shouldShowRequestPermissionRationale || boo);
                    }
                }

                if (shouldShowRequestPermissionRationale) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setPositiveButton("确认", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    });
                    builder.setNegativeButton("取消", (dialog, which) -> System.exit(0));
                    builder.setMessage("need READ_EXTERNAL_STORAGE");
                    builder.create().show();
                } else if (!allPermissionsGrant) {
                    //end
                    System.exit(0);
                    //requestPermissions(fails.toArray(new String[]{}), QUERY);
                }
                else if (allPermissionsGrant){
                    start(shareKeyFragment);
                }
            }
        }
    }

    private void start(Fragment fragment) {
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (shareKeyFragment.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
