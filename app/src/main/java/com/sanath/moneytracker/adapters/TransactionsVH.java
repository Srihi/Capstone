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

public class TransactionsVH extends RecyclerView.ViewHolder {

    @BindView(R.id.imageViewTransactionIcon)
    ImageView imageViewTransactionIcon;
    @BindView(R.id.textViewTransactionDescription)
    TextView textViewTransactionDescription;
    @BindView(R.id.textViewTransactionAmount)
    TextView textViewTransactionAmount;
    @BindView(R.id.textViewTransactionDetails)
    TextView textViewTransactionDetails;


    public TransactionsVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
