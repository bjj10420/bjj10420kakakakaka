package com.example.schedulemanager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * 유틸리티 클래스
 */
public class Util {

    // 디피를 픽셀로 변환
    public static float convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    // 리소스내의 파일이름을 가지고 아이디를 찾아주는 함수
    public static int findIdByFileName(String name, Context mContext) {
        Resources res = mContext.getResources();
        int id = res.getIdentifier(name, "id", mContext.getPackageName());
        return id;
    }

    /**
     * 두 뷰의 충돌 판정을 체크
     * @param v1
     * @param v2
     * @return
     */
    public static boolean checkCollision(View v1, View v2) {
        Rect R1=new Rect(v1.getLeft(), v1.getTop(), v1.getRight(), v1.getBottom());
        Rect R2=new Rect((int)v2.getTranslationX(), (int)v2.getTranslationY(), (int)v2.getTranslationX() + v2.getWidth(), (int)v2.getTranslationY() + v2.getHeight());
        return R1.intersect(R2);
    }

    /**
     * 날짜값에서 월값 추출
     * @param scheduleDate
     * @return
     */
    public static String getMonthFromDate(String scheduleDate) {
        String month = scheduleDate.substring(4,6);
        return month;
    }
}