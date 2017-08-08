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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.schedulemanager.calendar.CalendarPagerAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private HashMap<String, String> iconNameMap;    // 해당하는 텍스트에 매칭시키는 아이콘명이 저장되는 맵
    private View.OnTouchListener touchListener;     // 드래그 터치 리스너
    private float dX;                               // 드래그 시의 X좌표
    private float dY;                               // 드래그 시의 Y좌표
    private int lastAction;
    private View copiedView;                        // 드래그를 시작할 때 임시로 저장 해놓는 뷰
    private RelativeLayout totalLayout;             // 최상위 프레임 레이아웃
    private View centerIcon;                        // 중앙 아이콘 뷰

    private View calendarRow;                       // 임시 저장용 달력칸
    private Typeface typeface;                      // 글꼴
    private View calendarLayout;                    // 메인 캘린더 레이아웃
    private ViewPager calendarPager;                // 메인 캘린더 뷰 페이져 객체
    private HashMap<Integer, HashMap<Integer, Schedule>> scheduleMapByMonth;
                                                    // 달력월값을 키로 갖는 스케쥴 저장소

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
    }

    /**
     * 달력 셋팅, 초기화
     */
    private void initCalndar() {
        final CalendarPagerAdapter calendarAdapter = new CalendarPagerAdapter(this, typeface);

        // 달력 구성 어댑터 생성 및 셋팅
        calendarAdapter.initCalendar();
        calendarAdapter.notifyDataSetChanged();
        calendarPager = (ViewPager) findViewById(R.id.timetable_param_setter_calendar_viewpager);
        // 스케쥴 맵 전달
        calendarAdapter.setScheduleMapByMonth(scheduleMapByMonth);
        calendarPager.setAdapter(calendarAdapter);
        // 달력에 연도, 월 표시
        calendarPager.setCurrentItem(12);
        final TextView calendarDateText =  (TextView) findViewById(R.id.timetable_param_setter_calendar_date);
        calendarDateText.setTypeface(typeface);
        setCalendarTitleDate(calendarDateText, calendarAdapter, 12);
        // 이벤트 리스너 추가
        setPagingEvent(calendarDateText, calendarAdapter);
    }

    private void setPagingEvent(final TextView calendarDateText, final CalendarPagerAdapter calendarAdapter) {
        calendarPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setCalendarTitleDate(calendarDateText, calendarAdapter, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
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
        // nextCal을 같이 변경
        calendarAdapter.getNextCal().set(calendarAdapter.getThisCal().get(Calendar.YEAR),
                calendarAdapter.getThisCal().get(Calendar.MONTH) + position,
                calendarAdapter.getThisCal().get(Calendar.DATE)
        );
        calendarDateText.setText(calendarAdapter.getDateString(position));
    }

    private void initData() {
         DBHelper dbHelper = new DBHelper(this);
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

        // 전체 레이아웃 설정
        totalLayout = (RelativeLayout) findViewById(R.id.totalLayout);
        // 중앙의 스케쥴 아이콘
        centerIcon = findViewById(R.id.centerIcon);
        // 캘린더 레이아웃 설정
        calendarLayout = findViewById(R.id.calendarLayout);

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
                            totalLayout.removeView(copiedView);
                            hoverView(view);
                            dX = view.getX() - event.getRawX();
                            dY = 0;
                            if(buttonPanelId == R.id.buttonPanel)
                                dY = view.getY() - event.getRawY();
                            else
                            //TODO 일단 Y값을 고정값으로 맞춰주었지만 수정해야함(원인 불명)
                                dY = view.getY() - event.getRawY() + 2000;
                            break;

                        case MotionEvent.ACTION_MOVE:
                                if(copiedView.getVisibility() == View.GONE){
                                copiedView.setAlpha(0.7f);
                                copiedView.setVisibility(View.VISIBLE);
                            }
                            copiedView.setY(event.getRawY() + dY);
                            copiedView.setX(event.getRawX() + dX);
                            boolean isCollided = Util.checkCollision(centerIcon, copiedView);
                            changeCenterIconColor(isCollided);
                            break;

                        case MotionEvent.ACTION_UP:
//                            if(checkCollision(centerIcon, copiedView))

                            if(calendarRow != null) {
                                Log.d("calendarRow", "calendarRow X" + calendarRow.getX());
                                Log.d("calendarRow", "calendarRow Y" + calendarRow.getY());
                            }
//                            if(Util.checkCollision(centerIcon, copiedView)) addScheduleForToday(String.valueOf(view.getTag()));
                            if(Util.checkCollision(calendarRow, copiedView)) Log.d("calendarRowCollision!!!", "calendarRowCollision");
                            changeCenterIconColor(false);
                            totalLayout.removeView(copiedView);
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
     * 스케쥴 버튼을 드래그하여 중앙 아이콘으로 가져갔을때 추가
     * @param tagName
     */
    private void addScheduleForToday(String tagName) {
        // 오늘 날짜
        Date dateOfToday = new Date();
        DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = dateFormatter.format(dateOfToday);
        // 넘버
        int number = DBHelper.dbHelper.getScheduleCountForToday(dateString);
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
     * 지정 글꼴 리턴
     * @return
     */
    public Typeface getApplicationFont() {
        Typeface mTypeface = null;
        mTypeface = Typeface.createFromAsset(getAssets(), "nanumgothic.ttf");
        return mTypeface;
    }

    public void setCalendarRow(View calendarRow) {
        this.calendarRow = calendarRow;
    }
}
