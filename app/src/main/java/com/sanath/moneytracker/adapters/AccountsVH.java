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

public class AccountsVH extends RecyclerView.ViewHolder {

    @BindView(R.id.imageViewAccountIcon)
    ImageView imageViewAccountIcon;
    @BindView(R.id.textViewAccountName)
    TextView textViewAccountName;
    @BindView(R.id.textViewBalance)
    TextView textViewBalance;


    public AccountsVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
