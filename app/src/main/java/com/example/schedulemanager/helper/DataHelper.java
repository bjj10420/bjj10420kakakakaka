package com.example.schedulemanager.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.example.schedulemanager.R;
import com.example.schedulemanager.calendar.CalendarPagerAdapter;
import com.example.schedulemanager.vo.ActivityVO;
import com.example.schedulemanager.vo.CalendarCellInfo;
import com.example.schedulemanager.vo.RectAndView;
import com.example.schedulemanager.vo.Schedule;
import com.github.mikephil.charting.data.PieDataSet;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * 모든 데이터 처리 담당
 */
public class DataHelper {

    public static final int PICK_FROM_GALLARY = 10;                               // 갤러리 액티비티 요청값


    public static DataHelper dataHelper;
    private DBHelper dbHelper;
    private HashMap<String, String> iconNameMap;                            // 해당하는 텍스트에 매칭시키는 아이콘명이 저장되는 맵
    private Typeface typeface;                                              // 글꼴
    private Context context;

    private HashMap<Integer, HashMap<Integer, Schedule>> scheduleMapByMonth;// 달력월값을 키로 갖는 스케쥴 저장소
    private HashMap<Integer, View> currentCalendarViewMap;                  // 현재 보고있는 달력의 각 칸을 저장해놓는 저장소
    private HashMap<Integer, View> arroundViewGroup;                        // 드래그중에 포인터주위의 뷰들
    private float dX;                                                       // 드래그 시의 X좌표
    private float dY;                                                       // 드래그 시의 Y좌표
    private PieDataSet dailyScheduleDataSet;                                // 데일리 스케쥴 차트용 데이터 저장소
    private String selectedDateData;                                        // 선택된 날짜(연월포함)
    private String yearMonthKey;                                            // 선택된 연월
    private TreeMap<Integer, Schedule> dailyScheduleMap;                    // 선택된 일자의 하루 스케쥴 맵
    private String dateValue;                                               // 선택된 일
    private String dateOfToday;                                             // 오늘 일자
    private HashMap<Integer, RectAndView> rectZoneWithView;                 // 이벤트 구역을 나누는 rect존과 매칭하는 뷰를 담는 저장소
    private TreeMap<Integer, RectAndView> rectZoneWithViewSorted;           // 이벤트 구역을 나누는 rect존과 매칭하는 뷰를 담는 저장소
    private int currentPageIndex;

    private ArrayList<String> categories;                                   // 기타버튼 클릭시 활성화되는 패널의 카테고리들
    private CharSequence[] categoryArray;                                   // 아이템 추가시 나오는 옵션메뉴 리스트용
    private HashMap<String, ArrayList<ActivityVO>> activities;              // 기타버튼 클릭시 활성화되는 패널의 활동들
    private ArrayList<Drawable> drawableList;                               // 프로젝트내의 drawable을 모두 로드하여 담는 저장소

    private int mode;                                                       // 드래그시의 입력모드
    private ArrayList<View> multiModeViews;                                 // 멀티모드시의 저장소

    public void initData(Context context) {
        initField(context);
        if(isFirstLoading()) {
            makeBasicData();
            updateIsFirstLoadingValue();
        }
        loadBasicDatas();
    }

    /**
     * 기본 카테고리 데이터, 활동 데이터, 프로젝트 내 이미지 데이터를 불러온다
     */
    private void loadBasicDatas() {
        loadBasicCategories();
        loadBasicActivities();
        loadBasicDrawables();
    }

