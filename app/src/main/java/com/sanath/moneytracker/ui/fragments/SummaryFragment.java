package com.sanath.moneytracker.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sanath.moneytracker.R;
import com.sanath.moneytracker.adapters.Summary;
import com.sanath.moneytracker.adapters.SummaryAdapter;
import com.sanath.moneytracker.common.SummaryLoader;
import com.sanath.moneytracker.common.Utils;
import com.sanath.moneytracker.data.DataContract;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SummaryFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Summary>> {
    private static final String TAG = TransactionsFragment.class.getSimpleName();

    private static final int ACCOUNT_SUMMARY_LOADER = 0x000002;
    private static final int EXPENSES_SUMMARY_LOADER = 0x000003;


    public static SummaryFragment fragment;
    private Unbinder unbinder;

    @BindView(R.id.recyclerViewAccountSummary)
    RecyclerView recyclerViewAccountSummary;
    @BindView(R.id.recyclerViewExpenseSummary)
    RecyclerView recyclerViewExpenseSummary;
    @BindView(R.id.textViewBalance)
    TextView textViewBalance;
    @BindView(R.id.textViewTotal)
    TextView textViewTotal;

    private SummaryAdapter summaryAdapterAccounts;
    private ArrayList<Summary> accountsSummaries = new ArrayList<>();

    private SummaryAdapter summaryAdapterExpenses;
    private ArrayList<Summary> expensesSummaries = new ArrayList<>();


    public static SummaryFragment newInstance() {
        fragment = new SummaryFragment();
        return fragment;
    }


    public SummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getLoaderManager().initLoader(ACCOUNT_SUMMARY_LOADER, null, this);
        getLoaderManager().initLoader(EXPENSES_SUMMARY_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        unbinder = ButterKnife.bind(this, view);

        summaryAdapterAccounts = new SummaryAdapter(getActivity(), accountsSummaries);
        recyclerViewAccountSummary.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerViewAccountSummary.setAdapter(summaryAdapterAccounts);

        summaryAdapterExpenses = new SummaryAdapter(getActivity(), expensesSummaries);
        recyclerViewExpenseSummary.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerViewExpenseSummary.setAdapter(summaryAdapterExpenses);
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
    public Loader<ArrayList<Summary>> onCreateLoader(int id, Bundle args) {
        if (id == ACCOUNT_SUMMARY_LOADER) {
            return new SummaryLoader(getActivity(), DataContract.AccountTypes.TRANSFER);
        } else {
            return new SummaryLoader(getActivity(), DataContract.AccountTypes.EXPENSES);
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Summary>> loader, ArrayList<Summary> data) {
        if (loader.getId() == ACCOUNT_SUMMARY_LOADER) {
            accountsSummaries.clear();
            accountsSummaries.addAll(data);
            summaryAdapterAccounts.notifyDataSetChanged();
            double balance = 0.0;
            for (Summary summary : data) {
                balance = balance + summary.getValue();
            }
            textViewBalance.setText(Utils.getAmountWithCurrency(balance));
        } else {
            expensesSummaries.clear();
            expensesSummaries.addAll(data);
            summaryAdapterExpenses.notifyDataSetChanged();
            double balance = 0.0;
            for (Summary summary : data) {
                balance = balance + summary.getValue();
            }
            textViewTotal.setText(Utils.getAmountWithCurrency(balance));
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Summary>> loader) {
        if (loader.getId() == ACCOUNT_SUMMARY_LOADER) {
            accountsSummaries.clear();
            summaryAdapterAccounts.notifyDataSetChanged();
            double balance = 0.0;
            if (textViewBalance != null) {
                textViewBalance.setText(Utils.getAmountWithCurrency(balance));
            }
        } else {
            expensesSummaries.clear();
            summaryAdapterExpenses.notifyDataSetChanged();
            double balance = 0.0;
            if (textViewTotal != null) {
                textViewTotal.setText(Utils.getAmountWithCurrency(balance));
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getLoaderManager().destroyLoader(ACCOUNT_SUMMARY_LOADER);
        getLoaderManager().destroyLoader(EXPENSES_SUMMARY_LOADER);
    }
}
