package com.example.schedulemanager.panel.managerpanel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schedulemanager.R;
import com.example.schedulemanager.Util;
import com.example.schedulemanager.helper.DataHelper;
import com.example.schedulemanager.helper.UIHelper;
import com.example.schedulemanager.vo.ActivityVO;

import me.drakeet.materialdialog.MaterialDialog;

import static com.example.schedulemanager.helper.DataHelper.PICK_FROM_GALLARY;
import static com.example.schedulemanager.helper.DataHelper.dataHelper;
import static com.example.schedulemanager.helper.EventHelper.eventHelper;

public class ManagerUserInput {

    private View userInputView;
    private MaterialDialog alert;
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
        for(Bitmap bitmap : dataHelper.getDrawableList()) {
            rowLayout = decideRowLayoutByChildCount(rowLayout);
            addBoxPanelItemViewToPanel(bitmap, rowLayout, userInputIconBoxPanel);
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

    private void addBoxPanelItemViewToPanel(Bitmap bitmap, LinearLayout rowLayout, LinearLayout itemInfoIconBoxPanel) {
        View iconBoxPanelItemView = makeBoxPanelItemView(bitmap);
        rowLayout.addView(iconBoxPanelItemView);
        Log.d("addBoxPanelItemViewToPanel 체크", String.valueOf(itemInfoIconBoxPanel));
        if(rowLayout.getChildCount() == 4) itemInfoIconBoxPanel.addView(rowLayout);
    }

    private View makeBoxPanelItemView(Bitmap bitmap) {
        View boxPanelItemView = new View(context);
        boxPanelItemView.setBackgroundDrawable(new BitmapDrawable(context.getResources(), bitmap));
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
        setUserInputIconBoxPanelVisible(false);
        setUserInputMainPanel(true);
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
        setUserInputIconClickEvent();
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        Intent chooser = Intent.createChooser(intent, "Choose a Picture");
        ((Activity)context).startActivityForResult(chooser, PICK_FROM_GALLARY);
    }

    private void setUserInputIconClickEvent() {
        View activityIconView = userInputView.findViewById(R.id.userInputIcon);
        activityIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionActivityIconView();
            }
        });
    }

    private void actionActivityIconView() {
        setUserInputIconBoxPanelVisible(true);
        setUserInputMainPanel(false);
    }


    private void setBtnClickEvents() {
        setInputBtnClickEvent();
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
        setUserInputIconBoxPanelVisible(false);
        setUserInputMainPanel(true);
    }

    private void setUserInputMainPanel(boolean isVisible) {
        View userInputIconMainPanel = userInputView.findViewById(R.id.userInputMainPanel);
        userInputIconMainPanel.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void setUserInputIconBoxPanelVisible(boolean isVisible) {
        View itemInfoIconBoxPanel = userInputView.findViewById(R.id.itemIconBoxPanel);
        itemInfoIconBoxPanel.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void setInputBtnClickEvent() {
        View inputBtn = userInputView.findViewById(R.id.userInputBtn);
        inputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInputBtn();
            }
        });
    }

    private void userInputViewOff() {
        alert.dismiss();
    }

    public void userInputBtn() {
        checkInvalidateData();
    }

    private void checkInvalidateData() {
        if(isCategorySelected() && isActivityNameEntered()){
            actionValidateInput();
        }
        else
            showReCheckToast();
    }

    private void actionValidateInput() {
        showUserInputView();
        userInputViewOff();
    }

    private void showUserInputView() {
        View userInputView = makeUserInputView();
        eventHelper.getEtcPanel().getEtcPanelEvent().panelItemClickEvent(userInputView);
    }

    private View makeUserInputView() {
        ActivityVO activityVO = makeNewActivityVO();
        View userInputView = UIHelper.uiHelper.makeFavoriteButton(activityVO);
        setUserInputViewPosition(userInputView);
        return userInputView;
    }

    private void setUserInputViewPosition(View userInputView) {
        userInputView.setLeft(300);
        userInputView.setTop(300);
//          int x = UIHelper.uiHelper.getTotalLayout().getMeasuredWidth() / 2;
//          int y = UIHelper.uiHelper.getTotalLayout().getMeasuredWidth() / 2;
//        userInputView.setTranslationX(300);
//        userInputView.setTranslationY(800);
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

    private void showReCheckToast() {
        Util.customToast(context, "모든 정보를 입력하세요");
    }

    private void setCloseBtnClickEvent() {
        View closeBtn = userInputView.findViewById(R.id.userInputCloseBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInputViewOff();
            }
        });
    }

    private View initPanelUserInputView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View userInputView = inflater.inflate(R.layout.manager_user_input, null);
        return userInputView;
    }

    public void setAlert(MaterialDialog alert) {
        this.alert = alert;
    }

}
