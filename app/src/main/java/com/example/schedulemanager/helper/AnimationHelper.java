package com.example.schedulemanager.helper;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.daasuu.ei.Ease;
import com.daasuu.ei.EasingInterpolator;
import com.example.schedulemanager.R;

public class AnimationHelper {

    public void init(){
//        doBounceAnimation(UIHelper.uiHelper.getCenterIcon());
    }

    public void doBounceAnimation(View targetView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(targetView, "translationY", 0, -50, 0);
        animator.setInterpolator(new EasingInterpolator(Ease.QUAD_IN_OUT));
        animator.setStartDelay(50);
        animator.setDuration(2500);
        animator.setRepeatCount(Animation.INFINITE);
        animator.start();
    }


    public void fadeAnimation(Context context, final View tv, boolean isfadeOut) {
        final Animation animationFade;
        tv.setAlpha(0f);
        if (isfadeOut) {
            animationFade = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        } else {
            animationFade = AnimationUtils.loadAnimation(context,R.anim.fade_in);
        }
        Handler mhandler = new Handler();
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                tv.setAlpha(1f);
                tv.startAnimation(animationFade);
            }
        }, 0);
    }
}
