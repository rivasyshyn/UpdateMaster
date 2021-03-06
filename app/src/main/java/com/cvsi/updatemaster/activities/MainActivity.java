package com.cvsi.updatemaster.activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cvsi.updatemaster.R;
import com.cvsi.updatemaster.controller.AbstractViewController;
import com.cvsi.updatemaster.controller.ListFragment;
import com.cvsi.updatemaster.controller.PackageFragment;
import com.cvsi.updatemaster.controller.RemoteImageProvider;
import com.cvsi.updatemaster.data.Resource;
import com.cvsi.updatemaster.dialogs.ErrorDialog;
import com.cvsi.updatemaster.dialogs.SettingsDialog;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Set;


public class MainActivity extends FragmentActivity implements SettingsDialog.OnSettingsChangedListener, AbstractViewController.OnItemSelectedListener, ErrorDialog.OnActionListener, RemoteImageProvider {

    private String mRepoUrl;
    private ProgressBar mProgressBar;
    Picasso mPicasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        mRepoUrl = sharedPreferences.getString("repository", getString(R.string.default_repository));

        mPicasso = Picasso.with(this);

        update(null);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setupActionBar(boolean show) {
        try {
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(show);
            //actionBar.setDisplayShowHomeEnabled(show);
            try {
                actionBar.setHomeButtonEnabled(show);
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            String tag = "settings";
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (fragment == null) {
                Bundle bundle = new Bundle();
                bundle.putString("url", mRepoUrl);
                fragment = Fragment.instantiate(this, SettingsDialog.class.getName(), bundle);
                fragmentTransaction.add(fragment, tag);
            }
            fragmentTransaction.show(fragment);
            fragmentTransaction.commit();
            return true;
        }


        if (id == R.id.action_refresh) {
            onRefreshAction();
            return true;
        }

        if (id == R.id.action_about) {
            SplashActivity.startAsAbout(this);
            return true;
        }

        if (id == android.R.id.home) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                getSupportFragmentManager().popBackStack();
                if (getSupportFragmentManager().getBackStackEntryCount() == 2) {
                    setupActionBar(false);
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onRefreshAction() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        update(null);
    }

    @Override
    public void onSettingsChanged(String url) {
        if (!TextUtils.isEmpty(url)) {
            SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
            sharedPreferences.edit().putString("repository", url).commit();
            mRepoUrl = url;
        }
        update(null);
    }

    private void update(String url) {
        mProgressBar.setVisibility(View.VISIBLE);
        Ion.with(this)
                .load(url != null ? url : mRepoUrl)
                .progressBar(mProgressBar)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        if (e != null) {
                            displayError();
                            return;
                        }

                        updateView(new GsonBuilder()
                                .excludeFieldsWithoutExposeAnnotation()
                                .create()
                                .fromJson(result, Resource.class));
                    }
                });

    }

    private void displayError() {

    }

    private void updateView(Resource resource) {
        Fragment fragment = getCorespondentFragment(resource);
        if (fragment == null) {
            ErrorDialog.show(this,
                    getString(R.string.error),
                    getString(R.string.err_cant_display),
                    new ErrorDialog.Action(1, getString(R.string.ok)),
                    null);
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(R.id.container, fragment)
                .addToBackStack(null).commit();

    }

    private Fragment getCorespondentFragment(Resource resource) {
        if (resource.getType() == Resource.ResourceType.ITEM) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("data", resource);
            return Fragment.instantiate(this, ListFragment.class.getName(), bundle);
        }
        if (resource.getType() == Resource.ResourceType.PACKAGE) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("data", resource);
            return Fragment.instantiate(this, PackageFragment.class.getName(), bundle);
        }

        return null;
    }

    @Override
    public void onItemSelected(Resource resource) {
        if (resource.getType() != Resource.ResourceType.PACKAGE && !TextUtils.isEmpty(resource.getUrl())) {
            update(resource.getUrl());
        } else {
            updateView(resource);
        }
        setupActionBar(true);
    }

    @Override
    public void onAction(ErrorDialog.Action action) {

    }

    @Override
    public void loadImage(ImageView view, String url) {
        try {
            mPicasso.load(Uri.parse(url)).into(view);
        }catch (Exception e) {}
    }
}
