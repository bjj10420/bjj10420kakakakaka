package com.example.schedulemanager.helper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.schedulemanager.R;
import com.example.schedulemanager.vo.ActivityVO;
import com.example.schedulemanager.vo.Schedule;
import com.example.schedulemanager.Util;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * 모든 UI처리 담당
 */
public class UIHelper {

    private Context context;
    private DataHelper dataHelper;
    private RelativeLayout totalLayout;             // 최상위 프레임 레이아웃
    private View centerIcon;                        // 중앙 아이콘 뷰
    private View calendarLayout;                    // 메인 캘린더 레이아웃
    private View scheduleLayout;                    // 하루 일정 레이아웃
    private View closestView;                       // 드래그 이벤트 도중 포인터주위의 가장 가까운 뷰
    private View selectedCalendarCellView;          // 선택된 달력 셀 뷰
    private TextView backBtn;                       // 하단 뒤로가기 버튼
    private View cancelBtn;                         // 하단 X 버튼
    private PieChart pieChart;                      // 데일리 스케쥴 챠트 화면
    private View copiedView;                        // 드래그를 시작할 때 임시로 저장 해놓는 뷰
    private View firstCalendarCell;                 // 캘린더가 랜더링된 후의 첫 셀뷰
    private LinearLayout etcLayout;                 // 기타 버튼을 눌렀을때 나오는 패널

    public void initUI(Context context, DataHelper dataHelper) {
        initFields(context, dataHelper);
        //TODO 스트링 리스트 파라메터를 나중에 DB에서 읽어오게 해야 함
        //TODO 나중에 버튼 패널의 아이콘들에 weight를 줘야한다
        initButtonPanel(R.id.buttonPanel, testCode());
        initButtonPanel(R.id.buttonPanel2, testCode2());
        initEtcLayout();
    }

    private ArrayList<String> testCode() {
        ArrayList<String> testList = new ArrayList<String>();
        testList.add("교류");
        testList.add("이메일");
        testList.add("여가");
        testList.add("만남");
        return testList;
    }
    private ArrayList<String> testCode2() {
        ArrayList<String> testList2 = new ArrayList<String>();
        testList2.add("약속");
        testList2.add("독서");
        testList2.add("학교");
        testList2.add("기타");
        return testList2;
    }

    private void initFields(Context context, DataHelper dataHelper) {
        this.context = context;
        this.dataHelper = dataHelper;

        totalLayout = (RelativeLayout) Util.getViewById(this.context,R.id.totalLayout);
        centerIcon = Util.getViewById(this.context,R.id.centerIcon);
        calendarLayout = Util.getViewById(this.context,R.id.calendarLayout);
        scheduleLayout = Util.getViewById(this.context,R.id.scheduleLayout);
        etcLayout = (LinearLayout)Util.getViewById(this.context, R.id.etcLayout);
        backBtn = (TextView) Util.getViewById(this.context,R.id.back_btn);
        backBtn.setTypeface(this.dataHelper.getTypeface());
        cancelBtn = Util.getViewById(this.context,R.id.cancel_btn);
        pieChart = (PieChart) Util.getViewById(this.context,R.id.chart);
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
        LinearLayout buttonPanel = (LinearLayout) Util.getViewById(context,panelId);
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
            LinearLayout buttonView = new LinearLayout(context);
            buttonView.setOrientation(LinearLayout.VERTICAL);
            buttonView.setGravity(Gravity.CENTER);
            buttonView.setLayoutParams(buttonViewParams);
            // 아이콘 뷰 설정
            View iconView = new View(context);

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
                case 7 : iconView.setBackgroundResource(R.drawable.etc);
                    break;
                case 8 : iconView.setBackgroundResource(R.drawable.shopping);
                    break;
            }

            iconView.setLayoutParams(iconParams);
            // 텍스트 뷰 설정
            TextView textView = new TextView(context);
//            textView.setText(textData);
            setTextWithFont(textView, textData);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(textParams);
            textView.setTextColor(Color.parseColor("#404040"));
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

    /**
     * 이게 진짜
     */
    private void initRowLayout(LinearLayout rowLayout, ArrayList<ActivityVO> activityVOs) {

        for(ActivityVO activityVO : activityVOs){
            View buttonView = setButtonView(activityVO);
            rowLayout.addView(buttonView);
        }
    }

    private View setButtonView(ActivityVO activityVO) {
        ImageView iconView = makePanelIconView(activityVO);
        TextView textView = makeTextView(activityVO);
        LinearLayout panelButtonView = makePanelButtonView(iconView, textView);
        panelButtonView.setTag(activityVO.getActivityName());

        return panelButtonView;
    }

    private TextView makeTextView(ActivityVO activityVO) {
        float textHeight = Util.convertDpToPixel(15);
        ViewGroup.LayoutParams textParams = new ViewGroup.LayoutParams((int) Util.convertDpToPixel(50),
                (int) textHeight);
        TextView textView = new TextView(context);
        String textData = activityVO.getActivityName();
        setTextWithFont(textView, textData);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(textParams);
        textView.setTextColor(Color.parseColor("#404040"));
        return textView;
    }

