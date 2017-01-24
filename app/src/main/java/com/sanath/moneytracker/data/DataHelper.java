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
        final String CREATE_ACCOUNT_TABLE = "create table " + AccountEntry.TABLE_NAME + " ( " +
                AccountEntry._ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
                AccountEntry.COLUMN_NAME + "TEXT NOT NULL, " +
                AccountEntry.COLUMN_TYPE + "INTEGER NOT NULL, " +
                AccountEntry.COLUMN_ICON + "INTEGER, " +
                AccountEntry.COLUMN_COLOR + "INTEGER " +
                ")";
        db.execSQL(CREATE_ACCOUNT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String DROP_ACCOUNT_TABLE = "DROP TABLE IF EXISTS " + AccountEntry.TABLE_NAME;
        db.execSQL(DROP_ACCOUNT_TABLE);
        onCreate(db);
    }
}
