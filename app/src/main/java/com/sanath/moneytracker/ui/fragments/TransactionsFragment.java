package com.sanath.moneytracker.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.sanath.moneytracker.data.DataContract.TransactionTypes;
import com.sanath.moneytracker.ui.activities.AddTransactionActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionsFragment extends Fragment implements View.OnClickListener {
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
}
