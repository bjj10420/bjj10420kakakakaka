package com.example.schedulemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        values.put(no_colum, schedule.getNo());
        values.put(dateValue_colum, schedule.getDate());
        values.put(activityName_colum, schedule.getActivityName());
        values.put(orderValue_colum, schedule.getOrder());
        values.put(timeValue_colum, schedule.getTime());
        values.put(memoValue_colum, schedule.getMemo());
        long result = DB.insert(scheduleTableName, null, values);
        return result;
    }

    /**
     * 당일의 스케쥴 갯수 카운트
     * @param dateForToday
     * @return
     */
    public int getScheduleCountForToday(String dateForToday){
        DB = getWritableDatabase();

        String sql = String.format(Locale.getDefault(),
       "SELECT COUNT(*) FROM %s WHERE %s = %s",
        scheduleTableName, 							// 테이블 이름
        dateValue_colum,	                                    // 조건 칼럼
        dateForToday);

        Cursor c = DB.rawQuery(sql, null);
        int count = 0;
        if(c != null) {
            c.moveToFirst();
            count = c.getInt(0);
        }
        return count;
    }
}
