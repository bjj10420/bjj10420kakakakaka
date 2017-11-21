package com.example.schedulemanager.helper;

import android.content.Context;
import com.example.schedulemanager.task.IconBoxTask;
import com.example.schedulemanager.task.PanelLoadigTask;
import static com.example.schedulemanager.helper.DataHelper.dataHelper;
import static com.example.schedulemanager.helper.EventHelper.eventHelper;

public class TaskHelper {

    Context context;

    public TaskHelper(Context context) {
        this.context = context;
    }

    public void loadETCAndManagerPanel() {
        if (eventHelper.getEtcPanel() == null && eventHelper.getManagerPanel() == null) {
            PanelLoadigTask panelLoadigTask = new PanelLoadigTask(context);
            panelLoadigTask.execute();
        }
    }

    public void loadIconBox(){
        if(dataHelper.getDrawableList().isEmpty())
        new IconBoxTask(context).execute();
    }

}
