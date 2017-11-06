package com.example.schedulemanager.panel.etcpanel;

import android.animation.Animator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.schedulemanager.R;
import com.example.schedulemanager.helper.DialogHelper;
import com.example.schedulemanager.helper.EventHelper;
import com.example.schedulemanager.helper.UIHelper;
import com.example.schedulemanager.panel.managerpanel.ManagerPanel;

public class ETCPanelEvent implements View.OnClickListener{

    Context context;
    ETCPanel etcPanel;
    View copiedView;
    private int _xDelta;
    private int _yDelta;

    public ETCPanelEvent(ETCPanel etcPanel, Context context) {
        this.context = context;
        this.etcPanel = etcPanel;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.etcCloseBtn : panelLayoutOffForTotalLayout();
                break;
            case R.id.etcManagerBtn : panelLayoutOffForManagerPanel();
                break;
            case R.id.etcAddBtn : addItemClickEvent();
                break;
            case R.id.etcUserInputBtn : userInputEvent();
                break;

            default : panelItemClickEvent(v);
        }
      }

    private void userInputEvent() {
        new DialogHelper().showUserInputDialog(context);
    }

    public void panelItemClickEvent(View v) {
        panelLayoutOffForTotalLayout();
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
                        actionBasicUpEvent(copiedView);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moveCopiedView(X, Y);
                        actionBasicMoveEvent();
                        break;
                }
                UIHelper.uiHelper.getTotalLayout().invalidate();
                return true;
            }
        });
    }

    private void actionBasicUpEvent(View copiedView) {
        EventHelper.eventHelper.actionUpEvent(copiedView);
    }

    private void actionBasicMoveEvent() {
        EventHelper.eventHelper.actionMoveBasicEvent(copiedView, true);
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

    public void panelLayoutOffForTotalLayout() {
        etcPanel.setEtcLayoutVisible(false);
        UIHelper.uiHelper.setTotalLayoutVisible(true);
    }

    public void panelLayoutOffForManagerPanel() {
        ManagerPanel managerPanel = EventHelper.eventHelper.getManagerPanel();
        UIHelper.uiHelper.slideUpManagerPanel(managerPanel, etcPanel);
    }


    private void addItemClickEvent() {
        new DialogHelper().showActivityItemAddDialog(context);
    }

}
