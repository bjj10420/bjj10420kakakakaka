package com.example.schedulemanager;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
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
     * 두 뷰의 충돌 판정을 체크 (v1이 최상위 레이아웃의 자식인 경우)
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
     * 두 뷰의 충돌 판정을 체크 (v1이 다른레이아웃의 자식인 경우)
     * @param v1
     * @param v2
     * @return
     */
    public static boolean checkCollisionForChildView(View v1, View v2) {
        int[] numberArray = new int[2];

        v1.getLocationInWindow(numberArray);

        Rect R1=new Rect(numberArray[0], numberArray[1], numberArray[0] + v1.getWidth(), numberArray[1] + v1.getHeight());
        Rect R2=new Rect((int)v2.getTranslationX(), (int)v2.getTranslationY(), (int)v2.getTranslationX() + v2.getWidth(), (int)v2.getTranslationY() + v2.getHeight());
        Log.d("충돌체크 함수 rect값 검사 R1 ", numberArray[0] +  " , " + numberArray[1]);
        Log.d("충돌체크 함수 rect값 검사 R2 ", (int)v2.getTranslationX() +  " , " + (int)v2.getTranslationY());

        return R1.intersect(R2);
    }

    public static boolean checkCollisionByRect(Rect rect1, Rect rect2) {
        return rect1.intersect(rect2);
    }

    /**
     * 두 뷰의 충돌 판정을 체크 (v1이 다른레이아웃의 자식인 경우)
     * @param v1
     * @param v2
     * @return
     */
    public static boolean checkCollisionForChildView2(View v1, View v2) {
        int r1Left = (int)v1.getLeft() - 50;
        int r1Top = (int)v1.getTop() + 570;

        Rect R1 = new Rect(r1Left, r1Top, r1Left + v1.getWidth(), r1Top + v1.getHeight());
        Rect R2 = new Rect((int)v2.getTranslationX(), (int)v2.getTranslationY(), (int)v2.getTranslationX() + v2.getWidth(), (int)v2.getTranslationY() + v2.getHeight());

//      if(v1.getTag() != null)
        Log.d("충돌체크 함수 rect값 검사 v1 태그 ", (String) v1.getTag() + "일");
//      Log.d("충돌체크 함수 rect값 검사 R1 ", numberArray[0] +  " , " + numberArray[1]);
        Log.d("충돌체크 함수 rect값 검사 R1 ", r1Left +  " , " + r1Top);
        Log.d("충돌체크 함수 rect값 검사 R2 ", (int)v2.getTranslationX() +  " , " + (int)v2.getTranslationY());

        return R1.intersect(R2);
    }

    /**
     * 날짜값에서 연월값 추출
     * @param scheduleDate
     * @return
     */
    public static String getYearMonthFromDate(String scheduleDate) {
        String yearMonth = scheduleDate.substring(0,6);
        return yearMonth;
    }

    /**
     * 화면상의 두점 사이의 거리
     */
    public static double getDistanceFromTwoPoints(float x1, float y1, float x2, float y2){
        double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        return distance;
    }

    /**
     * context와 resourceId를 가지고 뷰를 구해주는 함수
     */
    public static View getViewById(Context context,int resourceId){
        View view = ((Activity) context).findViewById(resourceId);
        return view;
    }
}