    private ImageView makePanelIconView(ActivityVO activityVO) {
        ImageView iconView = new ImageView(context);
        float valueOf50DP = Util.convertDpToPixel(50);

        // 각 버튼 레이아웃 파라메터
        ViewGroup.LayoutParams iconParams = new ViewGroup.LayoutParams((int) valueOf50DP,
                (int) valueOf50DP);
        iconView.setImageBitmap(BitmapFactory.decodeByteArray(activityVO.getImageData(),0,activityVO.getImageData().length));
        iconView.setLayoutParams(iconParams);
        return iconView;
    }

    private LinearLayout makePanelButtonView(ImageView iconView, TextView textView) {
        LinearLayout buttonView = new LinearLayout(context);
        // 각 버튼 뷰 레이아웃 파라메터
        LinearLayout.LayoutParams buttonViewParams = new LinearLayout.LayoutParams(0,
                (int) Util.convertDpToPixel(65));
        buttonViewParams.weight = 1;
        buttonView.setOrientation(LinearLayout.VERTICAL);
        buttonView.setGravity(Gravity.CENTER);
        buttonView.setLayoutParams(buttonViewParams);
        buttonView.addView(iconView);
        buttonView.addView(textView);

        return buttonView;
    }

    /**
     * 각 버튼뷰를 생성
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private View makeButtonView(Drawable background, String textData, int width, int height) {
        // 각 버튼의 높이
        float valueOf50DP = Util.convertDpToPixel(50);
        // 각 텍스트의 높이
        float textHeight = Util.convertDpToPixel(15);
        // 각 버튼 뷰 레이아웃 파라메터
        LinearLayout.LayoutParams buttonViewParams = new LinearLayout.LayoutParams(width,
                height);

        // 각 버튼 레이아웃 파라메터
        ViewGroup.LayoutParams iconParams = new ViewGroup.LayoutParams((int) valueOf50DP,
                (int) valueOf50DP);
        // 각 텍스트 파라메터
        ViewGroup.LayoutParams textParams = new ViewGroup.LayoutParams((int) valueOf50DP,
                (int) textHeight);

        // 버튼뷰 설정
        LinearLayout buttonView = new LinearLayout(context);
        buttonView.setOrientation(LinearLayout.VERTICAL);
        buttonView.setGravity(Gravity.CENTER);
        buttonView.setLayoutParams(buttonViewParams);
        // 아이콘 뷰 설정
        View iconView = new View(context);

//           iconView.setBackgroundResource(findIdByFileName(iconNameMap.get(textData), this));

        iconView.setBackground(background);
        iconView.setLayoutParams(iconParams);
        // 텍스트 뷰 설정
        TextView textView = new TextView(context);
//        textView.setText(textData);
        setTextWithFont(textView, textData);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(textParams);
        textView.setTextColor(Color.parseColor("#404040"));

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
        Resources res = context.getResources();
        tickedIconDrawable = res.getDrawable(R.drawable.schedule_icon);
        if(isTicked)
            tickedIconDrawable.setColorFilter(Color.parseColor("#4bf442"), PorterDuff.Mode.SRC_IN);
        else
            tickedIconDrawable.clearColorFilter();
        this.centerIcon.setBackground(tickedIconDrawable);
    }

    /**
     * 지정된 뷰의 복사본을 만들어 최상위 프레임레이아웃의 자식으로 보냄
     * @param view
     */
    public void hoverView(View view) {
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
     * 아이콘 버튼 드래그시 바텀버튼 전환
     */
    public void changeBottomButton(boolean isBackBtnVisible) {
        backBtn.setVisibility(isBackBtnVisible ? View.GONE : View.VISIBLE);
        cancelBtn.setVisibility(isBackBtnVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * 하루 일정을 셋팅
     * @param dailyScheduleMap
     */
    public void setDailyScheduleDisplay(TreeMap<Integer, Schedule> dailyScheduleMap) {

        List<PieEntry> entries = new ArrayList<>();
        // 모든 스케쥴은 균등한 점유값을 갖는다
        // 100을 개수로 나눈값으로 지정
        float fillValue = 100 / dailyScheduleMap.size();
        for(Integer dateValue : dailyScheduleMap.keySet()) {
            Schedule schedule = dailyScheduleMap.get(dateValue);
            String label = schedule.getActivityName();
            String memo = schedule.getMemo();
            if(memo != null && !memo.equals("")) label += "(" + memo + ")";
            entries.add(new PieEntry(fillValue, label));
        }

        PieDataSet dailyScheduleDataSet = new PieDataSet(entries, "Election Results");
        dataHelper.setDailyScheduleDataSet(dailyScheduleDataSet);

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

        pieChart.getOnTouchListener().setLastHighlighted(null);
        pieChart.highlightValues(null);
        pieChart.setData(data);
        pieChart.setDrawHoleEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setDrawSliceText(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelColor(Color.parseColor("#404040"));
        pieChart.setEntryLabelTypeface(dataHelper.getTypeface());
        pieChart.setRotationEnabled(false);
        pieChart.invalidate(); // refresh
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
        TextView dailyScheduleDateText = (TextView) Util.getViewById(context, R.id.dailyScheduleDateText);
//        dailyScheduleDateText.setText(sb);
        setTextWithFont(dailyScheduleDateText, sb.toString());

    }

    public View getCopiedView() {
        return copiedView;
    }

    public RelativeLayout getTotalLayout() {
        return totalLayout;
    }

    public View getCenterIcon() {
        return centerIcon;
    }

    public View getClosestView() {
        return closestView;
    }

    public View getCalendarLayout() {
        return calendarLayout;
    }

    public View getScheduleLayout() {
        return scheduleLayout;
    }

    public View getBackBtn() {
        return backBtn;
    }

    public View getCancelBtn() {
        return cancelBtn;
    }

    public PieChart getPieChart() {
        return pieChart;
    }

    public void setClosestView(View closestView) {
        Log.d("setClosestView 체크", "setClosestView 체크");
        this.closestView = closestView;
    }

    /**
     * 데이터 유무에 따른 문구표시 설정
     * @param isVisible
     */
    public void setNoDateText(boolean isVisible) {
        View noDateTextView = Util.getViewById(context, R.id.dailyScheduleNoDataText);
        noDateTextView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * 파이챠트 리셋
     * @param pieChart
     */
    public void resetPiechart(PieChart pieChart) {
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
        pieChart.getOnTouchListener().setLastHighlighted(null);
        pieChart.highlightValues(null);
    }

    /**
     * 파이챠트 업데이트
     * @param memoContent
     * @param pieEntry
     */
    public void updatePiechart(String memoContent, PieEntry pieEntry,String activityName) {
        StringBuilder sb = new StringBuilder();
        sb.append(activityName).append("(").append(String.valueOf(memoContent)).append(")");
        (pieEntry).setLabel(sb.toString());
    }

    /**
     * 폰트로 텍스트를 설정
     * @param textView
     * @param text
     */
    public void setTextWithFont(TextView textView, String text){
        Typeface typeface = dataHelper.getTypeface();
        textView.setTypeface(typeface);
        textView.setText(text);
    }

    public void setCalendarLayoutVisible(View v) {
        calendarLayout.setVisibility(View.VISIBLE);
        // 중앙 아이콘 비표시
        v.setVisibility(View.GONE);
    }

    public void setTotalLayoutVisible(boolean isVisible) {
        totalLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setEtcLayoutVisible(boolean isVisible) {
        etcLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
    public View getFirstCalendarCell() {
        return firstCalendarCell;
    }

    public void setFirstCalendarCell(View firstCalendarCell) {
        this.firstCalendarCell = firstCalendarCell;
    }

    public View getSelectedCalendarCellView() {
        return selectedCalendarCellView;
    }

    public void setSelectedCalendarCellView(View selectedCalendarCellView) {
        this.selectedCalendarCellView = selectedCalendarCellView;
    }

    /**
     * 기타 버튼을 눌렀을 때 나오는 패널 초기화
     */
    public void initEtcLayout(){
        for(String category : DataHelper.dataHelper.getCategories()){
            attachTitle(category);
            View rowLayout = makeRowLayout(category);
            etcLayout.addView(rowLayout);
            addBottomMargin(rowLayout, (int) Util.convertDpToPixel(10));
        }
    }

    private View makeRowLayout(String category) {
        LinearLayout rowLayout = makeRowLayout();
        composeRowLayout(rowLayout, category);
        return rowLayout;
    }

    private void composeRowLayout(LinearLayout rowLayout, String category) {
        ArrayList<ActivityVO> activities = DataHelper.dataHelper.getActivities().get(category);
        initRowLayout(rowLayout, activities);
    }

    private void attachTitle(String category) {
        TextView textView = makeTitleTextView(category);
        etcLayout.addView(textView);
        addBottomMargin(textView, (int) Util.convertDpToPixel(10));
    }

    private void addBottomMargin(View view, int marginValue) {
        LinearLayout.LayoutParams textViewParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        textViewParams.setMargins(0, 0, 0, marginValue);
    }

    private TextView makeTitleTextView(String category) {
        TextView textView = new TextView(context);
        float textHeight = Util.convertDpToPixel(15);
        ViewGroup.LayoutParams textParams = new ViewGroup.LayoutParams((int) Util.convertDpToPixel(50),
                (int) textHeight);
        String textData = category;
        setTextWithFont(textView, textData);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(textParams);
        textView.setTextColor(Color.parseColor("#404040"));
        return textView;
    }

    private LinearLayout makeRowLayout() {
        LinearLayout rowLayout = new LinearLayout(context);
        LinearLayout.LayoutParams rowLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowLayout.setLayoutParams(rowLayoutParams);
        return rowLayout;
    }

}
