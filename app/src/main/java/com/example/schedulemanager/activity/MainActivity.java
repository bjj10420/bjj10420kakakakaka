package com.example.schedulemanager.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.schedulemanager.R;
import com.example.schedulemanager.calendar.CalendarPagerAdapter;
import com.example.schedulemanager.calendar.DialogHelper;
import com.example.schedulemanager.helper.CalendarHelper;
import com.example.schedulemanager.helper.DBHelper;
import com.example.schedulemanager.helper.DataHelper;
import com.example.schedulemanager.helper.EventHelper;
import com.example.schedulemanager.helper.UIHelper;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        //        initScheduleDisplay();
    }

    @Override
    public void onBackPressed() {
        EventHelper.eventHelper.onBackPresssed();
    }
}
