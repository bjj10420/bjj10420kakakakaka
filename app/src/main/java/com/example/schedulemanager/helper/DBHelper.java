package com.example.schedulemanager.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.schedulemanager.R;
import com.example.schedulemanager.vo.ActivityVO;
import com.example.schedulemanager.vo.Schedule;
import com.example.schedulemanager.Util;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by bjj on 2017-07-25.
 */
public class DBHelper extends SQLiteOpenHelper {

    // DB NAME
    public static final String DATABASE_NAME = "schedule_manager.sqlite";
    // DB VERSION ( 숫자가 변하면 업그레이드 ( onUpgrade ) 됨 ) ( 새로 설치하면 onCreate )
    public static final int DB_VERSION_1 = 1;
    public static SQLiteDatabase DB;
    public static DBHelper dbHelper;
    private final Context context;

    // 스케쥴 관리 테이블
    public String scheduleTableName = "schedule";
    // 스케쥴 관리 테이블 컬럼명
    String no_colum = "no";
    String dateValue_colum = "dateValue";
    String activityName_colum = "activityName";
    String orderValue_colum = "orderValue";
    String timeValue_colum = "timeValue";
    String memoValue_colum = "memoValue";

    // 카테고리 관리 테이블
    public String categoryTableName = "category";
    // 카테고리 관리 테이블 컬럼명(중복은 동일 컬럼명 사용)
    String categoryTable_no_colum = "no";
    String categoryTable_categoryName_colum = "categoryName";

