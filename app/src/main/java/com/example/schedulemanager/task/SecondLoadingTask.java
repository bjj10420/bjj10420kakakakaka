package com.example.schedulemanager.task;


import android.content.Context;
import android.os.AsyncTask;

import com.example.schedulemanager.Util;
import com.example.schedulemanager.panel.managerpanel.ManagerPanel;

import static com.example.schedulemanager.helper.DataHelper.dataHelper;
import static com.example.schedulemanager.helper.EventHelper.eventHelper;

public class SecondLoadingTask extends AsyncTask<Integer, Integer, Void>{

    Context context;
    ManagerPanel managerPanel;

    public SecondLoadingTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        managerPanel = new ManagerPanel(context);
        publishProgress();
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Util.customToast(context, "아이콘 박스 로딩 완료");
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        dataHelper.loadBasicDrawables();
        managerPanel.initManagerPanel();
        eventHelper.setManagerPanel(managerPanel);

    }
}
