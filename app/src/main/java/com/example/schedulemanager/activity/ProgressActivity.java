package com.example.schedulemanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.schedulemanager.R;

import java.util.Timer;
import java.util.TimerTask;

public class ProgressActivity extends Activity {

    public static ProgressActivity progressActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        progressActivity = this;
        TimerTask mTask = new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        };

        Timer mTimer = new Timer();

        mTimer.schedule(mTask, 3000);
    }
}
