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

public class CategoriesAdapter extends CursorRecyclerAdapter<CategoriesVH> {

    private final Context context;

    public CategoriesAdapter(Context context, Cursor c) {
        super(c);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(CategoriesVH holder, Cursor cursor) {
        holder.textViewCategoryName.setText(cursor.getString(cursor.getColumnIndex(AccountEntry.COLUMN_NAME)));
        int icon = cursor.getInt(cursor.getColumnIndex(AccountEntry.COLUMN_ICON));
        int selectedColor = cursor.getInt(cursor.getColumnIndex(AccountEntry.COLUMN_COLOR));
        MaterialDrawableBuilder builder = Utils.getMaterialDrawableBuilder(context, icon, Color.WHITE);
        holder.imageViewCategoryIcon.setImageDrawable(builder.build());
        Drawable background = holder.imageViewCategoryIcon.getBackground();
        Utils.setBackgroundColor(background,selectedColor);
    }

    @Override
    public CategoriesVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.list_item_category, parent, false);
        return new CategoriesVH(root);
    }
}
