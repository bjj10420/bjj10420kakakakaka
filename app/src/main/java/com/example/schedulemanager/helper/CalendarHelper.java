package com.example.schedulemanager.helper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.TextView;

import com.example.schedulemanager.R;
import com.example.schedulemanager.activity.MainActivity;
import com.example.schedulemanager.vo.RectAndView;
import com.example.schedulemanager.vo.Schedule;
import com.example.schedulemanager.Util;
import com.example.schedulemanager.calendar.CalendarPagerAdapter;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * 달력의 모든 처리 담당
 */
public class CalendarHelper {

    private Context context;
    private CalendarPagerAdapter calendarPagerAdapter;
    private ViewPager calendarPager;                // 메인 캘린더 뷰 페이져 객체
    private DataHelper dataHelper;
    private UIHelper uiHelepr;
    EventHelper eventHelper;

    public void initCalendar(Context context, Typeface typeface, UIHelper uiHelper, DataHelper dataHelper) {
        this.context = context;
        this.uiHelepr = uiHelper;
        this.dataHelper = dataHelper;

        initCaledarPagerAndPagerAdapter(typeface);
        initCaledarDateTextAndPagingEvent(typeface);
    }

    private void initCaledarDateTextAndPagingEvent(Typeface typeface) {
        final TextView calendarDateText =  (TextView) Util.getViewById(context, R.id.timetable_param_setter_calendar_date);
        calendarDateText.setTypeface(typeface);
        setCalendarTitleDate(calendarDateText, calendarPagerAdapter, 12);
        setPagingEvent(calendarDateText, calendarPagerAdapter);
    }

    private void initCaledarPagerAndPagerAdapter(Typeface typeface) {
        initCalendarPagerAdapter(typeface);
        initCalendarPager();
        setEventAfterPagerRendering();
    }

    private void initCalendarPager() {
        calendarPager = (ViewPager) Util.getViewById(context, R.id.timetable_param_setter_calendar_viewpager);
        // 스케쥴 맵 전달
        calendarPagerAdapter.setScheduleMapByMonth(dataHelper.getScheduleMapByMonth());
        calendarPager.setAdapter(calendarPagerAdapter);
        // 초기 페이지 설정
        calendarPager.setCurrentItem(12);
    }

    private void initCalendarPagerAdapter(Typeface typeface) {
        calendarPagerAdapter = new CalendarPagerAdapter(context, typeface);
        // 달력 구성 어댑터 생성 및 셋팅
        calendarPagerAdapter.initCalendar();
        calendarPagerAdapter.notifyDataSetChanged();
    }

