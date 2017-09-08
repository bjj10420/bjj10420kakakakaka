package com.example.schedulemanager.helper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

import com.example.schedulemanager.calendar.CalendarPagerAdapter;
import com.example.schedulemanager.vo.CalendarCellInfo;
import com.example.schedulemanager.vo.Schedule;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeMap;

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
    private TreeMap<Integer, Schedule> dailyScheduleMap; // 선택된 일자의 하루 스케쥴 맵
    private String dateValue;                       // 선택된 일
    private String dateOfToday;                     // 오늘 일자
    private HashMap<Rect, View> rectZoneWithView;   // 이벤트 구역을 나누는 rect존과 매칭하는 뷰를 담는 저장소


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
     * 스케쥴맵에 추가
     * @param newOrderValue
     */
    public void addToDailyScheduleMapByMonth(int newOrderValue, String activityName) {
        HashMap<Integer, Schedule> scheduleMapForThisMonth = scheduleMapByMonth.get(Integer.parseInt(yearMonthKey));
        Schedule schedule = new Schedule();
        schedule.setDate(selectedDateData);
        schedule.setOrder(newOrderValue);
        schedule.setActivityName(activityName);
        Log.d("스케쥴맵에 스케쥴추가", schedule.getOrder() + ", " + activityName + ", " + dateValue + ", newOrderValue = " + newOrderValue);
        scheduleMapForThisMonth.put(Integer.parseInt(dateValue + "000" + newOrderValue), schedule);
        // 데일리 맵에도 추가
        dailyScheduleMap.put(Integer.parseInt(dateValue + "000" + newOrderValue), schedule);
    }

    /**
     * 스케쥴맵에서 제거
     * @param scheduleOrderValue
     */
     public void removeFromDailyScheduleMapByMonth(int scheduleOrderValue) {
     Log.d("데일리 스케쥴 맵 사이즈 삭제전", String.valueOf(dailyScheduleMap.size()));
     HashMap<Integer, Schedule> scheduleMapForThisMonth = scheduleMapByMonth.get(Integer.parseInt(yearMonthKey));
     scheduleMapForThisMonth.remove(Integer.parseInt(dateValue + "000" + scheduleOrderValue));
     dailyScheduleMap.remove(Integer.parseInt(dateValue + "000" + scheduleOrderValue));

     for(Integer key : dailyScheduleMap.keySet()) {
     Log.d("데일리 스케쥴 맵 남은 데이터확인", dailyScheduleMap.get(key).getActivityName());
     }
     Log.d("데일리 스케쥴 맵 사이즈 삭제후", String.valueOf(dailyScheduleMap.size()));

     }

     /**
     * 스케쥴맵에서 스케쥴 가져오기
     * @param scheduleOrderValue
     */
    public Schedule getScheduleFromDailyScheduleMapByMonth(int scheduleOrderValue) {
        HashMap<Integer, Schedule> scheduleMapForThisMonth = scheduleMapByMonth.get(Integer.parseInt(yearMonthKey));
        for(Integer key : scheduleMapForThisMonth.keySet()) {
            Log.d("이번달 스케쥴맵 컨텐츠 체크", String.valueOf(key));
        }
        Schedule schedule = scheduleMapForThisMonth.get(Integer.parseInt(dateValue + "000" + scheduleOrderValue));
        return schedule;
    }

    /**
     * 스케쥴맵에서 인덱스 값으로 가져오기
     * @param index
     */
    public Schedule getScheduleFromDailyScheduleMapByMonth2(int index) {
        Schedule schedule = null;
        int count = 0;
        for(Integer key : dailyScheduleMap.keySet()){
            if(index == count)
                schedule = dailyScheduleMap.get(key);
            count++;
        }
        return schedule;
    }

    /**
     * 해당 인덱스로 추출된 스케쥴값의 order값 리턴
     * @param selectedIndex
     * @return
     */
    public int getOrderValueFromSchedule(int selectedIndex) {
        int orderValue = 0;
        int countIndex = 0;
           for(Integer key : dailyScheduleMap.keySet()) {
            if(countIndex == selectedIndex) {
                orderValue = dailyScheduleMap.get(key).getOrder();
                Log.d("getORderValueFromSchedule 메소드 데일리스케쥴맵 체크", dailyScheduleMap.get(key).getActivityName());
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

        for(Integer dateKey : thisMonthSchedules.keySet()) {
            Schedule schedule = thisMonthSchedules.get(dateKey);

            if(String.valueOf(dateKey).length() == 6 && String.valueOf(dateKey).substring(0, 2).equals(dateValue)
             || String.valueOf(dateKey).length() == 6 && String.valueOf(dateKey).substring(0, 1).equals(dateValue)
                    ){dailySchedules.put(dateKey, schedule);
                Log.d("데일리스케쥴맵 생성 체크 dateKey ", String.valueOf(dateKey));
                Log.d("데일리스케쥴맵 생성 체크 order ", String.valueOf(schedule.getOrder()));
                Log.d("데일리스케쥴맵 생성 체크 activityName ", String.valueOf(schedule.getActivityName()));
            }
            else if(String.valueOf(dateKey).length() == 5 && String.valueOf(dateKey).substring(0, 1).equals(dateValue)){
                dailySchedules.put(dateKey, schedule);
                Log.d("데일리스케쥴맵 생성 체크 dateKey ", String.valueOf(dateKey));
                Log.d("데일리스케쥴맵 생성 체크 order ", String.valueOf(schedule.getOrder()));
                Log.d("데일리스케쥴맵 생성 체크 activityName ", String.valueOf(schedule.getActivityName()));
                }}
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

    public void setDailyScheduleMap(TreeMap<Integer, Schedule> dailyScheduleMap) {
        this.dailyScheduleMap = dailyScheduleMap;
    }

    public TreeMap<Integer, Schedule> getDailyScheduleMap() {
        return dailyScheduleMap;
    }

    public String getDateValue() {
        return dateValue;
    }

    public void setDateValue(String dateValue) {
        this.dateValue = dateValue;
    }
//
    public String getYearMonthKey() {
        return yearMonthKey;
    }

    /**
     * 데일리 스케쥴안의 각 데이터의 순서값중 최대값을 추출
     */
    public int getMaxOrderAmongDailyScheduleMap() {
        Log.d("getMaxOrderAmongDailyScheduleMap", "getMaxOrderAmongDailyScheduleMap");
        int temp = 0;
            for(int key : dailyScheduleMap.keySet()) {
                int order = dailyScheduleMap.get(key).getOrder();
                if(order > temp) temp = order;
            }
        return temp;
    }

    /**
     * 현재월안의 값중에서 날짜값이 파라메터와 같은 데이터중에서 가장 큰 순서값
     */
    public int getMaxOrderAmongScheduleMapByThisMonth(String dateKey) {
        String yearMonthKey = makeYearMonthKey();
        HashMap<Integer, Schedule> scheduleMapForThisMonth = scheduleMapByMonth.get(Integer.parseInt(yearMonthKey));
        Log.d("getMaxOrderAmongScheduleMapByThisMonth", "getMaxOrderAmongScheduleMapByThisMonth");
        int temp = 0;
        for(int key : scheduleMapForThisMonth.keySet()) {
            Schedule schedule = scheduleMapForThisMonth.get(key);
            int order = schedule.getOrder();
            if(schedule.getDate().equals(dateKey) && order > temp) temp = order;
        }
        return temp;
    }

    /**
     * 연월스트링 값 생성
     * @return
     */
    private String makeYearMonthKey() {
        CalendarPagerAdapter calendarPagerAdapter = EventHelper.eventHelper.getCalendarHelper().getCalendarPagerAdapter();
        String year = String.valueOf(calendarPagerAdapter.getBaseCal().get(Calendar.YEAR));
        int month = calendarPagerAdapter.getBaseCal().get(Calendar.MONTH);
        String baseCalMonthString = month < 10 ? "0" + month : String.valueOf(month);
        String yearMonthKey = year + baseCalMonthString;
        return yearMonthKey;
    }//

    public boolean isEmptyData(String dateKey){
        boolean isEmptyData = true;
        String yearMonthKey = makeYearMonthKey();
        HashMap<Integer, Schedule> scheduleMapForThisMonth = scheduleMapByMonth.get(Integer.parseInt(yearMonthKey));

        //TODO 만약 비어져있으면 여기서 한번 생성해준다 (초기화단계에서 해주도록 나중에 리팩토링)
        if(scheduleMapForThisMonth == null) {
            HashMap<Integer, Schedule> scheduleMap = new HashMap<Integer, Schedule>();
            scheduleMapByMonth.put(Integer.valueOf(yearMonthKey), scheduleMap);
            scheduleMapForThisMonth = scheduleMap;
        }

        Log.d("데이터가 비어있는지 체크", String.valueOf(dateKey));
        Log.d("yearMonthKey 상세정보", String.valueOf(yearMonthKey));
        Log.d("scheduleMapByMonth 상세정보", String.valueOf(scheduleMapByMonth));

        Log.d("scheduleMapForThisMonth 상세정보", String.valueOf(scheduleMapForThisMonth));
        Schedule scheduleData = scheduleMapForThisMonth.get(Integer.parseInt(dateKey.substring(6,8) + "0000"));
        if(scheduleData != null) isEmptyData = false;
        Log.d("데이터가 비어있는지 체크", String.valueOf(isEmptyData));
        return isEmptyData;
    }

    public void setDateOfToday(String dateOfToday) {
        this.dateOfToday = dateOfToday;
    }

    public String getDateOfToday() {
        return dateOfToday;
    }

    /**
     * 이벤트 범위를 지정해주는 렉트존을 생성
     */
    public void makeRectZoneWithFirstCell() {

        View firstCellView = getFirstCellView();
        CalendarCellInfo calendarCellInfo = makeCalendarCellInfo(firstCellView);

        rectZoneWithView = new HashMap<Rect, View>();
        makeRectToPut(calendarCellInfo);

        }

    private void makeRectToPut(CalendarCellInfo calendarCellInfo) {
        for(int i = 0; i < 31; i++) {

            if(i != 0 && (i % 7) == 0 ) {
                calendarCellInfo.setLeft(calendarCellInfo.getOriginalLeft());
                calendarCellInfo.updateTop(i);
            }
            else
                calendarCellInfo.updateLeft(i);


            makeRectAndPut(calendarCellInfo);
        }
    }

    private void makeRectAndPut(CalendarCellInfo calendarCellInfo) {
        Rect rect = new Rect(calendarCellInfo.getLeft(), calendarCellInfo.getTop(),
                calendarCellInfo.getLeft() + calendarCellInfo.getWidth(),
                calendarCellInfo.getTop() + calendarCellInfo.getHeight());
        Log.d("렉트존 생성 체크", "left = " + calendarCellInfo.getLeft() + " , top = " + calendarCellInfo.getTop());
        rectZoneWithView.put(rect, null);
    }

    private CalendarCellInfo makeCalendarCellInfo(View firstCellView) {
        CalendarCellInfo calendarCellInfo = new CalendarCellInfo();
        int[] viewWindowInfos = getViewWindowInfo(firstCellView);
        calendarCellInfo.setWidth(firstCellView.getWidth());
        calendarCellInfo.setHeight(firstCellView.getHeight());
        calendarCellInfo.setLeft(viewWindowInfos[0]);
        calendarCellInfo.setOriginalLeft(viewWindowInfos[0]);
        calendarCellInfo.setTop(viewWindowInfos[1]);
        calendarCellInfo.setOriginalTop(viewWindowInfos[1]);

        return calendarCellInfo;
    }

    private int[] getViewWindowInfo(View firstCellView) {
        int[] numberArray = new int[2];
        firstCellView.getLocationInWindow(numberArray);
        Log.d("dastaHelper.getCurrentCalendarViewMap() #2-1", String.valueOf(numberArray[0]));
        Log.d("dastaHelper.getCurrentCalendarViewMap() #2-2", String.valueOf(numberArray[1]));
        firstCellView.setBackgroundColor(Color.parseColor("#c8c8c8"));

        return numberArray;
    }

    private View getFirstCellView() {
        View firstCell = EventHelper.eventHelper.getUiHelper().getFirstCalendarCell();
        return firstCell;
    }
}
