package com.example.schedulemanager.panel.managerpanel;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.schedulemanager.R;
import com.example.schedulemanager.Util;
import com.example.schedulemanager.helper.DBHelper;
import com.example.schedulemanager.helper.DataHelper;
import com.example.schedulemanager.vo.ActivityVO;
import com.takeiteasy.materialexpansionpanel.panel.MaterialExpansionPanelView;

import java.util.ArrayList;

// 기타버튼 => 관리 화면
public class ManagerPanel {

    Context context;
    private RelativeLayout managerLayout;               // 기타 버튼을 눌렀을때 나오는 패널
    private LinearLayout managerContentsLayout;
    private ManagerPanelEvent managerPanelEvent;

    public ManagerPanel(Context context) {
        init(context);
    }

    public void init(Context context) {
        initFields(context);
        initBottomButtonEvents();
    }

    private void initFields(Context context) {
        this.context = context;
        managerLayout = (RelativeLayout) Util.getViewById(this.context, R.id.managerLayout);
        managerContentsLayout = (LinearLayout) Util.getViewById(context, R.id.managerContentsLayout);
        managerPanelEvent = new ManagerPanelEvent(this, context);
    }

    private void initBottomButtonEvents() {
        View closeButton = Util.getViewById(context, R.id.managerCloseBtn);
        closeButton.setOnClickListener(managerPanelEvent);
    }

    public void setManagerLayoutVisible(boolean isVisible) {
        managerLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void initManagerPanel() {
        composeContentsLayout();
    }

    private void composeContentsLayout() {
        for (String category : DataHelper.dataHelper.getCategories()) {
//            addMenuBarViewToContentsLayout(category);
//            addDetailViewToContentsLayout(category);
//            addEmptyViewToContentsLayout();
            addExpansionPanel(category);
        }
        addLastRowViewToContentsLayout();
    }

    private void addEmptyViewToContentsLayout() {
        View emptyView = makeEmptyView(5);
        managerContentsLayout.addView(emptyView);
    }

    private void addLastRowViewToContentsLayout() {
        addMenuBarViewToContentsLayout("카테고리 +");
    }

    private void addDetailViewToContentsLayout(String category) {
        View detailView = makeDetailView(category);
        View emptyView = makeEmptyView(15);
        managerContentsLayout.addView(detailView);
        managerContentsLayout.addView(emptyView);
    }

    private void addMenuBarViewToContentsLayout(String category) {
        //
        View menuBarView = makeMenuBar(category);
        managerContentsLayout.addView(menuBarView);
    }

    public void addExpansionPanel(String category) {
        MaterialExpansionPanelView panelView = new MaterialExpansionPanelView(context);
        View detailView = makeDetailView(category);
        detailView.setVisibility(View.VISIBLE);
        panelView.setDrawingCacheBackgroundColor(R.color.strokeBrown);
        panelView.setBackgroundColor(Color.parseColor("#ffffff"));
        panelView.setTitle(category);
        panelView.setContentView(detailView);
        panelView.setSubtitle("");
        View positiveButton = panelView.findViewById(R.id.panel_button_positive);
        View negativeButton = panelView.findViewById(R.id.panel_button_negative);
        positiveButton.setVisibility(View.GONE);
        negativeButton.setVisibility(View.GONE);

        managerContentsLayout.addView(panelView);
        panelView.expand();
    }

    private View makeDetailView(String category) {
        LinearLayout detailView = (LinearLayout) inflateDetailView();
        ArrayList<ActivityVO> activities = DataHelper.dataHelper.getActivities().get(category);
        if(activities != null) {
            for (ActivityVO activityVO : activities) {
                View detailItemView = inflateDetailItemView();
                setDetailItemView(detailItemView, activityVO);
                detailView.addView(detailItemView);
            }
        }
        return detailView;
    }

    private View inflateDetailItemView() {
        View detailItemView = ((Activity) context).getLayoutInflater().inflate(R.layout.manager_detail_item, null);
        ViewGroup.LayoutParams detailViewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) Util.convertDpToPixel(35));
        detailItemView.setLayoutParams(detailViewParams);
        return detailItemView;
    }

    private View makeEmptyView(int height) {
        View emptyView = new View(context);
        int heightValue = (int) Util.convertDpToPixel(height);
        ViewGroup.LayoutParams emptyViewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                heightValue);
        emptyView.setLayoutParams(emptyViewParams);
        return emptyView;
    }

    private View makeMenuBar(String menuTitle) {
        View menuBarView = makeMenuBarView();
        setMenuBarText(menuBarView, menuTitle);
        setMenuBarCancel(menuBarView);
        menuBarView.setOnClickListener(managerPanelEvent);
        return menuBarView;
    }

    private void setMenuBarCancel(View menuBarView) {
        View menuBarTextView = menuBarView.findViewById(R.id.menu_bar_cancel);
        menuBarTextView.setOnClickListener(managerPanelEvent);
    }

    private void setMenuBarText(View menuBarView, String menuTitle) {
        TextView menuBarTextView = (TextView) menuBarView.findViewById(R.id.menu_bar_text);
        Util.setTextWithBoldFont(menuBarTextView, menuTitle);

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
        Util.setFontAllChildView(context, detailView, DataHelper.dataHelper.getTypeface(), false);
        return detailView;
    }

    private void setDetailItemView(View detailItemView, ActivityVO activityVO) {
        setFavoriteCheck(detailItemView, activityVO);
        setFavoriteIcon(detailItemView, activityVO);
        setFavoriteName(detailItemView, activityVO);
        setDetailItemViewEvent(detailItemView, activityVO);
    }

    private void setDetailItemViewEvent(View detailItemView, final ActivityVO activityVO) {
        CheckBox checkBox = (CheckBox) detailItemView.findViewById(R.id.favorite_check);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               managerPanelEvent.checkBoxEvent(b, activityVO);
            }
        });
        detailItemView.setOnClickListener(managerPanelEvent);
        // 태그첨부
        detailItemView.setTag(activityVO);
    }

    private void setFavoriteName(View detailItemView, ActivityVO activityVO) {
        TextView textView = (TextView) detailItemView.findViewById(R.id.favorite_name);
        Util.setTextWithBoldFont(textView, activityVO.getActivityName());
    }

    private void setFavoriteCheck(View detailItemView, ActivityVO activityVO) {
        int id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
        CheckBox checkBox =  ((CheckBox) detailItemView.findViewById(R.id.favorite_check));
        checkBox.setButtonDrawable(id);
        checkBox.setChecked(activityVO.isFavorite().equals("T") ? true : false);
    }

    private void setFavoriteIcon(View detailItemView, ActivityVO activityVO) {
        ImageView iconView = (ImageView) detailItemView.findViewById(R.id.favorite_icon);
        iconView.setImageBitmap(BitmapFactory.decodeByteArray(activityVO.getImageData(),0,activityVO.getImageData().length));
    }

    public void clearContentsLayout(){
        managerContentsLayout.removeAllViews();
    }

    public void redrawManagerPanel(){
        clearContentsLayout();
        initManagerPanel();
    }

    public RelativeLayout getManagerLayout() {
        return managerLayout;
    }

    public ManagerPanelEvent getManagerPanelEvent() {
        return managerPanelEvent;
    }
}
