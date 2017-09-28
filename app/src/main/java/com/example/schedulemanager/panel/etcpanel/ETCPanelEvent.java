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
        panelLayoutOff();
        hoverCopiedView(v);
    }

    // 뷰를 복사하여 클릭된 지점의 뷰와 같은 위치에 넣음
    private void hoverCopiedView(View v) {
        int[] numberArray = new int[2];
        v.getLocationInWindow(numberArray);
        int originalViewLeft = numberArray[0];
        int originalViewTop = numberArray[1];
        UIHelper.uiHelper.hoverView(v, true);
    }

    private void panelLayoutOff() {
        etcPanel.setEtcLayoutVisible(false);
        UIHelper.uiHelper.setTotalLayoutVisible(true);
    }
}
