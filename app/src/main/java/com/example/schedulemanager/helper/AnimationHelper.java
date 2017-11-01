package com.example.schedulemanager.helper;

import android.animation.ObjectAnimator;
import android.view.View;

import com.daasuu.ei.Ease;
import com.daasuu.ei.EasingInterpolator;

/**
 * Created by bjj10 on 2017-11-01.
 */

public class AnimationHelper {

    public void init(){
        doBounceAnimation(UIHelper.uiHelper.getCenterIcon());
    }

    private void doBounceAnimation(View targetView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(targetView, "translationY", 0, 25, 0);
        animator.setInterpolator(new EasingInterpolator(Ease.ELASTIC_IN_OUT));
        animator.setStartDelay(100);
        animator.setDuration(15000);
        animator.start();
    }
}
