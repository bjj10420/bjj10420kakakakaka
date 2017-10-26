package com.example.schedulemanager.panel.managerpanel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.schedulemanager.R;
import com.example.schedulemanager.Util;
import com.example.schedulemanager.helper.DialogHelper;
import com.example.schedulemanager.interface_.GeneralCallback;

import static com.example.schedulemanager.helper.DataHelper.PICK_FROM_GALLARY;
import static com.example.schedulemanager.helper.DataHelper.dataHelper;

public class ManagerPanelItemAdd {
    private View addView;
    private Dialog alert;
    private Context context;

    public View init(Context context) {
        initField(context);
        initInfoIconBoxPanel();
        setAddViewEvent();
        return addView;
    }

    private void initInfoIconBoxPanel() {
        LinearLayout itemInfoIconBoxPanel = (LinearLayout) addView.findViewById(R.id.itemIconBoxPanel);
        LinearLayout rowLayout = null;
        for(Drawable drawable : dataHelper.getDrawableList()) {
            rowLayout = decideRowLayoutByChildCount(rowLayout);
            addBoxPanelItemViewToPanel(drawable, rowLayout, itemInfoIconBoxPanel);
        }
    }

    private LinearLayout decideRowLayoutByChildCount(LinearLayout rowLayout) {
        if(rowLayout != null && rowLayout.getChildCount() < 5) return rowLayout;
        else {
            LinearLayout newRowLayout = new LinearLayout(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            newRowLayout.setLayoutParams(params);
            return newRowLayout;
        }
    }

    private void addBoxPanelItemViewToPanel(Drawable drawable, LinearLayout rowLayout, LinearLayout itemInfoIconBoxPanel) {
        View iconBoxPanelItemView = makeBoxPanelItemView(drawable);
        rowLayout.addView(iconBoxPanelItemView);
        Log.d("addBoxPanelItemViewToPanel 체크", String.valueOf(itemInfoIconBoxPanel));
        if(rowLayout.getChildCount() == 4) itemInfoIconBoxPanel.addView(rowLayout);
    }

    private View makeBoxPanelItemView(Drawable drawable) {
        View boxPanelItemView = new View(context);
        boxPanelItemView.setBackground(drawable);
        LinearLayout.LayoutParams viewParams = makeViewParams();
        boxPanelItemView.setLayoutParams(viewParams);
        boxPanelItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionBoxPanelItemViewClicked(view);
            }
        });
        return  boxPanelItemView;
    }

    private void actionBoxPanelItemViewClicked(View view) {
        setInfoViewIcon(view);
        setItemInfoIconBoxPanelVisible(false);
        setItemInfoMainPanel(true);
    }

    private void setInfoViewIcon(View view) {
        View activityIconView = addView.findViewById(R.id.itemAddIcon);
        activityIconView.setBackground(view.getBackground());
    }

    private LinearLayout.LayoutParams makeViewParams() {
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(0,
                (int) Util.convertDpToPixel(50));
        viewParams.weight = 1;
        return viewParams;
    }

    private void initField(Context context) {
        this.context = context;
        addView = initPanelItemInfoView(context);
    }

    private void setAddViewEvent() {
        setBtnClickEvents();
        setIconBoxPanelEvents();
        setInfoIconClickEvent();
        setCategorySelectBtnEvent();
    }

    private void setCategorySelectBtnEvent() {
        View categorySelectBtn = addView.findViewById(R.id.itemAddCategorySelect);
        categorySelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCategorySelectBtn();
            }
        });

    }

    private void actionCategorySelectBtn() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Image Source");

        builder.setItems(dataHelper.getCategoryArray(),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
//
    private void setIconBoxPanelEvents() {
        setIconBoxPanelCloseEvent();
        setIconBoxPanelFileEvent();
    }

    private void setIconBoxPanelFileEvent() {
        View iconBoxPanelFileBtn = addView.findViewById(R.id.itemInfoIconBoxPanelFile);
        iconBoxPanelFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionIconBoxPanelFileBtn();
            }
        });
    }

    private void actionIconBoxPanelFileBtn() {
        // GET IMAGE FROM THE GALLERY
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        Intent chooser = Intent.createChooser(intent, "Choose a Picture");
        ((Activity)context).startActivityForResult(chooser, PICK_FROM_GALLARY);

    }

    private void setInfoIconClickEvent() {
        View activityIconView = addView.findViewById(R.id.itemAddIcon);
        activityIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionActivityIconView();
            }
        });
    }

    private void actionActivityIconView() {
        setItemInfoIconBoxPanelVisible(true);
        setItemInfoMainPanel(false);
    }


    private void setBtnClickEvents() {
        setAddBtnClickEvent();
        setCloseBtnClickEvent();
    }

    private void setIconBoxPanelCloseEvent() {
        View iconBoxPanelCloseBtn = addView.findViewById(R.id.itemInfoIconBoxPanelClose);
        iconBoxPanelCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionIconBoxPanelCloseBtn();
            }
        });
    }

    private void actionIconBoxPanelCloseBtn() {
        setItemInfoIconBoxPanelVisible(false);
        setItemInfoMainPanel(true);
    }

    private void setItemInfoMainPanel(boolean isVisible) {
        View itemAddIconMainPanel = addView.findViewById(R.id.itemAddMainPanel);
        itemAddIconMainPanel.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void setItemInfoIconBoxPanelVisible(boolean isVisible) {
        View itemInfoIconBoxPanel = addView.findViewById(R.id.itemIconBoxPanel);
        itemInfoIconBoxPanel.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void setAddBtnClickEvent() {
        View addBtn = addView.findViewById(R.id.itemAddBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionAddBtn();
            }
        });
    }
    private void showConfirmMessage() {
        // 삭제 버튼 클릭
        new DialogHelper().setChoiceStyleDialogWithMessage(context, null, new GeneralCallback() {
            @Override
            public void onCallBack() {
                actionRemoveConfirmBtn();
            }

        }, "삭제", "취소", "삭제하시겠습니까?");
    }

    private void actionRemoveConfirmBtn() {
        removeInfoVO();
        removeManagerPanelItemInfoDB();
        showRemoveToast();
        infoViewOff();
    }

    private void infoViewOff() {
        alert.dismiss();
    }

    private void actionAddBtn() {
        updateMangerPanelItemInfoDB();
        showChangeToast();
    }

    private void updateMangerPanelItemInfoDB() {
//        DBHelper.dbHelper.updateActivityNameAndIcon(activityVO, originalActivityName);
    }

    private void showChangeToast() {
        Toast.makeText(context, "변경되었습니다.", Toast.LENGTH_SHORT).show();
    }

    private void removeInfoVO() {
//        dataHelper.getActivities().get(activityVO.getCategoryName()).remove(activityVO);
    }

    private void removeManagerPanelItemInfoDB() {
//        DBHelper.dbHelper.deleteActivity(activityVO.getActivityName());
    }

    private void showRemoveToast() {
        Toast.makeText(context, "제거되었습니다.", Toast.LENGTH_SHORT).show();
    }

    private void setCloseBtnClickEvent() {
        View closeBtn = addView.findViewById(R.id.itemAddCloseBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoViewOff();
            }
        });
    }

    private View initPanelItemInfoView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemInfoView = inflater.inflate(R.layout.manager_item_add, null);
//        setItemInfoView(itemInfoView, activityVO);
        return itemInfoView;
    }

    public void setAlert(Dialog alert) {
        this.alert = alert;
    }

}
