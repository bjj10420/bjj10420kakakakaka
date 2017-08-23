package com.example.schedulemanager.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.schedulemanager.R;
import com.example.schedulemanager.Schedule;
import com.example.schedulemanager.Util;
import com.example.schedulemanager.calendar.CalendarPagerAdapter;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * 달력의 모든 처리 담당
 */
public class CalendarHelper {
    private CalendarPagerAdapter calendarPagerAdapter;
    private ViewPager calendarPager;                // 메인 캘린더 뷰 페이져 객체

    public void initCalendar(Context context) {
        calendarPagerAdapter = new CalendarPagerAdapter(this, typeface);

        // 달력 구성 어댑터 생성 및 셋팅
        calendarPagerAdapter.initCalendar();
        calendarPagerAdapter.notifyDataSetChanged();
        calendarPager = (ViewPager) Util.getViewById(context, R.id.timetable_param_setter_calendar_viewpager);
        // 스케쥴 맵 전달
        calendarPagerAdapter.setScheduleMapByMonth(scheduleMapByMonth);
        calendarPager.setAdapter(calendarPagerAdapter);
        // 달력에 연도, 월 표시
        calendarPager.setCurrentItem(12);
        final TextView calendarDateText =  (TextView) findViewById(R.id.timetable_param_setter_calendar_date);
        calendarDateText.setTypeface(typeface);
        setCalendarTitleDate(calendarDateText, calendarPagerAdapter, 12);
        // 이벤트 리스너 추가
        setPagingEvent(calendarDateText, calendarPagerAdapter);
    }

