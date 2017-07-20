package com.example.schedulemanager;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private HashMap<String, String> iconNameMap;    // 해당하는 텍스트에 매칭시키는 아이콘명이 저장되는 맵
    private View.OnTouchListener touchListener;     // 드래그 터치 리스너
    private float dX;
    private float dY;
    private int lastAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 초기화
        init();
    }

    private void init() {
        initData();
        initUI();
        initEvent();
    }

    private void initData() {
        /**
         * 1. favorite테이블에서 메인에 등록된 버튼들의 정보를 로딩
         * 2. hashMap에다 해당 text를 키로 하여 데이터에 아이콘명을 추가
         */
        iconNameMap = new HashMap<String, String>();
        //TODO DB를 구축하면 아이콘 네임맵으로 저장 시켜줘야 함
        // TEST용으로
//        iconNameMap.put("")
    }

    private void initUI() {
        //TODO 스트링 리스트 파라메터를 나중에 DB에서 읽어오게 해야 함
        //TODO 나중에 버튼 패널의 아이콘들에 weight를 줘야한다
        ArrayList<String> testList = new ArrayList<String>();
        ArrayList<String> testList2 = new ArrayList<String>();

        testList.add("교류");
        testList.add("이메일");
        testList.add("여가");
        testList.add("만남");
        testList2.add("약속");
        testList2.add("독서");
        testList2.add("학교");
        testList2.add("공부");

        initButtonPanel(R.id.buttonPanel, testList);
        initButtonPanel(R.id.buttonPanel2, testList2);
    }

    /**
     * 모서리의 버튼 패널 초기화
     * 1. 넘겨받은 스트링 리스트데이터를 for 문으로 순환
     * 2. 데이터 하나에 뷰를 생성해서 해당 아이콘 코드값(아이콘명)으로 이미지뷰를 지정, 스트링 데이터로 텍스트 뷰 지정
     * 3. 해당 패널에 생성한 뷰 추가
      * @param panelId
     * @param stringArrayList
     */
    private void initButtonPanel(int panelId, ArrayList<String> stringArrayList) {
        // 추가되는 패널 뷰
        LinearLayout buttonPanel = (LinearLayout) findViewById(panelId);
        // 각 버튼의 높이
        float buttonHeight = convertDpToPixel(50);
        // 각 텍스트의 높이
        float textHeight = convertDpToPixel(15);
        // 각 버튼 뷰 레이아웃 파라메터
        LinearLayout.LayoutParams buttonViewParams = new LinearLayout.LayoutParams(0,
                (int) convertDpToPixel(65));
        buttonViewParams.weight = 1;

        // 각 버튼 레이아웃 파라메터
        ViewGroup.LayoutParams iconParams = new ViewGroup.LayoutParams((int) buttonHeight,
        (int) buttonHeight);
        // 각 텍스트 파라메터
        ViewGroup.LayoutParams textParams = new ViewGroup.LayoutParams((int) buttonHeight,
        (int) textHeight);
        //TODO 테스트용 코드
        int count = 0;
        if(panelId == R.id.buttonPanel2) count = 4;

        for(String textData : stringArrayList){
            // 버튼뷰 설정
            LinearLayout buttonView = new LinearLayout(this);
            buttonView.setOrientation(LinearLayout.VERTICAL);
            buttonView.setGravity(Gravity.CENTER);
            buttonView.setLayoutParams(buttonViewParams);
            // 아이콘 뷰 설정
            View iconView = new View(this);

//            iconView.setBackgroundResource(findIdByFileName(iconNameMap.get(textData), this));

            //TODO 테스트용 코드
                switch(count) {
                    case 0 : iconView.setBackgroundResource(R.drawable.community);
                            break;
                    case 1 : iconView.setBackgroundResource(R.drawable.email);
                        break;
                    case 2 : iconView.setBackgroundResource(R.drawable.leasure);
                        break;
                    case 3 : iconView.setBackgroundResource(R.drawable.meet);
                        break;
                    case 4 : iconView.setBackgroundResource(R.drawable.promise);
                        break;
                    case 5 : iconView.setBackgroundResource(R.drawable.read);
                        break;
                    case 6 : iconView.setBackgroundResource(R.drawable.school);
                        break;
                    case 7 : iconView.setBackgroundResource(R.drawable.shopping);
                        break;
                    case 8 : iconView.setBackgroundResource(R.drawable.study);
                        break;
                }

            iconView.setLayoutParams(iconParams);
            // 텍스트 뷰 설정
            TextView textView = new TextView(this);
            textView.setText(textData);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(textParams);
            // 태그첨부
            buttonView.setTag(textData);
            // 추가
            buttonView.addView(iconView);
            buttonView.addView(textView);



            buttonPanel.addView(buttonView);

            //TODO 테스트용 코드
            count++;
        }
    }

    private void initEvent() {

        // 드래그 이벤트 설정
        setDragEvent(R.id.buttonPanel);
    }



    /**
     * 해당 패널내의 자식 아이콘들의 드래그 이벤트를 설정
     * @param buttonPanelId
     */
    private void setDragEvent(int buttonPanelId) {
        LinearLayout buttonPanel = (LinearLayout) findViewById(buttonPanelId);
        for(int i = 0; i < buttonPanel.getChildCount(); i++){
            View buttonView = buttonPanel.getChildAt(i);
            buttonView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    Log.d("태그확인", String.valueOf(view.getTag()));
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:

                            dX = view.getX() - event.getRawX();
                            dY = view.getY() - event.getRawY();
                            lastAction = MotionEvent.ACTION_DOWN;
                            break;

                        case MotionEvent.ACTION_MOVE:
                            view.setY(event.getRawY() + dY);
                            view.setX(event.getRawX() + dX);
                            lastAction = MotionEvent.ACTION_MOVE;
                            break;

                        case MotionEvent.ACTION_UP:
                            if (lastAction == MotionEvent.ACTION_DOWN)
                                Toast.makeText(MainActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            return false;
                    }
                    return false;
                }
            });
        }
    }

    // 디피를 픽셀로 변환
    public static float convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    // 리소스내의 파일이름을 가지고 아이디를 찾아주는 함수
    public int findIdByFileName(String name, Context mContext) {
        Resources res = mContext.getResources();
        int id = res.getIdentifier(name, "id", mContext.getPackageName());
        return id;
    }
}
