package com.sanath.moneytracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanath.moneytracker.R;
import com.sanath.moneytracker.common.CursorRecyclerAdapter;
import com.sanath.moneytracker.common.Utils;
import com.sanath.moneytracker.data.DataContract.AccountEntry;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

/**
 * Created by sanathnandasiri on 2/4/17.
 */

public class AccountsAdapter extends CursorRecyclerAdapter<AccountsVH> {

    private final Context context;

    public AccountsAdapter(Context context, Cursor c) {
        super(c);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(AccountsVH holder, Cursor cursor) {
        holder.textViewAccountName.setText(cursor.getString(cursor.getColumnIndex(AccountEntry.COLUMN_NAME)));
        int icon = cursor.getInt(cursor.getColumnIndex(AccountEntry.COLUMN_ICON));
        int selectedColor = cursor.getInt(cursor.getColumnIndex(AccountEntry.COLUMN_COLOR));
        MaterialDrawableBuilder builder = Utils.getMaterialDrawableBuilder(context, icon, Color.WHITE);
        holder.imageViewAccountIcon.setImageDrawable(builder.build());
        Drawable background = holder.imageViewAccountIcon.getBackground();
        Utils.setBackgroundColor(background, selectedColor);
    }

    @Override
    public AccountsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.list_item_account, parent, false);
        return new AccountsVH(root);
    }
}