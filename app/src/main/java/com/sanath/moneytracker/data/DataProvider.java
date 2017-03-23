package com.sanath.moneytracker.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.sanath.moneytracker.common.Utils;

import static com.sanath.moneytracker.data.DataContract.*;

public class DataProvider extends ContentProvider {
    public static final String TAG = DataProvider.class.getSimpleName();

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static final int ACCOUNT = 300;
    private static final int ACCOUNTS = 301;
    private static final int ACCOUNTS_BY_TYPE = 302;

    private static final int JOURNALS = 400;
    private static final int JOURNAL = 401;

    private static final int POSTINGS = 500;
    private static final int POSTING = 501;

    private static final int TRANSACTIONS = 600;

    private DataHelper dataHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, PATH_ACCOUNT, ACCOUNTS);
        matcher.addURI(authority, PATH_ACCOUNT + "/*", ACCOUNT);
        matcher.addURI(authority, PATH_ACCOUNT + "/by/*", ACCOUNTS_BY_TYPE);

        matcher.addURI(authority, PATH_JOURNAL, JOURNALS);
        matcher.addURI(authority, PATH_JOURNAL + "/*", JOURNAL);

        matcher.addURI(authority, PATH_POSTING, POSTINGS);
        matcher.addURI(authority, PATH_POSTING + "/*", POSTING);

        matcher.addURI(authority, PATH_TRANSACTION, TRANSACTIONS);
        return matcher;
    }

    public DataProvider() {
    }

    @Override
    public boolean onCreate() {
        dataHelper = new DataHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case ACCOUNT:
                return AccountEntry.CONTENT_ITEM_TYPE;
            case ACCOUNTS:
                return AccountEntry.CONTENT_TYPE;
            case JOURNAL:
                return JournalEntry.CONTENT_ITEM_TYPE;
            case JOURNALS:
                return JournalEntry.CONTENT_TYPE;
            case POSTING:
                return PostingEntry.CONTENT_ITEM_TYPE;
            case POSTINGS:
                return PostingEntry.CONTENT_TYPE;
            case TRANSACTIONS:
                return TransactionEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri : " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase database = dataHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case ACCOUNT: {
                cursor = database.query(AccountEntry.TABLE_NAME, projection, AccountEntry._ID + "=?", new String[]{uri.getLastPathSegment()}, null, null, sortOrder);
            }
            break;
            case ACCOUNTS: {
                cursor = database.query(AccountEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            }
            break;
            case JOURNAL: {
                cursor = database.query(JournalEntry.TABLE_NAME, projection, JournalEntry._ID + "=?", new String[]{uri.getLastPathSegment()}, null, null, sortOrder);
            }
            break;
            case JOURNALS: {
                cursor = database.query(JournalEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            }
            break;
            case POSTINGS: {
                cursor = database.query(PostingEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            }
            break;
            case TRANSACTIONS: {
                SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
                queryBuilder.setTables("posting " +
                        "inner join " +
                        "journal" +
                        " on " +
                        "journal._id" +
                        " == " +
                        "posting.journal_id" +
                        " inner join " +
                        "account on " +
                        "account._id" +
                        " == " +
                        "posting.account_id" +
                        "");
                cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
            }
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
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
            case JOURNALS: {
                long id = database.insert(JournalEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = JournalEntry.buildAccountUri(id);
                } else {
                    throw new SQLException("Failed to insert new row into uri : " + uri);
                }
            }
            break;
            case POSTINGS: {
                long id = database.insert(PostingEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = PostingEntry.buildAccountUri(id);
                } else {
                    throw new SQLException("Failed to insert new row into uri : " + uri);
                }
            }
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        getContext().getContentResolver().notifyChange(TransactionEntry.CONTENT_URI, null);
        return returnUri;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase database = dataHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int rows;
        switch (match) {
            case ACCOUNT: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rows = database.update(AccountEntry.TABLE_NAME,
                            values,
                            AccountEntry._ID + "=" + id,
                            null);
                } else {
                    rows = database.update(AccountEntry.TABLE_NAME,
                            values,
                            AccountEntry._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
            }
            break;
            case ACCOUNTS: {
                rows = database.update(AccountEntry.TABLE_NAME, values, selection, selectionArgs);
            }
            break;
            case POSTING: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rows = database.update(PostingEntry.TABLE_NAME,
                            values,
                            PostingEntry._ID + "=" + id,
                            null);
                } else {
                    rows = database.update(PostingEntry.TABLE_NAME,
                            values,
                            PostingEntry._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
            }
            break;
            case POSTINGS: {
                rows = database.update(PostingEntry.TABLE_NAME, values, selection, selectionArgs);
            }
            break;
            case JOURNAL: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rows = database.update(JournalEntry.TABLE_NAME,
                            values,
                            JournalEntry._ID + "=" + id,
                            null);
                } else {
                    rows = database.update(JournalEntry.TABLE_NAME,
                            values,
                            JournalEntry._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
            }
            break;
            case JOURNALS: {
                rows = database.update(PostingEntry.TABLE_NAME, values, selection, selectionArgs);
            }
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        getContext().getContentResolver().notifyChange(TransactionEntry.CONTENT_URI, null);
        return rows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase database = dataHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int rows;
        switch (match) {
            case ACCOUNT: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rows = database.delete(AccountEntry.TABLE_NAME,
                            AccountEntry._ID + "=" + id,
                            null);
                } else {
                    rows = database.delete(AccountEntry.TABLE_NAME,
                            AccountEntry._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
            }
            break;
            case ACCOUNTS: {
                rows = database.delete(AccountEntry.TABLE_NAME, selection, selectionArgs);
            }
            break;
            case POSTING: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rows = database.delete(PostingEntry.TABLE_NAME,
                            PostingEntry._ID + "=" + id,
                            null);
                } else {
                    rows = database.delete(PostingEntry.TABLE_NAME,
                            PostingEntry._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
            }
            break;
            case POSTINGS: {
                rows = database.delete(PostingEntry.TABLE_NAME, selection, selectionArgs);
            }
            break;
            case JOURNAL: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rows = database.delete(JournalEntry.TABLE_NAME,
                            JournalEntry._ID + "=" + id,
                            null);
                } else {
                    rows = database.delete(JournalEntry.TABLE_NAME,
                            JournalEntry._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
            }
            break;
            case JOURNALS: {
                rows = database.delete(AccountEntry.TABLE_NAME, selection, selectionArgs);
            }
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        getContext().getContentResolver().notifyChange(TransactionEntry.CONTENT_URI, null);
        return rows;
    }
}
