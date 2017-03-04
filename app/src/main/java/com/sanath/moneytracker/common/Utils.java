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

import com.sanath.moneytracker.data.DataContract;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.text.NumberFormat;
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
}
