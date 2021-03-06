package com.example.schedulemanager.panel.etcpanel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.schedulemanager.R;
import com.example.schedulemanager.activity.GuideActivity;
import com.example.schedulemanager.activity.ProgressActivity;
import com.example.schedulemanager.helper.DialogHelper;
import com.example.schedulemanager.helper.UIHelper;
import com.example.schedulemanager.panel.managerpanel.ManagerPanel;

import static com.example.schedulemanager.helper.EventHelper.eventHelper;

public class ETCPanelEvent implements View.OnClickListener, View.OnTouchListener{

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
            case R.id.etcCloseBtn : etcPanelFadeOutForTotalLayout();
                break;


            default : panelItemClickEvent(v, false);
        }
      }

    private void userInputEvent() {
        new DialogHelper().showUserInputDialog(context);
    }

    public void panelItemClickEvent(View v, boolean isFromUserInput) {
        etcPanelOffForTotalLayout();
        hoverCopiedView(v, isFromUserInput);
    }

    // 뷰를 복사하여 클릭된 지점의 뷰와 같은 위치에 넣음
    private void hoverCopiedView(View v, boolean isFromUserInput) {
        UIHelper.uiHelper.hoverView(v, true);
        setCopiedView(v, isFromUserInput);
    }

    private void setCopiedView(View v, boolean isFromUserInput) {
        int[] numberArray = new int[2];
        int originalViewLeft = getPositionValue(numberArray, 0, v);
        int originalViewTop = getPositionValue(numberArray, 1, v);
        getCopiedView();
        setCopiedViewTouchEvent();
        setCopiedViewPosition(originalViewLeft, originalViewTop, isFromUserInput);
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
        eventHelper.actionUpEvent(copiedView);
    }

    private void actionBasicMoveEvent() {
        eventHelper.actionMoveBasicEvent(copiedView, true);
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

    private void setCopiedViewPosition(int originalViewLeft, int originalViewTop, boolean isFromUserInput) {
        int x = UIHelper.uiHelper.getTotalLayout().getMeasuredWidth() / 2;
        int y = UIHelper.uiHelper.getTotalLayout().getMeasuredHeight() / 2;

        copiedView.setX(isFromUserInput ? x : originalViewLeft);
        copiedView.setY(isFromUserInput ? y : originalViewTop);
    }

    public void etcPanelFadeOutForTotalLayout() {
        etcPanel.getEtcLayout().animate().alpha(0.0f).withEndAction(new Runnable() {
            @Override
            public void run() {
                etcPanel.setEtcLayoutVisible(false);
                etcPanel.getEtcLayout().animate().alpha(1.0f);

            }
        });
        UIHelper.uiHelper.setTotalLayoutVisible(true);
    }

    public void etcPanelOffForTotalLayout() {
        etcPanel.setEtcLayoutVisible(false);
        UIHelper.uiHelper.setTotalLayoutVisible(true);
    }

    public void panelLayoutOffForManagerPanel() {
        final ManagerPanel managerPanel = eventHelper.getManagerPanel();
        if(managerPanel == null){
            Intent intent = new Intent(context, ProgressActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(0, 0);

        }
        else
            UIHelper.uiHelper.slideUpManagerPanel(managerPanel, etcPanel);

    }


    private void addItemClickEvent() {
        new DialogHelper().showActivityItemAddDialog(context);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()){
            case MotionEvent.ACTION_DOWN :
                changeButtonColorWhenDown(view);
                break;
            case MotionEvent.ACTION_UP :
                restoreButtonColorWhenUp(view);
                excuteByButtonId(view);

                break;
            case MotionEvent.ACTION_MOVE :
                restoreButtonColorWhenUp(view);
                break;
        }
        return true;
    }

    private void excuteByButtonId(View view) {
        switch (view.getId()){
            case R.id.etcManagerBtn :
                restoreButtonColorWhenUp(view);
                panelLayoutOffForManagerPanel();
                break;
            case R.id.etcAddBtn : addItemClickEvent();
                break;
            case R.id.etcGuideBtn : guideEvent();
                break;
            case R.id.etcUserInputBtn : userInputEvent();
                break;
        }
    }

    private void guideEvent() {
        Intent intent = new Intent(context, GuideActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
    }

    private void restoreButtonColorWhenUp(View view) {
        setIconViewColorFilter(view, false);
        setTextViewColorFilter(view, false);
    }

    private void changeButtonColorWhenDown(final View view) {
        setIconViewColorFilter(view, true);
        setTextViewColorFilter(view, true);
    }

    private void setIconViewColorFilter(View view, boolean isFilter) {
        View icon = ((LinearLayout) view).getChildAt(0);
        if(isFilter)
            icon.getBackground().setColorFilter(Color.parseColor("#8b461f"), PorterDuff.Mode.SRC_IN);
        else
            icon.getBackground().clearColorFilter();
    }

    private void setTextViewColorFilter(View view, boolean isFilter) {
        TextView text = (TextView) ((LinearLayout) view).getChildAt(1);
        if(isFilter)
            text.setTextColor(Color.parseColor("#8b461f"));
        else
            text.setTextColor(Color.parseColor("#b9b9b9"));
    }


}
