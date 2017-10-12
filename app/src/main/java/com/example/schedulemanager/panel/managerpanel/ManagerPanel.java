package com.example.schedulemanager.panel.managerpanel;

import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

    public void initPanel(){

    }
}
