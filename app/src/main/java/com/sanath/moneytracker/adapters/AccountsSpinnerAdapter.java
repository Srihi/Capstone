package com.sanath.moneytracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.sanath.moneytracker.R;
import com.sanath.moneytracker.common.Utils;
import com.sanath.moneytracker.data.DataContract.AccountEntry;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

/**
 * Created by sanathnandasiri on 2/19/17.
 */

public class AccountsSpinnerAdapter extends CursorAdapter implements SpinnerAdapter {

    public AccountsSpinnerAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View root = LayoutInflater.from(context).inflate(R.layout.spinner_item_accounts, parent, false);
        return root;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.textViewName)).setText(cursor.getString(cursor.getColumnIndex(AccountEntry.COLUMN_NAME)));
        int icon = cursor.getInt(cursor.getColumnIndex(AccountEntry.COLUMN_ICON));
        int selectedColor = cursor.getInt(cursor.getColumnIndex(AccountEntry.COLUMN_COLOR));
        MaterialDrawableBuilder builder = Utils.getMaterialDrawableBuilder(context, icon, Color.WHITE);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageViewIcon);
        imageView.setImageDrawable(builder.build());
        Drawable background = imageView.getBackground();
        Utils.setBackgroundColor(background, selectedColor);
    }
}