    // 활동 관리 테이블
    public String activityTableName = "activity";
    // 활동 관리 테이블 컬럼명(중복은 동일 컬럼명 사용)
    String activityTable_no_colum = "no";
    String activityTable_categoryName_colum = "categoryName";
    String activityTable_activityName_colum = "activityName";
    String activityTable_isFavorite_colum = "isFavorite";
    String acitivytTable_icon_colum = "icon";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DB_VERSION_1);
        dbHelper = this;
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 트랜잭션 시작
        db.beginTransaction();
        // 1번 쿼리: 스케쥴 테이블 생성
        String sql = new StringBuilder("CREATE TABLE ").append(scheduleTableName).append(" (")
                      .append(no_colum).append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
                      .append(dateValue_colum).append(" TEXT NOT NULL, ")
                      .append(activityName_colum).append(" TEXT NOT NULL, ")
                      .append(orderValue_colum).append(" INTEGER, ")
                      .append(timeValue_colum).append(" TEXT, ")
                      .append(memoValue_colum).append(" TEXT)")
                      .toString();
        db.execSQL(sql);

        // 2번 쿼리: 카테고리 관리 테이블 생성
        String sql2 = new StringBuilder("CREATE TABLE ").append(categoryTableName).append(" (")
                .append(categoryTable_no_colum).append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
                .append(categoryTable_categoryName_colum).append(" TEXT NOT NULL)")
                .toString();
        db.execSQL(sql2);

        // 3번 쿼리: 활동 관리 테이블 생성
        String sql3 = new StringBuilder("CREATE TABLE ").append(activityTableName).append(" (")
                .append(activityTable_no_colum).append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
                .append(activityTable_categoryName_colum).append(" TEXT NOT NULL, ")
                .append(activityTable_activityName_colum).append(" TEXT NOT NULL, ")
                .append(activityTable_isFavorite_colum).append(" TEXT NOT NULL, ")
                .append(acitivytTable_icon_colum).append(" TEXT NOT NULL)")
                .toString();
        db.execSQL(sql3);

        // 트랜잭션 성공
        db.setTransactionSuccessful();
        // 트랜잭션 종료
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // 스케쥴 1개 추가
    public long insertSchedule(Schedule schedule){
        ContentValues values = new ContentValues();
        values.clear();
        //TODO no에서 sqlite 에러가 나서 이쪽을 막고 나중에 같은 date값을 order로 표시하게
//        values.put(no_colum, schedule.getNo());
        values.put(dateValue_colum, schedule.getDate());
        values.put(activityName_colum, schedule.getActivityName());
        values.put(orderValue_colum, schedule.getOrder());
        values.put(timeValue_colum, schedule.getTime());
        values.put(memoValue_colum, schedule.getMemo());
        Log.d("인설트스케쥴 체크", String.valueOf(schedule.getOrder()));
        long result = DB.insert(scheduleTableName, null, values);
        return result;
    }

    // 아이콘과 함께 활동 1개 추가
    public long insertActivityWithIcon(ActivityVO vo){
        ContentValues values = new ContentValues();
        values.clear();
        values.put(activityTable_categoryName_colum, vo.getCategoryName());
        values.put(activityTable_activityName_colum, vo.getActivityName());
        values.put(activityTable_isFavorite_colum, vo.isFavorite());
        values.put(acitivytTable_icon_colum, vo.getImageData());

        long result = DB.insert(activityTableName, null, values);
        return result;
    }

    // 카테고리 1개 추가
    public long insertCategory(String categoryName){
        ContentValues values = new ContentValues();
        values.clear();
        values.put(categoryTable_categoryName_colum, categoryName);

        long result = DB.insert(categoryTableName, null, values);
        return result;
    }

    /**
     * 스케쥴 1개 삭제 (순서값 포함)
     * @param dateColumValue
     * @param orderColumValue
     */
    public void deleteSchedule(String dateColumValue, int orderColumValue) {
        DB = getWritableDatabase();
        int resultNumber = DB.delete(scheduleTableName, dateValue_colum + " = ? AND " + orderValue_colum + " = ?",
                new String[]{dateColumValue, String.valueOf(orderColumValue)});
     }

    /**
     * 스케쥴 1개 삭제 (순서값 없이)
     * @param dateColumValue
     */
    public void deleteSchedule(String dateColumValue) {
        DB = getWritableDatabase();
        int resultNumber = DB.delete(scheduleTableName, dateValue_colum + " = ?" ,
                new String[]{dateColumValue});
    }

    /**
     * 당일의 스케쥴 갯수 카운트
     * @param date
     * @return
     */
    public int getScheduleCountForDate(String date){
        DB = getWritableDatabase();

        String sql = String.format(Locale.getDefault(),
       "SELECT COUNT(*) FROM %s WHERE %s = %s",
        scheduleTableName, 							// 테이블 이름
        dateValue_colum,	                        // 조건 칼럼
                date);

        Cursor c = DB.rawQuery(sql, null);
        int count = 0;
        if(c != null) {
            c.moveToFirst();
            count = c.getInt(0);
        }
        return count;
    }

    /**
     * 모든 스케쥴을 읽어와서 스케쥴맵으로 저장
     * @return
     * @param allScheduleMap
     */
    public void selectAllSchedule(HashMap<Integer, HashMap<Integer, Schedule>> allScheduleMap){
        DB = getWritableDatabase();

        String sql = String.format(Locale.getDefault(),
                "SELECT * FROM %s order by " + orderValue_colum + " asc ",
                scheduleTableName
                );

        Cursor c = DB.rawQuery(sql, null);
        if(c != null) {
            while (c.moveToNext()) {
                Schedule schedule = new Schedule();
                String scheduleDate = c.getString(c.getColumnIndex(dateValue_colum));
                schedule.setDate(scheduleDate);
                schedule.setActivityName(c.getString(c.getColumnIndex(activityName_colum)));
                schedule.setOrder(c.getInt(c.getColumnIndex(orderValue_colum)));
                schedule.setMemo(c.getString(c.getColumnIndex(memoValue_colum)));
                schedule.setTime(c.getString(c.getColumnIndex(timeValue_colum)));

                String scheduleYearMonth = Util.getYearMonthFromDate(scheduleDate);
                // 스케쥴 추가
                addScheduleToMapByMonth(allScheduleMap, Integer.parseInt(scheduleYearMonth), schedule);
            }
        }
     }

    public void selectActivities(String categoryName, ArrayList<ActivityVO> activities){
        DB = getWritableDatabase();

        String sql = String.format(Locale.getDefault(),
                "SELECT * FROM %s where %s = '%s'",
                activityTableName, activityTable_categoryName_colum, categoryName
        );

        Cursor c = DB.rawQuery(sql, null);
        if(c != null) {
            while (c.moveToNext()) {
                ActivityVO activityVO = new ActivityVO();
                activityVO.setCategoryName(c.getString(c.getColumnIndex(activityTable_categoryName_colum)));
                activityVO.setActivityName(c.getString(c.getColumnIndex(activityTable_activityName_colum)));
                activityVO.setFavorite(c.getString(c.getColumnIndex(activityTable_isFavorite_colum)));
                activityVO.setImageData(c.getString(c.getColumnIndex(acitivytTable_icon_colum)));
                activities.add(activityVO);
            }
        }
    }

    public void selectAllCategories(ArrayList<String> categories){
        DB = getWritableDatabase();
        String sql = String.format(Locale.getDefault(),
                "SELECT * FROM %s",
                categoryTableName
        );

        Cursor c = DB.rawQuery(sql, null);
        if(c != null) {
            while (c.moveToNext()) {
                  categories.add(c.getString(c.getColumnIndex(categoryTable_categoryName_colum)));
            }
        }
    }

    /**
     * 날짜에 맞는 해쉬맵을 불러와 스케쥴추가
     * @param allScheduleMap
     * @param scheduleYearMonth
     * @param schedule
     */
    private void addScheduleToMapByMonth(HashMap<Integer, HashMap<Integer, Schedule>> allScheduleMap, int scheduleYearMonth, Schedule schedule) {
        HashMap<Integer, Schedule> scheduleMap = allScheduleMap.get(scheduleYearMonth);
        //만약 해당 월의 scheduleMap이 없다면 생성
        if(scheduleMap == null){
            scheduleMap = new HashMap<Integer, Schedule>();
            allScheduleMap.put(scheduleYearMonth, scheduleMap);
        }

        scheduleMap.put(Integer.parseInt(schedule.getDate().substring(6,8) + "000" + schedule.getOrder()), schedule);
        Log.d("해당 월의 스케쥴맵에 추가할 때 키값 체크", String.valueOf(Integer.parseInt(schedule.getDate().substring(6,8) + "000" + schedule.getOrder())));
    }


    /**
     * 해당 스케쥴 데이터의 메모를 DB에 업데이트
     * @param parameter
     */
    public int updateMemo(Object parameter, final Entry e, String dateValue, int orderValue) {
        String memo = String.valueOf(parameter);
        DB = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.clear();
        values.put(memoValue_colum, memo);

        int result = DB.update(scheduleTableName, values,
                dateValue_colum + " = ? AND "
                        + orderValue_colum + " = ?",
                new String[] { dateValue,
                        String.valueOf(orderValue)});
        return result;
    }

    public int updateFavoriteChecked(String actvityName, String isFavoriteChecked) {
        DB = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.clear();
        values.put(activityTable_isFavorite_colum, isFavoriteChecked);

        int result = DB.update(activityTableName, values,
                activityTable_activityName_colum + " = ?",
                new String[] {actvityName});
        return result;
    }

    public int updateActivityNameAndIcon(ActivityVO activityVO, String originalActivityName) {
        DB = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.clear();
        values.put(activityTable_activityName_colum, activityVO.getActivityName());
        values.put(acitivytTable_icon_colum, activityVO.getImageData());

        int result = DB.update(activityTableName, values,
                activityTable_activityName_colum + " = ?",
                new String[] {originalActivityName});
        return result;
    }

    public void deleteActivity(String activityName) {
        DB = getWritableDatabase();
        int resultNumber = DB.delete(activityTableName, activityTable_activityName_colum + " = ?",
                new String[]{activityName});
    }

    public void deleteCategory(String categoryName) {
        DB = getWritableDatabase();
        int resultNumber = DB.delete(categoryTableName, categoryTable_categoryName_colum + " = ?",
                new String[]{categoryName});
    }

    public void deleteClearAllData(){
        DB = getWritableDatabase();
        DB.delete(activityTableName, null, null);
        DB.delete(categoryTableName, null, null);
    }
}
