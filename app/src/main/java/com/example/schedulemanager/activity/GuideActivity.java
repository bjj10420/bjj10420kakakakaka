package com.example.schedulemanager.activity;

import android.app.Activity;
import android.os.Bundle;

import com.example.schedulemanager.R;

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

}
