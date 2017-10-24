package com.example.schedulemanager.panel.managerpanel;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.schedulemanager.R;
import com.example.schedulemanager.calendar.DialogHelper;
import com.example.schedulemanager.helper.DBHelper;
import com.example.schedulemanager.helper.EventHelper;
import com.example.schedulemanager.helper.UIHelper;
import com.example.schedulemanager.vo.ActivityVO;

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

    private void managerCloseBtnEvent() {
        managerPanelLayoutOff();
        etcLayoutON();
        redrawMainFavoritePanel();
        resetMainFavoritePanelEvents();
    }

    private void resetMainFavoritePanelEvents() {
        eventHelper.setDragEvents();
    }

    private void redrawMainFavoritePanel() {
        uiHelper.clearButtonPanel();
        uiHelper.newInitButtonPanel(dataHelper.getActivities());
    }

    private void etcLayoutON() {
        eventHelper.getEtcPanel().setEtcLayoutVisible(true);
    }

    private void managerPanelLayoutOff() {
        managerPanel.setManagerLayoutVisible(false);
    }

    private void itemClickEvent(View view){
        if(view.getTag() instanceof ActivityVO)
            listItemClickEvent(view);
        else
            menubarTitleItemClickEvent(view);
    }

    private void listItemClickEvent(View view) {
        new DialogHelper().showActivityItemDialog(context, null, null, view);
    }

    private void menubarTitleItemClickEvent(View view) {
        ViewGroup viewParent = (ViewGroup) view.getParent();
        View detailView = viewParent.getChildAt(viewParent.indexOfChild(view) + 1);
        detailView.setVisibility(detailView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    private void updateActivitiesMap(boolean isFaovriteChecked, ActivityVO activityVO) {
        activityVO.setFavorite(isFaovriteChecked ? "T" : "F");
    }

    private void updateDBFavoriteData(boolean isFaovriteChecked, String activityName) {
        DBHelper.dbHelper.updateFavoriteChecked(activityName, isFaovriteChecked ? "T" : "F");
    }

    public void checkBoxEvent(boolean b, ActivityVO activityVO) {
        updateDBFavoriteData(b, activityVO.getActivityName());
        updateActivitiesMap(b, activityVO);
    }
}