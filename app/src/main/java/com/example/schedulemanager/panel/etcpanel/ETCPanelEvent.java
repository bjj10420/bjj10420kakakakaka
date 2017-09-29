package com.example.schedulemanager.panel.etcpanel;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.schedulemanager.helper.EventHelper;
import com.example.schedulemanager.helper.UIHelper;

public class ETCPanelEvent implements View.OnClickListener{

    ETCPanel etcPanel;
    View copiedView;
    private int _xDelta;
    private int _yDelta;

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
        UIHelper.uiHelper.hoverView(v, true);
        setCopiedView(v);
    }

    private void setCopiedView(View v) {
        int[] numberArray = new int[2];
        int originalViewLeft = getPositionValue(numberArray, 0, v);
        int originalViewTop = getPositionValue(numberArray, 1, v);
        getCopiedView();
        setCopiedViewTouchEvent();
        setCopiedViewPosition(originalViewLeft, originalViewTop);
    }

    private void getCopiedView() {
        copiedView = UIHelper.uiHelper.getCopiedView();
   }

    private void setCopiedViewTouchEvent() {
        copiedView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int X = (int) event.getRawX();
                final int Y = (int) event.getRawY();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) copiedView.getLayoutParams();
                        _xDelta = X - lParams.leftMargin;
                        _yDelta = Y - lParams.topMargin;
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moveCopiedView(X, Y);
                        actionBasicEvent();
                        break;
                }
                UIHelper.uiHelper.getTotalLayout().invalidate();
                return true;
            }
        });
    }

    private void actionBasicEvent() {
        EventHelper.eventHelper.actionMoveBasicEvent(copiedView);
    }

    private void moveCopiedView(int X, int Y) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) copiedView
                .getLayoutParams();
        layoutParams.leftMargin = X - _xDelta;
        layoutParams.topMargin = Y - _yDelta;
        layoutParams.rightMargin = -250;
        layoutParams.bottomMargin = -250;
        copiedView.setLayoutParams(layoutParams);
    }

    private int getPositionValue(int[] numberArray, int arrayIndex, View v){
        v.getLocationInWindow(numberArray);
        return numberArray[arrayIndex];
    }

    private void setCopiedViewPosition(int originalViewLeft, int originalViewTop) {
        copiedView.setX(originalViewLeft);
        copiedView.setY(originalViewTop);
    }

    private void panelLayoutOff() {
        etcPanel.setEtcLayoutVisible(false);
        UIHelper.uiHelper.setTotalLayoutVisible(true);
    }
}
