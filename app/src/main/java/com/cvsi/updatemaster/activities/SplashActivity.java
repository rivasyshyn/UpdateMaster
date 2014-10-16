package com.cvsi.updatemaster.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.cvsi.updatemaster.BuildConfig;
import com.cvsi.updatemaster.R;


public class SplashActivity extends Activity {

    private Handler mHandler;
    private final int mTimeout = 700; //1 sec.
    private boolean shown = false;
    private static final String KEY_TYPE = SplashActivity.class.getPackage().getName() + "KEY_TYPE";
    private boolean mIsAbout;
    private Runnable mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mHandler = new Handler();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mIsAbout = bundle.getString(KEY_TYPE, "").equals("about");
        }

        if(mIsAbout){
            findViewById(android.R.id.content).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        TextView textView = (TextView) findViewById(R.id.tv_logo);
        textView.setText(getString(R.string.splash_message, BuildConfig.VERSION_NAME));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mIsAbout) {
            mHandler.removeCallbacks(mTask);
            shown = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mIsAbout){
            return;
        }
        mTask = new Runnable() {
            @Override
            public void run() {
                finish();
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(intent);
            }
        };
        if (shown) {
            mTask.run();
            return;
        }
        mHandler.postDelayed(mTask, mTimeout);
    }

    public static final void startAsAbout(Activity activity) {
        Intent intent = new Intent(activity, SplashActivity.class);
        intent.putExtra(KEY_TYPE, "about");
        activity.startActivity(intent);
    }

}
