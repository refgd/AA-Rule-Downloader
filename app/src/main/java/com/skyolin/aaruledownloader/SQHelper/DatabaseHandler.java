package com.skyolin.aaruledownloader.SQHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "aaRuleManager";

    // RULEList table name
    private static final String TABLE_RULE = "RULEList";
    private static final String TABLE_UPDATE = "UPDATE_TABLE";

    // RULEList Table Columns names
    private static final String KEY_INDEX = "idx";
    private static final String KEY_NAME = "name";
    private static final String KEY_VER = "ver";
    private static final String KEY_URL = "url";
    private static final String KEY_UP = "hasupdate";
    
    // UPDATE Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_UPDATE = "update_time";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_RULE + "("
                + KEY_INDEX + " TEXT PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_VER + " INTEGER,"
                + KEY_URL + " TEXT,"
                + KEY_UP + " INTEGER"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_UPDATE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_UPDATE + " INTEGER"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        ContentValues values = new ContentValues();
        values.put(KEY_UPDATE, 1);
        db.insert(TABLE_UPDATE, null, values);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UPDATE);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new rule
    public long addRule(ruleField rule) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_INDEX, rule.getIndex());
        values.put(KEY_NAME, rule.getName());
        values.put(KEY_VER, rule.getVersion());
        values.put(KEY_URL, rule.getUrl());
        values.put(KEY_UP, rule.hasUpdate());

        // Inserting Row
        long id = db.insert(TABLE_RULE, null, values);
        db.close(); // Closing database connection
        return id;
    }

    // Getting single dns
    public ruleField getRule(String idx) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RULE, new String[] { KEY_INDEX,
                        KEY_NAME, KEY_VER, KEY_URL, KEY_UP }, KEY_INDEX + "=?",
                new String[] { idx }, null, null, null, null);
        ruleField rule = null;
        if (cursor != null && cursor.moveToFirst())
            rule = new ruleField(cursor.getString(0), cursor.getString(1), Integer.parseInt(cursor.getString(2)), cursor.getString(3), Integer.parseInt(cursor.getString(4)));

        cursor.close();
        db.close();
        return rule;
    }

    // Getting All ruleFields
    public List<ruleField> getAllRULE() {
        List<ruleField> RULEList = new ArrayList<ruleField>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RULE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ruleField dns = new ruleField(cursor.getString(0),
                        cursor.getString(1), Integer.parseInt(cursor.getString(2)), cursor.getString(3), Integer.parseInt(cursor.getString(4)));
                // Adding dns to list
                RULEList.add(dns);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return dns list
        return RULEList;
    }

    // Updating single dns
    public int updateRule(ruleField rule) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_INDEX, rule.getIndex());
        values.put(KEY_NAME, rule.getName());
        values.put(KEY_VER, rule.getVersion());
        values.put(KEY_URL, rule.getUrl());
        values.put(KEY_UP, rule.hasUpdate());

        // updating row
        int r = db.update(TABLE_RULE, values, KEY_INDEX + " = ?",
                new String[] { rule.getIndex() });
        db.close();

        return r;
    }

    // Deleting single dns
    public void deleteRULE(ruleField rule) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RULE, KEY_INDEX + " = ?",
                new String[]{ rule.getIndex() });
        db.close();
    }

    // Deleting single dns by id
    public void deleteRULEByIndex(String idx) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RULE, KEY_INDEX + " = ?",
                new String[] { idx });
        db.close();
    }

    // Getting dnss Count
    public int getRULECount() {
        String countQuery = "SELECT  * FROM " + TABLE_RULE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int c = cursor.getCount();
        cursor.close();

        cursor.close();
        db.close();
        // return count
        return c;
    }

    public int getUpdateTime(){
        SQLiteDatabase db = this.getReadableDatabase();
        int r = 1;
        Cursor cursor = db.query(TABLE_UPDATE, new String[] { KEY_ID,
                        KEY_UPDATE }, KEY_ID + "=?",
                new String[] { "1" }, null, null, null, null);
        if (cursor != null && cursor.moveToFirst())
            r = Integer.parseInt(cursor.getString(1));

        cursor.close();
        db.close();

        return r;
    }

    public void setUpdateTime(){
        SQLiteDatabase db = this.getWritableDatabase();

        Long tsLong = System.currentTimeMillis()/1000;
        ContentValues values = new ContentValues();
        values.put(KEY_UPDATE, tsLong);

        // updating row
        db.update(TABLE_UPDATE, values, KEY_ID + " = ?", new String[] { "1" });

        db.close();
    }

}
