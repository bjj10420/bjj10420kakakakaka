package com.example.schedulemanager.helper;

import com.example.schedulemanager.R;
import com.example.schedulemanager.vo.ActivityVO;

import java.util.ArrayList;

import static com.example.schedulemanager.helper.DataHelper.dataHelper;


public class BasicDataHelper {

    public ArrayList<ActivityVO> init(){
        ArrayList<ActivityVO> basicDataList = new ArrayList<ActivityVO>();

        ActivityVO basicActivity11 = new ActivityVO("비지니스", "은행", "F", dataHelper.dataHelper.getByteArrayFromDrawable(R.drawable.icon_1));
        ActivityVO basicActivity12 = new ActivityVO("비지니스", "발표", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Board_icon));
//        ActivityVO basicActivity13 = new ActivityVO("비지니스", "출장", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Briefcase_icon));
//        ActivityVO basicActivity14 = new ActivityVO("비지니스", "부동산", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Building_icon));
//        ActivityVO basicActivity15 = new ActivityVO("비지니스", "회의", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Bulb_icon));
//        ActivityVO basicActivity16 = new ActivityVO("비지니스", "계산", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Calculator_icon));
//        ActivityVO basicActivity17 = new ActivityVO("비지니스", "약속", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Clock_icon));
//        ActivityVO basicActivity18 = new ActivityVO("비지니스", "투자", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Coins_icon));
//        ActivityVO basicActivity19 = new ActivityVO("비지니스", "주식", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Computer_icon));
//        ActivityVO basicActivity20 = new ActivityVO("비지니스", "문서", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Document_icon));
//        ActivityVO basicActivity21 = new ActivityVO("비지니스", "문서정리", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Document_Folders_icon));
//        ActivityVO basicActivity22 = new ActivityVO("비지니스", "문서찾기", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Documents_glasses_icon));
//        ActivityVO basicActivity23 = new ActivityVO("비지니스", "이메일", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Envelope_icon));
//        ActivityVO basicActivity24 = new ActivityVO("비지니스", "공장", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Factory_icon));
//        ActivityVO basicActivity25 = new ActivityVO("비지니스", "문서수집", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Folder_icon));
//        ActivityVO basicActivity26 = new ActivityVO("비지니스", "목표", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Goal_icon));
//        ActivityVO basicActivity27 = new ActivityVO("비지니스", "외부미팅", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Handshake_icon));
//        ActivityVO basicActivity28 = new ActivityVO("비지니스", "추정", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Libra_icon));
//        ActivityVO basicActivity29 = new ActivityVO("비지니스", "노트", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Notebook_icon));
//        ActivityVO basicActivity30 = new ActivityVO("비지니스", "메모", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Notes_icon));
//        ActivityVO basicActivity31 = new ActivityVO("비지니스", "상사", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Office_chair_icon));
//        ActivityVO basicActivity32 = new ActivityVO("비지니스", "해외", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Planet_icon));
//        ActivityVO basicActivity33 = new ActivityVO("비지니스", "인쇄", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Printer_icon));
//        ActivityVO basicActivity34 = new ActivityVO("비지니스", "보안", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Safe_icon));
//        ActivityVO basicActivity35 = new ActivityVO("비지니스", "미션", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Sandglass_icon));
//        ActivityVO basicActivity36 = new ActivityVO("비지니스", "계획", "F", dataHelper.getByteArrayFromDrawable(R.drawable.plan_icon));
//        ActivityVO basicActivity37 = new ActivityVO("비지니스", "배치", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Schedule_separate_icon));
//        ActivityVO basicActivity38 = new ActivityVO("비지니스", "지불", "F", dataHelper.getByteArrayFromDrawable(R.drawable.Shield_icon));

//        ActivityVO basicActivity39 = new ActivityVO("비지니스", "계획", "F", dataHelper.getByteArrayFromDrawable(R.drawable.plan_icon));
//        ActivityVO basicActivity40 = new ActivityVO("비지니스", "계획", "F", dataHelper.getByteArrayFromDrawable(R.drawable.plan_icon));
//        ActivityVO basicActivity41 = new ActivityVO("비지니스", "계획", "F", dataHelper.getByteArrayFromDrawable(R.drawable.plan_icon));
        basicDataList.add(basicActivity11);

        return basicDataList;
    }
}
