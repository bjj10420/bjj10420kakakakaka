package com.example.schedulemanager.task;


import android.content.Context;
import android.os.AsyncTask;

import com.example.schedulemanager.Util;
import com.example.schedulemanager.panel.etcpanel.ETCPanel;
import com.example.schedulemanager.panel.managerpanel.ManagerPanel;

import static com.example.schedulemanager.helper.DataHelper.dataHelper;
import static com.example.schedulemanager.helper.EventHelper.eventHelper;
import static com.example.schedulemanager.helper.UIHelper.uiHelper;

public class PanelLoadigTask extends AsyncTask<Integer, Integer, Void>{

    Context context;
    ETCPanel etcPanel;
    ManagerPanel managerPanel;

    public PanelLoadigTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        etcPanel = new ETCPanel(context);
        managerPanel = new ManagerPanel(context);
        publishProgress();
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Util.customToast(context, "로딩을 완료했습니다");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        etcPanel.initETCPanel();
        eventHelper.setEtcPanel(etcPanel);
        managerPanel.initManagerPanel();
        eventHelper.setManagerPanel(managerPanel);
    }
}
