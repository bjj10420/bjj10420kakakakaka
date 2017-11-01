package com.example.schedulemanager.helper;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Animation;

import com.daasuu.ei.Ease;
import com.daasuu.ei.EasingInterpolator;

public class AnimationHelper {

    public void init(){
        doBounceAnimation(UIHelper.uiHelper.getCenterIcon());
    }

    private void doBounceAnimation(View targetView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(targetView, "translationY", 0, -50, 0);
        animator.setInterpolator(new EasingInterpolator(Ease.QUAD_IN_OUT));
        animator.setStartDelay(50);
        animator.setDuration(2500);
        animator.setRepeatCount(Animation.INFINITE);
        animator.start();
    }
}
