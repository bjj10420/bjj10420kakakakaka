package com.example.schedulemanager.panel.managerpanel;


import android.view.View;
import android.view.ViewGroup;

import com.example.schedulemanager.R;
import com.example.schedulemanager.helper.EventHelper;

public class ManagerPanelEvent implements View.OnClickListener {

    ManagerPanel managerPanel;

    public ManagerPanelEvent(ManagerPanel managerPanel) {
        this.managerPanel = managerPanel;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.managerCloseBtn:
                managerPanelLayoutOff();
                etcLayoutON();
                break;

            default:
                menubarTitleItemClickEvent(view);
        }
    }

    private void etcLayoutON() {
        EventHelper.eventHelper.getEtcPanel().setEtcLayoutVisible(true);
    }

    private void managerPanelLayoutOff() {
        managerPanel.setManagerLayoutVisible(false);
    }

    private void menubarTitleItemClickEvent(View view) {
        ViewGroup viewParent = (ViewGroup) view.getParent();
        View detailView = viewParent.getChildAt(viewParent.indexOfChild(view) + 1);
        detailView.setVisibility(detailView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }
}