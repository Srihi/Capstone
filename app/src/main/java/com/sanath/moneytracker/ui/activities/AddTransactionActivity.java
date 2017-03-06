package com.sanath.moneytracker.ui.activities;

import android.app.DatePickerDialog;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
import com.sanath.moneytracker.R;
import com.sanath.moneytracker.adapters.AccountsSpinnerAdapter;
import com.sanath.moneytracker.data.DataContract;
import com.sanath.moneytracker.data.DataContract.AccountEntry;
import com.sanath.moneytracker.data.DataContract.JournalEntry;
import com.sanath.moneytracker.data.DataContract.PostingEntry;
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

    private Date transactionDate;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat sdfPeriod = new SimpleDateFormat("MM/yyyy", Locale.getDefault());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        unbinder = ButterKnife.bind(this);
        transactionType = getIntent().getIntExtra(KEY_TRANSACTION_TYPE, TransactionTypes.INCOME);
        isEdit = getIntent().getBooleanExtra(KEY_IS_EDIT, false);
        setActivityTitle();

        setupUI();

        initLoaders();

        setLabels();

        loadAdMobBannerAd();
    }

    private void loadAdMobBannerAd() {
        final AdRequest adRequest = new AdRequest.Builder().build();
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                adView.setVisibility(View.GONE);
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                adView.setVisibility(View.GONE);
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

        ContentValues journalValues = new ContentValues();
        journalValues.put(JournalEntry.COLUMN_TYPE, transactionType);
        journalValues.put(JournalEntry.COLUMN_PERIOD, sdfPeriod.format(transactionDateTime));
        journalValues.put(JournalEntry.COLUMN_DESCRIPTION, editTextDescription.getText().toString());
        journalValues.put(JournalEntry.COLUMN_DATE_TIME, transactionDateTime);

        ContentValues postingValuesSource = new ContentValues();
        postingValuesSource.put(PostingEntry.COLUMN_ACCOUNT_ID, sourceAccountId);
        postingValuesSource.put(PostingEntry.COLUMN_AMOUNT, -amount);
        postingValuesSource.put(PostingEntry.COLUMN_DATE_TIME, transactionDateTime);
        postingValuesSource.put(DataContract.PostingEntry.COLUMN_CREDIT_DEBIT, DataContract.CreditType.CREDIT);

        ContentValues postingValuesDestination = new ContentValues();
        postingValuesDestination.put(PostingEntry.COLUMN_ACCOUNT_ID, destinationAccountId);
        postingValuesDestination.put(PostingEntry.COLUMN_AMOUNT, amount);
        postingValuesDestination.put(PostingEntry.COLUMN_DATE_TIME, transactionDateTime);
        postingValuesDestination.put(DataContract.PostingEntry.COLUMN_CREDIT_DEBIT, DataContract.CreditType.DEBIT);

        ArrayList<ContentProviderOperation> operations = new
                ArrayList<>();
        operations.add(ContentProviderOperation.newInsert(JournalEntry.CONTENT_URI).withValues(journalValues).build());

        operations.add(ContentProviderOperation.newInsert(PostingEntry.CONTENT_URI).
                withValues(postingValuesSource).withValueBackReference(PostingEntry.COLUMN_JOURNAL_ID, 0).build());

        operations.add(ContentProviderOperation.newInsert(PostingEntry.CONTENT_URI).
                withValues(postingValuesDestination).withValueBackReference(PostingEntry.COLUMN_JOURNAL_ID, 0).build());

        try {
            getContentResolver().applyBatch(DataContract.CONTENT_AUTHORITY, operations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(TAG, e.getMessage(), e);
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
        if (transactionType == TransactionTypes.INCOME) {
            setTitle(getString(R.string.activity_title_add_income));
        } else if (transactionType == TransactionTypes.EXPENSES) {
            setTitle(getString(R.string.activity_title_add_expense));
        } else if (transactionType == TransactionTypes.TRANSFER) {
            setTitle(getString(R.string.activity_title_add_transfer));
        }
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
