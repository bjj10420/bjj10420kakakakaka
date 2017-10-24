package com.example.schedulemanager.panel.managerpanel;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.schedulemanager.R;

public class ManagerPanelItemInfo {
    View infoView;
    private AlertDialog alert;

    public View init(Context context){
        infoView = initPanelItemInfoView(context);
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

    private View initPanelItemInfoView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemIfnoView = inflater.inflate(R.layout.manager_item_info, null);
        return itemIfnoView;
    }

    public void setAlert(AlertDialog alert) {
        this.alert = alert;
    }
}
