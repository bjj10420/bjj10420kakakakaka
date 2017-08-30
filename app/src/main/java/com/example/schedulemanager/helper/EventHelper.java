package com.example.schedulemanager.helper;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.example.schedulemanager.interface_.GeneralCallback;
import com.example.schedulemanager.R;
import com.example.schedulemanager.interface_.GeneralCallback2;
import com.example.schedulemanager.vo.Schedule;
import com.example.schedulemanager.Util;
import com.example.schedulemanager.calendar.DialogHelper;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

/**
 * 모든 이벤트를 처리, 관리
 */
public class EventHelper {

    public static EventHelper eventHelper;
    private Context context;
    private DataHelper dataHelper;
    private UIHelper uiHelper;
    private CalendarHelper calendarHelper;

    public void initEvent(Context context, DataHelper dataHelper, UIHelper uiHelper, CalendarHelper calendarHelper) {
        this.context = context;
        this.dataHelper = dataHelper;
        this.uiHelper = uiHelper;
        this.calendarHelper = calendarHelper;

        // 스테틱 저장
        eventHelper = this;
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
        setDailyScheduleClickEvent();
    }

    /**
     * 해당 패널내의 자식 아이콘들의 드래그 이벤트를 설정
     * @param buttonPanelId
     */
    private void setDragEvent(final int buttonPanelId) {
        LinearLayout buttonPanel = (LinearLayout) Util.getViewById(context, buttonPanelId);

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
        uiHelper.getTotalLayout().removeView(uiHelper.getCopiedView());
        uiHelper.hoverView(view);

        // dX설정
        dataHelper.setdX(view.getX() - event.getRawX());
        float dY = 0;
        if(buttonPanelId == R.id.buttonPanel)
            dY = view.getY() - event.getRawY();
        else
            //TODO 일단 Y값을 고정값으로 맞춰주었지만 수정해야함(원인 불명)
            dY = view.getY() - event.getRawY() + 2000;
        // dY설정
        dataHelper.setdY(dY);
    }

    /**
     * 아이콘 버튼 드래그 중 이벤트
     * @param view
     * @param event
     */
    private void actionMoveEvent(View view, MotionEvent event) {
        View copiedView = uiHelper.getCopiedView();
        setCopiedViewVisible(copiedView, event);
        // 메인모드 처리
        if(uiHelper.getCenterIcon().getVisibility() == View.VISIBLE)
            changeCenterIconColorWhenCollided(copiedView);
        // 캘린더모드 처리
        if(uiHelper.getCenterIcon().getVisibility() == View.GONE &&
                calendarHelper.checkCollisionForCalendarCell())
            changeCalendarCellColorWhenCollided();
        // 하단 버튼 전환(뒤로가기 => X )
        uiHelper.changeBottomButton(true);
    }

    private void changeCalendarCellColorWhenCollided() {
        calendarHelper.changeCalendarCellColor(dataHelper.getArroundViewGroup().get(calendarHelper.findTheClosestView()));
//        dataHelper.setDateValue(String.valueOf(uiHelper.getClosestView().getTag()));
    }

    private void changeCenterIconColorWhenCollided(View copiedView) {
        boolean isCollided = Util.checkCollision(uiHelper.getCenterIcon(), copiedView);
        uiHelper.changeCenterIconColor(isCollided);
    }

    /**
     * 복사된 뷰 표시
     * @param copiedView
     * @param event
     */
    private void setCopiedViewVisible(View copiedView, MotionEvent event) {
        if(copiedView.getVisibility() == View.GONE){
            copiedView.setAlpha(0.7f);
            copiedView.setVisibility(View.VISIBLE);
        }
        copiedView.setY(event.getRawY() + dataHelper.getdY());
        copiedView.setX(event.getRawX() + dataHelper.getdX());
    }

