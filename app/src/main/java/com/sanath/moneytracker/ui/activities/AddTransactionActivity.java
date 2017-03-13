package com.sanath.moneytracker.ui.activities;

import android.app.DatePickerDialog;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sanath.moneytracker.R;
import com.sanath.moneytracker.adapters.AccountsSpinnerAdapter;
import com.sanath.moneytracker.common.Utils;
import com.sanath.moneytracker.data.DataContract;
import com.sanath.moneytracker.data.DataContract.AccountEntry;
import com.sanath.moneytracker.data.DataContract.JournalEntry;
import com.sanath.moneytracker.data.DataContract.PostingEntry;
import com.sanath.moneytracker.data.DataContract.TransactionEntry;
import com.sanath.moneytracker.data.DataContract.TransactionTypes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by sanathnandasiri on 2/19/17.
 */

public class AddTransactionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private static final String TAG = AddTransactionActivity.class.getSimpleName();

    public static final String KEY_TRANSACTION_TYPE = "TRANSACTION_TYPE";
    public static final String KEY_IS_EDIT = "IS_EDIT";

    private static final int SOURCE_ACCOUNT_LOADER = 0x000003;
    private static final int DESTINATION_ACCOUNT_LOADER = 0x000002;


    @BindView(R.id.spinnerSourceAccount)
    Spinner spinnerSourceAccount;
    @BindView(R.id.spinnerDestinationAccount)
    Spinner spinnerDestinationAccount;
    @BindView(R.id.textViewSourceAccount)
    TextView textViewSourceAccount;
    @BindView(R.id.textViewDestinationAccount)
    TextView textViewDestinationAccount;
    @BindView(R.id.imageViewDownArrow)
    ImageView imageViewDownArrow;
    @BindView(R.id.editTextAmount)
    EditText editTextAmount;
    @BindView(R.id.editTextDate)
    EditText editTextDate;
    @BindView(R.id.editTextDescription)
    EditText editTextDescription;
    @BindView(R.id.adView)
    AdView adView;

    private Unbinder unbinder;

    private int transactionType = TransactionTypes.INCOME;
    private boolean isEdit = false;

    private AccountsSpinnerAdapter sourceAccountAdapter;
    private AccountsSpinnerAdapter destinationAccountAdapter;
    private int selectedSourceAccount;
    private int selectedDestinationAccount;

    private Date transactionDate;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat sdfPeriod = new SimpleDateFormat("MM/yyyy", Locale.getDefault());

    private FirebaseAnalytics analytics;

    private double amount = 0.0;

    private boolean isAccountDataLoaded = false;

    private int postingSourceId = -1;
    private int postingDestinationId = -1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        unbinder = ButterKnife.bind(this);
        analytics = FirebaseAnalytics.getInstance(this);

        setupUI();

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri uri = intent.getData();
        if (action != null && action.equals(Intent.ACTION_EDIT) && uri != null) {
            getTransactionDetails(uri);
            isEdit = true;
        } else {
            transactionType = getIntent().getIntExtra(KEY_TRANSACTION_TYPE, TransactionTypes.INCOME);
            isEdit = getIntent().getBooleanExtra(KEY_IS_EDIT, false);
            initLoaders();
        }

        setActivityTitle();
        setLabels();
        loadAdMobBannerAd();
    }

    private void getTransactionDetails(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            transactionType = cursor.getInt(cursor.getColumnIndex(JournalEntry.COLUMN_TYPE));
            editTextDescription.setText(cursor.getString(cursor.getColumnIndex(JournalEntry.COLUMN_DESCRIPTION)));
            transactionDate = new Date(cursor.getLong(cursor.getColumnIndex(JournalEntry.COLUMN_DATE_TIME)));
            setFormattedDate(transactionDate);
            initLoaders();
            cursor.close();
        }
    }

    private void loadAccountData(Uri uri) {
        Cursor cursorSourceTransaction = getContentResolver().query(TransactionEntry.CONTENT_URI,
                Utils.getProjectionForTransaction(),
                JournalEntry.TABLE_NAME + "." + JournalEntry._ID + "=?",
                new String[]{uri.getLastPathSegment()},
                TransactionEntry.COLUMN_CREDIT_DEBIT + " ASC");
        if (cursorSourceTransaction != null && cursorSourceTransaction.moveToFirst()) {
            amount = Math.abs(cursorSourceTransaction.getDouble(cursorSourceTransaction.getColumnIndex(TransactionEntry.COLUMN_AMOUNT)));
            editTextAmount.setText(String.valueOf(amount));
            selectedSourceAccount = cursorSourceTransaction.getInt(
                    cursorSourceTransaction.getColumnIndex(TransactionEntry.COLUMN_ACCOUNT_ID));
            postingSourceId = cursorSourceTransaction.getInt(cursorSourceTransaction.getColumnIndex(TransactionEntry.POSTING_ID));
            if (selectedSourceAccount != -1) {
                spinnerSourceAccount.setSelection(
                        sourceAccountAdapter.getSelectedAccountPosition(selectedSourceAccount));
            }

            if (cursorSourceTransaction.moveToNext()) {
                selectedDestinationAccount = cursorSourceTransaction.getInt(
                        cursorSourceTransaction.getColumnIndex(TransactionEntry.COLUMN_ACCOUNT_ID));
                postingDestinationId = cursorSourceTransaction.getInt(cursorSourceTransaction.getColumnIndex(TransactionEntry.POSTING_ID));

                if (selectedDestinationAccount != -1) {
                    spinnerDestinationAccount.setSelection(
                            destinationAccountAdapter.getSelectedAccountPosition(selectedDestinationAccount));
                }
            }
            cursorSourceTransaction.close();
        }
        isAccountDataLoaded = true;
    }

    private void loadAdMobBannerAd() {
        final AdRequest adRequest = new AdRequest.Builder().build();
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (adView != null) {
                    adView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (adView != null) {
                    adView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (adView != null) {
                    adView.setVisibility(View.GONE);
                }
            }
        });
        adView.loadAd(adRequest);
    }

    private void setupUI() {
        sourceAccountAdapter = new AccountsSpinnerAdapter(this, null, true);
        spinnerSourceAccount.setAdapter(sourceAccountAdapter);
        spinnerSourceAccount.setOnItemSelectedListener(sourceAccountSelectedListener);

        destinationAccountAdapter = new AccountsSpinnerAdapter(this, null, true);
        spinnerDestinationAccount.setAdapter(destinationAccountAdapter);
        transactionDate = new Date();
        editTextDate.setOnClickListener(this);
        setFormattedDate(transactionDate);
    }

    private void setFormattedDate(Date date) {
        editTextDate.setText(sdf.format(date));
    }

    private void saveTransaction() {
        //save transactions here
        double amount = Double.parseDouble(editTextAmount.getText().toString());
        long transactionDateTime = transactionDate.getTime();
        int sourceAccountId = getSelectedAccountId(((Cursor) spinnerSourceAccount.getSelectedItem()));
        int destinationAccountId = getSelectedAccountId(((Cursor) spinnerDestinationAccount.getSelectedItem()));

        ContentValues journalValues = getJournalContentValues(transactionDateTime);

        ContentValues postingValuesSource =
                getPostingSourceContentValues(transactionDateTime,
                        sourceAccountId,
                        -amount,
                        PostingEntry.COLUMN_CREDIT_DEBIT,
                        DataContract.CreditType.CREDIT);

        ContentValues postingValuesDestination =
                getPostingSourceContentValues(transactionDateTime,
                        destinationAccountId,
                        amount,
                        PostingEntry.COLUMN_CREDIT_DEBIT,
                        DataContract.CreditType.DEBIT);

        ArrayList<ContentProviderOperation> operations = null;
        if (isEdit) {
            String journalId = getIntent().getData().getLastPathSegment();
            operations = new
                    ArrayList<>();
            operations.add(ContentProviderOperation.newUpdate(JournalEntry.buildAccountUri(Long.valueOf(journalId)))
                    .withValues(journalValues).build());

            operations.add(ContentProviderOperation.newUpdate(PostingEntry.buildAccountUri(postingSourceId)).
                    withValues(postingValuesSource).build());

            operations.add(ContentProviderOperation.newUpdate(PostingEntry.buildAccountUri(postingDestinationId)).
                    withValues(postingValuesDestination).build());
        } else {
            operations = new
                    ArrayList<>();
            operations.add(ContentProviderOperation.newInsert(JournalEntry.CONTENT_URI).withValues(journalValues).build());

            operations.add(ContentProviderOperation.newInsert(PostingEntry.CONTENT_URI).
                    withValues(postingValuesSource).withValueBackReference(PostingEntry.COLUMN_JOURNAL_ID, 0).build());

            operations.add(ContentProviderOperation.newInsert(PostingEntry.CONTENT_URI).
                    withValues(postingValuesDestination).withValueBackReference(PostingEntry.COLUMN_JOURNAL_ID, 0).build());
        }

        logTransactionToAnalytics();

        try {
            getContentResolver().applyBatch(DataContract.CONTENT_AUTHORITY, operations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @NonNull
    private ContentValues getPostingSourceContentValues(long transactionDateTime, int sourceAccountId, double value, String columnCreditDebit, int credit) {
        ContentValues postingValuesSource = new ContentValues();
        postingValuesSource.put(PostingEntry.COLUMN_ACCOUNT_ID, sourceAccountId);
        postingValuesSource.put(PostingEntry.COLUMN_AMOUNT, value);
        postingValuesSource.put(PostingEntry.COLUMN_DATE_TIME, transactionDateTime);
        postingValuesSource.put(columnCreditDebit, credit);
        return postingValuesSource;
    }

    @NonNull
    private ContentValues getJournalContentValues(long transactionDateTime) {
        ContentValues journalValues = new ContentValues();
        journalValues.put(JournalEntry.COLUMN_TYPE, transactionType);
        journalValues.put(JournalEntry.COLUMN_PERIOD, sdfPeriod.format(transactionDateTime));
        journalValues.put(JournalEntry.COLUMN_DESCRIPTION, editTextDescription.getText().toString());
        journalValues.put(JournalEntry.COLUMN_DATE_TIME, transactionDateTime);
        return journalValues;
    }

    private void logTransactionToAnalytics() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.analytic_tag_transaction));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getName());
        analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }

    private String getName() {
        switch (transactionType) {
            case TransactionTypes.EXPENSES:
                return getString(R.string.analytic_tag_expenses);
            case TransactionTypes.INCOME:
                return getString(R.string.analytic_tag_income);
            case TransactionTypes.TRANSFER:
                return getString(R.string.analytic_tag_transfer);
            default:
                return getString(R.string.analytic_tag_expenses);
        }
    }

    private int getSelectedAccountId(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(AccountEntry._ID));
    }

    private void showDatePicker() {
        final Calendar date = Calendar.getInstance(Locale.getDefault());
        date.setTime(transactionDate);
        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                date.set(year, month, dayOfMonth);
                setFormattedDate(date.getTime());
                transactionDate = date.getTime();
            }
        }, date.get(Calendar.YEAR), date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void setLabels() {
        if (transactionType == TransactionTypes.TRANSFER) {
            textViewSourceAccount.setText(R.string.account);
            textViewDestinationAccount.setText(R.string.account);
            //imageViewDownArrow.setVisibility(View.VISIBLE);
        } else if (transactionType == TransactionTypes.INCOME) {
            textViewSourceAccount.setText(R.string.source);
            textViewDestinationAccount.setText(R.string.account);
        }
    }

    private void initLoaders() {
        if (transactionType != TransactionTypes.TRANSFER) {
            getSupportLoaderManager().initLoader(SOURCE_ACCOUNT_LOADER, null, this);
            getSupportLoaderManager().initLoader(DESTINATION_ACCOUNT_LOADER, null, this);
        } else {
            getSupportLoaderManager().initLoader(SOURCE_ACCOUNT_LOADER, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.memu_add_transaction, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!isEdit) {
            menu.removeItem(R.id.action_delete);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            saveTransaction();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setActivityTitle() {
        int id = R.string.activity_title_add_income;
        if (transactionType == TransactionTypes.INCOME) {
            id = isEdit ? R.string.activity_title_edit_income : R.string.activity_title_add_income;
        } else if (transactionType == TransactionTypes.EXPENSES) {
            id = isEdit ? R.string.activity_title_edit_expense : R.string.activity_title_add_expense;
        } else if (transactionType == TransactionTypes.TRANSFER) {
            id = isEdit ? R.string.activity_title_edit_transfer : R.string.activity_title_add_transfer;
        }
        setTitle(id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == SOURCE_ACCOUNT_LOADER) {
            //account data loading
            return getCursorLoader(DataContract.AccountTypes.TRANSFER);
        } else {
            if (transactionType == TransactionTypes.INCOME) {
                //income category data loading
                return getCursorLoader(DataContract.AccountTypes.INCOME);

            } else if (transactionType == TransactionTypes.EXPENSES) {
                //expense category data loading
                return getCursorLoader(DataContract.AccountTypes.EXPENSES);

            } else {
                //for transfer money we remove sources account from destination account list
                return new CursorLoader(this, AccountEntry.CONTENT_URI, null,
                        AccountEntry.COLUMN_TYPE + " =? and " + AccountEntry._ID + "!= ?",
                        new String[]{String.valueOf(DataContract.AccountTypes.TRANSFER), String.valueOf(selectedSourceAccount)},
                        null);
            }
        }
    }

    @NonNull
    private CursorLoader getCursorLoader(int accountType) {
        return new CursorLoader(this, AccountEntry.CONTENT_URI, null,
                AccountEntry.COLUMN_TYPE + " =?",
                new String[]{String.valueOf(accountType)},
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == DESTINATION_ACCOUNT_LOADER) {
            if (transactionType == TransactionTypes.INCOME) {
                sourceAccountAdapter.swapCursor(data);
            } else {
                destinationAccountAdapter.swapCursor(data);
            }
            DatabaseUtils.dumpCursor(data);
        } else {
            if (transactionType == TransactionTypes.INCOME) {
                destinationAccountAdapter.swapCursor(data);
            } else {
                sourceAccountAdapter.swapCursor(data);
            }
            DatabaseUtils.dumpCursor(data);
        }
        if (isEdit && !isAccountDataLoaded) {
            loadAccountData(getIntent().getData());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == DESTINATION_ACCOUNT_LOADER) {
            destinationAccountAdapter.swapCursor(null);
        } else {
            sourceAccountAdapter.swapCursor(null);
        }
    }

    AdapterView.OnItemSelectedListener sourceAccountSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (transactionType == TransactionTypes.TRANSFER) {
                Cursor cursor = (Cursor) sourceAccountAdapter.getItem(position);
                //always remove selected source account from destination account list
                selectedSourceAccount = cursor.getInt(cursor.getColumnIndex(AccountEntry._ID));
                getSupportLoaderManager().restartLoader(DESTINATION_ACCOUNT_LOADER, null, AddTransactionActivity.this);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.editTextDate) {
            showDatePicker();
        }
    }
}
