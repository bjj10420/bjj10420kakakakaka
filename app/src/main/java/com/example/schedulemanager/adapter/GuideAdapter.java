package com.example.schedulemanager.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.schedulemanager.R;

/**
 * Created by bjj10 on 2017-12-04.
 */

public class GuideAdapter extends PagerAdapter {

    private final LayoutInflater layoutInflater;
    Context context;

    public GuideAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View convertView = layoutInflater.inflate(R.layout.guide_page, null);
        ((ViewPager) container).addView(convertView, 0);
        return  convertView;
    }
}
