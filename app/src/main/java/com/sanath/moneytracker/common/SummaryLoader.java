package com.sanath.moneytracker.common;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.sanath.moneytracker.adapters.Summary;

import java.util.ArrayList;

/**
 * Created by sanathnandasiri on 3/21/17.
 */

public class SummaryLoader extends AsyncTaskLoader<ArrayList<Summary>> {

    private final Context context;
    private final int accountType;

    public SummaryLoader(Context context, int accountType) {
        super(context);
        this.context = context;
        this.accountType = accountType;
    }

    @Override
    public ArrayList<Summary> loadInBackground() {
        return Utils.getSummaries(context, accountType);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
