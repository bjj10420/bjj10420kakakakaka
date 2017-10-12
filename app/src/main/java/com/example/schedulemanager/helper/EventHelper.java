package com.example.schedulemanager.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.example.schedulemanager.interface_.GeneralCallback;
import com.example.schedulemanager.R;
import com.example.schedulemanager.interface_.GeneralCallback2;
import com.example.schedulemanager.panel.etcpanel.ETCPanel;
import com.example.schedulemanager.panel.managerpanel.ManagerPanel;
import com.example.schedulemanager.vo.Schedule;
import com.example.schedulemanager.Util;
import com.example.schedulemanager.calendar.DialogHelper;
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

                    if(isEtcButton(view)) {
                        actionEtcButtonClicked();
                        return true;
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
                    return true;
                }
            });
        }
    }

    private void actionEtcButtonClicked() {
        Log.d("isEtcButton", "isEtcButton ");
        uiHelper.setTotalLayoutVisible(false);
        etcPanel.setEtcLayoutVisible(true);
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
//        dataHelper.setdX(view.getX() - event.getRawX());
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
//
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
        // 하단 버튼 전환(뒤로가기 => X )
        uiHelper.changeBottomButton(true);
    }

    private void changeCalendarCellColorWhenCollided() {
        calendarHelper.changeCalendarCellColor(dataHelper.getArroundViewGroup().get(calendarHelper.findTheClosestView()));
//        dataHelper.setDateValue(String.valueOf(uiHelper.getClosestView().getTag()));
    }

    private void changeCenterIconColorWhenCollided(View copiedView) {
        boolean isCollided = Util.checkCollision(uiHelper.getCenterIcon(), copiedView);
        Log.d("changeCenterIconColorWhenCollided 테스트", "changeCenterIconColorWhenCollided 테스트 isCollided = " + isCollided);
        uiHelper.changeCenterIconColor(isCollided);
    }

    // 기타패널 아이콘 충돌시 사용
    private void changeCenterIconColorWhenCollidedForEtcIcon(View copiedView) {
        boolean isCollided = Util.checkCollisionForEtcIcon(uiHelper.getCenterIcon(), copiedView);
        Log.d("changeCenterIconColorWhenCollided 테스트", "changeCenterIconColorWhenCollided 테스트 isCollided = " + isCollided);
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
    public void actionUpEvent(View view) {
        View centerIcon = uiHelper.getCenterIcon();
        View closestView = uiHelper.getClosestView();
        View copiedView = uiHelper.getCopiedView();
        boolean isCanceled = false;

        actionWhenCenterIconActivated(centerIcon, copiedView, view);
        isCanceled = actionAtCancelBtnWhenCalendarActivated(copiedView, closestView);
        actionAtCalendarCellWhenCalendarActivated(centerIcon, closestView, copiedView, view, isCanceled);
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

            Log.d("actionAtDailyScheduleLayout 체크", "actionAtDailyScheduleLayout 체크 entryCount = " + entryCount);

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
    private void actionAtCalendarCellWhenCalendarActivated(View centerIcon, View closestView, View copiedView, View view, boolean isCanceled) {
        if(centerIcon.getVisibility() == View.GONE &&
                uiHelper.getScheduleLayout().getVisibility() == View.GONE &&
                closestView != null && !isCanceled) {
            setClosestView(closestView);
            setDataHelperDateValue((String) closestView.getTag());
            addToDB(String.valueOf(view.getTag()), dataHelper.getDateValue());
            addToDataStructrue((String) view.getTag(), dataHelper.getSelectedDateData());
            addToCalendar(closestView);
        }
    }

    private void addToCalendar(View closestView) {
          calendarHelper.setCheckMark(true, closestView);
    }

    private void setClosestView(View closestView) {
        Log.d("메인 캘린더 셀 액션", "메인 캘린더 셀 액션");
        closestView.setBackgroundColor(Color.parseColor("#ffffff"));
    }

    private void setDataHelperDateValue(String dataValueTag) {
        String dateValue = dataValueTag;
        // TODO 처음 앱을 설치하고 일정을 스케쥴했을때 DATE값이 없으므로 여기서 저장
        dataHelper.setDateValue(dateValue);
        dataHelper.makeSelectedDateData(dateValue);
    }

    // 자료구조에 추가
    private void addToDataStructrue(String activityName, String selectedDateData) {
        int newOrder = 0;
        if(!dataHelper.isEmptyData(selectedDateData))
            newOrder = dataHelper.getMaxOrderAmongScheduleMapByThisMonth(selectedDateData) + 1;
        dataHelper.addToDataStructure(newOrder,activityName);
        Log.d("페이징한뒤데이트벨류확인", String.valueOf(Integer.parseInt(dataHelper.getDateValue())));
    }

    // 오늘날짜 자료구조에 추가
    private void addToTodayDataStructrue(String activityName, String selectedDateData, String yearMonthKey) {
        int newOrder = 0;
        if(!dataHelper.isTodayEmptyData(selectedDateData, yearMonthKey))
            newOrder = dataHelper.getMaxOrderAmongScheduleMapByThisMonthForToday(selectedDateData, yearMonthKey) + 1;
        dataHelper.addToTodayDataStructure(newOrder,activityName, yearMonthKey);
        Log.d("페이징한뒤데이트벨류확인", String.valueOf(Integer.parseInt(dataHelper.getDateValue())));
    }

    // 메인 달력 활성화 중 취소 버튼 위에서 마우스 업
    private boolean actionAtCancelBtnWhenCalendarActivated(View copiedView, View closestView) {
        boolean isCanceled = false;

        if(Util.checkCollisionForChildView(uiHelper.getCancelBtn(), copiedView)){
            isCanceled = true;
            if(calendarHelper.isToday(closestView))
                closestView.setBackgroundResource(R.drawable.calendar_item_selected_bg);
            else
                closestView.setBackgroundColor(Color.parseColor("#ffffff"));
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
//                Log.d("dastaHelper.getCurrentCalendarViewMap()", String.valueOf(dataHelper.getCurrentCalendarViewMap()));
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
                new DialogHelper().setChoiceStyleDialog(context, new GeneralCallback() {
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
                });
            }

            @Override
            public void onNothingSelected() {

            }
        });
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

        Log.d("인덱스벨류 체크", String.valueOf(index));
        Log.d("오더벨류 체크", String.valueOf(orderValue));

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
//
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
        View etcLayout = etcPanel.getEtcLayout();

        // 기타버튼 패널화면이 ON이면 닫아준다
        if(etcLayout.getVisibility() == View.VISIBLE) {
            etcPanel.setEtcLayoutVisible(false);
            uiHelper.setTotalLayoutVisible(true);
        }
        // 하루 일정화면이 ON이면 닫아준다
        else if(scheduleLayout.getVisibility() == View.VISIBLE) {
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
        addScheduleForDiailyScheduleMode(dataHelper.getSelectedDateData(), tagName);
    }

    /**
     * 스케쥴 버튼을 드래그하여 메인 캘린더의 한칸으로 가져갔을때 추가
     * @param tagName
     */
    public void addToDB(String tagName, String dateValue) {
//        String dateString = calendarHelper.makeDateString(dataHelper.getDateValue());
        String dateString = calendarHelper.dataHelper.makeDateString2(dateValue, calendarHelper);

        Log.d("스케쥴 추가 체크", dateString + ", " + tagName);
        addScheduleIntoDB(dateString, tagName);
    }
    //

    /**
     * 데이트 스트링과 태그네임으로 DB에 스케쥴 추가(스케쥴일정화면)
     * @param dateString
     * @param tagName
     */
    private void addScheduleForDiailyScheduleMode(String dateString, String tagName) {
        // 이름
        String activityName = tagName;

        // 넘버
//        int number = DBHelper.dbHelper.getScheduleCountForDate(dateString);
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

//        HashMap<Integer, Schedule> dailySchedule = dataHelper.makeDailyScheduleMap(yearMonthKey, dateValue);
//        //정렬
//        TreeMap<Integer,Schedule> tm = new TreeMap<Integer,Schedule>(dailySchedule);
//
//        dataHelper.setDailyScheduleMap(tm);

        // 넘버
//        int number = DBHelper.dbHelper.getScheduleCountForDate(dateString);
        int number = 0;
        Log.d("스케쥴DB에추가 체크", "dateString = " + dateString);
        if(!dataHelper.isEmptyData(dateString))
        number = dataHelper.getMaxOrderAmongScheduleMapByThisMonth(dateString) + 1;

        // 삽입할 스케쥴 데이터 객체 생성
        Schedule newSchedule = makeNewSchedule(number, dateString, activityName);

        // DB에 삽입
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

        setDataHelperDateValue(String.valueOf(dateNumber));
        addScheduleIntoDB(dateString, tagName);
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
}
