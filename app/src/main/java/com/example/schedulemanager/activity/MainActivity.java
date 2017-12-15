package com.example.schedulemanager.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.schedulemanager.R;
import com.example.schedulemanager.helper.AnimationHelper;
import com.example.schedulemanager.helper.CalendarHelper;
import com.example.schedulemanager.helper.DataHelper;
import com.example.schedulemanager.helper.EventHelper;
import com.example.schedulemanager.helper.TaskHelper;
import com.example.schedulemanager.helper.UIHelper;
import com.example.schedulemanager.panel.managerpanel.ManagerPanel;

import static com.example.schedulemanager.helper.EventHelper.eventHelper;

public class MainActivity extends AppCompatActivity {

    public static MainActivity mainActivity;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        init();
       //
    }

    private void init() {
        DataHelper dataHelper = new DataHelper();
        dataHelper.initData(this);

        UIHelper uiHelper = new UIHelper();
        uiHelper.initUI(this, dataHelper);

        CalendarHelper calendarHelper = new CalendarHelper();
        calendarHelper.initCalendar(this, dataHelper.getTypeface(), uiHelper, dataHelper);

        EventHelper eventHelper = new EventHelper();
        eventHelper.initEvent(this, dataHelper, uiHelper, calendarHelper);
        calendarHelper.setEventHelper(eventHelper);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        new TaskHelper(this).loadETCPanel();
    }

    @Override
    public void onBackPressed() {
        eventHelper.onBackPresssed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        eventHelper.onActivityResult(requestCode, resultCode, data);
    }
}
