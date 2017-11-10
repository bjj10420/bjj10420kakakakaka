package com.example.schedulemanager.panel.managerpanel;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.schedulemanager.R;
import com.example.schedulemanager.helper.DialogHelper;
import com.example.schedulemanager.interface_.GeneralCallback;
import com.example.schedulemanager.interface_.GeneralCallback2;
import com.example.schedulemanager.panel.etcpanel.ETCPanel;
import com.example.schedulemanager.vo.ActivityVO;

import java.util.ArrayList;

import static com.example.schedulemanager.helper.DBHelper.dbHelper;
import static com.example.schedulemanager.helper.DataHelper.dataHelper;
import static com.example.schedulemanager.helper.EventHelper.eventHelper;
import static com.example.schedulemanager.helper.UIHelper.uiHelper;

public class ManagerPanelEvent implements View.OnClickListener{

    private Context context;
    ManagerPanel managerPanel;

    public ManagerPanelEvent(ManagerPanel managerPanel, Context context) {
        this.managerPanel = managerPanel;
        this.context = context;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.managerCloseBtn:
                managerCloseBtnEvent();
                break;

            default:
                itemClickEvent(view);
        }
    }

    public void managerCloseBtnEvent() {
        switchLayout();
        redraw();
        resetMainFavoritePanelEvents();
    }

    private void switchLayout() {
        managerPanelLayoutOff();
        etcLayoutON();
    }

    private void redraw() {
        redrawMainFavoritePanel();
//        redrawETCPanel();
    }

    private void redrawETCPanel() {
        ETCPanel etcPanel = eventHelper.getEtcPanel();
        etcPanel.clearEtcContentsLayout();
        etcPanel.initETCPanel();
    }

    private void resetMainFavoritePanelEvents() {
        eventHelper.setPanelEvents();
    }

    private void redrawMainFavoritePanel() {
        uiHelper.clearButtonPanel();
        uiHelper.newInitButtonPanel(dataHelper.getActivities());
    }

    private void etcLayoutON() {
        eventHelper.getEtcPanel().setEtcLayoutVisible(true);
    }

    public void managerPanelLayoutOff() {
        uiHelper.slideDownManagerPanel(managerPanel);
    }

    private void itemClickEvent(View view){
        if(isListItemCancelBtn(view))
            removeCategoryEvent(view);
        else if(view.getTag() instanceof ActivityVO)
            listItemClickEvent(view);
        else if(isAddCategoryBar(view))
            addCategoryEvent(view);
        else
            menubarTitleItemClickEvent(view);
    }

    private void removeCategoryEvent(View view) {
        // 삭제 버튼 클릭
        final String categoryName = ((TextView)((RelativeLayout) view.getParent()).getChildAt(0)).getText().toString();
        new DialogHelper().setChoiceStyleDialogWithMessage(context, null, new GeneralCallback() {
            @Override
            public void onCallBack() {
                removeCategory(categoryName);
            }

        }, "삭제", "취소",  categoryName + "카테고리를 삭제하시겠습니까?");

    }

    private void removeCategory(String categoryName) {
        removeCategoryFromDB(categoryName);
        removeCategoryFromMap(categoryName);
        redrawManagerAndETCPanel();
    }

    private boolean isListItemCancelBtn(View view) {
        return view.getId() == R.id.menu_bar_cancel;
    }

    private void addCategoryEvent(View view) {
        new DialogHelper().setOneBtnStyleWithTextDialog(context, "추가할 카테고리 이름을 입력하세요", new GeneralCallback2() {
            @Override
            public void onCallBack(Object parameter) {
                String category = (String) parameter;
                addCategory(category);
            }
        }, "추가");
    }

    private void addCategory(String category) {
        addCategoryToDB(category);
        addCategoryToMap(category);
        redrawManagerAndETCPanel();
    }

    private void redrawManagerAndETCPanel() {
        managerPanel.redrawManagerPanel();
        redrawETCPanel();
    }

    private void addCategoryToMap(String category) {
        dataHelper.getCategories().add(category);
        dataHelper.makeCategoryArray();
        dataHelper.getActivities().put(category, new ArrayList<ActivityVO>());
    }

    private void removeCategoryFromMap(String category) {
        dataHelper.getCategories().remove(category);
        dataHelper.makeCategoryArray();
        dataHelper.getActivities().remove(category);
    }

    private void addCategoryToDB(String category) {
        dbHelper.insertCategory(category);
    }

    private void removeCategoryFromDB(String category) {
        dbHelper.deleteCategory(category);
    }

    private boolean isAddCategoryBar(View view) {
        return view.getId() == R.id.addCategoryBtn;
    }

    private void listItemClickEvent(View view) {
        new DialogHelper().showActivityItemInfoDialog(context, view);
    }

    private void menubarTitleItemClickEvent(View view) {
        ViewGroup viewParent = (ViewGroup) view.getParent();
        setDetailView(viewParent, view);
        setCancelView(view);
       }

    private void setCancelView(View view) {
        View menuBarTextView = view.findViewById(R.id.menu_bar_cancel);
        menuBarTextView.setVisibility(menuBarTextView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    private void setDetailView(ViewGroup viewParent, View view) {
        View detailView = viewParent.getChildAt(viewParent.indexOfChild(view) + 1);
        detailView.setVisibility(detailView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    private void updateActivitiesMap(boolean isFaovriteChecked, ActivityVO activityVO) {
        activityVO.setFavorite(isFaovriteChecked ? "T" : "F");
    }

    private void updateDBFavoriteData(boolean isFaovriteChecked, String activityName) {
        dbHelper.updateFavoriteChecked(activityName, isFaovriteChecked ? "T" : "F");
    }

    public void checkBoxEvent(boolean b, ActivityVO activityVO) {
        updateDBFavoriteData(b, activityVO.getActivityName());
        updateActivitiesMap(b, activityVO);
    }
}