package com.sanath.moneytracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanath.moneytracker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sanathnandasiri on 2/4/17.
 */

public class CategoriesVH extends RecyclerView.ViewHolder {

    @BindView(R.id.imageViewCategoryIcon)
    ImageView imageViewCategoryIcon;
    @BindView(R.id.textViewCategoryName)
    TextView textViewCategoryName;


    public CategoriesVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
