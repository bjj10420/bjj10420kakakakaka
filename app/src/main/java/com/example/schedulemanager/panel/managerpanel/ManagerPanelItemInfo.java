package com.example.schedulemanager.panel.managerpanel;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.schedulemanager.R;
import com.example.schedulemanager.vo.ActivityVO;

public class ManagerPanelItemInfo {
    View infoView;
    private AlertDialog alert;

    public View init(Context context, View view){
        infoView = initPanelItemInfoView(context, (ActivityVO) view.getTag());
        setInfoViewEvent(infoView);
        return infoView;
    }

    private void setInfoViewEvent(final View infoView) {
        View closeBtn = infoView.findViewById(R.id.itemInfoCloseBtn);
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
        setItemInfoView(itemInfoView, activityVO);
        return itemInfoView;
    }

    private void setItemInfoView(View itemInfoView, ActivityVO activityVO) {
        TextView activityName = (TextView) itemInfoView.findViewById(R.id.itemInfoActivity);
        activityName.setText(activityVO.getActivityName());
    }

    public void setAlert(AlertDialog alert) {
        this.alert = alert;
    }


}
