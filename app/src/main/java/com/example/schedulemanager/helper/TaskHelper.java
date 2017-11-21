package com.example.schedulemanager.helper;

import android.content.Context;
import com.example.schedulemanager.task.SecondLoadingTask;
import com.example.schedulemanager.task.FirstLoadigTask;
import static com.example.schedulemanager.helper.DataHelper.dataHelper;
import static com.example.schedulemanager.helper.EventHelper.eventHelper;

public class TaskHelper {

    Context context;

    public TaskHelper(Context context) {
        this.context = context;
    }

    public void loadETCPanel() {
        if (eventHelper.getEtcPanel() == null) {
            FirstLoadigTask firstLoadigTask = new FirstLoadigTask(context);
            firstLoadigTask.execute();
        }
    }

    public void loadIconBox(){
        if(dataHelper.getDrawableList().isEmpty() && eventHelper.getManagerPanel() == null)
        new SecondLoadingTask(context).execute();
    }

}
