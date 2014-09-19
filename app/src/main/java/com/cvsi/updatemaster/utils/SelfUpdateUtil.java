package com.cvsi.updatemaster.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.cvsi.updatemaster.data.PackageInfo;
import com.cvsi.updatemaster.data.Resource;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rivasyshyn on 18.02.14.
 */
public class SelfUpdateUtil {

    private static final String TAG = SelfUpdateUtil.class.getSimpleName();
    private static final String APK_FILE_TEMPLATE = "update_%1$s.apk";

    private static String sLastUpdateName;
    private static long sLastLoadId;

    public static enum Status {
        NONE, LESS, EQUAL, GREAT
    }

    /**
     * Check if any update available
     */
    public void getNewConfig(Context context, String url, FutureCallback<Resource> callback) {
        if (TextUtils.isEmpty(url)) {
            callback.onCompleted(new NullPointerException(), null);
            return;
        }

        Ion.with(context)
                .load(url)
                .as(Resource.class)
                .setCallback(callback);
    }

    /**
     * Download apk file
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void downloadApk(Context context, PackageInfo info, final FutureCallback<Boolean> listener) {
        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(info.getApkLink()));
        request.setDestinationInExternalPublicDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "update " + info.getApkVersion() + ".apk");

        sLastLoadId = downloadManager.enqueue(request);

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(sLastLoadId);
                    Cursor c = downloadManager.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        int status = c.getInt(columnIndex);
                        switch (status) {
                            case DownloadManager.STATUS_SUCCESSFUL:
                                columnIndex = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                                sLastUpdateName = c.getString(columnIndex);
                                listener.onCompleted(null, true);
                                context.unregisterReceiver(this);
                                break;
                            case DownloadManager.STATUS_FAILED:
                                listener.onCompleted(new Exception(), false);
                                context.unregisterReceiver(this);
                                break;
                        }

                    }
                }
            }
        }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /**
     * install apk file from path
     */
    public void installApk(final Activity activity) {
        Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(Uri.parse("file://" + sLastUpdateName), "application/vnd.android.package-archive");
        activity.startActivity(promptInstall);
    }

    /**
     * clean storage from files
     */
    public void cleanApk() {
//        ApiHelper.deleteFile(new File(Utils.getStorage(Storages.APK)));
    }

    public UpdateInfo getUpdateInfo(Context context, PackageInfo packageInfo) {

        UpdateInfo updateInfo = new UpdateInfo();
        updateInfo.status = Status.NONE;
        updateInfo.info = packageInfo;
        updateInfo.currentVersion = getLocalAppVersion(context, packageInfo.getPackageName());

        if (updateInfo.currentVersion != null) {

            ApkVersion localApkVersion = ApkVersion.valueOf(updateInfo.currentVersion);
            ApkVersion remoteVersion = ApkVersion.valueOf(packageInfo.getApkVersion());

            int val = localApkVersion.compareTo(remoteVersion);

            updateInfo.status = val == 0 ? Status.EQUAL : (val > 0 ? Status.GREAT : Status.LESS);
        }

        if(updateInfo.status == Status.GREAT) {
            updateInfo.isUpdateAvailable = true;
        }

        return updateInfo;
    }

    private String getLocalAppVersion(Context context, String applicationId) {

        PackageManager packageManager = context.getPackageManager();

        List<android.content.pm.PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);

        for (android.content.pm.PackageInfo packageInfo : installedPackages) {
            if (packageInfo.packageName.equals(applicationId)) {
                return packageInfo.versionName;
            }
        }

        return null;
    }


    public static class ApkVersion implements Comparable<ApkVersion> {
        Object[] components;
        String original;

        private ApkVersion() {
        }

        public static ApkVersion valueOf(String value) {
            if (TextUtils.isEmpty(value)) {
                throw new IllegalArgumentException("value can't be empty or null");
            }

            ApkVersion version = new ApkVersion();
            version.original = value;
            String[] parts = value.split("[\\. ]");
            version.components = new Object[parts.length];
            int i = 0;
            for (String part : parts) {
                try {
                    version.components[i] = Integer.valueOf(part);
                } catch (NumberFormatException e) {
                    version.components[i] = part;
                }
                i++;
            }
            return version;
        }

        @Override
        public String toString() {
            return original;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ApkVersion)) return false;

            ApkVersion that = (ApkVersion) o;

            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            if (!Arrays.equals(components, that.components)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(components);
        }

        @Override
        public int compareTo(ApkVersion another) {
            if (this == another) return 0;

            int len = components.length < another.components.length ? components.length : another.components.length;

            for (int i = 0; i < len; i++) {
                if (components[i] instanceof Integer && another.components[i] instanceof Integer) {
                    int res = ((Integer) components[i]).compareTo((Integer) another.components[i]);
                    if (res == 0) {
                        continue;
                    } else {
                        return res;
                    }
                } else if (components[i] instanceof Integer) {
                    return 1;
                } else if (another.components[i] instanceof Integer) {
                    return -1;
                } else {
                    return 0;
                }
            }

            return 0;
        }
    }

    public static class UpdateInfo {
        Boolean isUpdateAvailable;
        Status status;
        PackageInfo info;
        String currentVersion;

        public Boolean isUpdateAvailable() {
            return isUpdateAvailable;
        }

        public String getCurrentVersion() {
            return currentVersion;
        }

        public PackageInfo getInfo() {
            return info;
        }

        public Status getStatus() {
            return status;
        }
    }

}
