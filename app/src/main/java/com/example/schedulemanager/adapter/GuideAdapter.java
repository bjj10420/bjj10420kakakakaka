package com.example.schedulemanager.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.schedulemanager.R;

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
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View convertView = null;
        switch (position) {
            case 0 : convertView = layoutInflater.inflate(R.layout.guide_page0, null);
                break;
            case 1 : convertView = layoutInflater.inflate(R.layout.guide_page1, null);
                break;
        }
        container.addView(convertView);
        return  convertView;
    }
}
