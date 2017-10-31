package com.example.schedulemanager.panel.managerpanel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schedulemanager.R;
import com.example.schedulemanager.Util;
import com.example.schedulemanager.helper.DBHelper;
import com.example.schedulemanager.panel.etcpanel.ETCPanel;
import com.example.schedulemanager.vo.ActivityVO;

import static com.example.schedulemanager.helper.DataHelper.PICK_FROM_GALLARY;
import static com.example.schedulemanager.helper.DataHelper.dataHelper;
import static com.example.schedulemanager.helper.EventHelper.eventHelper;

public class ManagerUserInput {
    private View userInputView;
    private Dialog alert;
    private Context context;
    private TextView categoryText;
    private EditText activityName;

    public View init(Context context) {
        initField(context);
        initUserInputIconBoxPanel();
        setUserInputViewEvent();
        return userInputView;
    }

    private void initUserInputIconBoxPanel() {
        LinearLayout userInputIconBoxPanel = (LinearLayout) userInputView.findViewById(R.id.itemIconBoxPanel);
        LinearLayout rowLayout = null;
        for(Drawable drawable : dataHelper.getDrawableList()) {
            rowLayout = decideRowLayoutByChildCount(rowLayout);
            addBoxPanelItemViewToPanel(drawable, rowLayout, userInputIconBoxPanel);
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
        View activityIconView = userInputView.findViewById(R.id.userInputIcon);
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
        userInputView = initPanelUserInputView(context);
        categoryText = (TextView) userInputView.findViewById(R.id.userInputCategoryText);
        activityName = (EditText) userInputView.findViewById(R.id.userInputActivity);
    }

    private void setUserInputViewEvent() {
        setBtnClickEvents();
        setIconBoxPanelEvents();
        setInfoIconClickEvent();
    }

    private void setIconBoxPanelEvents() {
        setIconBoxPanelCloseEvent();
        setIconBoxPanelFileEvent();
    }

    private void setIconBoxPanelFileEvent() {
        View iconBoxPanelFileBtn = userInputView.findViewById(R.id.itemInfoIconBoxPanelFile);
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
        View activityIconView = userInputView.findViewById(R.id.userInputIcon);
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
        View iconBoxPanelCloseBtn = userInputView.findViewById(R.id.itemInfoIconBoxPanelClose);
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
        View userInputIconMainPanel = userInputView.findViewById(R.id.userInputMainPanel);
        userInputIconMainPanel.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void setItemInfoIconBoxPanelVisible(boolean isVisible) {
        View itemInfoIconBoxPanel = userInputView.findViewById(R.id.itemIconBoxPanel);
        itemInfoIconBoxPanel.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void setAddBtnClickEvent() {
        View addBtn = userInputView.findViewById(R.id.userInputBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionAddBtn();
            }
        });
    }

    private void addViewOff() {
        alert.dismiss();
    }

    private void actionAddBtn() {
        checkInvalidateData();
//        updateMangerPanelItemInfoDB();
//        showAddToast();
    }

    private void checkInvalidateData() {
        if(isCategorySelected() && isActivityNameEntered()){
            actionValidateAdd();
        }
        else
            showReCheckToast();
    }

    private void actionValidateAdd() {
        addNewActivityVO();
        redrawPanel();
        addViewOff();
        showAddToast();
    }

    private void redrawPanel() {
        redrawETCPanel();
        redrawManagerPanel();
    }

    private void addNewActivityVO() {
        ActivityVO activityVO = makeNewActivityVO();
        addActivityToActivities(activityVO);
        addItemIntoDB(activityVO);
    //
    }

    public void redrawManagerPanel() {
        ManagerPanel managerPanel = eventHelper.getManagerPanel();
        managerPanel.redrawManagerPanel();
    }

    private void redrawETCPanel() {
        ETCPanel etcPanel = eventHelper.getEtcPanel();
        etcPanel.clearEtcContentsLayout();
        etcPanel.initETCPanel();

    }

    private void addItemIntoDB(ActivityVO activityVO) {
        DBHelper.dbHelper.insertActivityWithIcon(activityVO);
    }

    private void addActivityToActivities(ActivityVO activityVO) {
        addToActivitiesMap(activityVO);
    }

    private void addToActivitiesMap(ActivityVO activityVO) {
        dataHelper.getActivities().get(activityVO.getCategoryName()).add(activityVO);
    }

    private ActivityVO makeNewActivityVO() {
        ActivityVO activityVo = new ActivityVO();
        activityVo.setCategoryName(categoryText.getText().toString());
        activityVo.setActivityName(activityName.getText().toString());
        activityVo.setImageData(dataHelper.getByteArrayFromDrawable((BitmapDrawable) userInputView.findViewById(R.id.userInputIcon).getBackground()));
        activityVo.setFavorite("F");
        return activityVo;
    }

    private boolean isActivityNameEntered() {
        return activityName.getText().toString().equals("") ? false : true;
    }

    private boolean isCategorySelected() {
        return categoryText.getVisibility() == View.VISIBLE ? true : false;
    }

    private void showAddToast() {
        Toast.makeText(context, "추가되었습니다.", Toast.LENGTH_SHORT).show();
    }

    private void showReCheckToast() {
        Toast.makeText(context, "모든 정보를 입력하세요", Toast.LENGTH_SHORT).show();
    }

    private void setCloseBtnClickEvent() {
        View closeBtn = userInputView.findViewById(R.id.userInputCloseBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addViewOff();
            }
        });
    }

    private View initPanelUserInputView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemInfoView = inflater.inflate(R.layout.manager_user_input, null);
//        setItemInfoView(itemInfoView, activityVO);
        return itemInfoView;
    }

    public void setAlert(Dialog alert) {
        this.alert = alert;
    }

}
