package com.sanath.moneytracker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanath.moneytracker.R;
import com.sanath.moneytracker.common.Utils;

import java.util.ArrayList;

/**
 * Created by sanathnandasiri on 3/20/17.
 */

public class SummaryAdapter extends RecyclerView.Adapter<SummaryVH> {

    private final Context context;
    private final ArrayList<Summary> summaries;

    public SummaryAdapter(Context context, ArrayList<Summary> summaries) {
        super();
        this.context = context;
        this.summaries = summaries;
    }

    @Override
    public SummaryVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.list_item_summary, parent, false);
        SummaryVH vh = new SummaryVH(root);
        return vh;
    }

    @Override
    public void onBindViewHolder(SummaryVH holder, int position) {
        Summary summary = summaries.get(position);
        holder.textViewTitle.setText(summary.getTitle());
        holder.textViewValue.setText(Utils.getAmountWithCurrency(summary.getValue()));
    }

    @Override
    public int getItemCount() {
        return summaries != null ? summaries.size() : 0;
    }
}
