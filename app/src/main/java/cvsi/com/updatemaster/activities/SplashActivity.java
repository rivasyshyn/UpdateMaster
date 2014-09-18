package cvsi.com.updatemaster.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import cvsi.com.updatemaster.BuildConfig;
import cvsi.com.updatemaster.R;


public class SplashActivity extends Activity {

    private Handler mHandler;
    private final int mTimeout = 700; //1 sec.
    private boolean shown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mHandler = new Handler();

        TextView textView = (TextView) findViewById(R.id.tv_logo);
        textView.setText(getString(R.string.splash_message, BuildConfig.VERSION_NAME));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(task);
        shown = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(shown){
            task.run();
            return;
        }
        mHandler.postDelayed(task, mTimeout);
    }

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            finish();
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            SplashActivity.this.startActivity(intent);
        }
    };

}
