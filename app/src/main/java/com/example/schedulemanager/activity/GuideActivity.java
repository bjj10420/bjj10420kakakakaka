package com.example.schedulemanager.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.schedulemanager.R;
import com.example.schedulemanager.adapter.GuideAdapter;

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
        // 버튼들을 카톡처럼 바꾸기전 커밋
        View closeBtn = findViewById(R.id.guideCloseBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
