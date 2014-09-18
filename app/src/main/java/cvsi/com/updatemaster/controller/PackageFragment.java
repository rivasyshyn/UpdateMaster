package cvsi.com.updatemaster.controller;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cvsi.com.updatemaster.R;
import cvsi.com.updatemaster.data.Resource;
import cvsi.com.updatemaster.dialogs.ErrorDialog;

/**
 * Created by rivasyshyn on 17.09.2014.
 */
public class PackageFragment extends AbstractViewController {

    ImageView ivLogo;
    TextView tvTitle;
    TextView tvDesc;
    Button btInstall;
    private long mLastLoadId;
    private String mLastUpdateName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_package, null);

        ivLogo = (ImageView) v.findViewById(R.id.iv_logo);
        tvTitle = (TextView) v.findViewById(R.id.tv_title);
        tvDesc = (TextView) v.findViewById(R.id.tv_desc);
        btInstall = (Button) v.findViewById(R.id.btn_install);

        super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }

    @Override
    protected void updateView(Resource resource) {

        tvTitle.setText(resource.getName());
        tvDesc.setText(resource.getDescription());
        btInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    downloadApk();
                } catch (Exception e) {
                    ErrorDialog.show(getActivity(),
                            getString(R.string.error),
                            getString(R.string.err_cant_load_package),
                            new ErrorDialog.Action(15, getString(R.string.ok)),
                            null);
                }
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void downloadApk() throws Exception {

        final DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(getData().getUrl()));
        request.setDestinationInExternalPublicDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "update.apk");

        mLastLoadId = downloadManager.enqueue(request);

        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(mLastLoadId);
                    Cursor c = downloadManager.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        int status = c.getInt(columnIndex);
                        switch (status) {
                            case DownloadManager.STATUS_SUCCESSFUL:
                                columnIndex = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                                mLastUpdateName = c.getString(columnIndex);
                                context.unregisterReceiver(this);
                                installApk();
                                break;
                            case DownloadManager.STATUS_FAILED:
                                ErrorDialog.show(getActivity(),
                                        getString(R.string.error),
                                        getString(R.string.err_cant_load_package),
                                        new ErrorDialog.Action(1, getString(R.string.ok)),
                                        null);
                                context.unregisterReceiver(this);
                                break;
                        }

                    }
                }
            }
        }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public void installApk() {
        Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(Uri.parse("file://" + mLastUpdateName), "application/vnd.android.package-archive");
        getActivity().startActivity(promptInstall);
    }
}
