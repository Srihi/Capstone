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

import com.sanath.moneytracker.R;
import com.sanath.moneytracker.adapters.CategoriesAdapter;
import com.sanath.moneytracker.common.ItemClickListener;
import com.sanath.moneytracker.data.DataContract;
import com.sanath.moneytracker.ui.activities.AddCategoryActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryChildFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ItemClickListener<Uri> {

    private static final String KEY_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    private static final int CATEGORY_LOADER = 0X000003;

    private int accountType = DataContract.AccountTypes.EXPENSES;

    private Unbinder unbinder;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private CategoriesAdapter categoriesAdapter;

    public static CategoryChildFragment newInstance(int accountType) {
        CategoryChildFragment instance = new CategoryChildFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_ACCOUNT_TYPE, accountType);
        instance.setArguments(args);
        return instance;
    }

    public CategoryChildFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_child, container, false);
        unbinder = ButterKnife.bind(this, view);

        accountType = getArguments().getInt(KEY_ACCOUNT_TYPE, DataContract.AccountTypes.EXPENSES);

        categoriesAdapter = new CategoriesAdapter(getActivity(), null, this);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(categoriesAdapter);

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(CATEGORY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), DataContract.AccountEntry.CONTENT_URI, null,
                DataContract.AccountEntry.COLUMN_TYPE + " =?",
                new String[]{String.valueOf(accountType)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        categoriesAdapter.swapCursor(data);
        DatabaseUtils.dumpCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        categoriesAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(Uri uri) {
        startActivity(new Intent(Intent.ACTION_EDIT, uri, getActivity(), AddCategoryActivity.class));
    }
}
