package com.example.schedulemanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.schedulemanager.R;
import com.example.schedulemanager.helper.TaskHelper;
import com.example.schedulemanager.helper.UIHelper;
import com.example.schedulemanager.interface_.GeneralCallback2;
import com.example.schedulemanager.panel.managerpanel.ManagerPanel;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.schedulemanager.helper.EventHelper.eventHelper;

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
                overridePendingTransition(0, 0);
            }
        };

        Timer mTimer = new Timer();
        mTimer.schedule(mTask, 3000);
        new TaskHelper(MainActivity.mainActivity).loadManagerPanel(new GeneralCallback2() {
            @Override
            public void onCallBack(Object parameter) {
                UIHelper.uiHelper.slideUpManagerPanel((ManagerPanel) parameter, eventHelper.getEtcPanel());
            }
        });

    }
}
