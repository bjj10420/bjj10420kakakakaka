package com.example.schedulemanager.helper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.schedulemanager.Util;
import com.example.schedulemanager.interface_.GeneralCallback;
import com.example.schedulemanager.interface_.GeneralCallback2;
import com.example.schedulemanager.panel.managerpanel.ManagerPanelItemAdd;
import com.example.schedulemanager.panel.managerpanel.ManagerPanelItemInfo;
import com.example.schedulemanager.panel.managerpanel.ManagerUserInput;

/**
 * 여러가지 용도의 다이얼로그 박스를 생성
 */
public class DialogHelper {

    private AlertDialog alert;


    public void setOneBtnStyleWithTextDialog(Context context, String message, final GeneralCallback2 callback1, String btnString){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(context, 3);
        final EditText et = new EditText(context);

        alt_bld.setMessage(message).setView(et).setCancelable(
                true).setPositiveButton(btnString,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        callback1.onCallBack(et.getText().toString());
                    }
                });
        AlertDialog alert = alt_bld.create();

        // Icon for AlertDialog
        alert.show();
    }


    public void setOneBtnStyleDialog(Context context, final GeneralCallback callback1, String btnString){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(context, 3);
        alt_bld.setCancelable(
                true).setPositiveButton(btnString,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        callback1.onCallBack();
                    }
                });
        AlertDialog alert = alt_bld.create();

        // Icon for AlertDialog
        alert.show();
    }


    /**
     * 선택형 다이얼 로그 생성
     */
    public void setChoiceStyleDialog(Context context, final GeneralCallback callback1, final GeneralCallback callback2, String leftBtnString, String rightBtnString){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(context, 3);
        alt_bld.setCancelable(
                true).setPositiveButton(rightBtnString,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        callback1.onCallBack();
                    }
                }).setNegativeButton(leftBtnString,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        callback2.onCallBack();

                    }
                });
        AlertDialog alert = alt_bld.create();
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
//
    /**
     * 활동 관리 창에서 아이템 클릭시나타나는 아이템정보표시 다이얼로그
     */
    public void showActivityItemInfoDialog(Context context, View view){
        ManagerPanelItemInfo itemInfo =  new ManagerPanelItemInfo();
        View dialogView = itemInfo.init(context, view);
        setItemInfoAlertDialog(context, itemInfo, dialogView);
    }

    /**
     * 기타 패널에서 추가 버튼 클릭시나타나는 아이템정보표시 다이얼로그
     */
    public void showActivityItemAddDialog(Context context){
        ManagerPanelItemAdd itemAdd =  new ManagerPanelItemAdd();
        View dialogView = itemAdd.init(context);
        setItemAddAlertDialog(context, itemAdd, dialogView);
    }

    public void showUserInputDialog(Context context){
        ManagerUserInput uesrInput =  new ManagerUserInput();
        View dialogView = uesrInput.init(context);
        setUserInputAlertDialog(context, uesrInput, dialogView);
    }

    private void setItemInfoAlertDialog(Context context, ManagerPanelItemInfo itemInfo, View dialogView) {
        Dialog dialog = new Dialog(context);
        setDialogBasicOptions(dialog, dialogView, 300, 250);
        itemInfo.setAlert(dialog);
    }

    private void setItemAddAlertDialog(Context context, ManagerPanelItemAdd itemAdd, View dialogView) {
        Dialog dialog = new Dialog(context);
        setDialogBasicOptions(dialog, dialogView, 300, 300);
        itemAdd.setAlert(dialog);
    }

    private void setUserInputAlertDialog(Context context, ManagerUserInput userInput, View dialogView) {
        Dialog dialog = new Dialog(context);
        setDialogBasicOptions(dialog, dialogView, 300, 300);
        userInput.setAlert(dialog);
    }

    private void setDialogBasicOptions(Dialog dialog, View dialogView, int width, int height) {
        dialog.setContentView(dialogView);
        WindowManager.LayoutParams params = makeDialogParams(dialog, width, height);
        dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    private WindowManager.LayoutParams makeDialogParams(Dialog dialog, int width, int height) {
        WindowManager.LayoutParams params =  dialog.getWindow().getAttributes();
        params.width = (int) Util.convertDpToPixel(width);
        params.height = (int) Util.convertDpToPixel(height);
        return  params;
    }

    /**
     * 메세지를 설정한 선택형 다이얼 로그 생성
     */
    public void setChoiceStyleDialogWithMessage(Context context, final GeneralCallback callback1, final GeneralCallback callback2, String leftBtnString, String rightBtnString, String message){
        final AlertDialog.Builder alt_bld = new AlertDialog.Builder(context, 3);
        alt_bld.setMessage(message).setCancelable(
                true).setPositiveButton(rightBtnString,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(callback1 != null)
                            callback1.onCallBack();
                        else
                            alert.dismiss();
                    }
                }).setNegativeButton(leftBtnString,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
//                        dialog.cancel();
                        callback2.onCallBack();

                    }
                });
        alert = alt_bld.create();

        // Icon for AlertDialog
        alert.show();
    }
}
