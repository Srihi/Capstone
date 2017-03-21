package com.sanath.moneytracker.common;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.sanath.moneytracker.adapters.Summary;
import com.sanath.moneytracker.data.DataContract;

import java.util.ArrayList;

/**
 * Created by sanathnandasiri on 3/21/17.
 */

public class SummaryLoader extends AsyncTaskLoader<ArrayList<Summary>> {

    private final Context context;
    private final int accountType;

    public SummaryLoader(Context context, int accountType) {
        super(context);
        this.context = context;
        this.accountType = accountType;
    }

    @Override
    public ArrayList<Summary> loadInBackground() {
        Cursor cursor = context.getContentResolver().query(DataContract.AccountEntry.CONTENT_URI, null,
                DataContract.AccountEntry.COLUMN_TYPE + " =?",
                new String[]{String.valueOf(accountType)},
                null);
        ArrayList<Summary> summaries = new ArrayList<>();
        Summary summary;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                double value = Utils.getBalance(context, cursor.getInt(cursor.getColumnIndex(DataContract.AccountEntry._ID)));
                if (value > 0) {
                    summary = new Summary(cursor.getString(cursor.getColumnIndex(DataContract.AccountEntry.COLUMN_NAME)),
                            value
                    );
                    summaries.add(summary);
                }
            } while (cursor.moveToNext());

            cursor.close();
        }
        return summaries;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
