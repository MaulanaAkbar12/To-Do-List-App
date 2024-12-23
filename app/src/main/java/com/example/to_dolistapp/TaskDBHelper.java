package com.example.to_dolistapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class TaskDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tasklist.db";
    private static final int DATABASE_VERSION = 2;

    private static final String SQL_CREATE_CATEGORY_TABLE =
            "CREATE TABLE " + TaskContract.CategoryEntry.TABLE_NAME + " (" +
                    TaskContract.CategoryEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TaskContract.CategoryEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL);";

    private static final String SQL_CREATE_PRIORITY_TABLE =
            "CREATE TABLE " + TaskContract.PriorityEntry.TABLE_NAME + " (" +
                    TaskContract.PriorityEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TaskContract.PriorityEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL);";

    private static final String SQL_CREATE_TASK_TABLE =
            "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + " (" +
                    TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TaskContract.TaskEntry.COLUMN_TASK + " TEXT NOT NULL, " +
                    TaskContract.TaskEntry.COLUMN_CATEGORY_ID + " INTEGER, " +
                    TaskContract.TaskEntry.COLUMN_PRIORITY_ID + " INTEGER, " +
                    TaskContract.TaskEntry.COLUMN_NOTES + " TEXT, " +
                    TaskContract.TaskEntry.COLUMN_DUE_DATE + " TEXT, " +
                    TaskContract.TaskEntry.COLUMN_DUE_TIME + " TEXT, " +
                    TaskContract.TaskEntry.COLUMN_COMPLETED + " INTEGER DEFAULT 0, " +
                    "FOREIGN KEY(" + TaskContract.TaskEntry.COLUMN_CATEGORY_ID + ") REFERENCES " +
                    TaskContract.CategoryEntry.TABLE_NAME + "(" + TaskContract.CategoryEntry.COLUMN_ID + "), " +
                    "FOREIGN KEY(" + TaskContract.TaskEntry.COLUMN_PRIORITY_ID + ") REFERENCES " +
                    TaskContract.PriorityEntry.TABLE_NAME + "(" + TaskContract.PriorityEntry.COLUMN_ID + "));";

    public TaskDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(SQL_CREATE_CATEGORY_TABLE);
        db.execSQL(SQL_CREATE_PRIORITY_TABLE);
        db.execSQL(SQL_CREATE_TASK_TABLE);

        // Insert initial categories
        insertInitialCategories(db);

        // Insert initial priorities
        insertInitialPriorities(db);
    }

    private void insertInitialCategories(SQLiteDatabase db) {
        String[] categories = {"Work", "Study", "Personal"};

        for (String category : categories) {
            ContentValues values = new ContentValues();
            values.put(TaskContract.CategoryEntry.COLUMN_NAME, category);
            db.insert(TaskContract.CategoryEntry.TABLE_NAME, null, values);
        }
    }

    private void insertInitialPriorities(SQLiteDatabase db) {
        String[] priorities = {"High", "Medium", "Low"};

        for (String priority : priorities) {
            ContentValues values = new ContentValues();
            values.put(TaskContract.PriorityEntry.COLUMN_NAME, priority);
            db.insert(TaskContract.PriorityEntry.TABLE_NAME, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.CategoryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.PriorityEntry.TABLE_NAME);

        // Recreate tables
        onCreate(db);
    }

    // Method to get category names
    public String[] getCategoryNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {TaskContract.CategoryEntry.COLUMN_NAME};
        Cursor cursor = db.query(TaskContract.CategoryEntry.TABLE_NAME,
                projection,
                null, null, null, null, null);

        String[] categories = new String[cursor.getCount()];
        int i = 0;
        while (cursor.moveToNext()) {
            categories[i] = cursor.getString(
                    cursor.getColumnIndexOrThrow(TaskContract.CategoryEntry.COLUMN_NAME));
            i++;
        }
        cursor.close();
        return categories;
    }

    // Method to get priority names
    public String[] getPriorityNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {TaskContract.PriorityEntry.COLUMN_NAME};
        Cursor cursor = db.query(TaskContract.PriorityEntry.TABLE_NAME,
                projection,
                null, null, null, null, null);

        String[] priorities = new String[cursor.getCount()];
        int i = 0;
        while (cursor.moveToNext()) {
            priorities[i] = cursor.getString(
                    cursor.getColumnIndexOrThrow(TaskContract.PriorityEntry.COLUMN_NAME));
            i++;
        }
        cursor.close();
        return priorities;
    }
}
