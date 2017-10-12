package com.example.schedulemanager.panel.managerpanel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.schedulemanager.R;
import com.example.schedulemanager.Util;
import com.example.schedulemanager.helper.DataHelper;

// 기타버튼 => 관리 화면
public class ManagerPanel {

    Context context;
    private RelativeLayout managerLayout;               // 기타 버튼을 눌렀을때 나오는 패널
    private LinearLayout managerContentsLayout;
    private LinearLayout managerBottomButtonLayout;     // 기타 버튼 패널의 바텀 버튼 부분
    private ManagerPanelEvent managerPanelEvent;

    public ManagerPanel(Context context) {
        init(context);
    }

    public void init(Context context) {
        initFields(context);
        initBottomButtonEvents();
        adaptorFont();
    }

    private void initFields(Context context) {
        this.context = context;
        managerLayout = (RelativeLayout) Util.getViewById(this.context, R.id.managerLayout);
        managerContentsLayout = (LinearLayout) Util.getViewById(context, R.id.managerContentsLayout);
        managerBottomButtonLayout = (LinearLayout)Util.getViewById(this.context, R.id.managerBottomButtonLayout);
        managerPanelEvent = new ManagerPanelEvent(this);
    }

    private void initBottomButtonEvents() {
        Button closeButton = (Button) Util.getViewById(context, R.id.etcCloseBtn);
        closeButton.setOnClickListener(managerPanelEvent);
    }

    private void adaptorFont() {
        adaptorManagerBottomLayout();
    }

    private void adaptorManagerBottomLayout() {
        Util.setFontAllChildView(context, managerBottomButtonLayout, DataHelper.dataHelper.getTypeface(), true);
    }

    public void setManagerLayoutVisible(boolean isVisible) {
        managerLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void initManagerPanel() {
        composeContentsLayout();
    }

    private void composeContentsLayout() {
        View menuBarView = makeMenuBar("테스트");
        managerContentsLayout.addView(menuBarView);
    }

    private View makeMenuBar(String menuTitle) {
        View menuBarView = ((Activity) context).getLayoutInflater().inflate(R.layout.manager_menu_bar_item, null);
        menuBarView.setMinimumHeight((int) Util.convertDpToPixel(50));
        TextView menuBarTextView = (TextView) menuBarView.findViewById(R.id.menu_bar_text);
        setTextWithBoldFont(menuBarTextView, menuTitle);
        return menuBarView;
    }

    public void setTextWithBoldFont(TextView textView, String text){
        Typeface typeface = DataHelper.dataHelper.getTypeface();
        textView.setTypeface(typeface, Typeface.BOLD);
        textView.setText(text);
    }
}
