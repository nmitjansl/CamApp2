package com.escoladeltreball.org.camapp2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Aitor Rodriguez (aitorrq@gmail.com) on 2/22/18.
 */

class GridViewAdapter extends BaseAdapter {
    private final Context context;
    private final List<String> urls = new ArrayList<>();

    GridViewAdapter(Context context) {
        this.context = context;

        // Ensure we get a different ordering of images on each run.
        /*Collections.addAll(urls, Data.URLS);
        Collections.shuffle(urls);*/

        // Triple up the list.
        /*ArrayList<String> copy = new ArrayList<>(urls);
        urls.addAll(copy);
        urls.addAll(copy);*/
        urls.add("https://i.imgur.com/DYPjpkX.jpg");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SquaredImageView view = (SquaredImageView) convertView;
        if (view == null) {
            view = new SquaredImageView(context);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        String url = getItem(position);

        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .fit()
                .tag(context)
                .into(view);

        return view;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public String getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
