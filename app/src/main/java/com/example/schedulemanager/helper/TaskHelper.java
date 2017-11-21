package com.example.schedulemanager.helper;

import android.content.Context;
import android.os.AsyncTask;

import com.example.schedulemanager.interface_.GeneralCallback;
import com.example.schedulemanager.task.IconBoxLoadingTask;
import com.example.schedulemanager.task.ETCPanelLoadingTask;
import com.example.schedulemanager.task.ManagerPanelLoadingTask;

import static com.example.schedulemanager.helper.DataHelper.dataHelper;
import static com.example.schedulemanager.helper.EventHelper.eventHelper;

public class TaskHelper {

    Context context;

    public TaskHelper(Context context) {
        this.context = context;
    }

    public void loadETCPanel() {
        if (eventHelper.getEtcPanel() == null) {
            new ETCPanelLoadingTask(context).execute();
        }
    }

    public void loadIconBox(GeneralCallback generalCallback){
        if(dataHelper.getDrawableList().isEmpty())
        new IconBoxLoadingTask(context, generalCallback).execute();
    }

    public AsyncTask<Integer, Integer, Void> loadManagerPanel(){
        if(eventHelper.getManagerPanel() == null)
            return new ManagerPanelLoadingTask(context).execute();
        return null;
    }

}
