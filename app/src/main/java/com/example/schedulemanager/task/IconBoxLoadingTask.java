package com.example.schedulemanager.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.schedulemanager.Util;
import com.example.schedulemanager.panel.managerpanel.ManagerPanel;

import static com.example.schedulemanager.helper.DataHelper.dataHelper;
import static com.example.schedulemanager.helper.EventHelper.eventHelper;

public class IconBoxLoadingTask extends AsyncTask<Integer, Integer, Void>{

    Context context;

    public IconBoxLoadingTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Integer... integers) {
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
        Log.d("아이콘 박스 로딩 완료", "아이콘 박스 로딩 완료");
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        dataHelper.loadBasicDrawables();
    }
}
