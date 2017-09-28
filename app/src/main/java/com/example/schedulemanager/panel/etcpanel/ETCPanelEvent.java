package com.example.schedulemanager.panel.etcpanel;

import android.view.View;

import com.example.schedulemanager.helper.UIHelper;

/**
 * Created by bjj on 2017-09-28.
 */
public class ETCPanelEvent implements View.OnClickListener{

    ETCPanel etcPanel;

    public ETCPanelEvent(ETCPanel etcPanel) {
        this.etcPanel = etcPanel;
    }

    @Override
    public void onClick(View v) {
        panelItemClickEVent(v);
      }

    private void panelItemClickEVent(View v) {
        etcPanel.setEtcLayoutVisible(false);
        UIHelper.uiHelper.setTotalLayoutVisible(true);
    }
}
