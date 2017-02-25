package com.sanath.moneytracker.ui.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.sanath.moneytracker.R;
import com.sanath.moneytracker.adapters.TransactionAdapter;
import com.sanath.moneytracker.data.DataContract;
import com.sanath.moneytracker.data.DataContract.AccountEntry;
import com.sanath.moneytracker.data.DataContract.JournalEntry;
import com.sanath.moneytracker.data.DataContract.PostingEntry;
import com.sanath.moneytracker.data.DataContract.TransactionEntry;
import com.sanath.moneytracker.data.DataContract.TransactionTypes;
import com.sanath.moneytracker.ui.activities.AddTransactionActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionsFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = TransactionsFragment.class.getSimpleName();

    private static final int REQUEST_CODE_ADD_INCOME = 0x000001;
    private static final int TRANSACTION_LOADER = 0x000003;

    public static TransactionsFragment fragment;
    private Unbinder unbinder;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.floatingActionMenu)
    FloatingActionMenu floatingActionMenu;
    @BindView(R.id.menuItemIncome)
    FloatingActionButton menuItemIncome;
    @BindView(R.id.menuItemTransfer)
    FloatingActionButton menuItemTransfer;
    @BindView(R.id.menuItemExpenses)
    FloatingActionButton menuItemExpenses;

    private TransactionAdapter transactionAdapter;

    private SimpleDateFormat sdfPeriod = new SimpleDateFormat("MM/yyyy", Locale.getDefault());

    public static TransactionsFragment newInstance() {
        fragment = new TransactionsFragment();
        return fragment;
    }

    public TransactionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        unbinder = ButterKnife.bind(this, view);

        menuItemExpenses.setOnClickListener(this);
        menuItemIncome.setOnClickListener(this);
        menuItemTransfer.setOnClickListener(this);

        transactionAdapter = new TransactionAdapter(getActivity(), null);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(transactionAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_transactions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.menuItemIncome) {
            addIncome();
        } else if (v.getId() == R.id.menuItemExpenses) {
            addExpense();
        } else if (v.getId() == R.id.menuItemTransfer) {
            addTransfer();
        }
        floatingActionMenu.close(true);
    }

    private void addTransfer() {
        Intent intentIncome = new Intent(getActivity(), AddTransactionActivity.class);
        intentIncome.putExtra(AddTransactionActivity.KEY_TRANSACTION_TYPE, TransactionTypes.TRANSFER);
        startActivityForResult(intentIncome, REQUEST_CODE_ADD_INCOME);
    }

    private void addExpense() {
        Intent intentIncome = new Intent(getActivity(), AddTransactionActivity.class);
        intentIncome.putExtra(AddTransactionActivity.KEY_TRANSACTION_TYPE, TransactionTypes.EXPENSES);
        startActivityForResult(intentIncome, REQUEST_CODE_ADD_INCOME);
    }

    private void addIncome() {
        Intent intentIncome = new Intent(getActivity(), AddTransactionActivity.class);
        intentIncome.putExtra(AddTransactionActivity.KEY_TRANSACTION_TYPE, TransactionTypes.INCOME);
        startActivityForResult(intentIncome, REQUEST_CODE_ADD_INCOME);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(TRANSACTION_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), TransactionEntry.CONTENT_URI, new String[]{
                PostingEntry.TABLE_NAME + "." + PostingEntry._ID,
                PostingEntry.TABLE_NAME + "." + PostingEntry.COLUMN_ACCOUNT_ID,
                PostingEntry.TABLE_NAME + "." + PostingEntry.COLUMN_JOURNAL_ID,
                PostingEntry.TABLE_NAME + "." + PostingEntry.COLUMN_AMOUNT,
                PostingEntry.TABLE_NAME + "." + PostingEntry.COLUMN_CREDIT_DEBIT,
                PostingEntry.TABLE_NAME + "." + PostingEntry.COLUMN_DATE_TIME,
                JournalEntry.TABLE_NAME + "." + JournalEntry.COLUMN_DESCRIPTION,
                JournalEntry.TABLE_NAME + "." + JournalEntry.COLUMN_PERIOD,
                JournalEntry.TABLE_NAME + "." + JournalEntry.COLUMN_TYPE + " as " + TransactionEntry.TRANSACTION_TYPE,
                AccountEntry.TABLE_NAME + "." + AccountEntry.COLUMN_NAME,
                AccountEntry.TABLE_NAME + "." + AccountEntry.COLUMN_TYPE + " as " + AccountEntry.ACCOUNT_TYPE,
                AccountEntry.TABLE_NAME + "." + AccountEntry.COLUMN_COLOR,
                AccountEntry.TABLE_NAME + "." + AccountEntry.COLUMN_ICON,
        },
                "(((account.type == 1 and journal.type == 2 and posting.credit_debit == 0) " +
                        "or (account.type == 2 and journal.type == 3 and posting.credit_debit == 1) " +
                        "or (account.type == 0 and journal.type == 1 and posting.credit_debit == 1)) " +
                        "and period == ?)",
                new String[]{sdfPeriod.format(new Date())},
                PostingEntry.TABLE_NAME + "." + PostingEntry.COLUMN_DATE_TIME + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        transactionAdapter.swapCursor(data);
        DatabaseUtils.dumpCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        transactionAdapter.swapCursor(null);
    }
}
