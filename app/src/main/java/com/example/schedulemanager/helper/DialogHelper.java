package com.example.schedulemanager.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.schedulemanager.R;
import com.example.schedulemanager.Util;
import com.example.schedulemanager.interface_.GeneralCallback;
import com.example.schedulemanager.interface_.GeneralCallback2;
import com.example.schedulemanager.panel.managerpanel.ManagerPanelItemAdd;
import com.example.schedulemanager.panel.managerpanel.ManagerPanelItemInfo;
import com.example.schedulemanager.panel.managerpanel.ManagerUserInput;
import com.example.schedulemanager.vo.Schedule;
import com.github.mikephil.charting.data.PieEntry;

import me.drakeet.materialdialog.MaterialDialog;

import static com.example.schedulemanager.helper.EventHelper.eventHelper;

/**
 * 여러가지 용도의 다이얼로그 박스를 생성
 */
public class DialogHelper {

    private AlertDialog alert;


    public void setOneBtnStyleWithTextDialog(Context context, String message, final GeneralCallback2 callback1, String btnString){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(context, R.style.DialogCustomTheme);
        final EditText et = new EditText(context);
        et.setTextColor(R.color.strokeBrown);
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

        alert.show();
    }

    public void setDailyScheduleDialog(Context context, final GeneralCallback callback1, final GeneralCallback callback2, final GeneralCallback2 callback3, String leftBtnString, String rightBtnString){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(context, 3);
        alt_bld.setNeutralButton("변경", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callback3.onCallBack(null);
            }
        }).setCancelable(
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
        final EditText et = new EditText(context);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        MaterialDialog dialog = new MaterialDialog(context);
        setAddMemoDialogButton(dialog, callback1, callback2, et);
        setDialogBasicOptions(context, "메모 입력", dialog, et, 300, 250);
    }

    /**
     * 활동 관리 창에서 아이템 클릭시나타나는 아이템정보표시 다이얼로그
     */
    public void showActivityItemInfoDialog(Context context, View view){
        ManagerPanelItemInfo itemInfo =  new ManagerPanelItemInfo();
        View dialogView = itemInfo.init(context, view);
        setItemInfoAlertDialog(context, itemInfo, dialogView);
    }

    public void showScheduleInfoDialog(Context context, Schedule originalSchedule, PieEntry pieEntry, int orderValue){
        View dialogView = LayoutInflater.from(context).inflate(R.layout.schedule_modification, null);
        setScheduleInfoDialogViewContents(dialogView, originalSchedule);
        setScheduleInfoAlertDialog(context, dialogView, pieEntry, originalSchedule, orderValue);
    }

    private void setScheduleInfoDialogViewContents(View dialogView, Schedule originalSchedule) {
        TextView scheduleName = (TextView) dialogView.findViewById(R.id.scheduleModificationName);
        TextView scheduleMemo = (TextView) dialogView.findViewById(R.id.scheduleModificationMemo);
        scheduleName.setText(originalSchedule.getActivityName());
        scheduleMemo.setText(originalSchedule.getMemo());
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

    private void setScheduleInfoAlertDialog(Context context, View dialogView, PieEntry pieEntry, Schedule originalSchedule, int orderValue) {
        MaterialDialog dialog = new MaterialDialog(context);
        setScheduleInfoAlertDialogButton(dialog, dialogView, pieEntry, originalSchedule, orderValue);
        setDialogBasicOptions(context, "스케쥴 정보", dialog, dialogView, 300, 250);
    }

    private void setItemInfoAlertDialog(Context context, ManagerPanelItemInfo itemInfo, View dialogView) {
        MaterialDialog dialog = new MaterialDialog(context);
        setScheduleInfoAlertDialogButton(dialog, itemInfo);
        setDialogBasicOptions(context, "활동 정보", dialog, dialogView, 300, 250);
        itemInfo.setAlert(dialog);
    }

    private void setItemAddAlertDialog(Context context, ManagerPanelItemAdd itemAdd, View dialogView) {
        MaterialDialog dialog = new MaterialDialog(context);
        setAddAlertDialogButton(dialog, itemAdd);
        setDialogBasicOptions(context, "활동 추가", dialog, dialogView, 300, 300);
        itemAdd.setAlert(dialog);
    }

    private void setAddAlertDialogButton(final MaterialDialog dialog, final ManagerPanelItemAdd itemAdd) {
        dialog.setPositiveButton("추가", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemAdd.actionAddBtn();
            }
        });
    }

    private void setUserInputAlertDialogButton(final MaterialDialog dialog, final ManagerUserInput userInput) {
        dialog.setPositiveButton("입력", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInput.userInputBtn();
            }
        });
    }

    private void setAddMemoDialogButton(final MaterialDialog dialog, final GeneralCallback2 callback1, final GeneralCallback callback2, final EditText et){
        dialog.setNegativeButton("입력", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback1.onCallBack(et.getText().toString());
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton("취소", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void setScheduleInfoAlertDialogButton(final MaterialDialog dialog, final View dialogView, final PieEntry pieEntry, final Schedule originalSchedule, final int orderValue) {
        dialog.setNegativeButton("변경", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventHelper.modifyConfirm(dialogView, pieEntry, originalSchedule, orderValue);
                dialog.dismiss();

            }
        });
        dialog.setPositiveButton("취소", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    private void setScheduleInfoAlertDialogButton(final MaterialDialog dialog, final ManagerPanelItemInfo itemInfo) {
        dialog.setNegativeButton("변경", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemInfo.actionChangeBtn();
            }
        });
        dialog.setPositiveButton("삭제", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemInfo.actionRemoveBtn();
            }
        });
    }

    private void setUserInputAlertDialog(Context context, ManagerUserInput userInput, View dialogView) {
        MaterialDialog dialog = new MaterialDialog(context);
        setUserInputAlertDialogButton(dialog, userInput);
        setDialogBasicOptions(context, "사용자 입력", dialog, dialogView, 300, 300);
        userInput.setAlert(dialog);
    }

    private void setDialogBasicOptions(Context context, String title, final MaterialDialog dialog, View dialogView, int width, int height) {
        Util.setFontAllChildView(context, dialogView, DataHelper.dataHelper.getTypeface(), true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(dialogView);
        dialog.setTitle(title);
        dialog.show();
        setDialogButtonsBasicOptions(dialog);
    }

    private void setDialogButtonsBasicOptions(MaterialDialog dialog) {
        Button positiveButton = dialog.getPositiveButton();
        Button negativeButton = dialog.getNegativeButton();
        positiveButton.setTextColor(Color.parseColor("#404040"));
        negativeButton.setTextColor(Color.parseColor("#404040"));

        positiveButton.setTypeface(DataHelper.dataHelper.getTypeface(), Typeface.BOLD);
        if(negativeButton != null)
            negativeButton.setTypeface(DataHelper.dataHelper.getTypeface(), Typeface.BOLD);
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

    public void showCategorySelect(Context context, String title, CharSequence[] array, final GeneralCallback2 generalCallback) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, 5);
        builder.setTitle(title);
        builder.setCancelable(true);
        builder.setSingleChoiceItems(array, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        generalCallback.onCallBack(which);
                        }
                });
        builder.show();

    }
}
