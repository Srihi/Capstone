package com.sanath.moneytracker.ui.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sanath.moneytracker.R;
import com.sanath.moneytracker.adapters.Summary;
import com.sanath.moneytracker.common.Utils;
import com.sanath.moneytracker.data.DataContract;

import java.util.ArrayList;

/**
 * Created by sna on 3/29/2017.
 */

public class AccountSummaryWidgetService extends RemoteViewsService {

    private static final String TAG = AccountSummaryWidgetService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG, "onGetViewFactory() called with: intent = [" + intent + "]");
        return new AccountSummaryWidgetFactory(getApplicationContext(), intent, AppWidgetManager
                .getInstance(getApplicationContext()));
    }

    private class AccountSummaryWidgetFactory implements RemoteViewsFactory {

        private final int appWidgetId;
        private final Context context;
        private final AppWidgetManager appWidgetManager;
        private ArrayList<Summary> summaries;

        public AccountSummaryWidgetFactory(Context context, Intent intent, AppWidgetManager appWidgetManager) {
            this.context = context;
            this.appWidgetManager = appWidgetManager;
            summaries = new ArrayList<>();
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            Log.d(TAG, "onCreate");
        }

        @Override
        public void onDataSetChanged() {
            Log.d(TAG, "onDataSetChanged");
            if (summaries != null) {
                summaries.clear();
                summaries.addAll(Utils.getSummaries(this.context, DataContract.AccountTypes.TRANSFER));
                Log.d(TAG, "onDataSetChanged() returned: " + summaries.toString());
                updateBalance(summaries);
            }
        }

        private void updateBalance(ArrayList<Summary> summaries) {
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout
                    .account_summary_widget);
            remoteViews.setTextViewText(R.id.textViewBalance, Utils.getAmountWithCurrency(Utils
                    .getBalance(summaries)));
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }

        @Override
        public void onDestroy() {
            if (summaries != null) {
                summaries.clear();
            }
            summaries = null;
        }

        @Override
        public int getCount() {
            return summaries.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout
                    .list_item_widget_summary);
            Summary summary = summaries.get(position);
            remoteViews.setTextViewText(R.id.textViewTitle, summary.getTitle());
            remoteViews.setTextViewText(R.id.textViewValue, Utils.getAmountWithCurrency(summary
                    .getValue()));
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
