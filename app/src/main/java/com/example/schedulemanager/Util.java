package com.example.schedulemanager;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schedulemanager.helper.DataHelper;
import com.example.schedulemanager.interface_.CustomCondition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    // 기타패널아이콘을 중앙으로 할때 체크
    public static boolean checkCollisionForEtcIcon(View v1, View v2) {
        int[] numberArray = new int[2];
        v2.getLocationInWindow(numberArray);
        Rect R1=new Rect(v1.getLeft(), v1.getTop(), v1.getRight(), v1.getBottom());
        Rect R2=new Rect(numberArray[0], numberArray[1], numberArray[0] + v2.getWidth(), numberArray[1] + v2.getHeight());

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
        return R1.intersect(R2);
    }

    public static boolean checkCollisionByRect(Rect rect1, Rect rect2) {
        return rect1.intersect(rect2);
    }

    public static boolean checkCollisionForChildView2(View v1, View v2) {
        int[] numberArray = new int[2];
        int[] numberArray2 = new int[2];
        v1.getLocationInWindow(numberArray);
        v2.getLocationInWindow(numberArray2);
        Rect R1=new Rect(numberArray[0], numberArray[1], numberArray[0] + v1.getWidth(), numberArray[1] + v1.getHeight());
        Rect R2=new Rect(numberArray2[0], numberArray2[1], numberArray2[0] + v2.getWidth(), numberArray2[1] + v2.getHeight());
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

    /**
     * 부모뷰가 포함하는 모든 자식뷰들의 폰트를 설정해준다
     */
    public static void setFontAllChildView(Context context, final View parentView, Typeface typeface, boolean isBold) {
        try {
            if (parentView instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) parentView;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    setFontAllChildView(context, child, typeface, isBold);
                }
            } else if (parentView instanceof TextView ) {
                if(isBold)
                    ((TextView) parentView).setTypeface(typeface, Typeface.BOLD);
                else
                    ((TextView) parentView).setTypeface(typeface);
            }
        } catch (Exception e) {
        }
    }

    public static void setTextWithFont(TextView textView, String text){
        Typeface typeface = DataHelper.dataHelper.getTypeface();
        textView.setTypeface(typeface);
        textView.setText(text);
    }

    public static void setTextWithBoldFont(TextView textView, String text){
        Typeface typeface = DataHelper.dataHelper.getTypeface();
        textView.setTypeface(typeface, Typeface.BOLD);
        textView.setText(text);
    }

    public static boolean copyFile(String from, String to) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                int end = from.toString().lastIndexOf("/");
                String str1 = from.toString().substring(0, end);
                String str2 = from.toString().substring(end+1, from.length());
                File source = new File(str1, str2);
                File destination= new File(to, str2);
                if (source.exists()) {
                    FileChannel src = new FileInputStream(source).getChannel();
                    FileChannel dst = new FileOutputStream(destination).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean copyFileFromUri(Context context, Uri fileUri)
    {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            ContentResolver content = context.getContentResolver();
            inputStream = content.openInputStream(fileUri);

            File root = Environment.getExternalStorageDirectory();
            if (root == null) {
                Log.d("TAG", "Failed to get root");
            }

            // create a directory
            File saveDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "directory_name" + File.separator);
            // create direcotory if it doesn't exists
            saveDirectory.mkdirs();

            outputStream = new FileOutputStream(saveDirectory + "filename.extension"); // filename.png, .mp3, .mp4 ...
            if (outputStream != null) {
                Log.e("TAG", "Output Stream Opened successfully");
            }

            byte[] buffer = new byte[1000];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) >= 0) {
                outputStream.write(buffer, 0, buffer.length);
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception occurred " + e.getMessage());
        } finally {

        }
        return true;
    }

    public static void removeItemFromMap(HashMap<?, ?> map, CustomCondition customCondition){
        Iterator<? extends Map.Entry<?, ?>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<?, ?> entry = iter.next();
            if(customCondition.customCondition()){
                iter.remove();
            }
        }
    }

    public static void customToast(Context context, String msg) {
        LinearLayout layout = new LinearLayout(context);
        layout.setBackgroundResource(R.drawable.toast_bg);
        layout.setPadding(35,35,35,35);
        TextView tvToastMsg = new TextView(context);
        setTextWithFont(tvToastMsg, msg);
        tvToastMsg.setTextColor(Color.WHITE);
        tvToastMsg.setTextSize(16);
        layout.addView(tvToastMsg);
        Toast toastMsg = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        toastMsg.setView(layout);
        toastMsg.show();
    }

}
