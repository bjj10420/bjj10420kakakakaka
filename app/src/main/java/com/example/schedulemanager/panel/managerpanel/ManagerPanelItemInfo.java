package com.example.schedulemanager.panel.managerpanel;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schedulemanager.R;
import com.example.schedulemanager.Util;
import com.example.schedulemanager.helper.DBHelper;
import com.example.schedulemanager.helper.DialogHelper;
import com.example.schedulemanager.interface_.GeneralCallback;
import com.example.schedulemanager.vo.ActivityVO;

import static com.example.schedulemanager.helper.DataHelper.dataHelper;

public class ManagerPanelItemInfo {

    private View infoView;
    private View managerListItemView;
    private ActivityVO activityVO;
    private String originalActivityName;
    private Dialog alert;
    private Context context;

    public View init(Context context, View rowViewClicked) {
        initField(context, rowViewClicked);
        initInfoIconBoxPanel();
        setInfoViewEvent();
        return infoView;
    }

    private void initInfoIconBoxPanel() {
        LinearLayout itemInfoIconBoxPanel = (LinearLayout) infoView.findViewById(R.id.itemInfoIconBoxPanel);
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
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(0,
                (int) Util.convertDpToPixel(50));
        viewParams.weight = 1;
        boxPanelItemView.setLayoutParams(viewParams);
        return  boxPanelItemView;
    }

    private void initField(Context context, View rowViewClicked) {
        this.context = context;
        managerListItemView = rowViewClicked;
        activityVO = (ActivityVO) rowViewClicked.getTag();
        infoView = initPanelItemInfoView(context, activityVO);
        originalActivityName = activityVO.getActivityName();
    }

    private void setInfoViewEvent() {
        setBtnClickEvents();
        setIconBoxPanelCloseEvent();
    }

    private void setInfoIconClickEvent() {
        View activityIconView = infoView.findViewById(R.id.itemInfoIcon);
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
        setChangeBtnClickEvent();
        setRemoveBtnClcikEvent();
        setCloseBtnClickEvent();
        setInfoIconClickEvent();
    }

    private void setIconBoxPanelCloseEvent() {
        View iconBoxPanelCloseBtn = infoView.findViewById(R.id.itemInfoIconBoxPanelClose);
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
        View itemInfoIconMainPanel = infoView.findViewById(R.id.itemInfoMainPanel);
        itemInfoIconMainPanel.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void setItemInfoIconBoxPanelVisible(boolean isVisible) {
        View itemInfoIconBoxPanel = infoView.findViewById(R.id.itemInfoIconBoxPanel);
        itemInfoIconBoxPanel.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void setChangeBtnClickEvent() {
        View changeBtn = infoView.findViewById(R.id.itemInfoChangeBtn);
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionChangeBtn();
            }
        });
    }

    private void setRemoveBtnClcikEvent() {
        View removeBtn = infoView.findViewById(R.id.itemInfoRemoveBtn);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionRemoveBtn();
            }
        });
    }

    private void actionRemoveBtn() {
        showConfirmMessage();
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
        refreshRemoveManagerPanel();
        infoViewOff();
    }

    private void infoViewOff() {
        alert.dismiss();
    }

    private void actionChangeBtn() {
        saveInfoVO();
        updateMangerPanelItemInfoDB();
        showChangeToast();
        refreshChangeManagerPanel();
    }

    private void updateMangerPanelItemInfoDB() {
        DBHelper.dbHelper.updateActivityNameAndIcon(activityVO, originalActivityName);
    }

    private void refreshChangeManagerPanel() {
        TextView managerDetailItemText = (TextView) managerListItemView.findViewById(R.id.favorite_name);
        ImageView managerDetailItemIcon = (ImageView) managerListItemView.findViewById(R.id.favorite_icon);
        managerDetailItemText.setText(activityVO.getActivityName());
        managerDetailItemIcon.setImageBitmap(BitmapFactory.decodeByteArray(activityVO.getImageData(),0,activityVO.getImageData().length));
    }

    private void showChangeToast() {
        Toast.makeText(context, "변경되었습니다.", Toast.LENGTH_SHORT).show();
    }

    private void removeInfoVO() {
        dataHelper.getActivities().get(activityVO.getCategoryName()).remove(activityVO);
    }

    private void removeManagerPanelItemInfoDB() {
        DBHelper.dbHelper.deleteActivity(activityVO.getActivityName());
    }

    private void showRemoveToast() {
        Toast.makeText(context, "제거되었습니다.", Toast.LENGTH_SHORT).show();
    }

    private void refreshRemoveManagerPanel() {
         ((ViewManager)managerListItemView.getParent()).removeView(managerListItemView);
    }

    private void saveInfoVO() {
        saveActivityName();
        saveActivityIcon();
    }

    private void saveActivityIcon() {
        View activityIcon = infoView.findViewById(R.id.itemInfoIcon);
        activityVO.setImageData(dataHelper.getByteArrayFromDrawable(activityIcon.getBackground()));
    }

    private void saveActivityName() {
        EditText activityEdit = (EditText) infoView.findViewById(R.id.itemInfoActivity);
        activityVO.setActivityName(activityEdit.getText().toString());
    }

    private void setCloseBtnClickEvent() {
        View closeBtn = infoView.findViewById(R.id.itemInfoCloseBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoViewOff();
            }
        });
    }

    private View initPanelItemInfoView(Context context, ActivityVO activityVO) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemInfoView = inflater.inflate(R.layout.manager_item_info, null);
        setItemInfoView(itemInfoView, activityVO);
        return itemInfoView;
    }

    private void setItemInfoView(View itemInfoView, ActivityVO activityVO) {
        setActivityName(itemInfoView, activityVO);
        setActivityIcon(itemInfoView, activityVO);
        clearItemInfoViewFocus(itemInfoView);
    }

    private void clearItemInfoViewFocus(View itemInfoView) {
        EditText activityNameEditText = (EditText) itemInfoView.findViewById(R.id.itemInfoActivity);
        activityNameEditText.clearFocus();
    }

    private void setActivityIcon(View itemInfoView, ActivityVO activityVO) {
        View activityIconView = itemInfoView.findViewById(R.id.itemInfoIcon);
        activityIconView.setBackground(new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(activityVO.getImageData(), 0, activityVO.getImageData().length)));
    }

    private void setActivityName(View itemInfoView, ActivityVO activityVO) {
        TextView activityName = (TextView) itemInfoView.findViewById(R.id.itemInfoActivity);
        activityName.setText(activityVO.getActivityName());
    }

    public void setAlert(Dialog alert) {
        this.alert = alert;
    }

}
