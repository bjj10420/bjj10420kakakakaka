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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;

/**
 * 모든 데이터 처리 담당
 */
public class DataHelper {

    public static DataHelper dataHelper;
    private DBHelper dbHelper;
    private HashMap<String, String> iconNameMap;    // 해당하는 텍스트에 매칭시키는 아이콘명이 저장되는 맵
    private Typeface typeface;                      // 글꼴
    private Context context;

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
    private HashMap<Integer, RectAndView> rectZoneWithView;   // 이벤트 구역을 나누는 rect존과 매칭하는 뷰를 담는 저장소
    private TreeMap<Integer, RectAndView> rectZoneWithViewSorted;   // 이벤트 구역을 나누는 rect존과 매칭하는 뷰를 담는 저장소
    private int currentPageIndex;

    private ArrayList<String> categories;           // 기타버튼 클릭시 활성화되는 패널의 카테고리들
    private HashMap<String, ArrayList<ActivityVO>> activities;      // 기타버튼 클릭시 활성화되는 패널의 활동들


    public void initData(Context context) {
        initField(context);
        if(isFirstLoading()) {
            makeBasicData();
            updateIsFirstLoadingValue();
        }
//        saveImageIntoDB();
//        DBHelper.dbHelper.selectAllActivities();
        loadBasicDatas();
    }

    /**
     * 기본 카테고리 데이터, 활동 데이터를 불러온다
     */
    private void loadBasicDatas() {
        loadBasicCategories();
        loadBasicActivities();
    }

    private void loadBasicActivities() {

    }

    private void loadBasicCategories() {
        DBHelper.dbHelper.selectAllCategories(categories);
        Log.d("카테고리 불러오기체크", String.valueOf(categories));
    }

    private void initField(Context context) {
        dataHelper = this;
        this.context = context;
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
        // 페이지 초기화
        currentPageIndex = 12;

        categories = new ArrayList<String>();
        activities = new HashMap<String, ArrayList<ActivityVO>>();
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
        ActivityVO basicActivity12 = new ActivityVO("생활", "가기", false, getByteArrayFromDrawable(R.drawable.basic_go_to));
        ActivityVO basicActivity13 = new ActivityVO("생활", "약속", false, getByteArrayFromDrawable(R.drawable.promise));
        ActivityVO basicActivity14 = new ActivityVO("생활", "만남", false, getByteArrayFromDrawable(R.drawable.meet));
        ActivityVO basicActivity15 = new ActivityVO("생활", "쇼핑", false, getByteArrayFromDrawable(R.drawable.shopping));
        ActivityVO basicActivity16 = new ActivityVO("생활", "학교", false, getByteArrayFromDrawable(R.drawable.school));
        ActivityVO basicActivity18 = new ActivityVO("생활", "들리기", false, getByteArrayFromDrawable(R.drawable.basic_drop_by));
        ActivityVO basicActivity20 = new ActivityVO("생활", "청소", false, getByteArrayFromDrawable(R.drawable.basic_clean));
        ActivityVO basicActivity23 = new ActivityVO("생활", "공부", false, getByteArrayFromDrawable(R.drawable.study));

        ActivityVO basicActivity4 = new ActivityVO("사무", "서류 정리", false, getByteArrayFromDrawable(R.drawable.basic_arrage));
        ActivityVO basicActivity5 = new ActivityVO("사무", "체크 리스트", false, getByteArrayFromDrawable(R.drawable.basic_check_list));
        ActivityVO basicActivity6 = new ActivityVO("사무", "서류 준비", false, getByteArrayFromDrawable(R.drawable.basic_documentation));
        ActivityVO basicActivity7 = new ActivityVO("사무", "보고서 작성", false, getByteArrayFromDrawable(R.drawable.basic_report));
        ActivityVO basicActivity8 = new ActivityVO("사무", "메모", false, getByteArrayFromDrawable(R.drawable.basic_write));
        ActivityVO basicActivity9 = new ActivityVO("사무", "이메일", false, getByteArrayFromDrawable(R.drawable.email));
        ActivityVO basicActivity10 = new ActivityVO("사무", "회의", false, getByteArrayFromDrawable(R.drawable.community));
        ActivityVO basicActivity11 = new ActivityVO("사무", "주식", false, getByteArrayFromDrawable(R.drawable.basic_check_stock));
        ActivityVO basicActivity11_1 = new ActivityVO("사무", "전화", false, getByteArrayFromDrawable(R.drawable.basic_tel));

        ActivityVO basicActivity1 = new ActivityVO("취미", "사진", false, getByteArrayFromDrawable(R.drawable.basic_photo));
        ActivityVO basicActivity2 = new ActivityVO("취미", "음악", false, getByteArrayFromDrawable(R.drawable.leasure));
        ActivityVO basicActivity3 = new ActivityVO("취미", "여행", false, getByteArrayFromDrawable(R.drawable.basic_travel));

        ActivityVO basicActivity17 = new ActivityVO("기타", "메모", false, getByteArrayFromDrawable(R.drawable.basic_write));
        ActivityVO basicActivity19 = new ActivityVO("기타", "확인", false, getByteArrayFromDrawable(R.drawable.check_icon8));
        ActivityVO basicActivity21 = new ActivityVO("기타", "확인", false, getByteArrayFromDrawable(R.drawable.basic_drop_by));
        ActivityVO basicActivity22 = new ActivityVO("기타", "정보", false, getByteArrayFromDrawable(R.drawable.basic_information));

        ActivityVO[] activityVoArray = new ActivityVO[] {basicActivity12, basicActivity13,basicActivity14,basicActivity15,basicActivity16,
                basicActivity18,basicActivity20,basicActivity23,basicActivity4,basicActivity5,basicActivity6,
                basicActivity7,basicActivity8,basicActivity9,basicActivity10,basicActivity11,basicActivity11_1,
                basicActivity1,basicActivity2,basicActivity3,basicActivity17,basicActivity19,
                basicActivity21,basicActivity22};
        return  activityVoArray;
    }

