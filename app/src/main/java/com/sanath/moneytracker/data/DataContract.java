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
        int ALL = 4;
    }

    public interface AccountTypes {
        int TRANSFER = 0;
        int INCOME = 1;
        int EXPENSES = 2;
    }

    public interface CreditType {
        int CREDIT = 0;
        int DEBIT = 1;
    }


    public static final String PATH_ACCOUNT = "account";
    public static final String PATH_JOURNAL = "journal";
    public static final String PATH_POSTING = "posting";
    public static final String PATH_TRANSACTION = "transaction";

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
        public static final String ACCOUNT_TYPE = "account_type";


        public static Uri buildAccountUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildAccountsByTypeUri(int type) {
            return ContentUris.withAppendedId(CONTENT_URI.buildUpon().appendPath("by").build(), type);
        }
    }


    public static final class JournalEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_JOURNAL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_JOURNAL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_JOURNAL;

        // Table name
        public static final String TABLE_NAME = "journal";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PERIOD = "period";
        public static final String COLUMN_DATE_TIME = "datetime";


        public static Uri buildAccountUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class PostingEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POSTING).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POSTING;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POSTING;

        // Table name
        public static final String TABLE_NAME = "posting";
        public static final String COLUMN_JOURNAL_ID = "journal_id";
        public static final String COLUMN_ACCOUNT_ID = "account_id";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_CREDIT_DEBIT = "credit_debit";
        public static final String COLUMN_DATE_TIME = "datetime";


        public static Uri buildAccountUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class TransactionEntry {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRANSACTION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRANSACTION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRANSACTION;

        // Table name
        public static final String TABLE_NAME = "posting";
        public static final String COLUMN_JOURNAL_ID = "journal_id";
        public static final String COLUMN_ACCOUNT_ID = "account_id";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_CREDIT_DEBIT = "credit_debit";
        public static final String COLUMN_DATE_TIME = "datetime";
        public static final String TRANSACTION_TYPE = "transaction_type";


        public static Uri buildAccountUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
