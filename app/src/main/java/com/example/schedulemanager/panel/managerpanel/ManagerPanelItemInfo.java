package com.example.schedulemanager.panel.managerpanel;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.schedulemanager.R;
import com.example.schedulemanager.Util;
import com.example.schedulemanager.vo.ActivityVO;

public class ManagerPanelItemInfo {

    View infoView;
    private AlertDialog alert;
    private Context context;

    public View init(Context context, View view){
        this.context = context;
        infoView = initPanelItemInfoView(context, (ActivityVO) view.getTag());
        setInfoViewEvent(infoView);
        return infoView;
    }

    private void setInfoViewEvent(final View infoView) {
        View closeBtn = infoView.findViewById(R.id.itemInfoCloseBtn);
        setCloseBtnClickEvent(closeBtn);
    }

    private void setCloseBtnClickEvent(View closeBtn) {
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
    }

    private View initPanelItemInfoView(Context context, ActivityVO activityVO) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemInfoView = inflater.inflate(R.layout.manager_item_info, null);
        itemInfoView.setMinimumHeight((int) Util.convertDpToPixel(500));
        setItemInfoView(itemInfoView, activityVO);
        return itemInfoView;
    }

    private void setItemInfoView(View itemInfoView, ActivityVO activityVO) {
        setActivityName(itemInfoView, activityVO);
        setActivityIcon(itemInfoView, activityVO);
    }

    private void setActivityIcon(View itemInfoView, ActivityVO activityVO) {
        View activityIconView = itemInfoView.findViewById(R.id.itemInfoIcon);
        activityIconView.setBackground(new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(activityVO.getImageData(),0,activityVO.getImageData().length)));
    }

    private void setActivityName(View itemInfoView, ActivityVO activityVO) {
        TextView activityName = (TextView) itemInfoView.findViewById(R.id.itemInfoActivity);
        activityName.setText(activityVO.getActivityName());
    }

    public void setAlert(AlertDialog alert) {
        this.alert = alert;
    }


}
