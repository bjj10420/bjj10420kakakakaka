package com.example.schedulemanager.task;


import android.content.Context;
import android.os.AsyncTask;

import com.example.schedulemanager.panel.managerpanel.ManagerPanel;

import static com.example.schedulemanager.helper.EventHelper.eventHelper;

public class ManagerTask extends AsyncTask<Integer, Integer, Void>{

    Context context;
    ManagerPanel managerPanel;

    public ManagerTask(Context context) {
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
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        managerPanel.initManagerPanel();
        eventHelper.setManagerPanel(managerPanel);
   }
}
