package com.example.schedulemanager.panel.managerpanel;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.schedulemanager.R;
import com.example.schedulemanager.Util;
import com.example.schedulemanager.helper.DataHelper;
import com.example.schedulemanager.vo.ActivityVO;

import java.util.ArrayList;

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
        managerBottomButtonLayout = (LinearLayout) Util.getViewById(this.context, R.id.managerBottomButtonLayout);
        managerPanelEvent = new ManagerPanelEvent(this);
    }

    private void initBottomButtonEvents() {
        Button closeButton = (Button) Util.getViewById(context, R.id.managerCloseBtn);
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
        for (String category : DataHelper.dataHelper.getCategories()) {
            addMenuBarViewToContentsLayout(category);
            addDetailViewToContentsLayout(category);
            addEmptyViewToContentsLayout();
        }
    }

    private void addEmptyViewToContentsLayout() {
        View emptyView = makeEmptyView();
        managerContentsLayout.addView(emptyView);
    }

    private void addDetailViewToContentsLayout(String category) {
        View detailView = makeDetailView(category);
        managerContentsLayout.addView(detailView);
    }

    private void addMenuBarViewToContentsLayout(String category) {
        View menuBarView = makeMenuBar(category);
        managerContentsLayout.addView(menuBarView);
    }

    private View makeDetailView(String category) {
        LinearLayout detailView = (LinearLayout) inflateDetailView();
        ArrayList<ActivityVO> activities = DataHelper.dataHelper.getActivities().get(category);
        for(ActivityVO activityVO : activities){
            View detailItemView = inflateDetailItemView();
            setDetailView(detailItemView, activityVO);
            detailView.addView(detailItemView);
        }
        return detailView;
    }

    private View inflateDetailItemView() {
        View detailItemView = ((Activity) context).getLayoutInflater().inflate(R.layout.manager_detail_item, null);
        ViewGroup.LayoutParams detailViewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        detailItemView.setLayoutParams(detailViewParams);
        return detailItemView;
    }

    private View makeEmptyView() {
        View emptyView = new View(context);
        int heightValue = (int) Util.convertDpToPixel(5);
        ViewGroup.LayoutParams emptyViewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                heightValue);
        emptyView.setLayoutParams(emptyViewParams);
        return emptyView;
    }

    private View makeMenuBar(String menuTitle) {
        View menuBarView = makeMenuBarView();
        TextView menuBarTextView = (TextView) menuBarView.findViewById(R.id.menu_bar_text);
        Util.setTextWithBoldFont(menuBarTextView, menuTitle);
        menuBarView.setOnClickListener(managerPanelEvent);
        return menuBarView;
    }

    private View makeMenuBarView() {
        View menuBarView = ((Activity) context).getLayoutInflater().inflate(R.layout.manager_menu_bar_item, null);
        menuBarView.setMinimumHeight((int) Util.convertDpToPixel(65));
        return menuBarView;
    }

    private View inflateDetailView() {
        View detailView = ((Activity) context).getLayoutInflater().inflate(R.layout.manager_detail, null);
        ViewGroup.LayoutParams detailViewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        detailView.setLayoutParams(detailViewParams);
        return detailView;
    }

    private void setDetailView(View detailItemView, ActivityVO activityVO) {
        setFavoriteCheck(detailItemView, activityVO);
        setFavoriteIcon(detailItemView, activityVO);
        setFavoriteName(detailItemView, activityVO);
    }

    private void setFavoriteName(View detailItemView, ActivityVO activityVO) {
        TextView textView = (TextView) detailItemView.findViewById(R.id.favorite_name);
        Util.setTextWithBoldFont(textView, activityVO.getActivityName());
    }

    private void setFavoriteCheck(View detailItemView, ActivityVO activityVO) {

    }

    private TextView makeTextView(ActivityVO activityVO) {
        float textHeight = Util.convertDpToPixel(15);
        ViewGroup.LayoutParams textParams = new ViewGroup.LayoutParams((int) Util.convertDpToPixel(50),
                (int) textHeight);
        TextView textView = new TextView(context);
        String textData = activityVO.getActivityName();
        Util.setTextWithBoldFont(textView, textData);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(textParams);
        textView.setTextColor(Color.parseColor("#404040"));
        return textView;
    }

    private void setFavoriteIcon(View detailItemView, ActivityVO activityVO) {
        ImageView iconView = (ImageView) detailItemView.findViewById(R.id.favorite_icon);
        iconView.setImageBitmap(BitmapFactory.decodeByteArray(activityVO.getImageData(),0,activityVO.getImageData().length));
    }

}
