package com.sanath.moneytracker.ui.activities;

import android.database.DatabaseUtils;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sanath.moneytracker.R;
import com.sanath.moneytracker.adapters.AccountsSpinnerAdapter;
import com.sanath.moneytracker.data.DataContract;
import com.sanath.moneytracker.data.DataContract.AccountEntry;
import com.sanath.moneytracker.data.DataContract.TransactionTypes;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by sanathnandasiri on 2/19/17.
 */

public class AddTransactionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

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

    private Unbinder unbinder;

    private int transactionType = TransactionTypes.INCOME;
    private boolean isEdit = false;

    private AccountsSpinnerAdapter sourceAccountAdapter;
    private AccountsSpinnerAdapter destinationAccountAdapter;
    private int selectedSourceAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        unbinder = ButterKnife.bind(this);
        transactionType = getIntent().getIntExtra(KEY_TRANSACTION_TYPE, TransactionTypes.INCOME);
        isEdit = getIntent().getBooleanExtra(KEY_IS_EDIT, false);
        setActivityTitle();

        sourceAccountAdapter = new AccountsSpinnerAdapter(this, null, true);
        spinnerSourceAccount.setAdapter(sourceAccountAdapter);
        spinnerSourceAccount.setOnItemSelectedListener(sourceAccountSelectedListener);

        destinationAccountAdapter = new AccountsSpinnerAdapter(this, null, true);
        spinnerDestinationAccount.setAdapter(destinationAccountAdapter);
        // spinnerDestinationAccount.setOnItemSelectedListener(this);

        initLoaders();

        setLabels();
    }

    private void setLabels() {
        if (transactionType == TransactionTypes.TRANSFER) {
            textViewSourceAccount.setText(R.string.account);
            textViewDestinationAccount.setText(R.string.account);
            imageViewDownArrow.setVisibility(View.VISIBLE);
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveTransaction() {
        //save transactions here
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
                //expense category data loading
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
            destinationAccountAdapter.swapCursor(data);
            DatabaseUtils.dumpCursor(data);
        } else {
            sourceAccountAdapter.swapCursor(data);
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
}
