package com.example.schedulemanager.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.schedulemanager.R;
import com.example.schedulemanager.Util;

public class GuideAdapter extends PagerAdapter {

    private final LayoutInflater layoutInflater;
    private final Typeface typeface;
    Context context;

    public GuideAdapter(Context context, Typeface typeface) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.typeface = typeface;
    }

    @Override
    public int getCount() {
        return 4;
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
            case 2 : convertView = layoutInflater.inflate(R.layout.guide_page2, null);
                break;
            case 3 : convertView = layoutInflater.inflate(R.layout.guide_page3, null);
                break;
        }
        Util.setFontAllChildView(context, convertView, typeface, true);
        container.addView(convertView);
        return  convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        ((ViewPager) container).removeView(view);
    }
}
