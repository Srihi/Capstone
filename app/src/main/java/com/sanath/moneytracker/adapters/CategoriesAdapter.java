package com.sanath.moneytracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sanath.moneytracker.R;
import com.sanath.moneytracker.common.CursorRecyclerAdapter;
import com.sanath.moneytracker.common.ItemClickListener;
import com.sanath.moneytracker.common.Utils;
import com.sanath.moneytracker.data.DataContract.AccountEntry;
import com.sanath.moneytracker.ui.activities.AddTransactionActivity;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

/**
 * Created by sanathnandasiri on 2/4/17.
 */

public class CategoriesAdapter extends CursorRecyclerAdapter<CategoriesVH> {

    private final Context context;

    private ItemClickListener<Uri> itemClickListener;

    public CategoriesAdapter(Context context, Cursor c, ItemClickListener<Uri> itemClickListener) {
        super(c);
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(CategoriesVH holder, final Cursor cursor) {
        holder.textViewCategoryName.setText(cursor.getString(cursor.getColumnIndex(AccountEntry.COLUMN_NAME)));
        int icon = cursor.getInt(cursor.getColumnIndex(AccountEntry.COLUMN_ICON));
        int selectedColor = cursor.getInt(cursor.getColumnIndex(AccountEntry.COLUMN_COLOR));
        MaterialDrawableBuilder builder = Utils.getMaterialDrawableBuilder(context, icon, Color.WHITE);
        holder.imageViewCategoryIcon.setImageDrawable(builder.build());
        Drawable background = holder.imageViewCategoryIcon.getBackground();
        Utils.setBackgroundColor(background, selectedColor);
    }

    @Override
    public CategoriesVH onCreateViewHolder(final ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.list_item_category, parent, false);
        final CategoriesVH vh = new CategoriesVH(root);
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
