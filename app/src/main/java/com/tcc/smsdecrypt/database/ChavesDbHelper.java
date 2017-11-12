package com.tcc.smsdecrypt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by claudinei on 21/09/17.
 */

public class ChavesDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Chaves.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Chaves.FeedEntry.TABLE_NAME + " (" +
                    Chaves.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    Chaves.FeedEntry.COLUMN_EMAIL + TEXT_TYPE + " unique"+ COMMA_SEP +
                    Chaves.FeedEntry.COLUMN_TOKEN + TEXT_TYPE + COMMA_SEP +
                    Chaves.FeedEntry.COLUMN_REFRESH_TOKEN + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Chaves.FeedEntry.TABLE_NAME;

    public boolean insert(String email, String token, String refreshToken){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Chaves.FeedEntry.COLUMN_EMAIL, email);
        values.put(Chaves.FeedEntry.COLUMN_TOKEN, token);
        values.put(Chaves.FeedEntry.COLUMN_REFRESH_TOKEN, refreshToken);

        System.out.println("NOVOTOKEN" + token);

        Long id = null;
        int id2 = -1;
        try{
            id = db.insertWithOnConflict(Chaves.FeedEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_FAIL);
        }catch (Exception e){
            System.out.println("UPDATE");
            id2 = db.update(Chaves.FeedEntry.TABLE_NAME, values, Chaves.FeedEntry.COLUMN_EMAIL + "= '" + email + "'", null);
        }

        if( (id != null && id != -1) || (id2 != -1) ){
            return true;
        }else{
            return false;
        }
    }

    public List<Token> getChaves(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Chaves.FeedEntry.TABLE_NAME,null);
        List<Token> list = new ArrayList<Token>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String email = cursor.getString(cursor.getColumnIndex(Chaves.FeedEntry.COLUMN_EMAIL));
                String token = cursor.getString(cursor.getColumnIndex(Chaves.FeedEntry.COLUMN_TOKEN));
                String refreshToken = cursor.getString(cursor.getColumnIndex(Chaves.FeedEntry.COLUMN_REFRESH_TOKEN));
                Token tokenT = new Token(email, token, refreshToken);

                list.add(tokenT);
                cursor.moveToNext();
            }
        }
        return list;
    }

    public String getAccessToken(String email){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select " + Chaves.FeedEntry.COLUMN_TOKEN + " from " + Chaves.FeedEntry.TABLE_NAME
                + " where " + Chaves.FeedEntry.COLUMN_EMAIL + " = '" + email + "'",null);

        String token = null;
        if (cursor.moveToFirst()) {
            token = cursor.getString(cursor.getColumnIndex(Chaves.FeedEntry.COLUMN_TOKEN));
            System.out.println(token);
        }

        System.out.println("1");
        return token;

    }

    public ChavesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