    /**
     * 아이콘 버튼 드래그 중 업할때 이벤트
     * @param view
     */
    private void actionUpEvent(View view) {
        View centerIcon = uiHelper.getCenterIcon();
        View closestView = uiHelper.getClosestView();
        View copiedView = uiHelper.getCopiedView();

        actionWhenCenterIconActivated(centerIcon, copiedView, view);
        actionAtCancelBtnWhenCalendarActivated(copiedView, closestView);
        actionAtCalendarCellWhenCalendarActivated(centerIcon, closestView, copiedView, view);
        actionAtDailyScheduleLayout(centerIcon, closestView, copiedView, view);
        resetAfterMouseUp();
    }

    /**
     * 스케쥴 버튼을 드래그 한후 복구
     */
    private void resetAfterMouseUp() {
        // 중앙 아이콘 컬러 복구( 메인모드시 )
        uiHelper.changeCenterIconColor(false);
        // 하단 버튼 복구
        uiHelper.changeBottomButton(false);
        // 카피된 아이콘 제거
        uiHelper.getTotalLayout().removeView(uiHelper.getCopiedView());
    }

    // 데일리 스케쥴 위에서 마우스 업
    private void actionAtDailyScheduleLayout(View centerIcon, View closestView, View copiedView, View view) {
        if(centerIcon.getVisibility() == View.GONE &&
                uiHelper.getScheduleLayout().getVisibility() == View.VISIBLE &&
                closestView != null) {
            String activityName = String.valueOf(view.getTag());
            // DB에 추가
            addScheduleForTheSchedule(activityName);
            // 현재 보고 있는 스케쥴 챠트에서 추가 (추가전 엔트리 카운터 리턴)
            int entryCount = addScheduleToPieChart(activityName);
            // 자료구조에도 추가
            dataHelper.addToDailyScheduleMapByMonth(DBHelper.dbHelper.getScheduleCountForDate(dataHelper.getSelectedDateData()),activityName);
            uiHelper.pieChartReset(uiHelper.getPieChart());
            // 전환이 필요한경우
            if(entryCount == -1)
            uiHelper.setNoDateText(false);
        }
    }

    private int addScheduleToPieChart(String s) {
        int entryCount = dataHelper.getDailyScheduleDataSet().getEntryCount();
        int fillValue = 100 / (entryCount != -1 ? entryCount + 1 : 100);
        dataHelper.getDailyScheduleDataSet().addEntry(new PieEntry(fillValue, s));
        return entryCount;
    }

    // 메인 달력 활성화 중 캘린더 칸 위에서 마우스 업
    private void actionAtCalendarCellWhenCalendarActivated(View centerIcon, View closestView, View copiedView, View view) {
        if(centerIcon.getVisibility() == View.GONE &&
                uiHelper.getScheduleLayout().getVisibility() == View.GONE &&
                closestView != null) {
            Log.d("메인 캘린더 셀 액션", "메인 캘린더 셀 액션");
            closestView.setBackgroundColor(Color.parseColor("#ffffff"));
            String dateValue = String.valueOf(closestView.getTag());
            // TODO 처음 앱을 설치하고 일정을 스케쥴했을때 DATE값이 없으므로 여기서 저장
            dataHelper.setDateValue(dateValue);
            addScheduleForTheDate(String.valueOf(view.getTag()));
            calendarHelper.refreshCalendar();
            closestView = null;
        }
    }

    // 메인 달력 활성화 중 취소 버튼 위에서 마우스 업
    private void actionAtCancelBtnWhenCalendarActivated(View copiedView, View closestView) {
        if(Util.checkCollisionForChildView(uiHelper.getCancelBtn(), copiedView)){
            closestView.setBackgroundColor(Color.parseColor("#ffffff"));
            closestView = null;
        }

    }

    // 중앙 아이콘 활성화인 경우
    private void actionWhenCenterIconActivated(View centerIcon, View copiedView, View view) {
        if(centerIcon.getVisibility() == View.VISIBLE &&
                Util.checkCollision(centerIcon, copiedView)) {
            calendarHelper.addScheduleForToday(String.valueOf(view.getTag()));
        }
    }

