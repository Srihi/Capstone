package com.sanath.moneytracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.sanath.moneytracker.data.DataContract.*;

/**
 * Created by sanathnandasiri on 1/24/17.
 */

public class DataHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "money_tracker.db";
    private final static int DATABASE_VERSION = 1;

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACCOUNT_TABLE);
        db.execSQL(CREATE_JOURNAL_TABLE);
        db.execSQL(CREATE_POSTING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_ACCOUNT_TABLE);
        db.execSQL(DROP_JOURNAL_TABLE);
        db.execSQL(DROP_POSTING_TABLE);
        onCreate(db);
    }

    private final String CREATE_ACCOUNT_TABLE = "create table " + AccountEntry.TABLE_NAME + " ( " +
            AccountEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            AccountEntry.COLUMN_NAME + " TEXT NOT NULL, " +
            AccountEntry.COLUMN_TYPE + " INTEGER NOT NULL, " +
            AccountEntry.COLUMN_ICON + " INTEGER, " +
            AccountEntry.COLUMN_COLOR + " INTEGER " +
            ")";
    private final String DROP_ACCOUNT_TABLE = "DROP TABLE IF EXISTS " + AccountEntry.TABLE_NAME;

    private final String CREATE_JOURNAL_TABLE = "create table " + JournalEntry.TABLE_NAME + " ( " +
            JournalEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            JournalEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
            JournalEntry.COLUMN_TYPE + " INTEGER NOT NULL, " +
            JournalEntry.COLUMN_PERIOD + " TEXT NOT NULL, " +
            JournalEntry.COLUMN_DATE_TIME + " INTEGER NOT NULL " +
            ")";
    private final String DROP_JOURNAL_TABLE = "DROP TABLE IF EXISTS " + JournalEntry.TABLE_NAME;

    private final String CREATE_POSTING_TABLE = "create table " + PostingEntry.TABLE_NAME + " ( " +
            PostingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            PostingEntry.COLUMN_JOURNAL_ID + " INTEGER NOT NULL, " +
            PostingEntry.COLUMN_ACCOUNT_ID + " INTEGER NOT NULL, " +
            PostingEntry.COLUMN_AMOUNT + " REAL NOT NULL, " +
            PostingEntry.COLUMN_DATE_TIME + " INTEGER NOT NULL " +
            ")";
    private final String DROP_POSTING_TABLE = "DROP TABLE IF EXISTS " + PostingEntry.TABLE_NAME;

}
