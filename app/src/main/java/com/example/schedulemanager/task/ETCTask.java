package com.example.schedulemanager.task;


import android.content.Context;
import android.os.AsyncTask;
import com.example.schedulemanager.panel.etcpanel.ETCPanel;
import static com.example.schedulemanager.helper.EventHelper.eventHelper;
import static com.example.schedulemanager.helper.UIHelper.uiHelper;

public class ETCTask extends AsyncTask<Integer, Integer, Void>{

    Context context;
    ETCPanel etcPanel;

    public ETCTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Integer... integers) {

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        uiHelper.setTotalLayoutVisible(false);
        uiHelper.setProgressViewVisible(true);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        etcPanel = new ETCPanel(context);
        etcPanel.initETCPanel();
        eventHelper.setEtcPanel(etcPanel);

        uiHelper.setProgressViewVisible(false);
        etcPanel.setEtcLayoutVisible(true);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
   }
}
