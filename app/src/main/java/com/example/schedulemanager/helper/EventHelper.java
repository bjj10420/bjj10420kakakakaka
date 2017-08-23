package com.example.schedulemanager.helper;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.example.schedulemanager.GeneralCallback;
import com.example.schedulemanager.MainActivity;
import com.example.schedulemanager.R;
import com.example.schedulemanager.Util;
import com.example.schedulemanager.calendar.DialogHelper;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

/**
 * 모든 이벤트를 처리, 관리
 */
public class EventHelper {
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
     * 하루 스케쥴 이벤트 설정
     * @param chartId
     */
    private void setDailyScheduleEvent(int chartId) {

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(final Entry e, Highlight h) {
                // 다이얼로그 띄움
                new DialogHelper().setChoiceStyleDialog(MainActivity.this, new GeneralCallback() {
                    @Override
                    public void onCallBack() {
                        int index = dailyScheduleDataSet.getEntryIndex((PieEntry)e);
                        int orderValue = getOrderValueFromSchedule(index);
                        Log.d("orderValue값 체크", String.valueOf(orderValue));
                        new DBHelper(MainActivity.this).deleteSchedule(selectedDateData, orderValue);
                        dailyScheduleDataSet.removeEntry((PieEntry)e);
//                        reloadDailyScheduleData();
                        pieChart.notifyDataSetChanged();
                        pieChart.invalidate();
                        updateDailyScheduleMapByMonth(orderValue);
//                        scheduleMapByMonth.clear();
//                        dbHelper.selectAllSchedule(scheduleMapByMonth);
                    }
                });
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }
}
