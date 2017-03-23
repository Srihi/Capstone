package com.sanath.moneytracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sanath.moneytracker.common.Utils;

import java.util.Date;

import static com.sanath.moneytracker.data.DataContract.*;

/**
 * Created by sanathnandasiri on 1/24/17.
 */

public class DataHelper extends SQLiteOpenHelper {
    private static final String TAG = DataHelper.class.getSimpleName();

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

        addInitialData(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.setForeignKeyConstraintsEnabled(true);
        }
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
            PostingEntry.COLUMN_CREDIT_DEBIT + " INTEGER NOT NULL, " +
            PostingEntry.COLUMN_AMOUNT + " REAL NOT NULL, " +
            PostingEntry.COLUMN_DATE_TIME + " INTEGER NOT NULL, " +
            " FOREIGN KEY ( " + PostingEntry.COLUMN_JOURNAL_ID + " ) REFERENCES " +
            JournalEntry.TABLE_NAME + " ( " + JournalEntry._ID + " ) ON DELETE CASCADE, " +
            " FOREIGN KEY ( " + PostingEntry.COLUMN_ACCOUNT_ID + " ) REFERENCES " +
            AccountEntry.TABLE_NAME + " ( " + AccountEntry._ID + " ) ON DELETE CASCADE" +
            ")";

    private final String DROP_POSTING_TABLE = "DROP TABLE IF EXISTS " + PostingEntry.TABLE_NAME;

    private void addInitialData(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO " + AccountEntry.TABLE_NAME + " ( " + AccountEntry._ID + ", " + AccountEntry.COLUMN_NAME + "," + AccountEntry.COLUMN_TYPE + "," + AccountEntry.COLUMN_ICON + "," + AccountEntry.COLUMN_COLOR + " ) "
                    + "VALUES (1,'Bank',	0,111,-12627531);");
            db.execSQL("INSERT INTO " + AccountEntry.TABLE_NAME + " ( " + AccountEntry._ID + ", " + AccountEntry.COLUMN_NAME + "," + AccountEntry.COLUMN_TYPE + "," + AccountEntry.COLUMN_ICON + "," + AccountEntry.COLUMN_COLOR + " ) "
                    + "VALUES (2,'Wallet',0,1411,-1683200);");
            db.execSQL("INSERT INTO " + AccountEntry.TABLE_NAME + " ( " + AccountEntry._ID + ", " + AccountEntry.COLUMN_NAME + "," + AccountEntry.COLUMN_TYPE + "," + AccountEntry.COLUMN_ICON + "," + AccountEntry.COLUMN_COLOR + " ) "
                    + "VALUES (3,'Other',0,275,-14273992);");
            db.execSQL("INSERT INTO " + AccountEntry.TABLE_NAME + " ( " + AccountEntry._ID + ", " + AccountEntry.COLUMN_NAME + "," + AccountEntry.COLUMN_TYPE + "," + AccountEntry.COLUMN_ICON + "," + AccountEntry.COLUMN_COLOR + " ) "
                    + "VALUES (4,'Food',2,601,-10044566);");
            db.execSQL("INSERT INTO " + AccountEntry.TABLE_NAME + " ( " + AccountEntry._ID + ", " + AccountEntry.COLUMN_NAME + "," + AccountEntry.COLUMN_TYPE + "," + AccountEntry.COLUMN_ICON + "," + AccountEntry.COLUMN_COLOR + " ) "
                    + "VALUES (5,'Shopping',2,271,-769226);");
            db.execSQL("INSERT INTO " + AccountEntry.TABLE_NAME + " ( " + AccountEntry._ID + ", " + AccountEntry.COLUMN_NAME + "," + AccountEntry.COLUMN_TYPE + "," + AccountEntry.COLUMN_ICON + "," + AccountEntry.COLUMN_COLOR + " ) "
                    + "VALUES (6,'Car',2,266,-2825897);");
            db.execSQL("INSERT INTO " + AccountEntry.TABLE_NAME + " ( " + AccountEntry._ID + ", " + AccountEntry.COLUMN_NAME + "," + AccountEntry.COLUMN_TYPE + "," + AccountEntry.COLUMN_ICON + "," + AccountEntry.COLUMN_COLOR + " ) "
                    + "VALUES (7,'Fuel',2,663,-5317	);");
            db.execSQL("INSERT INTO " + AccountEntry.TABLE_NAME + " ( " + AccountEntry._ID + ", " + AccountEntry.COLUMN_NAME + "," + AccountEntry.COLUMN_TYPE + "," + AccountEntry.COLUMN_ICON + "," + AccountEntry.COLUMN_COLOR + " ) "
                    + "VALUES (8,'Home',2,731,-10011977	);");
            db.execSQL("INSERT INTO " + AccountEntry.TABLE_NAME + " ( " + AccountEntry._ID + ", " + AccountEntry.COLUMN_NAME + "," + AccountEntry.COLUMN_TYPE + "," + AccountEntry.COLUMN_ICON + "," + AccountEntry.COLUMN_COLOR + " ) "
                    + "VALUES (9,'Medical',2,1240,-3790808);");
            db.execSQL("INSERT INTO " + AccountEntry.TABLE_NAME + " ( " + AccountEntry._ID + ", " + AccountEntry.COLUMN_NAME + "," + AccountEntry.COLUMN_TYPE + "," + AccountEntry.COLUMN_ICON + "," + AccountEntry.COLUMN_COLOR + " ) "
                    + "VALUES (10,'Education',2,1139,-14273992);");
            db.execSQL("INSERT INTO " + AccountEntry.TABLE_NAME + " ( " + AccountEntry._ID + ", " + AccountEntry.COLUMN_NAME + "," + AccountEntry.COLUMN_TYPE + "," + AccountEntry.COLUMN_ICON + "," + AccountEntry.COLUMN_COLOR + " ) "
                    + "VALUES (11,'Coffee',2,373,-7508381);");
            db.execSQL("INSERT INTO " + AccountEntry.TABLE_NAME + " ( " + AccountEntry._ID + ", " + AccountEntry.COLUMN_NAME + "," + AccountEntry.COLUMN_TYPE + "," + AccountEntry.COLUMN_ICON + "," + AccountEntry.COLUMN_COLOR + " ) "
                    + "VALUES (12,'Gift',2,672,-688361	);");
            db.execSQL("INSERT INTO " + AccountEntry.TABLE_NAME + " ( " + AccountEntry._ID + ", " + AccountEntry.COLUMN_NAME + "," + AccountEntry.COLUMN_TYPE + "," + AccountEntry.COLUMN_ICON + "," + AccountEntry.COLUMN_COLOR + " ) "
                    + "VALUES (13,'Salary',1,213,-1023342);");
            db.execSQL("INSERT INTO " + AccountEntry.TABLE_NAME + " ( " + AccountEntry._ID + ", " + AccountEntry.COLUMN_NAME + "," + AccountEntry.COLUMN_TYPE + "," + AccountEntry.COLUMN_ICON + "," + AccountEntry.COLUMN_COLOR + " ) "
                    + "VALUES (14,'Freelance',1,275,-5434281);");
            db.execSQL("INSERT INTO " + AccountEntry.TABLE_NAME + " ( " + AccountEntry._ID + ", " + AccountEntry.COLUMN_NAME + "," + AccountEntry.COLUMN_TYPE + "," + AccountEntry.COLUMN_ICON + "," + AccountEntry.COLUMN_COLOR + " ) "
                    + "VALUES (15,'Other',	1,890,-14244198);");
            db.execSQL("INSERT INTO " + AccountEntry.TABLE_NAME + " ( " + AccountEntry._ID + ", " + AccountEntry.COLUMN_NAME + "," + AccountEntry.COLUMN_TYPE + "," + AccountEntry.COLUMN_ICON + "," + AccountEntry.COLUMN_COLOR + " ) "
                    + "VALUES (16,'Other',	2,890,-8875876);");
            long time = new Date().getTime();
            String period = Utils.getPeriodTag(time);

