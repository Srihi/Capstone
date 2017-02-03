package com.sanath.moneytracker.adapters;

import android.content.Context;
import android.database.Cursor;
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
        MaterialDrawableBuilder builder = Utils.getMaterialDrawableBuilder(context, icon, selectedColor);
        holder.imageViewAccountIcon.setImageDrawable(builder.build());
        holder.textViewAccountName.setText(cursor.getString(cursor.getColumnIndex(AccountEntry.COLUMN_NAME)));
    }

    @Override
    public AccountsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.list_item_account, parent, false);
        return new AccountsVH(root);
    }
}
