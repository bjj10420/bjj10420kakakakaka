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

    private HashMap<String, String> iconNameMap;    // 해당하는 텍스트에 매칭시키는 아이콘명이 저장되는 맵
    private float dX;                               // 드래그 시의 X좌표
    private float dY;                               // 드래그 시의 Y좌표
    private View copiedView;                        // 드래그를 시작할 때 임시로 저장 해놓는 뷰
    private RelativeLayout totalLayout;             // 최상위 프레임 레이아웃
    private View centerIcon;                        // 중앙 아이콘 뷰
    private Typeface typeface;                      // 글꼴
    private View calendarLayout;                    // 메인 캘린더 레이아웃
    private View scheduleLayout;                    // 하루 일정 레이아웃
    private ViewPager calendarPager;                // 메인 캘린더 뷰 페이져 객체
    private HashMap<Integer, HashMap<Integer, Schedule>> scheduleMapByMonth;
                                                    // 달력월값을 키로 갖는 스케쥴 저장소
    private HashMap<Integer, View> currentCalendarViewMap;
                                                    // 현재 보고있는 달력의 각 칸을 저장해놓는 저장소
    private HashMap<Integer, View> arroundViewGroup;
                                                    // 드래그중에 포인터주위의 뷰들
    private View closestView;                       // 드래그 이벤트 도중 포인터주위의 가장 가까운 뷰
    private CalendarPagerAdapter calendarPagerAdapter;
                                                    // 메인 캘린더 페이져 어댑터
    private DBHelper dbHelper;
    private View backBtn;                           // 하단 뒤로가기 버튼
    private View cancelBtn;                         // 하단 X 버튼
    private PieDataSet dailyScheduleDataSet;        // 데일리 스케쥴 차트용 데이터 저장소

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        initData();
        initUI();
        initEvent();
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
        PieChart pieChart = (PieChart) findViewById(R.id.chart);
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

    private void initData() {
         dbHelper = new DBHelper(this);
        /**
         * 1. favorite테이블에서 메인에 등록된 버튼들의 정보를 로딩
         * 2. hashMap에다 해당 text를 키로 하여 데이터에 아이콘명을 추가
         */
        iconNameMap = new HashMap<String, String>();
        //TODO DB를 구축하면 아이콘 네임맵으로 저장 시켜줘야 함
        // TEST용으로
//        iconNameMap.put("")
        // 글꼴 로딩
        typeface = getApplicationFont();
        // 모든 스케쥴 데이터 로딩
        scheduleMapByMonth = new HashMap<Integer, HashMap<Integer, Schedule>>();
        dbHelper.selectAllSchedule(scheduleMapByMonth);
        currentCalendarViewMap = new HashMap<Integer, View>();
        arroundViewGroup = new HashMap<Integer, View>();
     }

    private void initUI() {
        //TODO 스트링 리스트 파라메터를 나중에 DB에서 읽어오게 해야 함
        //TODO 나중에 버튼 패널의 아이콘들에 weight를 줘야한다
        ArrayList<String> testList = new ArrayList<String>();
        ArrayList<String> testList2 = new ArrayList<String>();

        testList.add("교류");
        testList.add("이메일");
        testList.add("여가");
        testList.add("만남");
        testList2.add("약속");
        testList2.add("독서");
        testList2.add("학교");
        testList2.add("공부");

        initButtonPanel(R.id.buttonPanel, testList);
        initButtonPanel(R.id.buttonPanel2, testList2);

        totalLayout = (RelativeLayout) findViewById(R.id.totalLayout);
        centerIcon = findViewById(R.id.centerIcon);
        calendarLayout = findViewById(R.id.calendarLayout);
        scheduleLayout = findViewById(R.id.scheduleLayout);
        backBtn = findViewById(R.id.back_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
    }

    /**
     * 모서리의 버튼 패널 초기화
     * 1. 넘겨받은 스트링 리스트데이터를 for 문으로 순환
     * 2. 데이터 하나에 뷰를 생성해서 해당 아이콘 코드값(아이콘명)으로 이미지뷰를 지정, 스트링 데이터로 텍스트 뷰 지정
     * 3. 해당 패널에 생성한 뷰 추가
      * @param panelId
     * @param stringArrayList
     */
    private void initButtonPanel(int panelId, ArrayList<String> stringArrayList) {
        // 추가되는 패널 뷰
        LinearLayout buttonPanel = (LinearLayout) findViewById(panelId);
        // 각 버튼의 높이
        float buttonHeight = Util.convertDpToPixel(50);
        // 각 텍스트의 높이
        float textHeight = Util.convertDpToPixel(15);
        // 각 버튼 뷰 레이아웃 파라메터
        LinearLayout.LayoutParams buttonViewParams = new LinearLayout.LayoutParams(0,
                (int) Util.convertDpToPixel(65));
        buttonViewParams.weight = 1;

        // 각 버튼 레이아웃 파라메터
        ViewGroup.LayoutParams iconParams = new ViewGroup.LayoutParams((int) buttonHeight,
        (int) buttonHeight);
        // 각 텍스트 파라메터
        ViewGroup.LayoutParams textParams = new ViewGroup.LayoutParams((int) buttonHeight,
        (int) textHeight);
        //TODO 테스트용 코드
        int count = 0;
        if(panelId == R.id.buttonPanel2) count = 4;

        for(String textData : stringArrayList){
            // 버튼뷰 설정
            LinearLayout buttonView = new LinearLayout(this);
            buttonView.setOrientation(LinearLayout.VERTICAL);
            buttonView.setGravity(Gravity.CENTER);
            buttonView.setLayoutParams(buttonViewParams);
            // 아이콘 뷰 설정
            View iconView = new View(this);

            // iconView.setBackgroundResource(findIdByFileName(iconNameMap.get(textData), this));

            //TODO 테스트용 코드
                switch(count) {
                    case 0 : iconView.setBackgroundResource(R.drawable.community);
                            break;
                    case 1 : iconView.setBackgroundResource(R.drawable.email);
                        break;
                    case 2 : iconView.setBackgroundResource(R.drawable.leasure);
                        break;
                    case 3 : iconView.setBackgroundResource(R.drawable.meet);
                        break;
                    case 4 : iconView.setBackgroundResource(R.drawable.promise);
                        break;
                    case 5 : iconView.setBackgroundResource(R.drawable.read);
                        break;
                    case 6 : iconView.setBackgroundResource(R.drawable.school);
                        break;
                    case 7 : iconView.setBackgroundResource(R.drawable.shopping);
                        break;
                    case 8 : iconView.setBackgroundResource(R.drawable.study);
                        break;
                }

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
            buttonPanel.addView(buttonView);

            //TODO 테스트용 코드
            count++;
        }
    }

    private void initEvent() {
        // 드래그 이벤트 설정
        setDragEvent(R.id.buttonPanel);
        setDragEvent(R.id.buttonPanel2);
        // 중앙 아이콘 클릭 이벤트 설정
        setCenterIconClickEvent();
        // 뒤로가기 버튼 클릭 이벤트 설정
        setBackBtnClickEvent();
        // 달력 내 사용 버튼 이벤트 설정( 이전, 다음 )
        setCalendarBtnEvent(R.id.timetable_param_setter_calendar_prev);
        setCalendarBtnEvent(R.id.timetable_param_setter_calendar_next);
        // 파이 챠트 탭(클릭) 이벤트 설정
        setDailyScheduleEvent(R.id.chart);
    }

    /**
     * 챠트 이벤트 설정
     * @param chartId
     */
    private void setDailyScheduleEvent(int chartId) {
        PieChart pieChart = (PieChart) findViewById(chartId);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // 다이얼로그 띄움
                new DialogHelper().setChoiceStyleDialog(MainActivity.this);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void setCalendarBtnEvent(int viewId) {
        // 달력 내 사용 버튼
        View calendarBtn =  findViewById(viewId);
        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageCalendar(v);
            }
        });
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

    private void setBackBtnClickEvent() {
        View backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 중앙 뷰 복구
                centerIcon.setVisibility(View.VISIBLE);
                calendarLayout.setVisibility(View.GONE);
            }
        });
    }

    private void setCenterIconClickEvent() {
        centerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarLayout.setVisibility(View.VISIBLE);
                // 중앙 아이콘 비표시
                v.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 해당 패널내의 자식 아이콘들의 드래그 이벤트를 설정
     * @param buttonPanelId
     */
    private void setDragEvent(final int buttonPanelId) {
        LinearLayout buttonPanel = (LinearLayout) findViewById(buttonPanelId);

        for(int i = 0; i < buttonPanel.getChildCount(); i++){
            View buttonView = buttonPanel.getChildAt(i);
            buttonView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            actionDownEvent(view, event, buttonPanelId);
                            break;

                        case MotionEvent.ACTION_MOVE:
                            actionMoveEvent(view, event);
                            break;

                        case MotionEvent.ACTION_UP:
                            actionUpEvent(view);
                            break;

                        default:
                            return false;
                    }
                    return true;
                }
            });
        }
    }

    /**
     * 아이콘 버튼 드래그 중 업할때 이벤트
     * @param view
     */
    private void actionUpEvent(View view) {
        // 중앙 아이콘 활성화인 경우
        if(centerIcon.getVisibility() == View.VISIBLE &&
                Util.checkCollision(centerIcon, copiedView)) {
            addScheduleForToday(String.valueOf(view.getTag()));
        }
        // 메인 달력 활성화 중 취소 버튼 위에서 마우스 업
        if(Util.checkCollisionForChildView(cancelBtn, copiedView)){
            closestView.setBackgroundColor(Color.parseColor("#ffffff"));
            closestView = null;
        }
        // 메인 달력 활성화 중 캘린더 칸 위에서 마우스 업
        if(centerIcon.getVisibility() == View.GONE &&
                closestView != null) {
            closestView.setBackgroundColor(Color.parseColor("#ffffff"));
            addScheduleForTheDate(String.valueOf(view.getTag()));
            refreshCalendar();
            closestView = null;
        }
        // 중앙 아이콘 컬러 복구( 메인모드시 )
        changeCenterIconColor(false);
        // 하단 버튼 복구
        changeBottomButton(false);
        // 카피된 아이콘 제거
        totalLayout.removeView(copiedView);
    }

    /**
     * 아이콘 버튼 드래그 중 이벤트
     * @param view
     * @param event
    */
    private void actionMoveEvent(View view, MotionEvent event) {
        // 복사된 뷰 표시 g
        if(copiedView.getVisibility() == View.GONE){
            copiedView.setAlpha(0.7f);
            copiedView.setVisibility(View.VISIBLE);
        }
        copiedView.setY(event.getRawY() + dY);
        copiedView.setX(event.getRawX() + dX);
        // 메인모드 처리
        if(centerIcon.getVisibility() == View.VISIBLE){
            boolean isCollided = Util.checkCollision(centerIcon, copiedView);
            changeCenterIconColor(isCollided);
        }
        // 캘린더모드 처리
        if(centerIcon.getVisibility() == View.GONE && checkCollisionForCalendarCell()) {
            changeCalendarCellColor(arroundViewGroup.get(findTheClosestView()));
        }
        // 하단 버튼 전환(뒤로가기 => X )
        changeBottomButton(true);
    }

    /**
     * 아이콘 버튼 드래그시 바텀버튼 전환
     */
    private void changeBottomButton(boolean isBackBtnVisible) {
        backBtn.setVisibility(isBackBtnVisible ? View.GONE : View.VISIBLE);
        cancelBtn.setVisibility(isBackBtnVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * 아이콘 버튼 터치 다운시 이벤트
     * @param view
     * @param event
     * @param buttonPanelId
     */
    private void actionDownEvent(View view, MotionEvent event, int buttonPanelId) {
        totalLayout.removeView(copiedView);
        hoverView(view);
        dX = view.getX() - event.getRawX();
        dY = 0;
        if(buttonPanelId == R.id.buttonPanel)
            dY = view.getY() - event.getRawY();
        else
            //TODO 일단 Y값을 고정값으로 맞춰주었지만 수정해야함(원인 불명)
            dY = view.getY() - event.getRawY() + 2000;
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

    /**
     * 지정 글꼴 리턴
     * @return
     */
    public Typeface getApplicationFont() {
        Typeface mTypeface = null;
        mTypeface = Typeface.createFromAsset(getAssets(), "nanumgothic.ttf");
        return mTypeface;
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
}
