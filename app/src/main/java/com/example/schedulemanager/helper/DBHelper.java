package com.example.schedulemanager.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.schedulemanager.vo.Schedule;
import com.example.schedulemanager.Util;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;

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
    public String scheduleTableName = "schedule";

    // 컬럼명
    String no_colum = "no";
    String dateValue_colum = "dateValue";
    String activityName_colum = "activityName";
    String orderValue_colum = "orderValue";
    String timeValue_colum = "timeValue";
    String memoValue_colum = "memoValue";


    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DB_VERSION_1);
        dbHelper = this;

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

        // 2번 쿼리: 즐겨찾기버튼 테이블 생성
        String sql2 = new StringBuilder("CREATE TABLE favoriteButtons (").append("no")
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
                .append("activityName").append(" TEXT NOT NULL)")
                .toString();
        db.execSQL(sql2);

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
        long result = DB.insert(scheduleTableName, null, values);
        return result;
    }

    /**
     * 스케쥴 1개 삭제
     * @param dateColumValue
     * @param orderColumValue
     */
    public void deleteSchedule(String dateColumValue, int orderColumValue) {
        DB = getWritableDatabase();
        int resultNumber = DB.delete(scheduleTableName, dateValue_colum + " = ? AND " + orderValue_colum + " = ?",
                new String[]{dateColumValue, String.valueOf(orderColumValue)});
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
                schedule.setMemo(c.getString(c.getColumnIndex(timeValue_colum)));
                schedule.setTime(c.getString(c.getColumnIndex(memoValue_colum)));

                String scheduleYearMonth = Util.getYearMonthFromDate(scheduleDate);
                // 스케쥴 추가
                addScheduleToMapByMonth(allScheduleMap, Integer.parseInt(scheduleYearMonth), schedule);
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
//        Log.d("addScheduleToMapByMonth" , String.valueOf(Integer.parseInt(schedule.getDate().substring(6,8))));
        scheduleMap.put(Integer.parseInt(schedule.getDate().substring(6,8) + "000" + schedule.getOrder()), schedule);
    }

    /**
     * 해당 스케쥴 데이터의 메모를 업데이트
     * @param parameter
     */
    public void updateMemo(Object parameter, final Entry e, int orderValue) {
        Log.d("업데이트 메모 테스트", String.valueOf(parameter));
        DB = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.clear();
        //TODO no에서 sqlite 에러가 나서 이쪽을 막고 나중에 같은 date값을 order로 표시하게
//        values.put(no_colum, schedule.getNo());
        values.put(dateValue_colum, schedule.getDate());
        values.put(activityName_colum, schedule.getActivityName());
        values.put(orderValue_colum, schedule.getOrder());
        values.put(timeValue_colum, schedule.getTime());
        values.put(memoValue_colum, schedule.getMemo());
        long result = DB.update(scheduleTableName, values
        , null, values);

    }
}
