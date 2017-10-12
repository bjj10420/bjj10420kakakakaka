package com.example.schedulemanager.panel.managerpanel;


import android.view.View;
import android.view.ViewGroup;

public class ManagerPanelEvent implements View.OnClickListener {

    ManagerPanel managerPanel;

    public ManagerPanelEvent(ManagerPanel managerPanel) {
        this.managerPanel = managerPanel;
    }

    @Override
    public void onClick(View view) {
        ViewGroup viewParent = (ViewGroup) view.getParent();
        View detailView = viewParent.getChildAt(viewParent.indexOfChild(view) + 1);
        detailView.setVisibility(View.VISIBLE);
    }
}