    private void setEventAfterPagerRendering() {
        calendarPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(uiHelepr.getCalendarLayout().getVisibility() == View.VISIBLE &&
                        calendarPagerAdapter.isFirstInit()) {
                    calendarPagerAdapter.setFirstInit(false);
                    // 초기화 플래그 OFF
                    Log.d("초기화 플래그 OFF", "초기화 플래그 OFF");
                }
            }
        });
    }

    /**
     * 캘린더의 상단 날짜 텍스트를 변경
     * @param calendarDateText
     * @param calendarAdapter
     * @param position
     */
    private void setCalendarTitleDate(TextView calendarDateText, CalendarPagerAdapter calendarAdapter, int position) {
        Log.d("setCalendarTitleDate 체크", "setCalendarTitleDate 체크");

        // nextCal을 같이 변경
        calendarAdapter.getNextCal().set(calendarAdapter.getThisCal().get(Calendar.YEAR),
                calendarAdapter.getThisCal().get(Calendar.MONTH) + position,
                calendarAdapter.getThisCal().get(Calendar.DATE)
        );
//
//        if(calendarAdapter.getBaseCal() != null)
//            calendarAdapter.getBaseCal().set(calendarAdapter.getThisCal().get(Calendar.YEAR),
//                    calendarAdapter.getThisCal().get(Calendar.MONTH) + position + 2,
//                    calendarAdapter.getThisCal().get(Calendar.DATE)
//            );

        calendarDateText.setText(calendarAdapter.getDateString(position));
    }

    /**
     * 현재 보고 있는 페이지의 캘린더뷰를 다시 리로드하여 저장
     */
    private void reloadCalendarView() {
        GridView calendarGridView = (GridView) calendarPagerAdapter.getViews().get(calendarPager.getCurrentItem()).findViewById(R.id.timetable_param_setter_calendar_gridview);
        int dateCount = 1;
        for(int i = 0; i < calendarGridView.getChildCount(); i++) {
            View calendarView = calendarGridView.getChildAt(i);
            if(((TextView)(calendarView.findViewById(R.id.calendar_item_day))).getText().equals("")) continue;
            // 활성화되어있는 View만 추가
            dataHelper.getCurrentCalendarViewMap().put(dateCount, calendarView);
            dateCount++;
            if(dateCount == 13) calendarView.setBackgroundColor(Color.parseColor("#b2b2b2"));
        }



    }

    private void setPagingEvent(final TextView calendarDateText, final CalendarPagerAdapter calendarAdapter) {
        calendarPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                dataHelper.getCurrentCalendarViewMap().clear();
                setCalendarTitleDate(calendarDateText, calendarAdapter, position);
                reloadCalendarView();
                redrawRectZone();
//                refreshCalendar();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 렉트존을 다시 준비
     */
    private void redrawRectZone() {
            Log.d("redrawRectZone 체크", "redrawRectZone 체크");
            claerRectZoneView();
            rematchRectAndView();
    }

    private void rematchRectAndView() {
        calendarPagerAdapter.callMatchRectZoneWithCurrentPageViewMap();
    }

    private void claerRectZoneView() {
        TreeMap<Integer, RectAndView> rectZoneWithView = dataHelper.getRectZoneWithViewSorted();
        for(Integer key : rectZoneWithView.keySet()) {
            RectAndView rav = rectZoneWithView.get(key);
            rav.setView(null);
        }
    }

    public CalendarPagerAdapter getCalendarPagerAdapter() {
        return calendarPagerAdapter;
    }
    /**
     * 캘린더 한칸을 클릭시 하루 일정화면으로 전환 (스케쥴이 한개이상 들어가있을경우)
     * @param dailySchedule
     */

    public void changeToScheduleLayout(TreeMap<Integer, Schedule> dailySchedule) {
        if(dailySchedule.size() > 0) {
            uiHelepr.setNoDateText(false);
            uiHelepr.getCalendarLayout().setVisibility(View.GONE);
            uiHelepr.getScheduleLayout().setVisibility(View.VISIBLE);
            uiHelepr.setDailyScheduleDisplay(dailySchedule);
        }
    }

    /**
     * 맵안에 저장되어 있는 모든 달력 칸과의 충돌 체크
     */
    public boolean checkCollisionForCalendarCell(){
        HashMap<Integer, View> arroundViewGroup = dataHelper.getArroundViewGroup();
        HashMap<Integer, View> currentCalendarViewMap = dataHelper.getCurrentCalendarViewMap();
        View copiedView = uiHelepr.getCopiedView();

        // 저장소의 뷰들은 먼저 클리어
        arroundViewGroup.clear();
        boolean isCellCollided = false;

        for (int dateKey : currentCalendarViewMap.keySet()){
            View calendarCellView = currentCalendarViewMap.get(dateKey);
            if(calendarCellView.getTag() != null)
//            Log.d("체크 캘린더 셀 충돌 캘린더셀뷰", String.valueOf(calendarCellView.getTag()) + "일");
//            Log.d("체크 캘린더 셀 충돌 카피드뷰", String.valueOf(copiedView));

            if(Util.checkCollisionForChildView(calendarCellView, copiedView)) {
                isCellCollided = true;
                // 후보군 저장소에 저장
                arroundViewGroup.put((int) Util.getDistanceFromTwoPoints(
                        calendarCellView.getX(), calendarCellView.getY(), copiedView.getX(), copiedView.getY()),
                        calendarCellView);
            }
        }

//        Log.d("체크 캘린더 셀 충돌", String.valueOf(isCellCollided));
        return isCellCollided;
    }

    /**
     * 맵안에 저장되어 있는 모든 달력 칸과의 충돌 체크(rect존을 이용)
     */
    public boolean checkCollisionForCalendarCellByRectZone(){
        HashMap<Integer, View> arroundViewGroup = dataHelper.getArroundViewGroup();
        HashMap<Integer, View> currentCalendarViewMap = dataHelper.getCurrentCalendarViewMap();
        View copiedView = uiHelepr.getCopiedView();
        int[] numberArray = new int[2];
        copiedView.getLocationInWindow(numberArray);
        Rect copiedViewRect = new Rect(numberArray[0], numberArray[1],
                numberArray[0] + copiedView.getWidth(), numberArray[1] + copiedView.getHeight());


        // 저장소의 뷰들은 먼저 클리어
        arroundViewGroup.clear();
        boolean isCellCollided = false;
        for (Integer indexKey : dataHelper.getRectZoneWithViewSorted().keySet()){
            RectAndView rav = dataHelper.getRectZoneWithViewSorted().get(indexKey);
            View calendarCellView = rav.getView();
            Rect rect = rav.getRect();

            if(calendarCellView != null && Util.checkCollisionByRect(rect, copiedViewRect)) {
                isCellCollided = true;

                // 후보군 저장
                arroundViewGroup.put((int) Util.getDistanceFromTwoPoints(
                        rect.left + rect.width() / 2, rect.top + rect.height() / 2, copiedView.getX() + copiedView.getWidth() / 2, copiedView.getY() + copiedView.getHeight() / 2),
                        calendarCellView);
//                arroundViewGroup.put((int) Util.getDistanceFromTwoPoints(
//                        rect.left , rect.top, copiedView.getX(), copiedView.getY()),
//                        calendarCellView);
            }
        }

        return isCellCollided;
    }


    /**
     * 스케쥴 추가시의 데이트 스트링 값 생성
     */
    public String makeDateString(String dateValue) {
        int baseCalMonth = calendarPagerAdapter.getBaseCal().get(Calendar.MONTH);
        String baseCalMonthString = baseCalMonth < 10 ? "0" + baseCalMonth : String.valueOf(baseCalMonth);
        String baseCalDateString = String.valueOf(Integer.parseInt(dateValue) < 10 ? "0" + dateValue : dateValue);
        String dateString = String.valueOf(calendarPagerAdapter.getBaseCal().get(Calendar.YEAR)) +
                baseCalMonthString + baseCalDateString;
        return dateString;
    }


    /**
     * 저장소에 있는 모든 뷰중에 최단거리를 추출
     */
    public int findTheClosestView() {
        int count = 0;
        int min = 0;

        for(int distance : dataHelper.getArroundViewGroup().keySet()) {
            if(count == 0) min = distance;
            else {
                if(distance < min)
                    min = distance;
            }
            count++;
        }
        return min;
    }

    /**
     * 드래그시 최단거리인 달력 한칸의 색을 변경
     * @param calendarCellView
     */
    public void changeCalendarCellColor(View calendarCellView) {
//        Log.d("포인트에 가장 가까운 뷰", String.valueOf(calendarCellView.getTag()));
        View closestView = uiHelepr.getClosestView();

        // 1. 원래 있는 경우 기존셀을 원복, 새로운 곳에는 색을 + 저장
        if(closestView != null) {
            if(isToday(closestView))
                closestView.setBackgroundResource(R.drawable.calendar_item_selected_bg);
            else
                closestView.setBackgroundColor(Color.parseColor("#ffffff"));


            calendarCellView.setBackgroundResource(R.drawable.calendar_hover_bg);
         }
        // 2. 첫 변화
        else {
            calendarCellView.setBackgroundResource(R.drawable.calendar_hover_bg);
         }

        if(calendarCellView != null)
        uiHelepr.setClosestView(calendarCellView);
    }

    /**
     * 오늘 날짜라 선택되어 있는지 판단
     * @param closestView
     */
    public boolean isToday(View closestView) {
        // extraView가 활성화인지( 오늘이라 선택되어있는지 )
//        boolean isToday = !((TextView)((RelativeLayout) closestView).getChildAt(2)).getText().equals("");
//        Log.d("데이터 헬퍼 체크", String.valueOf(DataHelper.dataHelper));
//        Log.d("데이터 헬퍼 체크", DataHelper.dataHelper.getDateOfToday());

        boolean isToday = DataHelper.dataHelper.getDateOfToday().equals(String.valueOf(closestView.getTag()));

        return isToday;
    }
//
    /**
     * 메인 달력 갱신
     */
    public void refreshCalendar() {
        dataHelper.getDbHelper().selectAllSchedule(dataHelper.getScheduleMapByMonth());
        // 어댑터 업데이트
        calendarPagerAdapter.getAdapters()[calendarPager.getCurrentItem()].notifyDataSetChanged();
        GridView calendarGridView = (GridView) calendarPagerAdapter.getViews().get(calendarPager.getCurrentItem()).findViewById(R.id.timetable_param_setter_calendar_gridview);
        calendarGridView.setAdapter(calendarPagerAdapter.getAdapters()[calendarPager.getCurrentItem()]);
        //TODO 처음 데이터가 아무것도 없는 상태에서 갱신이 안되기때문에 추가 처리해줌
        calendarPagerAdapter.notifyDataSetChanged();
    }

    /**
     * 메인 캘린더의 이전, 다음 버튼 클릭시 페이징
     * @param v
     */
    public void pageCalendar(View v) {
        int currentPage = -1;
        int keyValue = v.getId() == R.id.timetable_param_setter_calendar_prev ? -1 : +1;
        currentPage = calendarPager.getCurrentItem();
        calendarPager.setCurrentItem(currentPage + keyValue, true);
    }

    public void setEventHelper(EventHelper eventHelper) {
        this.eventHelper = eventHelper;
    }

    public void getCalendarCellClickEvent(View v) {
        Log.d("getCalendarCellClickEvent 체크", String.valueOf(v));

        MainActivity activity = ((MainActivity) context);
        String dateValue = String.valueOf(v.getTag());
        String year = String.valueOf(calendarPagerAdapter.getBaseCal().get(Calendar.YEAR));
        int month = calendarPagerAdapter.getBaseCal().get(Calendar.MONTH);
        String baseCalMonthString = month < 10 ? "0" + month : String.valueOf(month);
        String yearMonthKey = year + baseCalMonthString;
        Log.d("getCalendarCellClickEvent yearMonthKey 체크", String.valueOf(yearMonthKey));

        if(isNodataInThisMonth(yearMonthKey)){
            Log.d("getCalendarCellClickEvent isNodataInThisMonth(yearMonthKey) 체크", String.valueOf(isNodataInThisMonth(yearMonthKey)));
            return;
        }

        dataHelper.setYearMonthKeyAndDateValue(yearMonthKey, dateValue);
        dataHelper.setSelectedDateData(yearMonthKey + (Integer.parseInt(dateValue) < 10 ? "0" + dateValue : dateValue));
        uiHelepr.setDailyScheduleDateText(yearMonthKey, dateValue);
        HashMap<Integer, Schedule> dailySchedule = dataHelper.makeDailyScheduleMap(yearMonthKey, dateValue);
        //정렬
        TreeMap<Integer,Schedule> tm = new TreeMap<Integer,Schedule>(dailySchedule);
        dataHelper.setDailyScheduleMap(tm);
        changeToScheduleLayout(tm);
    }

    /**
     * 이번 달에는 데이터가 하나도 없는 경우
     */
    private boolean isNodataInThisMonth(String yearMonthKey) {
         HashMap<Integer, Schedule> thisMonthSchedules = dataHelper.getScheduleMapByMonth().get(Integer.parseInt(yearMonthKey));
        return thisMonthSchedules == null ? true : false;
    }

    /**
     * 선택되어있는 체크 마크 표시 설정
     * @param isVisible
     * @param closestView
     */
    public void setCheckMark(boolean isVisible, View closestView) {
        View calendarCellView = null;

        if(closestView != null)
            calendarCellView = closestView;
        else
            calendarCellView = dataHelper.getCurrentCalendarViewMap().get(Integer.parseInt(dataHelper.getDateValue()));

//        Log.d("셋체크마크확인", String.valueOf(calendarCellView));
        View checkMarkView = calendarCellView.findViewById(R.id.check_mark);
        checkMarkView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public ViewPager getCalendarPager() {
        return calendarPager;
    }
}
