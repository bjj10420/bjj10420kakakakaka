package com.example.schedulemanager;

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
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.schedulemanager.calendar.CalendarPagerAdapter;
import com.example.schedulemanager.calendar.DialogHelper;
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

    private float dX;                               // 드래그 시의 X좌표
    private float dY;                               // 드래그 시의 Y좌표
    private View copiedView;                        // 드래그를 시작할 때 임시로 저장 해놓는 뷰
      private CalendarPagerAdapter calendarPagerAdapter;
                                                    // 메인 캘린더 페이져 어댑터
    private DBHelper dbHelper;
                        // 하단 X 버튼
    private PieDataSet dailyScheduleDataSet;        // 데일리 스케쥴 차트용 데이터 저장소
    private String selectedDateData;                // 선택된 날짜 값
    private String yearMonthKey;                    // 선택된 연월 값
    private HashMap<Integer, Schedule> dailyScheduleMap;
                                                    // 선택된 일자의 하루 스케쥴 맵
    private String dateValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        DataHelper dataHelper = new DataHelper();
        dataHelper.initData(this);
        UIHelper uiHelper = new UIHelper();
        uiHelper.initUI(this);
        EventHelper eventHelper = new EventHelper();
        eventHelper.initEvent();
        initCalndar();
//        initScheduleDisplay();
    }

    /**
     * 스케쥴 화면 초기화
     */
    private void initScheduleDisplay() {
        PieChart pieChart = (PieChart) findViewById(R.id.chart);
        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(18.5f, "Green"));
        entries.add(new PieEntry(26.7f, "Yellow"));
        entries.add(new PieEntry(24.0f, "Red"));
        entries.add(new PieEntry(30.8f, "Blue"));

        PieDataSet set = new PieDataSet(entries, "Election Results");
        PieData data = new PieData(set);
        data.setValueTextSize(0f);
        data.setValueTextColor(Color.GRAY);

        // add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        set.setColors(colors);

        pieChart.setData(data);
        pieChart.setDrawHoleEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setDrawSliceText(false);
        pieChart.invalidate(); // refresh
    }

    /**
     * 하루 일정을 셋팅
     */
    private void setDailyScheduleDisplay(HashMap<Integer, Schedule> dailyScheduleMap) {
        List<PieEntry> entries = new ArrayList<>();
        // 모든 스케쥴은 균등한 점유값을 갖는다
        // 100을 개수로 나눈값으로 지정
        float fillValue = 100 / dailyScheduleMap.size();
            for(Integer dateValue : dailyScheduleMap.keySet()) {
                Schedule schedule = dailyScheduleMap.get(dateValue);
                entries.add(new PieEntry(fillValue, schedule.getActivityName()));
                Log.d("엔트리 추가 확인", "fillValue = " + fillValue);
            }

        dailyScheduleDataSet = new PieDataSet(entries, "Election Results");

        PieData data = new PieData(dailyScheduleDataSet);
        data.setValueTextSize(0f);
        data.setValueTextColor(Color.GRAY);

        // add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dailyScheduleDataSet.setColors(colors);
        dailyScheduleDataSet.setHighlightEnabled(true); // allow highlighting for DataSet

        pieChart.setData(data);
        pieChart.setDrawHoleEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setDrawSliceText(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate(); // refresh
    }

    /**
     * 달력 셋팅, 초기화
     */
    private void initCalndar() {
        calendarPagerAdapter = new CalendarPagerAdapter(this, typeface);

        // 달력 구성 어댑터 생성 및 셋팅
        calendarPagerAdapter.initCalendar();
        calendarPagerAdapter.notifyDataSetChanged();
        calendarPager = (ViewPager) findViewById(R.id.timetable_param_setter_calendar_viewpager);
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
     * 해당 인덱스로 추출된 스케쥴값의 order값 리턴
     * @param selectedIndex
     * @return
     */
    public int getOrderValueFromSchedule(int selectedIndex) {
        int orderValue = 0;
        int countIndex = 0;
        HashMap<Integer, Schedule> scheduleMapForThisMonth = scheduleMapByMonth.get(Integer.parseInt(yearMonthKey));

            for(Integer key : scheduleMapForThisMonth.keySet()) {
                if(countIndex == selectedIndex) {
                    orderValue = scheduleMapForThisMonth.get(key).getOrder();
                    break;
                }
                countIndex++;
            }
        return orderValue;
    }

    /**
     * 스케쥴 맵을 업데이트 (해당 스케쥴 맵에서도 제거)
     * @param scheduleOrderValue
     */
    private void updateDailyScheduleMapByMonth(int scheduleOrderValue) {
      HashMap<Integer, Schedule> scheduleMapForThisMonth = scheduleMapByMonth.get(Integer.parseInt(yearMonthKey));
        Log.d("업데이트 데일리 스케쥴맵 체크 삭제 전 개수", String.valueOf(scheduleMapForThisMonth.size()));
        scheduleMapForThisMonth.remove(Integer.parseInt(dateValue + "000" + scheduleOrderValue));
        Log.d("업데이트 데일리 스케쥴맵 맵 키 체크", dateValue + "000" + scheduleOrderValue);
        Log.d("업데이트 데일리 스케쥴맵 체크 삭제 후 개수", String.valueOf(scheduleMapForThisMonth.size()));

//        Iterator it = scheduleMapForThisMonth.keySet().iterator();
//
//        while(it.hasNext()) {
//           Schedule schedule = scheduleMapForThisMonth.get(it.next());
//           if(schedule.getOrder() == scheduleOrderValue) {
//              it.remove();
//               Log.d("업데이트 데일리 스케쥴맵 체크", "order값이 " + scheduleOrderValue + "인 값을 삭제하였습니다.");
//               break;
//          }
//         }
    }

    /**
     * 스케쥴 맵에서 해당 키(날짜+순서값)을 제거
     */
    private void removeScheduleMapByMonth() {
    }

    /**
     * 하루 일정 데이터를 리로드
     */
    private void reloadDailyScheduleData() {
        float fillValue = 100 / dailyScheduleDataSet.getEntryCount();

        for(int i = 0; i < dailyScheduleDataSet.getEntryCount(); i++) {
            PieEntry entry = dailyScheduleDataSet.getEntryForIndex(i);
            entry.setY(fillValue);
        }
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










    /**
     * 아이콘 버튼 드래그시 바텀버튼 전환
     */
    private void changeBottomButton(boolean isBackBtnVisible) {
        backBtn.setVisibility(isBackBtnVisible ? View.GONE : View.VISIBLE);
        cancelBtn.setVisibility(isBackBtnVisible ? View.VISIBLE : View.GONE);
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
     * 지정된 뷰의 복사본을 만들어 최상위 프레임레이아웃의 자식으로 보냄
     * @param view
     */
    private void hoverView(View view) {
        // 뷰 복사본 생성
        LinearLayout viewLayout = (LinearLayout) view;
        View iconView = viewLayout.getChildAt(0);
        TextView textView = (TextView) viewLayout.getChildAt(1);
        copiedView = makeButtonView(iconView.getBackground(), String.valueOf(textView.getText()),
                viewLayout.getWidth(), viewLayout.getHeight());
        copiedView.setVisibility(View.GONE);
        // 최상위 레이아웃으로 보냄
        totalLayout.addView(copiedView);
    }

    /**
     * 각 버튼뷰를 생성
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private View makeButtonView(Drawable background, String textData, int width, int height) {
        // 각 버튼의 높이
        float buttonHeight = Util.convertDpToPixel(50);
        // 각 텍스트의 높이
        float textHeight = Util.convertDpToPixel(15);
        // 각 버튼 뷰 레이아웃 파라메터
        LinearLayout.LayoutParams buttonViewParams = new LinearLayout.LayoutParams(width,
                height);

        // 각 버튼 레이아웃 파라메터
        ViewGroup.LayoutParams iconParams = new ViewGroup.LayoutParams((int) buttonHeight,
                (int) buttonHeight);
        // 각 텍스트 파라메터
        ViewGroup.LayoutParams textParams = new ViewGroup.LayoutParams((int) buttonHeight,
                (int) textHeight);

            // 버튼뷰 설정
            LinearLayout buttonView = new LinearLayout(this);
            buttonView.setOrientation(LinearLayout.VERTICAL);
            buttonView.setGravity(Gravity.CENTER);
            buttonView.setLayoutParams(buttonViewParams);
            // 아이콘 뷰 설정
            View iconView = new View(this);

//           iconView.setBackgroundResource(findIdByFileName(iconNameMap.get(textData), this));

            iconView.setBackground(background);
            iconView.setLayoutParams(iconParams);
            // 텍스트 뷰 설정
            TextView textView = new TextView(this);
            textView.setText(textData);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(textParams);
            // 태그첨부
            buttonView.setTag(textData);
            // 추가
            buttonView.addView(iconView);
            buttonView.addView(textView);

            return buttonView;
    }


    /**
     * 중앙 아이콘의 배경색 변경
     */
    @SuppressLint("NewApi")
    public void changeCenterIconColor(boolean isTicked){
        Drawable tickedIconDrawable = null;
        Resources res = getResources();
        tickedIconDrawable = res.getDrawable(R.drawable.schedule_icon);
        if(isTicked)
        tickedIconDrawable.setColorFilter(Color.parseColor("#4bf442"), PorterDuff.Mode.SRC_IN);
        else
        tickedIconDrawable.clearColorFilter();
        this.centerIcon.setBackground(tickedIconDrawable);
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



    public HashMap<Integer, View> getCurrentCalendarViewMap() {
        return currentCalendarViewMap;
    }

    public HashMap<Integer, HashMap<Integer, Schedule>> getScheduleMapByMonth() {
        return scheduleMapByMonth;
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

    @Override
    public void onBackPressed() {
        // 하루 일정화면이 ON이면 닫아준다
        if(scheduleLayout.getVisibility() == View.VISIBLE) {
            scheduleLayout.setVisibility(View.GONE);
            calendarLayout.setVisibility(View.VISIBLE);
        }
        // 캘린더화면이 ON이면 닫아준다
        else if(calendarLayout.getVisibility() == View.VISIBLE) {
            calendarLayout.setVisibility(View.GONE);
            centerIcon.setVisibility(View.VISIBLE);
        }
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

    public void setSelectedDateData(String selectedDateData) {
        Log.d("선택된날짜값테스트", selectedDateData);
        this.selectedDateData = selectedDateData;
    }

    public void setDailyScheduleMap(HashMap<Integer, Schedule> dailyScheduleMap) {
        this.dailyScheduleMap = dailyScheduleMap;
    }

    public void setYearMonthKeyAndDateValue(String yearMonthKey, String dateValue) {
        this.yearMonthKey = yearMonthKey;
        this.dateValue = dateValue;
    }
}