    private void setCenterIconClickEvent() {
        uiHelper.getCenterIcon().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiHelper.getCalendarLayout().setVisibility(View.VISIBLE);
                // 중앙 아이콘 비표시
                v.setVisibility(View.GONE);
            }
        });
    }

    private void setBackBtnClickEvent() {
        View backBtn = uiHelper.getBackBtn();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 중앙 뷰 복구
                uiHelper.getCenterIcon().setVisibility(View.VISIBLE);
                uiHelper.getCalendarLayout().setVisibility(View.GONE);
            }
        });
    }

    private void setCalendarBtnEvent(int viewId) {
        // 달력 내 사용 버튼
        View calendarBtn = Util.getViewById(context, viewId);
        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarHelper.pageCalendar(v);
            }
        });
    }

    /**
     * 하루 스케쥴 클릭 이벤트 설정
     */
    private void setDailyScheduleClickEvent() {

        uiHelper.getPieChart().setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(final Entry e, Highlight h) {
                // 다이얼로그 띄움
                new DialogHelper().setChoiceStyleDialog(context, new GeneralCallback() {
                    @Override
                    public void onCallBack() {
                        deleteSchedule(e);
                    }
                }, new GeneralCallback() {
                    @Override
                    public void onCallBack() {
                        new DialogHelper().setMessageDialog(context, new GeneralCallback2() {


                            @Override
                            public void onCallBack(Object parameter) {
                                DBHelper.updateMemo(parameter);
                            }
                        }, null);
                    }
                });
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    /**
     * 삭제 버튼이 눌렸을때 삭제
     * @param e
     */
    private void deleteSchedule(Entry e) {
        PieDataSet dailyScheduleDataSet = dataHelper.getDailyScheduleDataSet();
        int index = dailyScheduleDataSet.getEntryIndex((PieEntry) e);
        int orderValue = dataHelper.getOrderValueFromSchedule(index);
        PieChart pieChart = uiHelper.getPieChart();
//
        // DB에서 삭제
        new DBHelper(context).deleteSchedule(dataHelper.getSelectedDateData(), orderValue);
        // 현재 보는 스케쥴 챠트에서 삭제
        dailyScheduleDataSet.removeEntry((PieEntry) e);
        uiHelper.pieChartReset(pieChart);
        // 자료구조에서 삭제
        dataHelper.removeFromDailyScheduleMapByMonth(orderValue);
        // 데이터가 하나도 없을때는 처리
        if (dailyScheduleDataSet.getEntryCount() == 0) {
            uiHelper.setNoDateText(true);
            calendarHelper.setCheckMark(false);
        }
    }


    public void onBackPresssed(){
        View scheduleLayout = uiHelper.getScheduleLayout();
        View calendarLayout = uiHelper.getCalendarLayout();

        // 하루 일정화면이 ON이면 닫아준다
        if(scheduleLayout.getVisibility() == View.VISIBLE) {
            scheduleLayout.setVisibility(View.GONE);
            calendarLayout.setVisibility(View.VISIBLE);
        }
        // 캘린더화면이 ON이면 닫아준다
        else if(calendarLayout.getVisibility() == View.VISIBLE) {
            calendarLayout.setVisibility(View.GONE);
            uiHelper.getCenterIcon().setVisibility(View.VISIBLE);
        }
    }

    /**
     * 스케쥴 버튼을 드래그하여 하루 일정 스케쥴로 가져갔을때 추가
     * @param tagName
     */
    public void addScheduleForTheSchedule(String tagName) {
        Log.d("선택된 데이트 데이터", dataHelper.getSelectedDateData());
        addScheduleByDateString(dataHelper.getSelectedDateData(), tagName);
    }

    /**
     * 스케쥴 버튼을 드래그하여 메인 캘린더의 한칸으로 가져갔을때 추가
     * @param tagName
     */
    public void addScheduleForTheDate(String tagName) {
        String dateString = calendarHelper.makeDateString(dataHelper.getDateValue());
        addScheduleByDateString(dateString, tagName);
    }

    /**
     * 데이트 스트링과 태그네임으로 DB에 스케쥴 추가
     * @param dateString
     * @param tagName
     */
    private void addScheduleByDateString(String dateString, String tagName) {
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

    }

    public CalendarHelper getCalendarHelper() {
        return calendarHelper;
    }
}
