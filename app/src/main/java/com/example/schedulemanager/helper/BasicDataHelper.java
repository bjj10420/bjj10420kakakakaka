package com.example.schedulemanager.helper;

import com.example.schedulemanager.R;
import com.example.schedulemanager.vo.ActivityVO;

import java.util.ArrayList;

import static com.example.schedulemanager.helper.DataHelper.dataHelper;


public class BasicDataHelper {

    public ArrayList<ActivityVO> init(){
        ArrayList<ActivityVO> basicDataList = new ArrayList<ActivityVO>();
        makeBusiness(basicDataList);
        makeIT(basicDataList);
        makeMedical(basicDataList);

        return basicDataList;
    }

    private void makeMedical(ArrayList<ActivityVO> basicDataList) {
        ActivityVO basicActivity12 = new ActivityVO("의료", "구급차 타기", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_59));
        ActivityVO basicActivity13 = new ActivityVO("의료", "의약통 준비", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_60));
        ActivityVO basicActivity14 = new ActivityVO("의료", "심장 강화 운동", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_61));
        ActivityVO basicActivity15 = new ActivityVO("의료", "수액맞기", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_62));
        ActivityVO basicActivity16 = new ActivityVO("의료", "혈압측정", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_63));
        ActivityVO basicActivity17 = new ActivityVO("의료", "관장", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_64));
        ActivityVO basicActivity18 = new ActivityVO("의료", "시력검사", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_65));
        ActivityVO basicActivity19 = new ActivityVO("의료", "심박수 측정", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_67));
        ActivityVO basicActivity20 = new ActivityVO("의료", "의료 헬리콥터 타기", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_68));
        ActivityVO basicActivity22 = new ActivityVO("의료", "병원위치 확인", "T", dataHelper.getByteArrayFromDrawable(R.drawable.icon_69));
        ActivityVO basicActivity24 = new ActivityVO("의료", "의료기록 확인", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_70));
        ActivityVO basicActivity25 = new ActivityVO("의료", "입원", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_71));
        ActivityVO basicActivity26 = new ActivityVO("의료", "현미경 사용", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_72));
        ActivityVO basicActivity29 = new ActivityVO("의료", "산소 공급기 사용", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_73));
        ActivityVO basicActivity30 = new ActivityVO("의료", "간호사 상담", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_74));
        ActivityVO basicActivity31 = new ActivityVO("의료", "약준비", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_75));
        ActivityVO basicActivity32 = new ActivityVO("의료", "밴드 사용", "T", dataHelper.getByteArrayFromDrawable(R.drawable.icon_76));
        ActivityVO basicActivity322 = new ActivityVO("의료", "양수기 사용", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_77));
        ActivityVO basicActivity33 = new ActivityVO("의료", "X레이", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_78));
        ActivityVO basicActivity34 = new ActivityVO("의료", "치과진료", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_79));
        ActivityVO basicActivity35 = new ActivityVO("의료", "진찰받기", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_81));
        ActivityVO basicActivity37 = new ActivityVO("의료", "주사맞기", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_82));
        ActivityVO basicActivity38 = new ActivityVO("의료", "처방전 받기", "T", dataHelper.getByteArrayFromDrawable(R.drawable.icon_83));
        ActivityVO basicActivity39 = new ActivityVO("의료", "시험관 사용", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_84));
        ActivityVO basicActivity40 = new ActivityVO("의료", "체온측정", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_85));

        basicDataList.add(basicActivity12);
        basicDataList.add(basicActivity13);
        basicDataList.add(basicActivity14);
        basicDataList.add(basicActivity15);
        basicDataList.add(basicActivity16);
        basicDataList.add(basicActivity17);
        basicDataList.add(basicActivity18);
        basicDataList.add(basicActivity19);
        basicDataList.add(basicActivity20);
        basicDataList.add(basicActivity22);
        basicDataList.add(basicActivity24);
        basicDataList.add(basicActivity25);
        basicDataList.add(basicActivity26);
        basicDataList.add(basicActivity29);
        basicDataList.add(basicActivity30);
        basicDataList.add(basicActivity31);
        basicDataList.add(basicActivity32);
        basicDataList.add(basicActivity322);
        basicDataList.add(basicActivity33);
        basicDataList.add(basicActivity34);
        basicDataList.add(basicActivity35);
        basicDataList.add(basicActivity37);
        basicDataList.add(basicActivity38);
    }

    private void makeIT(ArrayList<ActivityVO> basicDataList) {
        ActivityVO basicActivity12 = new ActivityVO("IT", "충전", "T", dataHelper.getByteArrayFromDrawable(R.drawable.icon_31));
        ActivityVO basicActivity13 = new ActivityVO("IT", "영상녹화", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_32));
        ActivityVO basicActivity14 = new ActivityVO("IT", "사진촬영", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_33));
        ActivityVO basicActivity15 = new ActivityVO("IT", "저장", "T", dataHelper.getByteArrayFromDrawable(R.drawable.icon_34));
        ActivityVO basicActivity16 = new ActivityVO("IT", "e북사용", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_35));
        ActivityVO basicActivity17 = new ActivityVO("IT", "음향조절", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_36));
        ActivityVO basicActivity18 = new ActivityVO("IT", "지문스캔", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_37));
        ActivityVO basicActivity19 = new ActivityVO("IT", "USB 저장", "T", dataHelper.getByteArrayFromDrawable(R.drawable.icon_38));
        ActivityVO basicActivity20 = new ActivityVO("IT", "동영상 제작", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_39));
        ActivityVO basicActivity22 = new ActivityVO("IT", "타블렛 사용", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_41));
        ActivityVO basicActivity24 = new ActivityVO("IT", "마이크 테스트", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_43));
        ActivityVO basicActivity25 = new ActivityVO("IT", "멀티미디어 재생", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_44));
        ActivityVO basicActivity26 = new ActivityVO("IT", "스피커 테스트", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_45));
        ActivityVO basicActivity29 = new ActivityVO("IT", "프로젝터 사용", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_48));
        ActivityVO basicActivity30 = new ActivityVO("IT", "라디오 사용", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_49));
        ActivityVO basicActivity31 = new ActivityVO("IT", "안테나 정비", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_50));
        ActivityVO basicActivity32 = new ActivityVO("IT", "sdcard 사용", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_51));
        ActivityVO basicActivity322 = new ActivityVO("IT", "simcard 사용", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_52));
        ActivityVO basicActivity33 = new ActivityVO("IT", "씬 촬영", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_53));
        ActivityVO basicActivity34 = new ActivityVO("IT", "스마트 워치 사용", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_54));
        ActivityVO basicActivity35 = new ActivityVO("IT", "장비 설치", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_55));
        ActivityVO basicActivity37 = new ActivityVO("IT", "웹캠 사용", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_57));
        ActivityVO basicActivity38 = new ActivityVO("IT", "와이파이 설정", "F", dataHelper.getByteArrayFromDrawable(R.drawable.icon_58));

        basicDataList.add(basicActivity12);
        basicDataList.add(basicActivity13);
        basicDataList.add(basicActivity14);
        basicDataList.add(basicActivity15);
        basicDataList.add(basicActivity16);
        basicDataList.add(basicActivity17);
        basicDataList.add(basicActivity18);
        basicDataList.add(basicActivity19);
        basicDataList.add(basicActivity20);
        basicDataList.add(basicActivity22);
        basicDataList.add(basicActivity24);
        basicDataList.add(basicActivity25);
        basicDataList.add(basicActivity26);
        basicDataList.add(basicActivity29);
        basicDataList.add(basicActivity30);
        basicDataList.add(basicActivity31);
        basicDataList.add(basicActivity32);
        basicDataList.add(basicActivity322);
        basicDataList.add(basicActivity33);
        basicDataList.add(basicActivity34);
        basicDataList.add(basicActivity35);
        basicDataList.add(basicActivity37);
        basicDataList.add(basicActivity38);
    }

    private void makeBusiness(ArrayList<ActivityVO> basicDataList) {
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

    }
}
