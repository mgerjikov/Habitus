package com.example.android.habitus.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Martin on 8.7.2017 г..
 */

public class HabitusDbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "habits.db";

    /**
     * Database version. If the schema of the database is changed the version also
     * have to be changed incrementally. The convention is that the version starts from 1
     */
    private static final int DATABASE_VERSION = 1;


    public HabitusDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Here we create a String that contains the SQL statement to create
        // the habits table
        String SQL_CREATE_HABITS_TABLE = "CREATE TABLE " +
                HabitusContract.HabitusEntry.TABLE_NAME + "(" +
                HabitusContract.HabitusEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                HabitusContract.HabitusEntry.COLUMN_HABIT_NAME + " TEXT NOT NULL ," +
                HabitusContract.HabitusEntry.COLUMN_HABIT_TYPE + " TEXT ," +
                HabitusContract.HabitusEntry.COLUMN_HABIT_RANK + " INTEGER NOT NULL ," +
                HabitusContract.HabitusEntry.COLUMN_HABIT_MEASUREMENT + " INTEGER NOT NULL DEFAULT 0 " +
                ")";

        db.execSQL(SQL_CREATE_HABITS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }
}
