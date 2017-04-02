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
import com.sanath.moneytracker.data.DataContract;
import com.sanath.moneytracker.data.DataContract.AccountEntry;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

/**
 * Created by sanathnandasiri on 2/19/17.
 */

public class AccountsSpinnerAdapter extends CursorAdapter implements SpinnerAdapter {

    private final Context context;

    public AccountsSpinnerAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View root = LayoutInflater.from(context).inflate(R.layout.spinner_item_accounts, parent, false);
        return root;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndex(AccountEntry.COLUMN_NAME));
        ((TextView) view.findViewById(R.id.textViewName)).setText(name);
        int icon = cursor.getInt(cursor.getColumnIndex(AccountEntry.COLUMN_ICON));
        int selectedColor = cursor.getInt(cursor.getColumnIndex(AccountEntry.COLUMN_COLOR));
        MaterialDrawableBuilder builder = Utils.getMaterialDrawableBuilder(context, icon, Color.WHITE);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageViewIcon);
        imageView.setImageDrawable(builder.build());
        imageView.setContentDescription(String.format(context.getString(R.string
                .cd_account_icon),name));
        Drawable background = imageView.getBackground();
        Utils.setBackgroundColor(background, selectedColor);
    }

    public int getSelectedAccountPosition(int accountId) {
        Cursor cursor = getCursor();
        if (cursor == null) {
            return -1;
        }
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            if (cursor.getInt(cursor.getColumnIndex(AccountEntry._ID)) == accountId) {
                return i;
            }
        }
        return -1;
    }
}
