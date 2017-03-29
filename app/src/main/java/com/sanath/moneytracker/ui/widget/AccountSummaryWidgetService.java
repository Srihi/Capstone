package com.sanath.moneytracker.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.CursorLoader;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * Created by sna on 3/29/2017.
 */

public class AccountSummaryWidgetService extends RemoteViewsService {

    private static final String TAG = AccountSummaryWidgetService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new AccountSummaryWidgetFactory(getApplicationContext(), intent);
    }

    private class AccountSummaryWidgetFactory implements RemoteViewsFactory {

        private Context context;
        private CursorLoader cursorLoader;

        public AccountSummaryWidgetFactory(Context context, Intent intent) {
            this.context = context;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            return null;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 0;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
