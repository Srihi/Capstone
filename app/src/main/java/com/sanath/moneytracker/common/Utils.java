package com.sanath.moneytracker.common;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.widget.TextView;

import com.sanath.moneytracker.adapters.Summary;
import com.sanath.moneytracker.data.DataContract;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by sanathnandasiri on 2/4/17.
 */

public class Utils {
    public static MaterialDrawableBuilder getMaterialDrawableBuilder(Context context, int icon, int selectedColor) {
        return MaterialDrawableBuilder.with(context) // provide a context
                .setIcon(MaterialDrawableBuilder.IconValue.values()[icon])
                .setToActionbarSize()
                .setColor(selectedColor);
    }

    public static void setBackgroundColor(Drawable background, int color) {
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable) background).getPaint().setColor(color);
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setColor(color);
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable) background).setColor(color);
        }
    }

    @NonNull
    public static SpannableString getAmountWithCurrency(double amount) {
        return new SpannableString(NumberFormat.getCurrencyInstance(Locale.getDefault())
                .format(Math.abs(amount)));
    }

    public static double getBalance(Context context, int accountId) {
        double balance = 0.0;
        Cursor cursorBalance = context.getContentResolver().query(DataContract.PostingEntry.CONTENT_URI,
                new String[]{"sum(" + DataContract.PostingEntry.COLUMN_AMOUNT + ") as balance"},
                DataContract.PostingEntry.COLUMN_ACCOUNT_ID + "=?",
                new String[]{String.valueOf(accountId)}, null);
        if (cursorBalance != null && cursorBalance.moveToFirst()) {
            balance = cursorBalance.getDouble(cursorBalance.getColumnIndex("balance"));
            cursorBalance.close();
        }
        return balance;
    }

    public static String getPeriodTag(long transactionDateTime) {
        SimpleDateFormat sdfPeriod = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        return sdfPeriod.format(transactionDateTime);
    }

    @NonNull
    public static String[] getProjectionForTransaction() {
        return new String[]{
                DataContract.JournalEntry.TABLE_NAME + "." + DataContract.JournalEntry._ID,
                DataContract.PostingEntry.TABLE_NAME + "." + DataContract.PostingEntry.COLUMN_ACCOUNT_ID,
                DataContract.PostingEntry.TABLE_NAME + "." + DataContract.PostingEntry.COLUMN_JOURNAL_ID,
                DataContract.PostingEntry.TABLE_NAME + "." + DataContract.PostingEntry.COLUMN_AMOUNT,
                DataContract.PostingEntry.TABLE_NAME + "." + DataContract.PostingEntry.COLUMN_CREDIT_DEBIT,
                DataContract.PostingEntry.TABLE_NAME + "." + DataContract.PostingEntry.COLUMN_DATE_TIME,
                DataContract.JournalEntry.TABLE_NAME + "." + DataContract.JournalEntry.COLUMN_DESCRIPTION,
                DataContract.JournalEntry.TABLE_NAME + "." + DataContract.JournalEntry.COLUMN_PERIOD,
                DataContract.JournalEntry.TABLE_NAME + "." + DataContract.JournalEntry.COLUMN_TYPE + " as " + DataContract.TransactionEntry.TRANSACTION_TYPE,
                DataContract.AccountEntry.TABLE_NAME + "." + DataContract.AccountEntry.COLUMN_NAME,
                DataContract.AccountEntry.TABLE_NAME + "." + DataContract.AccountEntry.COLUMN_TYPE + " as " + DataContract.AccountEntry.ACCOUNT_TYPE,
                DataContract.PostingEntry.TABLE_NAME + "." + DataContract.PostingEntry._ID + " as " + DataContract.TransactionEntry.POSTING_ID,
                DataContract.AccountEntry.TABLE_NAME + "." + DataContract.AccountEntry.COLUMN_COLOR,
                DataContract.AccountEntry.TABLE_NAME + "." + DataContract.AccountEntry.COLUMN_ICON,
        };
    }

    @NonNull
    public static ArrayList<Summary> getSummaries(Context context, int accountType) {
        Cursor cursor = context.getContentResolver().query(DataContract.AccountEntry.CONTENT_URI, null,
                DataContract.AccountEntry.COLUMN_TYPE + " =?",
                new String[]{String.valueOf(accountType)},
                null);
        ArrayList<Summary> summaries = new ArrayList<>();
        Summary summary;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                double value = getBalance(context, cursor.getInt(cursor.getColumnIndex(DataContract.AccountEntry._ID)));
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

    public static void setBalance(ArrayList<Summary> data, TextView textView) {
        textView.setText(getAmountWithCurrency(getBalance(data)));
    }

    public static double getBalance(ArrayList<Summary> data) {
        double balance = 0.0;
        for (Summary summary : data) {
            balance = balance + summary.getValue();
        }
        return balance;
    }
}
