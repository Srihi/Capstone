package com.sanath.moneytracker.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.sanath.moneytracker.data.DataContract.*;

public class DataProvider extends ContentProvider {
    public static final String TAG = DataProvider.class.getSimpleName();

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static final int ACCOUNT = 300;
    private static final int ACCOUNTS = 301;
    private static final int ACCOUNTS_BY_TYPE = 302;

    private DataHelper dataHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;
        matcher.addURI(authority, PATH_ACCOUNT, ACCOUNTS);
        matcher.addURI(authority, PATH_ACCOUNT + "/*", ACCOUNT);
        matcher.addURI(authority, PATH_ACCOUNT + "/by/*", ACCOUNTS_BY_TYPE);
        return matcher;
    }

    public DataProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case ACCOUNT:
                return AccountEntry.CONTENT_ITEM_TYPE;
            case ACCOUNTS:
                return AccountEntry.CONTENT_TYPE;
            case ACCOUNTS_BY_TYPE:
                return AccountEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri : " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "insert() called with: uri = [" + uri + "], values = [" + values + "]");
        final SQLiteDatabase database = dataHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case ACCOUNTS: {
                long id = database.insert(AccountEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = AccountEntry.buildAccountUri(id);
                } else {
                    throw new SQLException("Failed to insert new row into uri : " + uri);
                }
            }
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public boolean onCreate() {
        dataHelper = new DataHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
