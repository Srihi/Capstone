package com.sanath.moneytracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanath.moneytracker.R;
import com.sanath.moneytracker.common.CursorRecyclerAdapter;
import com.sanath.moneytracker.common.ItemClickListener;
import com.sanath.moneytracker.common.Utils;
import com.sanath.moneytracker.data.DataContract.AccountEntry;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

/**
 * Created by sanathnandasiri on 2/4/17.
 */

public class AccountsAdapter extends CursorRecyclerAdapter<AccountsVH> {

    private final Context context;

    private ItemClickListener<Uri> itemClickListener;

    public AccountsAdapter(Context context, Cursor c, ItemClickListener<Uri> itemClickListener) {
        super(c);
        this.context = context;
        this.itemClickListener = itemClickListener;
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

        holder.textViewBalance.setText(Utils.getAmountWithCurrency(Utils.getBalance(context,cursor.getInt(cursor.getColumnIndex(AccountEntry._ID)))));
    }

    @Override
    public AccountsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.list_item_account, parent, false);
        final AccountsVH vh = new AccountsVH(root);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(
                            AccountEntry.buildAccountUri(getItemId(vh.getAdapterPosition())));
                }
            }
        });
        return vh;
    }
}