    private void loadBasicDrawables() {
        Field[] drawables = com.example.schedulemanager.R.drawable.class.getFields();
        for (Field f : drawables) {
            try {
                if(f.getName().contains("icon"))
                drawableList.add(context.getDrawable(f.getInt(null)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadBasicActivities() {
        for(String category : categories){
            if(activities.get(category) == null) activities.put(category, new ArrayList<ActivityVO>());
            DBHelper.dbHelper.selectActivities(category, activities.get(category));
        }
    }
//
    private void loadBasicCategories() {
        DBHelper.dbHelper.selectAllCategories(categories);
        Log.d("카테고리 불러오기체크", String.valueOf(categories));
        makeCategoryArray();
    }

    public void makeCategoryArray() {
        categoryArray = new CharSequence[categories.size()];
        for(int i = 0 ; i < categories.size() ; i++) {
            categoryArray[i] = categories.get(i);
        }
    }

    private void initField(Context context) {
        dataHelper = this;
        this.context = context;
        dbHelper = new DBHelper(context);
        iconNameMap = new HashMap<String, String>();

        // 글꼴 로딩
        typeface = getApplicationFont(context);
        // 모든 스케쥴 데이터 로딩
        scheduleMapByMonth = new HashMap<Integer, HashMap<Integer, Schedule>>();
        dbHelper.selectAllSchedule(scheduleMapByMonth);
        currentCalendarViewMap = new HashMap<Integer, View>();
        arroundViewGroup = new HashMap<Integer, View>();
        // 페이지 초기화
        currentPageIndex = 12;

        categories = new ArrayList<String>();
        activities = new HashMap<String, ArrayList<ActivityVO>>();
        drawableList = new ArrayList<Drawable>();

        mode = 1;
        multiModeViews = new ArrayList<View>();
    }

    private void updateIsFirstLoadingValue() {
        SharedPreferences sp = context.getSharedPreferences("scheduleManager", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isFirstLoading", false);
        editor.commit();
    }

    private void makeBasicData() {
        Log.d("makeBasicData", "makeBasicData");
        makeBasicCategoryData();
        makeBasicActivityData();
    }

    private void makeBasicActivityData() {
        ActivityVO[] activityVoArray = makeBasicActivityVoArray();
        insertActivityIntoDB(activityVoArray);
    }

    private ActivityVO[] makeBasicActivityVoArray() {
        ActivityVO basicActivity11 = new ActivityVO("사무", "주식", "F", getByteArrayFromDrawable(R.drawable.basic_check_stock_icon));

        ActivityVO[] activityVoArray = new ActivityVO[] {basicActivity11};
        return  activityVoArray;
    }

    private void insertActivityIntoDB(ActivityVO[] activityVOs) {
        for(int i = 0; i < activityVOs.length; i++) {
            DBHelper.dbHelper.insertActivityWithIcon(activityVOs[i]);
        }
    }

    private void makeBasicCategoryData() {
        String[] categoryNames = {"생활", "사무", "취미", "기타"};
        for(int i = 0; i < categoryNames.length; i++) {
            String categoryName = categoryNames[i];
            DBHelper.dbHelper.insertCategory(categoryName);
        }
    }

    /**
     * 앱 설치후 최초 로딩인지 판단
     */
    private boolean isFirstLoading() {
        SharedPreferences sp = context.getSharedPreferences("scheduleManager", Context.MODE_PRIVATE);
        return sp.getBoolean("isFirstLoading", true);
    }

    /**
     * 스케쥴맵에 추가
     * @param newOrderValue
     */
    public void addToDataStructure(int newOrderValue, String activityName) {
        //TODO yearMonth키를 원래 그냥 필드로 썼었는데 달력추가에서도 이 메소드를 쓰게되어 생성함수로  대체해줌
        HashMap<Integer, Schedule> scheduleMapForThisMonth = scheduleMapByMonth.get(Integer.parseInt(makeYearMonthKey()));
        addDataToThisMonthMap(newOrderValue, activityName, scheduleMapForThisMonth);
    }

    public void addToTodayDataStructure(int newOrderValue, String activityName, String yearMonthKey) {
        HashMap<Integer, Schedule> scheduleMapForThisMonth = scheduleMapByMonth.get(Integer.parseInt(yearMonthKey));
        addDataToThisMonthMap(newOrderValue, activityName, scheduleMapForThisMonth);
    }

    private void addDataToThisMonthMap(int newOrderValue, String activityName, HashMap<Integer, Schedule> scheduleMapForThisMonth) {
        Schedule schedule = new Schedule();
        schedule.setDate(selectedDateData);
        schedule.setOrder(newOrderValue);
        schedule.setActivityName(activityName);
        Log.d("스케쥴맵에 스케쥴추가", schedule.getOrder() + ", " + activityName + ", " + dateValue + ", newOrderValue = " + newOrderValue);
        scheduleMapForThisMonth.put(Integer.parseInt(dateValue + "000" + newOrderValue), schedule);
        // 데일리 맵에도 추가
        if(EventHelper.eventHelper.getUiHelper().getScheduleLayout().getVisibility() == View.VISIBLE)
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
            int orderValue = Integer.parseInt(String.valueOf(dateKey).split("000")[1]);

            if(String.valueOf(dateKey).length() == 6 && String.valueOf(dateKey).substring(0, 2).equals(dateValue)
             || String.valueOf(dateKey).length() == 6 && String.valueOf(dateKey).substring(0, 1).equals(dateValue) && orderValue > 9
                    ){dailySchedules.put(dateKey, schedule);
                Log.d("데일리스케쥴맵 생성 체크1 dateKey ", String.valueOf(dateKey));
                Log.d("데일리스케쥴맵 생성 체크1 order ", String.valueOf(schedule.getOrder()));
                Log.d("데일리스케쥴맵 생성 체크1 activityName ", String.valueOf(schedule.getActivityName()));
            }
            else if(String.valueOf(dateKey).length() == 5 && String.valueOf(dateKey).substring(0, 1).equals(dateValue)){
                dailySchedules.put(dateKey, schedule);
                Log.d("데일리스케쥴맵 생성 체크2 dateKey ", String.valueOf(dateKey));
                Log.d("데일리스케쥴맵 생성 체크2 order ", String.valueOf(schedule.getOrder()));
                Log.d("데일리스케쥴맵 생성 체크2 activityName ", String.valueOf(schedule.getActivityName()));
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

    public void makeSelectedDateData(String dateValue){
        String yearMonthKey = makeYearMonthKey();
        setSelectedDateData(yearMonthKey + (Integer.parseInt(dateValue) < 10 ? "0" + dateValue : dateValue));
    }

    public void makeSelectedTodayDateData(String dateValue, String yearMonthKey){
        setSelectedDateData(yearMonthKey + (Integer.parseInt(dateValue) < 10 ? "0" + dateValue : dateValue));
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
        int maxValue = getMaxOrderForThisMonth(dateKey, yearMonthKey);
        return maxValue;
    }

    public int getMaxOrderAmongScheduleMapByThisMonthForToday(String dateKey, String yearMonthKey) {
        int maxValue = getMaxOrderForThisMonth(dateKey, yearMonthKey);
        return maxValue;
    }

    private int getMaxOrderForThisMonth(String dateKey, String yearMonthKey) {
        HashMap<Integer, Schedule> scheduleMapForThisMonth = scheduleMapByMonth.get(Integer.parseInt(yearMonthKey));
        Log.d("getMaxOrderAmongScheduleMapByThisMonth", "getMaxOrderAmongScheduleMapByThisMonth" + ", dateKey = " + dateKey + ", yearMonthKey = " + yearMonthKey);
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
        String yearMonthKey = makeDateString2(null, EventHelper.eventHelper.getCalendarHelper());
        return yearMonthKey;
    }

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
        Log.d("DataHelper isEmptyData yearMonthKey 상세정보", String.valueOf(yearMonthKey));
        Log.d("scheduleMapByMonth 상세정보", String.valueOf(scheduleMapByMonth));

        Log.d("scheduleMapForThisMonth 상세정보", String.valueOf(scheduleMapForThisMonth));
        if(isExistSchedule(dateKey.substring(6,8), scheduleMapForThisMonth))
            isEmptyData = false;
        Log.d("데이터가 비어있는지 체크", String.valueOf(isEmptyData));
        return isEmptyData;
    }

    public boolean isTodayEmptyData(String dateKey, String yearMonthKey){
        boolean isEmptyData = true;
        HashMap<Integer, Schedule> scheduleMapForThisMonth = scheduleMapByMonth.get(Integer.parseInt(yearMonthKey));

        //TODO 만약 비어져있으면 여기서 한번 생성해준다 (초기화단계에서 해주도록 나중에 리팩토링)
        if(scheduleMapForThisMonth == null) {
            HashMap<Integer, Schedule> scheduleMap = new HashMap<Integer, Schedule>();
            scheduleMapByMonth.put(Integer.valueOf(yearMonthKey), scheduleMap);
            scheduleMapForThisMonth = scheduleMap;
        }

        Log.d("데이터가 비어있는지 체크", String.valueOf(dateKey));
        Log.d("DataHelper isTodayEmptyData yearMonthKey 상세정보", String.valueOf(yearMonthKey));
        Log.d("scheduleMapByMonth 상세정보", String.valueOf(scheduleMapByMonth));

        Log.d("scheduleMapForThisMonth 상세정보", String.valueOf(scheduleMapForThisMonth));
        if(isExistSchedule(dateKey.substring(6,8), scheduleMapForThisMonth))
            isEmptyData = false;
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
     * @param calendarPagerAdapter
     */
    public void makeRectZoneWithFirstCell(CalendarPagerAdapter calendarPagerAdapter) {

        if(rectZoneWithView == null) {
            rectZoneWithView = new HashMap<Integer, RectAndView>();

            View firstCellView = getFirstCellView();
            CalendarCellInfo calendarCellInfo = makeCalendarCellInfo(firstCellView);
            makeRectToPut(calendarCellInfo);

            sortRectZoneByKey();
            calendarPagerAdapter.callMatchRectZoneWithCurrentPageViewMap();
        }
    }

    private void makeRectToPut(CalendarCellInfo calendarCellInfo) {
        for(int i = 0; i <= 35; i++) {

            if(i != 0 && (i % 7) == 0 ) {
                calendarCellInfo.setLeft(calendarCellInfo.getOriginalLeft());
                calendarCellInfo.updateTop(i);
            }
            else
                calendarCellInfo.updateLeft(i);

//            Log.d("렉트 생성 인덱스 체크", String.valueOf(i));
            makeRectAndPut(i,calendarCellInfo);
        }
    }

    private void makeRectAndPut(int index, CalendarCellInfo calendarCellInfo) {
        Rect rect = new Rect(calendarCellInfo.getLeft(), calendarCellInfo.getTop(),
                calendarCellInfo.getLeft() + calendarCellInfo.getWidth(),
                calendarCellInfo.getTop() + calendarCellInfo.getHeight());
//        Log.d("렉트존 생성 체크", "left = " + calendarCellInfo.getLeft() + " , top = " + calendarCellInfo.getTop());
        RectAndView rav = new RectAndView();
        rav.setRect(rect);
        rectZoneWithView.put(index, rav);
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

        return numberArray;
    }

    private View getFirstCellView() {
        View firstCell = EventHelper.eventHelper.getUiHelper().getFirstCalendarCell();
        return firstCell;
    }
//
    public void matchRectZoneWithCurrentPageViewMap(int currentPageStartIndex) {
        int count = 0;
        int countForKeyValue = 0;

        for(Integer key : rectZoneWithViewSorted.keySet()) {
            if(count >= currentPageStartIndex){
                RectAndView rav = rectZoneWithViewSorted.get(key);
                rav.setView(getMapValueByIndex(countForKeyValue));
                countForKeyValue++;
            }
            count++;
        }
//        int startIndex = calen
    }

    /**
     * 해당 인댁스의 해쉬맵 값을 가지고 온다
     * @param countForKeyValue
     * @return
     */
    private View getMapValueByIndex(int countForKeyValue) {
        int index = 0;
        View view = null;
        TreeMap <Integer, View> tempTreeMap = new TreeMap <Integer, View>(currentCalendarViewMap);
        for(Integer key : tempTreeMap.keySet()) {
            if(index == countForKeyValue){
                view = tempTreeMap.get(key);
                break;
            }
            index++;
        }
        return view;
    }

    public HashMap<Integer, RectAndView> getRectZoneWithView() {
        return rectZoneWithView;
    }

    public void sortRectZoneByKey() {

        rectZoneWithViewSorted = new TreeMap<Integer, RectAndView>(rectZoneWithView);
    }

    public TreeMap<Integer, RectAndView> getRectZoneWithViewSorted() {
        return rectZoneWithViewSorted;
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public void setCurrentPageIndex(int currentPageIndex) {
        this.currentPageIndex = currentPageIndex;
    }

    public boolean isExistSchedule(String dateKey, HashMap<Integer, Schedule> scheduleMapForThisMonth){
        boolean isExistSchedule = false;
        for(Integer key : scheduleMapForThisMonth.keySet()) {
            Schedule schedule = scheduleMapForThisMonth.get(key);
            String scheduleDate = schedule.getDate().substring(6, 8);

            if(scheduleDate.equals(dateKey)) {
                isExistSchedule = true;
                break;
            }
        }
        return isExistSchedule;
    }

    /**
     * 스케쥴 추가시의 데이트 스트링 값 생성
     * @param dateValue
     * @param calendarHelper
     */
    public String makeDateString2(String dateValue, CalendarHelper calendarHelper) {
        int position = getCurrentPageIndex();
        String date = "";

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM", Locale.getDefault());
        if(dateValue != null)
            date = String.valueOf(Integer.parseInt(dateValue) < 10 ? "0" + dateValue : dateValue);
        String yearMonth = formatter.format(calendarHelper.getCalendarPagerAdapter().getThisCal2().getTimeInMillis());
        return yearMonth + date;
    }

    /*
     * Drawable을 바이트로 변환
     */
    public byte[] getByteArrayFromDrawable(int drawableId){
        Drawable drawable = ((Activity) context).getDrawable(drawableId);

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        return data;
    }

    /*
 * Drawable을 바이트로 변환
 */
    public byte[] getByteArrayFromDrawable(Drawable drawable){

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        return data;
    }
//    public void saveImageIntoDB(){
//        Drawable drawable = ((Activity) context).getDrawable(R.drawable.email);
//        DBHelper.dbHelper.insertActivityWithIcon(getByteArrayFromDrawable(drawable));
//    }

    public Bitmap getAppIcon(byte[] byteData){
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
        return bitmap;
    }

    public HashMap<String, ArrayList<ActivityVO>> getActivities() {
        return activities;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public int getAllFavoriteActivityCount(){
        int count = 0;
        for(String category : activities.keySet()) {
            ArrayList<ActivityVO> activityList = activities.get(category);
            count = countNumberOfFavorite(count, activityList);
        }
        return count;
    }

    private int countNumberOfFavorite(int count, ArrayList<ActivityVO> activityList) {
        for(ActivityVO vo : activityList) {
            if(vo.isFavorite().equals("T")) count++;
        }
        return count;
    }

    public ArrayList<Drawable> getDrawableList() {
        return drawableList;
    }

    public CharSequence[] getCategoryArray() {
        return categoryArray;
    }

    public void clearDateSchedules(String date) {
        HashMap<Integer, Schedule> scheduleMapForThisMonth = scheduleMapByMonth.get(Integer.parseInt(makeYearMonthKey()));
        Iterator<Map.Entry<Integer,Schedule>> iter = scheduleMapForThisMonth.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer,Schedule> entry = iter.next();
            String keyString = String.valueOf(entry.getKey());
            String dateKey = keyString.split("000")[0];

            if(dateKey.equals(date)){
                iter.remove();
            }
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public ArrayList<View> getMultiModeViews() {
        return multiModeViews;
    }

    public void setMultiModeViews(ArrayList<View> multiModeViews) {
        this.multiModeViews = multiModeViews;
    }
}
