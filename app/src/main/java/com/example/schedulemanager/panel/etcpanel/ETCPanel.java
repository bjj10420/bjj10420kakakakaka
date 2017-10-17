package com.example.schedulemanager.panel.etcpanel;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.schedulemanager.R;
import com.example.schedulemanager.Util;
import com.example.schedulemanager.helper.DataHelper;
import com.example.schedulemanager.vo.ActivityVO;

import java.util.ArrayList;

/**
 * 기타 버튼을 눌렀을때 띄워지는 창
 */
public class ETCPanel {

    Context context;
    private RelativeLayout etcLayout;               // 기타 버튼을 눌렀을때 나오는 패널
    private LinearLayout etcContentsLayout;
    private LinearLayout etcBottomButtonLayout;     // 기타 버튼 패널의 바텀 버튼 부분
    private ETCPanelEvent etcPanelEvent;

    public ETCPanel(Context context) {
        init(context);
    }

    private void init(Context context) {
        initFields(context);
        initBottomButtonEvents();
        adaptorFont();
    }

    private void initFields(Context context) {
        this.context = context;
        etcLayout = (RelativeLayout)Util.getViewById(this.context, R.id.etcLayout);
        etcContentsLayout = (LinearLayout) Util.getViewById(context, R.id.etcContentsLayout);
        etcBottomButtonLayout = (LinearLayout)Util.getViewById(this.context, R.id.etcBottomButtonLayout);
        etcPanelEvent = new ETCPanelEvent(this);
    }

    private void initBottomButtonEvents() {
        Button closeButton = (Button) Util.getViewById(context, R.id.etcCloseBtn);
        closeButton.setOnClickListener(etcPanelEvent);
        Button managerButton = (Button) Util.getViewById(context, R.id.etcManagerBtn);
        managerButton.setOnClickListener(etcPanelEvent);
    }

    private void adaptorFont() {
        adaptorEtcBottomLayout();
    }

    private void adaptorEtcBottomLayout() {
        Util.setFontAllChildView(context, etcBottomButtonLayout, DataHelper.dataHelper.getTypeface(), true);
    }

    /**
     * 기타 버튼을 눌렀을 때 나오는 패널 초기화
     */
    public void initETCPanel(){
        for(String category : DataHelper.dataHelper.getCategories()){
            attachTitle(category);
            View rowLayout = makeRowLayout(category);
            addExtraViewTo(rowLayout, category);
        }
    }

    private void addExtraViewTo(View rowLayout, String category) {
        if(getActivityNumberAboutCategory(category) > 5)
            addCoveredRowLayoutWithScrollView(rowLayout);
        else
            addRowLayout(rowLayout);
    }
    //
    private void addCoveredRowLayoutWithScrollView(View rowLayout) {
        Log.d("addCoveredRowLayoutWithScrollView", "addCoveredRowLayoutWithScrollView");
        HorizontalScrollView scroll = makeScrollView(rowLayout);
        etcContentsLayout.addView(scroll);
        addBottomMargin(scroll, (int) Util.convertDpToPixel(10));
    }

    private HorizontalScrollView makeScrollView(View rowLayout) {
        HorizontalScrollView scroll = new HorizontalScrollView(context);
        scroll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        scroll.addView(rowLayout);
        return scroll;
    }

    private void addRowLayout(View rowLayout) {
        Log.d("addRowLayout", "addRowLayout");
        etcContentsLayout.addView(rowLayout);
        addBottomMargin(rowLayout, (int) Util.convertDpToPixel(10));
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
        View iconView = makePanelIconView(activityVO);
        TextView textView = makeTextView(activityVO);
        LinearLayout panelButtonView = makePanelButtonView(iconView, textView);
        panelButtonView.setTag(activityVO.getActivityName());
        panelButtonView.setOnClickListener(etcPanelEvent);
        return panelButtonView;
    }

    private TextView makeTextView(ActivityVO activityVO) {
        float textHeight = Util.convertDpToPixel(15);
        ViewGroup.LayoutParams textParams = new ViewGroup.LayoutParams((int) Util.convertDpToPixel(50),
                (int) textHeight);
        TextView textView = new TextView(context);
        String textData = activityVO.getActivityName();
        setTextWithBoldFont(textView, textData);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(textParams);
        textView.setTextColor(Color.parseColor("#404040"));
        return textView;
    }

    private View makePanelIconView(ActivityVO activityVO) {
        View iconView = new View(context);
        float valueOf50DP = Util.convertDpToPixel(50);

        // 각 버튼 레이아웃 파라메터
        ViewGroup.LayoutParams iconParams = new ViewGroup.LayoutParams((int) valueOf50DP,
                (int) valueOf50DP);
        iconView.setBackground(new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(activityVO.getImageData(),0,activityVO.getImageData().length)));
        iconView.setLayoutParams(iconParams);
        return iconView;
    }

    private LinearLayout makePanelButtonView(View iconView, TextView textView) {
        LinearLayout buttonView = new LinearLayout(context);
        // 각 버튼 뷰 레이아웃 파라메터
        LinearLayout.LayoutParams buttonViewParams = new LinearLayout.LayoutParams(      (int) Util.convertDpToPixel(80),
                (int) Util.convertDpToPixel(65));
//        buttonViewParams.weight = 1;
        buttonView.setOrientation(LinearLayout.VERTICAL);
        buttonView.setGravity(Gravity.CENTER);
        buttonView.setLayoutParams(buttonViewParams);
        buttonView.addView(iconView);
        buttonView.addView(textView);

        return buttonView;
    }

    private void attachTitle(String category) {
        TextView textView = makeTitleTextView(category);
        etcContentsLayout.addView(textView);
        addBottomMargin(textView, (int) Util.convertDpToPixel(10));
    }

    private void addBottomMargin(View view, int marginValue) {
        LinearLayout.LayoutParams textViewParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        textViewParams.setMargins(0, 0, 0, marginValue);
    }

    private TextView makeTitleTextView(String category) {
        TextView textView = new TextView(context);
        String textData = category;
        setTextWithBoldFont(textView, textData);
        setTextViewBaseOptions(textView);
        return textView;
    }

    private void setTextViewBaseOptions(TextView textView) {
        float textHeight = Util.convertDpToPixel(15);
        ViewGroup.LayoutParams textParams = new ViewGroup.LayoutParams((int) Util.convertDpToPixel(50),
                (int) textHeight);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(textParams);
        textView.setTextColor(Color.parseColor("#404040"));
    }

    private LinearLayout makeRowLayout() {
        LinearLayout rowLayout = new LinearLayout(context);
        LinearLayout.LayoutParams rowLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowLayout.setLayoutParams(rowLayoutParams);
        return rowLayout;
    }

    private int getActivityNumberAboutCategory(String category){
        ArrayList<ActivityVO> activities = DataHelper.dataHelper.getActivities().get(category);
        return activities.size();
    }

    public void setTextWithBoldFont(TextView textView, String text){
        Typeface typeface = DataHelper.dataHelper.getTypeface();
        textView.setTypeface(typeface, Typeface.BOLD);
        textView.setText(text);
    }

    public void setEtcLayoutVisible(boolean isVisible) {
        etcLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public RelativeLayout getEtcLayout() {
        return etcLayout;
    }


}

