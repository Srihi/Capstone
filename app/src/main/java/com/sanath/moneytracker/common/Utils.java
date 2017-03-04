package com.sanath.moneytracker.common;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.SpannableString;

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
}
