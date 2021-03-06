package com.example.schedulemanager.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.schedulemanager.interface_.GeneralCallback;
import com.example.schedulemanager.R;
import com.example.schedulemanager.interface_.GeneralCallback2;
import com.example.schedulemanager.panel.etcpanel.ETCPanel;
import com.example.schedulemanager.panel.managerpanel.ManagerPanel;
import com.example.schedulemanager.vo.RectAndView;
import com.example.schedulemanager.vo.Schedule;
import com.example.schedulemanager.Util;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import static com.example.schedulemanager.helper.DataHelper.PICK_FROM_GALLARY;

/**
 * 모든 이벤트를 처리, 관리
 */
public class EventHelper {

    public static EventHelper eventHelper;
    private Context context;
    private DataHelper dataHelper;
    private UIHelper uiHelper;
    private CalendarHelper calendarHelper;
    private ETCPanel etcPanel;
    private ManagerPanel managerPanel;
    private AsyncTask<Integer, Integer, Void> loadManagerPanelTask;

    public void initEvent(Context context, DataHelper dataHelper, UIHelper uiHelper, CalendarHelper calendarHelper) {
        this.context = context;
        this.dataHelper = dataHelper;
        this.uiHelper = uiHelper;
        this.calendarHelper = calendarHelper;

        // 스테틱 저장
        eventHelper = this;
        // 패널 이벤트 설정
        setPanelEvents();
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

    public void setPanelEvents() {
        setDragEvents();
        setLongClickEvents();
    }

    private void setLongClickEvents() {
        setPanelLongClickEvent(R.id.buttonPanel);
        setPanelLongClickEvent(R.id.buttonPanel2);
    }

    private void setPanelLongClickEvent(int buttonPanelId) {
        LinearLayout buttonPanel = (LinearLayout) Util.getViewById(context, buttonPanelId);
        for(int i = 0; i < buttonPanel.getChildCount(); i++){
            View buttonView = buttonPanel.getChildAt(i);
            buttonView.setOnLongClickListener(getLongClickListener());
        }
    }

    public void setDragEvents() {
        setDragEvent(R.id.buttonPanel);
        setDragEvent(R.id.buttonPanel2);
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

                    if(isEtcButton(view)) {
                        actionEtcButtonClicked();
                        return false;
                    }

                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            actionDownEvent(view, event, buttonPanelId);
                            break;

                        case MotionEvent.ACTION_MOVE:
                            actionMoveEvent(event);
                            break;

                        case MotionEvent.ACTION_UP:
                            actionUpEvent(view);
                            break;

                        default:
                            return false;
                    }
                    return false;
                }
            });
        }
    }

    private void actionEtcButtonClicked() {
        Log.d("isEtcButton", "isEtcButton ");
        if(etcPanel == null){
            Util.customToast(context, "아이콘을 로딩 중입니다. 잠시만 기다려주심시오");
        }
        else {
            uiHelper.setTotalLayoutVisible(false);
            etcPanel.setEtcLayoutVisible(true);
//            if(loadManagerPanelTask == null)
//            loadManagerPanelTask = new TaskHelper(context).loadManagerPanel();
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
        uiHelper.hoverView(view, false);

        // dX설정
        float dY = 0;
        float dX = 0;

        int[] numberArray = new int[2];
        view.getLocationInWindow(numberArray);
        dY = numberArray[1] - event.getRawY();
        dX = numberArray[0] - event.getRawX();

        dataHelper.setdY(dY);
        dataHelper.setdX(dX);
    }

    private boolean isEtcButton(View view) {
            return String.valueOf(view.getTag()).equals("기타");
    }

    /**
     * 아이콘 버튼 드래그 중 이벤트
     * @param event
     */
    public void actionMoveEvent(MotionEvent event) {
        View copiedView = uiHelper.getCopiedView();
        setCopiedViewVisible(copiedView, event);
        actionMoveBasicEvent(copiedView, false);
        UIHelper.uiHelper.getTotalLayout().invalidate();
    }

    public void actionMoveBasicEvent(View copiedView, boolean isForEtcIcon) {
        // 메인모드 처리
        if(uiHelper.getCenterIcon().getVisibility() == View.VISIBLE) {
            if(isForEtcIcon) changeCenterIconColorWhenCollidedForEtcIcon(copiedView);
            else changeCenterIconColorWhenCollidedForEtcIcon(copiedView);
        }
        // 캘린더모드 처리
        if (uiHelper.getCenterIcon().getVisibility() == View.GONE
                && calendarHelper.checkCollisionForCalendarCellByRectZone()
                && uiHelper.getScheduleLayout().getVisibility() == View.GONE)
            changeCalendarCellColorWhenCollided();
    }

    private void changeCalendarCellColorWhenCollided() {
        if(isMultimode() == false)
            singleMode();
        else
            multiMode();
    }

    private void multiMode() {
        calendarHelper.changeCalendarMultiCellColor(dataHelper.getArroundViewGroup().get(calendarHelper.findTheClosestView()));
    }

    private boolean isMultimode() {
        return  dataHelper.getMode() != 1;
    }

    private void singleMode() {
        calendarHelper.changeCalendarCellColor(dataHelper.getArroundViewGroup().get(calendarHelper.findTheClosestView()));
    }

    // 기타패널 아이콘 충돌시 사용
    private void changeCenterIconColorWhenCollidedForEtcIcon(View copiedView) {
        boolean isCollided = Util.checkCollisionForEtcIcon(uiHelper.getCenterIcon(), copiedView);
        uiHelper.changeCenterIconColor(isCollided);
    }

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
     * @param touchedView
     */
    public void actionUpEvent(View touchedView) {
        View centerIcon = uiHelper.getCenterIcon();
        View closestView = uiHelper.getClosestView();
        View copiedView = uiHelper.getCopiedView();
        boolean isCanceled = false;

        actionWhenCenterIconActivated(centerIcon, copiedView, touchedView);
        isCanceled = actionAtCancelBtnWhenCalendarActivated(copiedView, closestView);
        isCanceled = checkPosition(copiedView);
        actionAtCalendarCellWhenCalendarActivated(centerIcon, closestView, touchedView, isCanceled);
        actionAtDailyScheduleLayout(centerIcon, closestView, copiedView, touchedView);
        resetAfterMouseUp();
    }

    private boolean checkPosition(View copiedView) {
        boolean isInCalendarViewMap = false;
        TreeMap<Integer, RectAndView> currentMap = dataHelper.getRectZoneWithViewSorted();
        if(currentMap == null) return false;
        for(Integer key : currentMap.keySet()){
            if(currentMap.get(key).getView() != null &&
                    Util.checkCollisionForChildView2(copiedView, currentMap.get(key).getView())){
                isInCalendarViewMap = true;
                break;
            }
        }
        return  isInCalendarViewMap ? false : true;
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
    @SuppressLint("LongLogTag")
    private void actionAtDailyScheduleLayout(View centerIcon, View closestView, View copiedView, View view) {
        if(centerIcon.getVisibility() == View.GONE &&
                uiHelper.getScheduleLayout().getVisibility() == View.VISIBLE
                ) {
            String activityName = String.valueOf(view.getTag());
            // DB에 추가
            addScheduleForTheSchedule(activityName);
            // 현재 보고 있는 스케쥴 챠트에서 추가 (추가전 엔트리 카운터 리턴)
            int entryCount = addScheduleToPieChart(activityName);

            // 자료구조에도 추가
            int newOrder = 0;
            if(dataHelper.getDailyScheduleMap() != null)
                if(dataHelper.getDailyScheduleMap().size() != 0)
                    newOrder = dataHelper.getMaxOrderAmongDailyScheduleMap() + 1;
            dataHelper.addToDataStructure(newOrder,activityName);
            uiHelper.resetPiechart(uiHelper.getPieChart());

            // 전환이 필요한경우 (추가가 된후 달력뷰에서도 체크마크표시를 해줘야한다)
            if(entryCount == 0) {
                uiHelper.setNoDateText(false);
                calendarHelper.setCheckMark(true, uiHelper.getSelectedCalendarCellView());
            }
            }
    }

    public void resetPieChartDataSet(PieDataSet pieDataSet, int fillValue) {
        List<PieEntry> pieEntryList = pieDataSet.getValues();

        Iterator it = pieEntryList.iterator();
        while(it.hasNext()) {
            PieEntry pieEntry = (PieEntry) it.next();
            pieEntry.setY(fillValue);
        }
    }

    private int addScheduleToPieChart(String s) {
        PieDataSet pieDataSet = dataHelper.getDailyScheduleDataSet();
        int entryCount = dataHelper.getDailyScheduleDataSet().getEntryCount();
        int fillValue = 100 / (entryCount != -1 ? entryCount + 1 : 100);
        pieDataSet.addEntry(new PieEntry(fillValue, s));
        // 데이터 값 재정의
        resetPieChartDataSet(pieDataSet, fillValue);
        return entryCount;
    }

    // 메인 달력 활성화 중 캘린더 칸 위에서 마우스 업
    private void actionAtCalendarCellWhenCalendarActivated(View centerIcon, View closestView, View touchedView, boolean isCanceled) {
        if(centerIcon.getVisibility() == View.GONE &&
                uiHelper.getScheduleLayout().getVisibility() == View.GONE &&
                closestView != null && !isCanceled) {
            if(isMultimode() == false)
                singleModeUp(closestView, touchedView);
            else
                multiModeUp(closestView, touchedView);
        }
    }

    private void multiModeUp(View closestView, View touchedView) {
        resotreMultiModeView(closestView, touchedView);
//        restoreModeVar();
     }

    private void restoreModeVar() {
        dataHelper.setMode(1);
    }

    private void resotreMultiModeView(View closestView, View touchedView) {
        singleModeUp(closestView, touchedView);
        for(View multiModeView : dataHelper.getMultiModeViews()){
            singleModeUp(multiModeView, touchedView);
        }
    }

    private void singleModeUp(View closestView, View touchedView) {
        restoreClosestViewColor(closestView);
        addTransition(closestView, touchedView);
    }

    private void addTransition(View closestView, View touchedView) {
        setDataHelperDateValue((String) closestView.getTag());
        addToDB(String.valueOf(touchedView.getTag()), dataHelper.getDateValue());
        addToDataStructrue((String) touchedView.getTag(), dataHelper.getSelectedDateData());
        addToCalendar(closestView);
    }

    private void addToCalendar(View closestView) {
          calendarHelper.setCheckMark(true, closestView);
    }

    private void restoreClosestViewColor(View closestView) {
        Log.d("restoreClosestViewColor 체크", "restoreClosetViewColor 체크");
        if(calendarHelper.isToday(closestView))
            closestView.setBackgroundResource(R.drawable.calendar_item_selected_bg);
        else
            closestView.setBackgroundColor(Color.parseColor("#ffffff"));
    }

    private void setDataHelperDateValue(String dataValueTag) {
        String dateValue = dataValueTag;
        dataHelper.setDateValue(dateValue);
        dataHelper.makeSelectedDateData(dateValue);
    }

    private void setDataHelperTodayDateValue(String dataValueTag, String yearMonthKey) {
        String dateValue = dataValueTag;
        dataHelper.setDateValue(dateValue);
        dataHelper.makeSelectedTodayDateData(dateValue, yearMonthKey);
    }

    // 자료구조에 추가
    private void addToDataStructrue(String activityName, String selectedDateData) {
        int newOrder = 0;
        if(!dataHelper.isEmptyData(selectedDateData))
            newOrder = dataHelper.getMaxOrderAmongScheduleMapByThisMonth(selectedDateData) + 1;
        dataHelper.addToDataStructure(newOrder,activityName);
    }

    // 오늘날짜 자료구조에 추가
    private void addToTodayDataStructrue(String activityName, String selectedDateData, String yearMonthKey) {
        int newOrder = 0;
        if(!dataHelper.isTodayEmptyData(selectedDateData, yearMonthKey))
            newOrder = dataHelper.getMaxOrderAmongScheduleMapByThisMonthForToday(selectedDateData, yearMonthKey) + 1;
        dataHelper.addToTodayDataStructure(newOrder,activityName, yearMonthKey);
    }

    // 메인 달력 활성화 중 취소 버튼 위에서 마우스 업
    private boolean actionAtCancelBtnWhenCalendarActivated(View copiedView, View closestView) {
        boolean isCanceled = false;

        if(Util.checkCollisionForChildView(uiHelper.getCancelBtn(), copiedView)){
            isCanceled = true;
            restoreClosestViewColor(closestView);
         }
        return isCanceled;
    }

    // 중앙 아이콘 활성화인 경우
    private void actionWhenCenterIconActivated(View centerIcon, View copiedView, View view) {
        if(centerIcon.getVisibility() == View.VISIBLE &&
                Util.checkCollisionForEtcIcon(centerIcon, copiedView)) {
            addScheduleForToday(String.valueOf(view.getTag()));
        }
    }

    private void setCenterIconClickEvent() {
        uiHelper.getCenterIcon().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiHelper.setCalendarLayoutVisible(v);
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
                // 삭제 버튼 클릭
                new DialogHelper().setDailyScheduleDialog(context, new GeneralCallback() {
                    @Override
                    public void onCallBack() {
                        deleteSchedule(e);
                    }
                }, new GeneralCallback() {
                    @Override
                    public void onCallBack() {
                        // 메모첨부 버튼 클릭
                        new DialogHelper().setMessageDialog(context, new GeneralCallback2() {
                            @Override
                            public void onCallBack(Object parameter) {
                                updateMemo(parameter, e);
                            }
                        }, null);
                    }
                }, new GeneralCallback2() {
                    @Override
                    public void onCallBack(Object parameter) {
                        // 변경 버튼
                        modifySchedule(e);
                    }
                }, "메모 첨부", "삭제");
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void modifySchedule(Entry e) {
        PieEntry pieEntry = ((PieEntry) e);
        int index = dataHelper.getDailyScheduleDataSet().getEntryIndex(pieEntry);
        int orderValue = dataHelper.getOrderValueFromSchedule(index);
        Schedule originalSchedule = dataHelper.getScheduleFromDailyScheduleMapByMonth2(index);
        new DialogHelper().showScheduleInfoDialog(context, originalSchedule, pieEntry, orderValue);
    }

    public void modifyConfirm(View v, PieEntry pieEntry, Schedule originalSchedule, int orderValue){
        String nameModified = ((TextView) v.findViewById(R.id.scheduleModificationName)).getText().toString();
        String memoModified = ((TextView) v.findViewById(R.id.scheduleModificationMemo)).getText().toString();

        originalSchedule.setActivityName(nameModified);
        originalSchedule.setMemo(memoModified);
        uiHelper.updatePiechart(memoModified, pieEntry, nameModified);
        uiHelper.resetPiechart(uiHelper.getPieChart());
        dataHelper.getDbHelper().updateSchedule(nameModified, memoModified, dataHelper.getSelectedDateData(), orderValue);

    }

    /**
     * 메모를 업데이트
     * @param parameter
     * @param e
     */
    private void updateMemo(Object parameter, Entry e) {
        String memoContent = "";
        String memo = String.valueOf(parameter);
        PieEntry pieEntry = ((PieEntry) e);
        int index = dataHelper.getDailyScheduleDataSet().getEntryIndex(pieEntry);
        int orderValue = dataHelper.getOrderValueFromSchedule(index);
        Schedule originalSchedule = dataHelper.getScheduleFromDailyScheduleMapByMonth2(index);
        String originalScheduleMemo = originalSchedule != null ? originalSchedule.getMemo() : "";

        // 원본이 있는경우
        if(originalScheduleMemo != null && !originalScheduleMemo.equals(""))
            memoContent = originalScheduleMemo + ", " + memo;
        else
            memoContent = memo;

        // DB업데이트
        int result = dataHelper.getDbHelper().updateMemo(memoContent, e, dataHelper.getSelectedDateData(), orderValue);
        Log.d("업데이트메모 결과값 체크 = ", String.valueOf(result) + ", " + orderValue);
        // 필드맵에 추가되어있는 스케쥴변경
        originalSchedule.setMemo(memoContent);
        // 파이챠트 변경//
        uiHelper.updatePiechart(memoContent, pieEntry,originalSchedule.getActivityName());
        uiHelper.resetPiechart(uiHelper.getPieChart());
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

        // DB에서 삭제
        new DBHelper(context).deleteSchedule(dataHelper.getSelectedDateData(), orderValue);
        // 현재 보는 스케쥴 챠트에서 삭제
        dailyScheduleDataSet.removeEntry((PieEntry) e);
        uiHelper.resetPiechart(pieChart);
        // 자료구조에서 삭제
        dataHelper.removeFromDailyScheduleMapByMonth(orderValue);
        // 데이터가 하나도 없을때는 처리
        if (dailyScheduleDataSet.getEntryCount() == 0) {
            uiHelper.setNoDateText(true);
            calendarHelper.setCheckMark(false, uiHelper.getSelectedCalendarCellView());
        }
    }

    public void onBackPresssed(){
        View scheduleLayout = uiHelper.getScheduleLayout();
        View calendarLayout = uiHelper.getCalendarLayout();
        View etcLayout = null;
        View managerLayout = null;

        if(etcPanel != null)
        etcLayout = etcPanel.getEtcLayout();
        if(managerPanel != null)
        managerLayout = managerPanel.getManagerLayout();

        if(managerLayout != null && managerLayout.getVisibility() == View.VISIBLE) {
            managerPanel.getManagerPanelEvent().managerCloseBtnEvent();
        }
        else if(etcLayout != null && etcLayout.getVisibility() == View.VISIBLE) {
            etcPanel.setEtcLayoutVisible(false);
            uiHelper.setTotalLayoutVisible(true);
        }
        else if(scheduleLayout.getVisibility() == View.VISIBLE) {
            scheduleLayout.setVisibility(View.GONE);
            calendarLayout.setVisibility(View.VISIBLE);
        }
        else if(calendarLayout.getVisibility() == View.VISIBLE) {
            calendarLayout.setVisibility(View.GONE);
            uiHelper.getCenterIcon().setVisibility(View.VISIBLE);
        }
        else
            finishActivity();
    }

    private void finishActivity() {
        ((Activity)context).finish();
    }

    /**
     * 스케쥴 버튼을 드래그하여 하루 일정 스케쥴로 가져갔을때 추가
     * @param tagName
     */
    public void addScheduleForTheSchedule(String tagName) {
        Log.d("선택된 데이트 데이터", dataHelper.getSelectedDateData());
        addScheduleForDiailyScheduleMode(dataHelper.getSelectedDateData(), tagName);
    }

    /**
     * 스케쥴 버튼을 드래그하여 메인 캘린더의 한칸으로 가져갔을때 추가
     * @param tagName
     */
    public void addToDB(String tagName, String dateValue) {
        String dateString = calendarHelper.dataHelper.makeDateString2(dateValue, calendarHelper);
        Log.d("스케쥴 추가 체크", dateString + ", " + tagName);
        addScheduleIntoDB(dateString, tagName);
    }

    /**
     * 데이트 스트링과 태그네임으로 DB에 스케쥴 추가(스케쥴일정화면)
     * @param dateString
     * @param tagName
     */
    private void addScheduleForDiailyScheduleMode(String dateString, String tagName) {
        // 이름
        String activityName = tagName;
        // 넘버
        int number = 0;
        if(dataHelper.getDailyScheduleMap() != null)
            if(dataHelper.getDailyScheduleMap().size() != 0)
                number = dataHelper.getMaxOrderAmongDailyScheduleMap() + 1;
        // 삽입할 스케쥴 데이터 객체 생성
        Schedule newSchedule = makeNewSchedule(number, dateString, activityName);
        // DB에 삽입
        long resultNum = DBHelper.dbHelper.insertSchedule(newSchedule);
    }

    /**
     * 데이트 스트링과 태그네임으로 DB에 스케쥴 추가(달력한칸에서 사용)
     * @param dateString
     * @param tagName
     */
    private void addScheduleIntoDB(String dateString, String tagName) {
        // 이름
        String activityName = tagName;

        int number = 0;
        Log.d("스케쥴DB에추가 체크", "dateString = " + dateString);
        if(!dataHelper.isEmptyData(dateString))
        number = dataHelper.getMaxOrderAmongScheduleMapByThisMonth(dateString) + 1;
        // 삽입할 스케쥴 데이터 객체 생성
        Schedule newSchedule = makeNewSchedule(number, dateString, activityName);
        // DB에 삽입
        long resultNum = DBHelper.dbHelper.insertSchedule(newSchedule);
    }

    private void addScheduleIntoTodayDB(String dateString, String tagName) {
        // 이름
        String activityName = tagName;

        int number = 0;
        Log.d("스케쥴DB에추가 체크", "dateString = " + dateString);
        if(!dataHelper.isTodayEmptyData(dateString, dateString.substring(0,6)))
            number = dataHelper.getMaxOrderAmongScheduleMapByThisMonthForToday(dateString, dateString.substring(0,6)) + 1;
        Schedule newSchedule = makeNewSchedule(number, dateString, activityName);
        long resultNum = DBHelper.dbHelper.insertSchedule(newSchedule);
    }

    private Schedule makeNewSchedule(int number, String dateString, String activityName) {
        // 삽입할 스케쥴 데이터 객체 생성
        Schedule newSchedule = new Schedule();

        newSchedule.setDate(dateString);
        newSchedule.setActivityName(activityName);
        //TODO 나머지 order, time, memo는 일단 공란
        newSchedule.setOrder(number);
        newSchedule.setTime("");
        newSchedule.setMemo("");
        return newSchedule;
    }

    public CalendarHelper getCalendarHelper() {
        return calendarHelper;
    }

    /**
     * 스케쥴 버튼을 드래그하여 중앙 아이콘으로 가져갔을때 추가
     * @param tagName
     */
    public void addScheduleForToday(String tagName) {
        // 오늘 날짜
        Date dateOfToday = new Date();
        DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = dateFormatter.format(dateOfToday);
        int dateNumber = dateOfToday.getDate();
        View viewOfToday = UIHelper.uiHelper.getViewOfToday();

        setDataHelperTodayDateValue(String.valueOf(dateNumber), dateString.substring(0,6));
        addScheduleIntoTodayDB(dateString, tagName);
        addToTodayDataStructrue(tagName, dateString, dateString.substring(0, 6));
        if(isCalendarExist())
        addToCalendar(viewOfToday);
    }

    private boolean isCalendarExist() {
        if(dataHelper.getCurrentCalendarViewMap().get(Integer.parseInt(dataHelper.getDateValue())) == null)
            return false;
        else
            return true;
    }

    public UIHelper getUiHelper() {
        return uiHelper;
    }

    public ETCPanel getEtcPanel() {
        return etcPanel;
    }

    public void setEtcPanel(ETCPanel etcPanel) {
        this.etcPanel = etcPanel;
    }

    public void setManagerPanel(ManagerPanel managerPanel) {
        this.managerPanel = managerPanel;
    }

    public ManagerPanel getManagerPanel() {
        return managerPanel;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case PICK_FROM_GALLARY:
                if (resultCode == Activity.RESULT_OK) {
                    Uri mImageCaptureUri = data.getData();
                    boolean isSuccess = Util.copyFileFromUri(context, mImageCaptureUri);
                    Log.d("uri", String.valueOf(mImageCaptureUri));
                    Log.d("copyFileFromUri", String.valueOf("isSuccess = " + isSuccess));

                }
                break;
        }
    }

    private int setDefaultIndex() {
        int defaultIndex = 0;
        int mode = dataHelper.getMode();
        switch (mode) {
            case 1 : defaultIndex = 0; break;
            case 3 : defaultIndex = 1; break;
            case 5 : defaultIndex = 2; break;
            case 7 : defaultIndex = 3; break;
            case 10 : defaultIndex = 4; break;
        }
        return defaultIndex;
    }

    public View.OnLongClickListener getLongClickListener() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final int defaultIndex = setDefaultIndex();
                final AlertDialog.Builder builder = new AlertDialog.Builder(context, 5);
                builder.setTitle("기간 지정");

                final CharSequence[] modeArray = {"1일", "3일", "5일", "7일", "10일"};
                builder.setSingleChoiceItems(modeArray, defaultIndex,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                switch (which){
                                    case 0 : dataHelper.setMode(1);
                                        break;
                                    case 1 : dataHelper.setMode(3);
                                        break;
                                    case 2 : dataHelper.setMode(5);
                                        break;
                                    case 3 : dataHelper.setMode(7);
                                        break;
                                    case 4 : dataHelper.setMode(10);
                                        break;
                                }
                            }
                        });
                builder.show();
                return false;
            }
        };
    }
}