            db.execSQL("INSERT INTO " + JournalEntry.TABLE_NAME + " ( " + JournalEntry._ID + ", " + JournalEntry.COLUMN_DESCRIPTION + "," + JournalEntry.COLUMN_TYPE + "," + JournalEntry.COLUMN_PERIOD + "," + JournalEntry.COLUMN_DATE_TIME + " ) "
                    + "VALUES (1,	'initial balance',	0	," + period + "	," + time + "	);");
            db.execSQL("INSERT INTO " + JournalEntry.TABLE_NAME + " ( " + JournalEntry._ID + ", " + JournalEntry.COLUMN_DESCRIPTION + "," + JournalEntry.COLUMN_TYPE + "," + JournalEntry.COLUMN_PERIOD + "," + JournalEntry.COLUMN_DATE_TIME + " ) "
                    + "VALUES (2,	'initial balance',	0	," + period + "	," + time + "	);");
            db.execSQL("INSERT INTO " + JournalEntry.TABLE_NAME + " ( " + JournalEntry._ID + ", " + JournalEntry.COLUMN_DESCRIPTION + "," + JournalEntry.COLUMN_TYPE + "," + JournalEntry.COLUMN_PERIOD + "," + JournalEntry.COLUMN_DATE_TIME + " ) "
                    + "VALUES (3,	'initial balance',	0	," + period + "	," + time + "	);");

            db.execSQL("INSERT INTO " + PostingEntry.TABLE_NAME + " ( " + PostingEntry._ID + ", " + PostingEntry.COLUMN_JOURNAL_ID + "," + PostingEntry.COLUMN_ACCOUNT_ID + "," + PostingEntry.COLUMN_CREDIT_DEBIT + "," + PostingEntry.COLUMN_AMOUNT + "," + PostingEntry.COLUMN_DATE_TIME + " ) "
                    + "VALUES (1, 1, 1, 1, 0.0," + time + "	);");
            db.execSQL("INSERT INTO " + PostingEntry.TABLE_NAME + " ( " + PostingEntry._ID + ", " + PostingEntry.COLUMN_JOURNAL_ID + "," + PostingEntry.COLUMN_ACCOUNT_ID + "," + PostingEntry.COLUMN_CREDIT_DEBIT + "," + PostingEntry.COLUMN_AMOUNT + "," + PostingEntry.COLUMN_DATE_TIME + " ) "
                    + "VALUES (2, 2, 2, 1, 0.0," + time + "	);");
            db.execSQL("INSERT INTO " + PostingEntry.TABLE_NAME + " ( " + PostingEntry._ID + ", " + PostingEntry.COLUMN_JOURNAL_ID + "," + PostingEntry.COLUMN_ACCOUNT_ID + "," + PostingEntry.COLUMN_CREDIT_DEBIT + "," + PostingEntry.COLUMN_AMOUNT + "," + PostingEntry.COLUMN_DATE_TIME + " ) "
                    + "VALUES (3, 3, 3, 1, 0.0," + time + "	);");
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            db.endTransaction();
        }
    }
}
