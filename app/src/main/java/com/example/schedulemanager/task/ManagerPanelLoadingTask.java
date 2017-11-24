package com.example.schedulemanager.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.schedulemanager.Util;
import com.example.schedulemanager.activity.ProgressActivity;
import com.example.schedulemanager.interface_.GeneralCallback;
import com.example.schedulemanager.interface_.GeneralCallback2;
import com.example.schedulemanager.panel.managerpanel.ManagerPanel;

import static com.example.schedulemanager.helper.DataHelper.dataHelper;
import static com.example.schedulemanager.helper.EventHelper.eventHelper;

public class ManagerPanelLoadingTask extends AsyncTask<Integer, Integer, Void>{

    Context context;
    ManagerPanel managerPanel;
    GeneralCallback2 callback;

    public ManagerPanelLoadingTask(Context context, GeneralCallback2 generalCallback) {
        this.context = context;
        this.callback = generalCallback;
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
        eventHelper.setManagerPanel(managerPanel);
        callback.onCallBack(managerPanel);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        managerPanel.initManagerPanel();
    }
}
