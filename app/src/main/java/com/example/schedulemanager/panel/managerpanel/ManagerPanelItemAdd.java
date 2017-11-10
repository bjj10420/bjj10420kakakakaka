package com.example.schedulemanager.panel.managerpanel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schedulemanager.R;
import com.example.schedulemanager.Util;
import com.example.schedulemanager.helper.DBHelper;
import com.example.schedulemanager.helper.DialogHelper;
import com.example.schedulemanager.interface_.GeneralCallback;
import com.example.schedulemanager.interface_.GeneralCallback2;
import com.example.schedulemanager.panel.etcpanel.ETCPanel;
import com.example.schedulemanager.vo.ActivityVO;

import me.drakeet.materialdialog.MaterialDialog;

import static com.example.schedulemanager.helper.DataHelper.PICK_FROM_GALLARY;
import static com.example.schedulemanager.helper.DataHelper.dataHelper;
import static com.example.schedulemanager.helper.EventHelper.eventHelper;

public class ManagerPanelItemAdd {
    private View addView;
    private MaterialDialog alert;
    private Context context;
    private TextView categoryText;
    private Button categorySelectBtn;
    private EditText activityName;

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
                (int) Util.convertDpToPixel(40));
        viewParams.weight = 1;
        viewParams.setMargins(20,20,20,20);
        return viewParams;
    }

    private void initField(Context context) {
        this.context = context;
        addView = initPanelItemAddView(context);
        categoryText = (TextView) addView.findViewById(R.id.itemAddCategoryText);
        categorySelectBtn = (Button) addView.findViewById(R.id.itemAddCategorySelect);
        activityName = (EditText) addView.findViewById(R.id.itemAddActivity);
    }

    private void setAddViewEvent() {
        setBtnClickEvents();
        setIconBoxPanelEvents();
        setInfoIconClickEvent();
        setCategoryEvents();
    }

    private void setCategoryEvents() {
        setCategorySelectBtnEvent();
        setCategoryTextEvent();
    }

    private void setCategoryTextEvent() {
        categoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCategorySelectBtn();
            }
        });
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
        final CharSequence[] categoryArray = dataHelper.getCategoryArray();

        new DialogHelper().showCategorySelect(context, "카테고리 선택", categoryArray, new GeneralCallback2() {
            @Override
            public void onCallBack(Object parameter) {
                setCategoryText(categoryArray, (Integer) parameter);
            }
        });
    }

    private void setCategoryText(CharSequence[] categoryArray, int which) {
        if(categorySelectBtn.getVisibility() == View.VISIBLE){
            switchCategoryBtnAndText();
        }
        categoryText.setText(categoryArray[which]);
    }

    private void switchCategoryBtnAndText() {
        categorySelectBtn.setVisibility(View.GONE);
        categoryText.setVisibility(View.VISIBLE);
    }

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

    private void addViewOff() {
        alert.dismiss();
    }

    public void actionAddBtn() {
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
        activityVo.setImageData(dataHelper.getByteArrayFromDrawable((BitmapDrawable)addView.findViewById(R.id.itemAddIcon).getBackground()));
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
        Util.customToast(context, "추가되었습니다");
    }

    private void showReCheckToast() {

        Util.customToast(context, "모든 정보를 입력하세요");
    }

    private void setCloseBtnClickEvent() {
        View closeBtn = addView.findViewById(R.id.itemAddCloseBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addViewOff();
            }
        });
    }

    private View initPanelItemAddView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemInfoView = inflater.inflate(R.layout.manager_item_add, null);
//        setItemInfoView(itemInfoView, activityVO);
        return itemInfoView;
    }

    public void setAlert(MaterialDialog alert) {
        this.alert = alert;
    }

}
