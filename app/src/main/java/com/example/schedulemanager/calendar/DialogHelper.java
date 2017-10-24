package com.example.schedulemanager.calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;

import com.example.schedulemanager.R;
import com.example.schedulemanager.activity.MainActivity;
import com.example.schedulemanager.interface_.GeneralCallback;
import com.example.schedulemanager.interface_.GeneralCallback2;

/**
 * 여러가지 용도의 다이얼로그 박스를 생성
 */
public class DialogHelper {

    /**
     * 선택형 다이얼 로그 생성
     */
    public void setChoiceStyleDialog(Context context, final GeneralCallback callback1, final GeneralCallback callback2){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(context, 3);
        alt_bld.setCancelable(
                true).setPositiveButton("삭제",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        callback1.onCallBack();
                    }
                }).setNegativeButton("메모 첨부",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
//                        dialog.cancel();
                        callback2.onCallBack();

                    }
                });
        AlertDialog alert = alt_bld.create();

        // Icon for AlertDialog
        alert.show();
    }

    /**
     * 메세지 입력형 다이얼 로그 생성
     */
    public void setMessageDialog(Context context, final GeneralCallback2 callback1, final GeneralCallback callback2){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(context, 3);
        final EditText et = new EditText(context);

        alt_bld.setMessage("메모 입력").setView(et).setCancelable(
                true).setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("메모입력테스트", et.getText().toString());
                        if(callback1 != null)
                        callback1.onCallBack(et.getText().toString());
                    }
                }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
//                        dialog.cancel();
                        if(callback2 != null)
                        callback2.onCallBack();

                    }
                });

        AlertDialog alert = alt_bld.create();

        // Icon for AlertDialog
        alert.show();
    }

    /**
     * 활동 관리 창에서 아이템 클릭시나타나는 아이템정보표시 다이얼로그
     */
    public void showActivityItemDialog(Context context, final GeneralCallback2 callback1, final GeneralCallback callback2){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(context, 3);
        final EditText et = new EditText(context);
//
        alt_bld.setMessage("메모 입력").setView(R.layout.manager_item_info);


        AlertDialog alert = alt_bld.create();

        // Icon for AlertDialog
        alert.show();
    }
}