    private void insertActivityIntoDB(ActivityVO[] activityVOs) {

        for(int i = 0; i < activityVOs.length; i++) {
            DBHelper.dbHelper.insertActivityWithIcon(activityVOs[i]);
        }
    }

    private void makeBasicCategoryData() {
        String[] categoryNames = {"취미", "여행", "사무", "생활", "기타"};
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
    public void addToDailyScheduleMapByMonth(int newOrderValue, String activityName) {
        //TODO yearMonth키를 원래 그냥 필드로 썼었는데 달력추가에서도 이 메소드를 쓰게되어 생성함수로  대체해줌
        HashMap<Integer, Schedule> scheduleMapForThisMonth = scheduleMapByMonth.get(Integer.parseInt(makeYearMonthKey()));
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

    public void makeSelectedDateData(String dateValue){
        String yearMonthKey = makeYearMonthKey();
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
//        CalendarPagerAdapter calendarPagerAdapter = EventHelper.eventHelper.getCalendarHelper().getCalendarPagerAdapter();
//        String year = String.valueOf(calendarPagerAdapter.getBaseCal().get(Calendar.YEAR));
//        int month = calendarPagerAdapter.getBaseCal().get(Calendar.MONTH);
        String yearMonthKey = makeDateString2(null, EventHelper.eventHelper.getCalendarHelper());
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
        for(int i = 0; i < 35; i++) {

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
            if(scheduleDate.equals(dateKey))
                isExistSchedule = true;
            break;
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
        String yearMonth = formatter.format(position == 0 ? calendarHelper.getCalendarPagerAdapter().getThisCal().getTimeInMillis()
                 : new Date(calendarHelper.getCalendarPagerAdapter().getNextCal().getTimeInMillis()));
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

//    public void saveImageIntoDB(){
//        Drawable drawable = ((Activity) context).getDrawable(R.drawable.email);
//        DBHelper.dbHelper.insertActivityWithIcon(getByteArrayFromDrawable(drawable));
//    }

    public Bitmap getAppIcon(byte[] byteData){
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
        return bitmap;
    }
}
