package com.sanath.moneytracker.ui.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionMenu;
import com.sanath.moneytracker.R;
import com.sanath.moneytracker.adapters.AccountsAdapter;
import com.sanath.moneytracker.common.ItemClickListener;
import com.sanath.moneytracker.data.DataContract.AccountEntry;
import com.sanath.moneytracker.data.DataContract.AccountTypes;
import com.sanath.moneytracker.ui.activities.AddAccountActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ItemClickListener<Uri> {
    private static final String TAG = AccountsFragment.class.getSimpleName();
    private static final int REQUEST_CODE_ADD_ACCOUNT = 0x000001;
    private static final int ACCOUNT_LOADER = 0x000002;

    private Unbinder unbinder;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.menuActionAdd)
    FloatingActionMenu floatingActionMenu;

    private AccountsAdapter accountsAdapter;


    public static AccountsFragment newInstance() {
        return new AccountsFragment();
    }

    public AccountsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);
        unbinder = ButterKnife.bind(this, view);
        floatingActionMenu.setOnMenuButtonClickListener(fabClickListener);

        accountsAdapter = new AccountsAdapter(getActivity(), null, this);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(accountsAdapter);

        return view;
    }


    View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivityForResult(new Intent(getActivity(), AddAccountActivity.class), REQUEST_CODE_ADD_ACCOUNT);
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(ACCOUNT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), AccountEntry.CONTENT_URI, null,
                AccountEntry.COLUMN_TYPE + " =?",
                new String[]{String.valueOf(AccountTypes.TRANSFER)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        accountsAdapter.swapCursor(data);
        DatabaseUtils.dumpCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        accountsAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_EDIT, uri, getActivity(), AddAccountActivity.class);
        startActivity(intent);
    }
}
