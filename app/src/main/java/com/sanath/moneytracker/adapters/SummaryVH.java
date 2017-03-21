package com.sanath.moneytracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sanath.moneytracker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sanathnandasiri on 3/20/17.
 */

class SummaryVH extends RecyclerView.ViewHolder {

    @BindView(R.id.textViewTitle)
    TextView textViewTitle;

    @BindView(R.id.textViewValue)
    TextView textViewValue;


    public SummaryVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
