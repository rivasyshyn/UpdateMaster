package com.cvsi.updatemaster.controller;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cvsi.updatemaster.data.Resource;

/**
 * Created by rivasyshyn on 17.09.2014.
 */
public abstract class AbstractViewController extends Fragment {

    private Resource mData;

    public static interface OnItemSelectedListener {
        public void onItemSelected(Resource resource);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getData();

        if(mData != null){
            updateView(mData);
        }
        return null;
    }

    protected void onItemSelected(Resource resource) {
        Activity activity = getActivity();
        if (activity instanceof OnItemSelectedListener) {
            ((OnItemSelectedListener) activity).onItemSelected(resource);
        } else {
            throw new IllegalStateException(String.format("activity %s should implement %s", activity, OnItemSelectedListener.class.getCanonicalName()));
        }
    }

    protected Resource getData() {
        if (mData == null) {
            mData = getArguments().getParcelable("data");
        }
        return mData;
    }

    protected abstract void updateView(Resource resource);

}
