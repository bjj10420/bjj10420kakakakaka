package com.example.schedulemanager.helper;

import com.example.schedulemanager.R;
import com.example.schedulemanager.vo.ActivityVO;

import java.util.ArrayList;

import static com.example.schedulemanager.helper.DataHelper.dataHelper;


public class BasicDataHelper {

    public ArrayList<ActivityVO> init(){
        ArrayList<ActivityVO> basicDataList = new ArrayList<ActivityVO>();

        ActivityVO basicActivity11 = new ActivityVO("비지니스", "은행", "F", dataHelper.dataHelper.getByteArrayFromDrawable(R.drawable.icon_1));
        ActivityVO basicActivity12 = new ActivityVO("비지니스", "발표", "T", dataHelper.getByteArrayFromDrawable(R.drawable.icon_12));
        ActivityVO basicActivity13 = new ActivityVO("비지니스", "출장", "T", dataHelper.getByteArrayFromDrawable(R.drawable.icon_22));
        ActivityVO basicActivity14 = new ActivityVO("비지니스", "부동산", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_23));
        ActivityVO basicActivity15 = new ActivityVO("비지니스", "회의", "T", dataHelper.getByteArrayFromDrawable(R.drawable.icon_24));
        ActivityVO basicActivity16 = new ActivityVO("비지니스", "계산", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_25));
        ActivityVO basicActivity17 = new ActivityVO("비지니스", "약속", "T", dataHelper.getByteArrayFromDrawable(R.drawable.icon_26));
        ActivityVO basicActivity18 = new ActivityVO("비지니스", "투자", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_27));
        ActivityVO basicActivity19 = new ActivityVO("비지니스", "주식", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_28));
        ActivityVO basicActivity20 = new ActivityVO("비지니스", "문서", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_3));
        ActivityVO basicActivity21 = new ActivityVO("비지니스", "문서정리", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_4));
        ActivityVO basicActivity22 = new ActivityVO("비지니스", "문서찾기", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_5));
        ActivityVO basicActivity23 = new ActivityVO("비지니스", "이메일", "T", dataHelper.getByteArrayFromDrawable(R.drawable.icon_6));
        ActivityVO basicActivity24 = new ActivityVO("비지니스", "공장", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_7));
        ActivityVO basicActivity25 = new ActivityVO("비지니스", "문서수집", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_8));
        ActivityVO basicActivity26 = new ActivityVO("비지니스", "목표", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_9));
        ActivityVO basicActivity27 = new ActivityVO("비지니스", "외부미팅", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_10));
        ActivityVO basicActivity28 = new ActivityVO("비지니스", "추정", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_11));
        ActivityVO basicActivity29 = new ActivityVO("비지니스", "노트", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_13));
        ActivityVO basicActivity30 = new ActivityVO("비지니스", "메모", "T", dataHelper.getByteArrayFromDrawable(R.drawable.icon_14));
        ActivityVO basicActivity31 = new ActivityVO("비지니스", "상사", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_15));
        ActivityVO basicActivity32 = new ActivityVO("비지니스", "해외", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_16));
        ActivityVO basicActivity33 = new ActivityVO("비지니스", "인쇄", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_17));
        ActivityVO basicActivity34 = new ActivityVO("비지니스", "보안", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_18));
        ActivityVO basicActivity35 = new ActivityVO("비지니스", "미션", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_19));
        ActivityVO basicActivity36 = new ActivityVO("비지니스", "계획", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_29));
        ActivityVO basicActivity37 = new ActivityVO("비지니스", "배치", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_20));
        ActivityVO basicActivity38 = new ActivityVO("비지니스", "지불", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_21));

//        ActivityVO basicActivity39 = new ActivityVO("비지니스", "계획", "F", dataHelper.getByteArrayFromDrawable(R.drawable.plan_icon));
//        ActivityVO basicActivity40 = new ActivityVO("비지니스", "계획", "F", dataHelper.getByteArrayFromDrawable(R.drawable.plan_icon));
//        ActivityVO basicActivity41 = new ActivityVO("비지니스", "계획", "F", dataHelper.getByteArrayFromDrawable(R.drawable.plan_icon));

        basicDataList.add(basicActivity11);
        basicDataList.add(basicActivity12);
        basicDataList.add(basicActivity13);
        basicDataList.add(basicActivity14);
        basicDataList.add(basicActivity15);
        basicDataList.add(basicActivity16);
        basicDataList.add(basicActivity17);
        basicDataList.add(basicActivity18);
        basicDataList.add(basicActivity19);
        basicDataList.add(basicActivity20);
        basicDataList.add(basicActivity21);
        basicDataList.add(basicActivity22);
        basicDataList.add(basicActivity23);
        basicDataList.add(basicActivity24);
        basicDataList.add(basicActivity25);
        basicDataList.add(basicActivity26);
        basicDataList.add(basicActivity27);
        basicDataList.add(basicActivity28);
        basicDataList.add(basicActivity29);
        basicDataList.add(basicActivity30);
        basicDataList.add(basicActivity31);
        basicDataList.add(basicActivity32);
        basicDataList.add(basicActivity33);
        basicDataList.add(basicActivity34);
        basicDataList.add(basicActivity35);
        basicDataList.add(basicActivity36);
        basicDataList.add(basicActivity37);
        basicDataList.add(basicActivity38);



        return basicDataList;
    }
}
