package com.example.schedulemanager.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.example.schedulemanager.R;
import com.example.schedulemanager.adapter.GuideAdapter;

/**
 * Created by bjj10 on 2017-12-04.
 */

public class GuideActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        init();
    }

    private void init() {
        ViewPager vp = (ViewPager) findViewById(R.id.view_pager);
        vp.setAdapter(new GuideAdapter(this, Typeface.createFromAsset(this.getAssets(), "nanumgothic.ttf")));
    }

}