    /**
     * 캘린더의 상단 날짜 텍스트를 변경
     * @param calendarDateText
     * @param calendarAdapter
     * @param position
     */
    private void setCalendarTitleDate(TextView calendarDateText, CalendarPagerAdapter calendarAdapter, int position) {
        // nextCal을 같이 변경
        calendarAdapter.getNextCal().set(calendarAdapter.getThisCal().get(Calendar.YEAR),
                calendarAdapter.getThisCal().get(Calendar.MONTH) + position,
                calendarAdapter.getThisCal().get(Calendar.DATE)
        );
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
            currentCalendarViewMap.put(dateCount, calendarView);
            Log.d("캘린더뷰맵삽입확인 = ", "dateCount = " + dateCount);

            dateCount++;
            if(dateCount == 13) calendarView.setBackgroundColor(Color.parseColor("#b2b2b2"));
        }
        Log.d("캘린더뷰맵개수확인 = ", String.valueOf(currentCalendarViewMap.size()));
    }

    private void setPagingEvent(final TextView calendarDateText, final CalendarPagerAdapter calendarAdapter) {
        calendarPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentCalendarViewMap.clear();
                setCalendarTitleDate(calendarDateText, calendarAdapter, position);
                reloadCalendarView();
//                refreshCalendar();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 일정 화면의 날짜 텍스트 지정
     * @param yearMonthKey
     * @param dateValue
     */
    public void setDailyScheduleDateText(String yearMonthKey, String dateValue) {
        String dateValueString = Integer.parseInt(dateValue) < 10 ? "0" + dateValue : dateValue;
        StringBuilder sb = new StringBuilder();
        sb.append(yearMonthKey.substring(0,4))
                .append(".")
                .append(yearMonthKey.substring(4,6))
                .append(".").append(dateValueString).toString();
        TextView dailyScheduleDateText = (TextView) findViewById(R.id.dailyScheduleDateText);
        dailyScheduleDateText.setText(sb);
    }

    public CalendarPagerAdapter getCalendarPagerAdapter() {
        return calendarPagerAdapter;
    }
    /**
     * 캘린더 한칸을 클릭시 하루 일정화면으로 전환 (스케쥴이 한개이상 들어가있을경우)
     * @param dailySchedule
     */

    public void changeToScheduleLayout(HashMap<Integer, Schedule> dailySchedule) {
        if(dailySchedule.size() > 0) {
            calendarLayout.setVisibility(View.GONE);
            scheduleLayout.setVisibility(View.VISIBLE);
            setDailyScheduleDisplay(dailySchedule);
        }
    }

    /**
     * 맵안에 저장되어 있는 모든 달력 칸과의 충돌 체크
     */
    public boolean checkCollisionForCalendarCell(){
        // 저장소의 뷰들은 먼저 클리어
        arroundViewGroup.clear();
        boolean isCellCollided = false;

        for (int dateKey : currentCalendarViewMap.keySet()){
            View calendarCellView = currentCalendarViewMap.get(dateKey);
            if(Util.checkCollisionForChildView(calendarCellView, copiedView)) {
                isCellCollided = true;
                // 후보군 저장소에 저장
                arroundViewGroup.put((int) Util.getDistanceFromTwoPoints(
                        calendarCellView.getX(), calendarCellView.getY(), copiedView.getX(), copiedView.getY()),
                        calendarCellView);
            }
        }
        Log.d("충돌 실험", "충돌 " + isCellCollided);
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
     * 스케쥴 버튼을 드래그하여 메인 캘린더의 한칸으로 가져갔을때 추가
     * @param tagName
     */
    private void addScheduleForTheDate(String tagName) {
        // 데이트 스트링값 생성
        String dateString = makeDateString(String.valueOf(closestView.getTag()));
        // 이름
        String activityName = tagName;
        // 삽입할 스케쥴 데이터 객체 생성
        Schedule newSchedule = new Schedule();
        // 넘버
        int number = DBHelper.dbHelper.getScheduleCountForDate(dateString);
        newSchedule.setDate(dateString);
        newSchedule.setActivityName(activityName);
        //TODO 나머지 order, time, memo는 일단 공란
        newSchedule.setOrder(number);
        newSchedule.setTime("");
        newSchedule.setMemo("");

        // DB에 삽입
        long resultNum = DBHelper.dbHelper.insertSchedule(newSchedule);
        Log.d(tagName + "을", " 삽입하였습니다 dateString = " + dateString);
    }

    /**
     * 스케쥴 버튼을 드래그하여 중앙 아이콘으로 가져갔을때 추가
     * @param tagName
     */
    private void addScheduleForToday(String tagName) {
        // 오늘 날짜
        Date dateOfToday = new Date();
        DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = dateFormatter.format(dateOfToday);
        // 넘버
        int number = DBHelper.dbHelper.getScheduleCountForDate(dateString);
        // 이름
        String activityName = tagName;

        // 삽입할 스케쥴 데이터 객체 생성
        Schedule newSchedule = new Schedule();
        newSchedule.setNo(number);
        newSchedule.setDate(dateString);
        newSchedule.setActivityName(activityName);
        //TODO 나머지 order, time, memo는 일단 공란
        newSchedule.setOrder(0);
        newSchedule.setTime("");
        newSchedule.setMemo("");

        // DB에 삽입
        long resultNum = DBHelper.dbHelper.insertSchedule(newSchedule);
        Log.d(tagName + "을", " 삽입하였습니다 number = " + number);
    }

    /**
     * 저장소에 있는 모든 뷰중에 최단거리를 추출
     */
    private int findTheClosestView() {
        int count = 0;
        int min = 0;

        for(int distance : arroundViewGroup.keySet()) {
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
    private void changeCalendarCellColor(View calendarCellView) {
        // 1. 원래 있는 경우 기존셀을 원복, 새로운 곳에는 색을 + 저장
        if(closestView != null) {
            closestView.setBackgroundColor(Color.parseColor(isToday(closestView) ? "#ffc000": "#ffffff"));
            calendarCellView.setBackgroundColor(Color.parseColor("#c8c8c8"));
            closestView = calendarCellView;
        }
        // 2. 첫 변화
        else {
            calendarCellView.setBackgroundColor(Color.parseColor("#c8c8c8"));
            closestView = calendarCellView;
        }
    }

    /**
     * 오늘 날짜라 선택되어 있는지 판단
     * @param closestView
     */
    private boolean isToday(View closestView) {
        // extraView가 활성화인지( 오늘이라 선택되어있는지 )
        boolean isToday = !((TextView)((RelativeLayout) closestView).getChildAt(2)).getText().equals("");
        return isToday;
    }

    /**
     * 메인 달력 갱신
     */
    private void refreshCalendar() {
        dbHelper.selectAllSchedule(scheduleMapByMonth);

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
    private void pageCalendar(View v) {
        int currentPage = -1;
        int keyValue = v.getId() == R.id.timetable_param_setter_calendar_prev ? -1 : +1;
        currentPage = calendarPager.getCurrentItem();
        calendarPager.setCurrentItem(currentPage + keyValue, true);
    }


}
