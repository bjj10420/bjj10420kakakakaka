package com.example.schedulemanager.panel.managerpanel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.schedulemanager.R;
import com.example.schedulemanager.Util;
import com.example.schedulemanager.helper.DBHelper;
import com.example.schedulemanager.helper.DataHelper;
import com.example.schedulemanager.helper.DialogHelper;
import com.example.schedulemanager.helper.TaskHelper;
import com.example.schedulemanager.interface_.GeneralCallback;
import com.example.schedulemanager.interface_.GeneralCallback2;
import com.example.schedulemanager.panel.etcpanel.ETCPanel;
import com.example.schedulemanager.vo.ActivityVO;
import com.example.schedulemanager.vo.DrawableItem;
import com.takeiteasy.materialexpansionpanel.container.MaxHeightNestedScrollView;
import com.takeiteasy.materialexpansionpanel.panel.MaterialExpansionPanelView;

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
        for(DrawableItem drawableItem : dataHelper.getDrawableList()) {
            rowLayout = decideRowLayoutByChildCount(rowLayout);
            addBoxPanelItemViewToPanel(drawableItem, rowLayout, itemInfoIconBoxPanel);
        }
        setIconBoxLoadingText(itemInfoIconBoxPanel);
    }

    private void setIconBoxLoadingText(LinearLayout itemInfoIconBoxPanel) {
        TextView iconBoxLoadingText = (TextView) itemInfoIconBoxPanel.findViewById(R.id.iconBoxLoadingText);
        Util.setTextWithFont(iconBoxLoadingText, "아이콘을 로딩중...");
        if(!dataHelper.getDrawableList().isEmpty()) {
            iconBoxLoadingText.setVisibility(View.GONE);
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

    private void addBoxPanelItemViewToPanel(DrawableItem drawableItem, LinearLayout rowLayout, LinearLayout itemInfoIconBoxPanel) {
        View iconBoxPanelItemView = makeBoxPanelItemView(drawableItem);
        rowLayout.addView(iconBoxPanelItemView);
        Log.d("addBoxPanelItemViewToPanel 체크", String.valueOf(itemInfoIconBoxPanel));
        if(rowLayout.getChildCount() == 4) itemInfoIconBoxPanel.addView(rowLayout);
    }

    private View makeBoxPanelItemView(DrawableItem drawableItem) {
        View boxPanelItemView = new View(context);
        boxPanelItemView.setBackgroundDrawable(new BitmapDrawable(context.getResources(), drawableItem.getBitmap()));
        LinearLayout.LayoutParams viewParams = makeViewParams();
        boxPanelItemView.setTag(drawableItem.getDrawableName());
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
        activityIconView.setTag(view.getTag());
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
        categorySelectBtn.setTypeface(dataHelper.getTypeface());
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
        if(dataHelper.getDrawableList().isEmpty())
            new TaskHelper(context).loadIconBox(new GeneralCallback(){
                @Override
                public void onCallBack() {
                    initInfoIconBoxPanel();
                }
            });
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
        ActivityVO activityVO = addNewActivityVO();
//        redrawPanel();
        refreshPanel(activityVO);
        addViewOff();
        showAddToast();
    }

    private void refreshPanel(ActivityVO activityVO) {
        refreshETCPanel(activityVO);
        refreshManagerPanel(activityVO);
    }

    private void refreshManagerPanel(ActivityVO activityVO) {
        if(eventHelper.getManagerPanel() == null) return;

        String categoryName = categoryText.getText().toString();
        int categoryIndex = (dataHelper.getCategories().indexOf(categoryName));
        View detailItemView = eventHelper.getManagerPanel().makeDetailItemView(activityVO);
        addNewItemToManagerPanel(detailItemView, categoryIndex);
    }

    private void addNewItemToManagerPanel(View detailItemView, int categoryIndex) {
        ((LinearLayout)
                ((MaxHeightNestedScrollView)
                        ((LinearLayout)((RelativeLayout)((MaterialExpansionPanelView)eventHelper.getManagerPanel().getManagerContentsLayout()
                                .getChildAt(categoryIndex + 1)).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(0)).addView(detailItemView);
    }

    private void refreshETCPanel(ActivityVO activityVO) {
        int categoryIndex = getCategoryIndex();
        ETCPanel etcPanel = eventHelper.getEtcPanel();
        addButtonToHorizontalScrollView(etcPanel, activityVO, categoryIndex);
    }

    private void addButtonToHorizontalScrollView(ETCPanel etcPanel, ActivityVO activityVO, int categoryIndex) {
        HorizontalScrollView scrollView = (HorizontalScrollView) etcPanel.getEtcContentsLayout().getChildAt(categoryIndex);
        LinearLayout rowLayout = ((LinearLayout) scrollView.getChildAt(0));
        View buttonView = etcPanel.setButtonView(activityVO);
        rowLayout.addView(buttonView);
    }

    private int getCategoryIndex() {
        String categoryName = categoryText.getText().toString();
        int categoryIndex = (dataHelper.getCategories().indexOf(categoryName) * 2) + 2;
        return categoryIndex;
    }

    private ActivityVO addNewActivityVO() {
        ActivityVO activityVO = makeNewActivityVO();
        addActivityToActivities(activityVO);
        addItemIntoDB(activityVO);
        return activityVO;
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
        activityVo.setImageData(addView.findViewById(R.id.itemAddIcon).getTag().toString());
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
//        Util.setFontAllChildView(context, itemInfoView, DataHelper.dataHelper.getTypeface(), true);
//        setItemInfoView(itemInfoView, activityVO);
        return itemInfoView;
    }

    public void setAlert(MaterialDialog alert) {
        this.alert = alert;
    }

}
