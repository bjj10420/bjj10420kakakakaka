package com.example.schedulemanager.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

import com.example.schedulemanager.activity.MainActivity;
import com.example.schedulemanager.vo.Schedule;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.HashMap;

/**
 * 모든 데이터 처리 담당
 */
public class DataHelper {

    public static DataHelper dataHelper;
    private DBHelper dbHelper;
    private HashMap<String, String> iconNameMap;    // 해당하는 텍스트에 매칭시키는 아이콘명이 저장되는 맵
    private Typeface typeface;                      // 글꼴
    private HashMap<Integer, HashMap<Integer, Schedule>> scheduleMapByMonth;// 달력월값을 키로 갖는 스케쥴 저장소
    private HashMap<Integer, View> currentCalendarViewMap;// 현재 보고있는 달력의 각 칸을 저장해놓는 저장소
    private HashMap<Integer, View> arroundViewGroup; // 드래그중에 포인터주위의 뷰들
    private float dX;                               // 드래그 시의 X좌표
    private float dY;                               // 드래그 시의 Y좌표
    private PieDataSet dailyScheduleDataSet;        // 데일리 스케쥴 차트용 데이터 저장소
    private String selectedDateData;                // 선택된 날짜(연월포함)
    private String yearMonthKey;                    // 선택된 연월
    private HashMap<Integer, Schedule> dailyScheduleMap; // 선택된 일자의 하루 스케쥴 맵
    private String dateValue;                       // 선택된 일

    public void initData(Context context) {
        dataHelper = this;
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
     * 스케쥴 맵을 업데이트 (해당 스케쥴 맵에서도 제거)
     * @param scheduleOrderValue
     */
    public void updateDailyScheduleMapByMonth(int scheduleOrderValue) {
        HashMap<Integer, Schedule> scheduleMapForThisMonth = scheduleMapByMonth.get(Integer.parseInt(yearMonthKey));
        Log.d("업데이트 데일리 스케쥴맵 체크 스케쥴 오더 값", String.valueOf(scheduleOrderValue));

        Log.d("업데이트 데일리 스케쥴맵 체크 삭제 전 개수", String.valueOf(scheduleMapForThisMonth.size()));
        scheduleMapForThisMonth.remove(Integer.parseInt(dateValue + "000" + scheduleOrderValue));
        Log.d("업데이트 데일리 스케쥴맵 맵 키 체크", dateValue + "000" + scheduleOrderValue);
        Log.d("업데이트 데일리 스케쥴맵 체크 삭제 후 개수", String.valueOf(scheduleMapForThisMonth.size()));

    }

    /**
     * 해당 인덱스로 추출된 스케쥴값의 order값 리턴
     * @param selectedIndex
     * @return
     */
    public int getOrderValueFromSchedule(int selectedIndex) {
        int orderValue = 0;
        int countIndex = 0;
        Log.d("getOrderValueFromSchedule 테스트 selectedIndex", String.valueOf(selectedIndex));
        for(Integer key : dailyScheduleMap.keySet()) {
            if(countIndex == selectedIndex) {
                orderValue = dailyScheduleMap.get(key).getOrder();
                Log.d("getOrderValueFromSchedule 테스트 orderValue", String.valueOf(orderValue));
                Log.d("getOrderValueFromSchedule 테스트 activityName", String.valueOf(dailyScheduleMap.get(key).getActivityName()));
                break;
            }
            countIndex++;
        }
        return orderValue;
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

    /**
     * 하루의 모든 스케쥴을 가지고와서 새로 하나 생성
     * @param yearMonthKey
     * @param dateValue
     */
    public HashMap<Integer, Schedule> makeDailyScheduleMap(String yearMonthKey, String dateValue) {
        HashMap<Integer, Schedule> dailySchedules = new HashMap<Integer, Schedule>();
        HashMap<Integer, Schedule> thisMonthSchedules = getScheduleMapByMonth().get(Integer.parseInt(yearMonthKey));
        Log.d("thisMonthSchedules 개수", String.valueOf(thisMonthSchedules.size()));
        for(Integer dateKey : thisMonthSchedules.keySet()) {
            Schedule schedule = thisMonthSchedules.get(dateKey);
            if(String.valueOf(dateKey).substring(0, 2).equals(dateValue))dailySchedules.put(dateKey, schedule);
        }
        return dailySchedules;
    }

    public HashMap<Integer, View> getCurrentCalendarViewMap() {
         return currentCalendarViewMap;
    }

    public HashMap<Integer, HashMap<Integer, Schedule>> getScheduleMapByMonth() {
        return scheduleMapByMonth;
    }

    public void setdX(float dX) {
        this.dX = dX;
    }

    public void setdY(float dY) {
        this.dY = dY;
    }

    public float getdX() {
        return dX;
    }

    public float getdY() {
        return dY;
    }

    public HashMap<Integer, View> getArroundViewGroup() {
        return arroundViewGroup;
    }

    public DBHelper getDbHelper() {
        return dbHelper;
    }

    public HashMap<String, String> getIconNameMap() {
        return iconNameMap;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public PieDataSet getDailyScheduleDataSet() {
        return dailyScheduleDataSet;
    }

    public String getSelectedDateData() {
        return selectedDateData;
    }

    public void setDailyScheduleDataSet(PieDataSet dailyScheduleDataSet) {
        this.dailyScheduleDataSet = dailyScheduleDataSet;
    }

    public void setYearMonthKeyAndDateValue(String yearMonthKey, String dateValue) {
        this.yearMonthKey = yearMonthKey;
        this.dateValue = dateValue;
    }

    public void setSelectedDateData(String selectedDateData) {
        this.selectedDateData = selectedDateData;
    }

    public void setDailyScheduleMap(HashMap<Integer,Schedule> dailyScheduleMap) {
        this.dailyScheduleMap = dailyScheduleMap;
    }

    public String getDateValue() {
        return dateValue;
    }
}
