package com.sanath.moneytracker.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by sanathnandasiri on 1/24/17.
 */

public class DataContract {
    public static final String CONTENT_AUTHORITY = "com.sanath.moneytracker.data.DataProvider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public interface TransactionTypes {
        int BALANCE = 0;
        int TRANSFER = 1;
        int INCOME = 2;
        int EXPENSES = 3;
    }

    public interface AccountTypes {
        int TRANSFER = 0;
        int INCOME = 1;
        int EXPENSES = 2;
    }


    public static final String PATH_ACCOUNT = "account";

    public static final class AccountEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACCOUNT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACCOUNT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACCOUNT;

        // Table name
        public static final String TABLE_NAME = "account";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_COLOR = "color";
        public static final String COLUMN_ICON = "icon";


        public static Uri buildAccountUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildAccountsByTypeUri(int type) {
            return ContentUris.withAppendedId(CONTENT_URI.buildUpon().appendPath("by").build(), type);
        }
    }
}
