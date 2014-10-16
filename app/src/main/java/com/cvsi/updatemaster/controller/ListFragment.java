package com.cvsi.updatemaster.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import com.cvsi.updatemaster.R;
import com.cvsi.updatemaster.data.Resource;
import com.cvsi.updatemaster.dialogs.ErrorDialog;

/**
 * Created by rivasyshyn on 17.09.2014.
 */
public class ListFragment extends AbstractViewController {

    ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, null);
        mListView = (ListView) v.findViewById(R.id.listView);
        super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }

    @Override
    protected void updateView(Resource resource) {
        List set = resource.getResources();
        if (set != null && set.size() > 0) {
            mListView.setAdapter(mListAdapter);
            mListView.setOnItemClickListener(mOnItemSelectedListener);
        } else {
            ErrorDialog.show(getActivity(),
                    getString(R.string.error),
                    getString(R.string.err_cant_display),
                    new ErrorDialog.Action(1, getString(R.string.ok)),
                    null);
        }
    }

    private AdapterView.OnItemClickListener mOnItemSelectedListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListFragment.this.onItemSelected((Resource) mListAdapter.getItem(position));
        }
    };

    private BaseAdapter mListAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return getData().getResources().size();
        }

        @Override
        public Object getItem(int position) {
            return getData().getResources().get(position);
        }

        @Override
        public long getItemId(int position) {
            return getData().getResources().get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = ListFragment.this.getActivity().getLayoutInflater().inflate(R.layout.listitem_line, null);
                ViewHolder vh = new ViewHolder();
                vh.img = (ImageView) convertView.findViewById(R.id.iv_logo);
                vh.title = (TextView) convertView.findViewById(R.id.tv_title);
                vh.desc = (TextView) convertView.findViewById(R.id.tv_desc);
                convertView.setTag(vh);
            }

            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            Resource resource = (Resource) getItem(position);

            viewHolder.img.setImageDrawable(null);
            loadImage(viewHolder.img, resource.getIcon());
            viewHolder.title.setText(resource.getName());
            viewHolder.desc.setText(resource.getDescription());

            return convertView;
        }
    };


    private static class ViewHolder {
        public ImageView img;
        public TextView title;
        public TextView desc;
    }
}
