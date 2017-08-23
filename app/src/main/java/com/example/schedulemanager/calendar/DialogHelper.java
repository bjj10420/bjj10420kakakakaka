package com.example.schedulemanager.calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.schedulemanager.interface_.GeneralCallback;

/**
 * 여러가지 용도의 다이얼로그 박스를 생성
 */
public class DialogHelper {

    /**
     * 선택형 다이얼 로그 생성
     */
    public void setChoiceStyleDialog(Context context, final GeneralCallback callback1){
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
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();

        // Icon for AlertDialog
        alert.show();
    }
}
