package com.example.schedulemanager.panel.managerpanel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.schedulemanager.R;
import com.example.schedulemanager.Util;
import com.example.schedulemanager.helper.DBHelper;
import com.example.schedulemanager.helper.DataHelper;
import com.example.schedulemanager.helper.DialogHelper;
import com.example.schedulemanager.interface_.GeneralCallback;
import com.example.schedulemanager.vo.ActivityVO;
import com.example.schedulemanager.vo.DrawableItem;

import org.w3c.dom.Text;

import me.drakeet.materialdialog.MaterialDialog;

import static com.example.schedulemanager.helper.DataHelper.PICK_FROM_GALLARY;
import static com.example.schedulemanager.helper.DataHelper.dataHelper;
import static com.example.schedulemanager.helper.EventHelper.eventHelper;

public class ManagerPanelItemInfo {

    private View infoView;
    private View managerListItemView;
    private ActivityVO activityVO;
    private String originalActivityName;
    private MaterialDialog alert;
    private Context context;
    private Uri initialURI;

    public View init(Context context, View rowViewClicked) {
        initField(context, rowViewClicked);
        initInfoIconBoxPanel();
        setInfoViewEvent();
        return infoView;
    }

    private void initInfoIconBoxPanel() {
        LinearLayout itemInfoIconBoxPanel = (LinearLayout) infoView.findViewById(R.id.itemIconBoxPanel);
        LinearLayout rowLayout = null;
        for(DrawableItem drawableItem : dataHelper.getDrawableList()) {
                rowLayout = decideRowLayoutByChildCount(rowLayout);
                addBoxPanelItemViewToPanel(drawableItem, rowLayout, itemInfoIconBoxPanel);
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
        View activityIconView = infoView.findViewById(R.id.itemInfoIcon);
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

    private void initField(Context context, View rowViewClicked) {
        this.context = context;
        managerListItemView = rowViewClicked;
        activityVO = (ActivityVO) rowViewClicked.getTag();
        infoView = initPanelItemInfoView(context, activityVO);
        originalActivityName = activityVO.getActivityName();
    }

    private void setInfoViewEvent() {
        setBtnClickEvents();
        setIconBoxPanelEvents();
        setInfoIconClickEvent();
    }

    private void setIconBoxPanelEvents() {
        setIconBoxPanelCloseEvent();
        setIconBoxPanelFileEvent();
    }

    private void setIconBoxPanelFileEvent() {
        View iconBoxPanelFileBtn = infoView.findViewById(R.id.itemInfoIconBoxPanelFile);
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
        View itemInfoIconBoxPanel = infoView.findViewById(R.id.itemIconBoxPanel);
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

    public void actionRemoveBtn() {
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
        refreshRemoveEtcPanel();
        infoViewOff();
    }

    private void refreshRemoveEtcPanel() {
        findAndRemoveETCButtonView();
    }

    private void infoViewOff() {
        alert.dismiss();
    }

    public void actionChangeBtn() {
        Log.d("액션체인지버튼체크", "액션체인지버튼체크");
        saveInfoVO();
        updateMangerPanelItemInfoDB();
        showChangeToast();
        refreshChangeManagerPanel();
        refreshChangeEtcPanel();
    }

    private void refreshChangeEtcPanel() {
        LinearLayout buttonView = findButtonView();
        changeButtonView(buttonView);
        Log.d("해당 아이템 체크", ((TextView)buttonView.getChildAt(1)).getText().toString());
    }

    private LinearLayout findButtonView() {
        int itemIndex = activityVO.getManagerIndex();
        HorizontalScrollView scrollView = findScrollView();
        LinearLayout rowLayout = ((LinearLayout) scrollView.getChildAt(0));
        LinearLayout buttonView = (LinearLayout) rowLayout.getChildAt(itemIndex);
        return  buttonView;
    }

    private void findAndRemoveETCButtonView() {
        int itemIndex = activityVO.getManagerIndex();
        HorizontalScrollView scrollView = findScrollView();
        LinearLayout rowLayout = ((LinearLayout) scrollView.getChildAt(0));
        rowLayout.removeViewAt(itemIndex);
    }

    private HorizontalScrollView findScrollView() {
        String category = activityVO.getCategoryName();
        int categoryIndex = (dataHelper.getCategories().indexOf(category) * 2) + 2;
        HorizontalScrollView scrollView = (HorizontalScrollView) eventHelper.getEtcPanel().getEtcContentsLayout().getChildAt(categoryIndex);
        return  scrollView;
    }

    private void changeButtonView(LinearLayout buttonView) {
        TextView etcItemText = (TextView)buttonView.getChildAt(1);
        View etcItemIcon = buttonView.getChildAt(0);
        etcItemText.setText(activityVO.getActivityName());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        etcItemIcon.setBackground(new BitmapDrawable(context.getResources(), BitmapFactory.decodeResource(
                context.getResources(), Util.getDrawableId(activityVO.getImageData()) , options)));
    }

    private void updateMangerPanelItemInfoDB() {
        DBHelper.dbHelper.updateActivityNameAndIcon(activityVO, originalActivityName);
    }

    private void refreshChangeManagerPanel() {
        TextView managerDetailItemText = (TextView) managerListItemView.findViewById(R.id.favorite_name);
        ImageView managerDetailItemIcon = (ImageView) managerListItemView.findViewById(R.id.favorite_icon);
        managerDetailItemText.setText(activityVO.getActivityName());
        managerDetailItemIcon.setImageBitmap(BitmapFactory.decodeResource(
                context.getResources(), Util.getDrawableId(activityVO.getImageData())));
    }

    private void showChangeToast() {
        Util.customToast(context, "변경되었습니다");
    }

    private void removeInfoVO() {
        dataHelper.getActivities().get(activityVO.getCategoryName()).remove(activityVO);
    }

    private void removeManagerPanelItemInfoDB() {
        DBHelper.dbHelper.deleteActivity(activityVO.getActivityName());
    }

    private void showRemoveToast() {
        Util.customToast(context, "제거되었습니다");
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
        activityVO.setImageData((String) activityIcon.getTag());
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
        Util.setFontAllChildView(context, itemInfoView, DataHelper.dataHelper.getTypeface(), true);
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
        activityIconView.setBackground(new BitmapDrawable(context.getResources(), BitmapFactory.decodeResource(
                context.getResources(), Util.getDrawableId(activityVO.getImageData()))));
    }

    private void setActivityName(View itemInfoView, ActivityVO activityVO) {
        TextView activityName = (TextView) itemInfoView.findViewById(R.id.itemInfoActivity);
        activityName.setText(activityVO.getActivityName());
    }

    public void setAlert(MaterialDialog alert) {
        this.alert = alert;
    }

}
