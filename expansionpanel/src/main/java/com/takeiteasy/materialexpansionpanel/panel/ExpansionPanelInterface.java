package com.takeiteasy.materialexpansionpanel.panel;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by VLogachev on 19.10.2016.
 */

public interface ExpansionPanelInterface {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ExpansionPanelStates.EXPANDED, ExpansionPanelStates.COLLAPSED})
    @interface ExpansionPanelStates {
        int EXPANDED = 0;
        int COLLAPSED = 1;
    }

    interface OnClickListener {
        void onPositiveButtonClicked();
        void onNegativeButtonClicked();
    }

    void expand();

    void collapse();
}
