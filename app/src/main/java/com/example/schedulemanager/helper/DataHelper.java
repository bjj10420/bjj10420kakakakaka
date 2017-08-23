package com.example.schedulemanager.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;

import com.example.schedulemanager.Schedule;

import java.util.HashMap;

/**
 * 모든 데이터 처리 담당
 */
public class DataHelper {
    private DBHelper dbHelper;
    private HashMap<String, String> iconNameMap;    // 해당하는 텍스트에 매칭시키는 아이콘명이 저장되는 맵
    private Typeface typeface;                      // 글꼴
    private HashMap<Integer, HashMap<Integer, Schedule>> scheduleMapByMonth;
    // 달력월값을 키로 갖는 스케쥴 저장소
    private HashMap<Integer, View> currentCalendarViewMap;
    // 현재 보고있는 달력의 각 칸을 저장해놓는 저장소
    private HashMap<Integer, View> arroundViewGroup;
    // 드래그중에 포인터주위의 뷰들


    public void initData(Context context) {
        dbHelper = new DBHelper(context);
        /**
         * 1. favorite테이블에서 메인에 등록된 버튼들의 정보를 로딩
         * 2. hashMap에다 해당 text를 키로 하여 데이터에 아이콘명을 추가
         */
        iconNameMap = new HashMap<String, String>();
        //TODO DB를 구축하면 아이콘 네임맵으로 저장 시켜줘야 함
        // TEST용으로
//        iconNameMap.put("")
        // 글꼴 로딩
        typeface = getApplicationFont(context);
        // 모든 스케쥴 데이터 로딩
        scheduleMapByMonth = new HashMap<Integer, HashMap<Integer, Schedule>>();
        dbHelper.selectAllSchedule(scheduleMapByMonth);
        currentCalendarViewMap = new HashMap<Integer, View>();
        arroundViewGroup = new HashMap<Integer, View>();
    }

    /**
     * 지정 글꼴 리턴
     * @return
     * @param context
     */
    public Typeface getApplicationFont(Context context) {
        Typeface mTypeface = null;
        mTypeface = Typeface.createFromAsset(context.getAssets(), "nanumgothic.ttf");
        return mTypeface;
    }
}
