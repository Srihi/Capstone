package com.sanath.moneytracker.common;

import android.content.Context;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

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
}
