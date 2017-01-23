package com.sanath.moneytracker.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionMenu;
import com.sanath.moneytracker.R;
import com.sanath.moneytracker.ui.activities.AddAccountActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountsFragment extends Fragment {
    private static final String TAG = AccountsFragment.class.getSimpleName();
    private static final int REQUEST_CODE_ADD_ACCOUNT = 0x000001;

    public static AccountsFragment fragment;
    private Unbinder unbinder;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.menuActionAdd)
    FloatingActionMenu floatingActionMenu;


    public static AccountsFragment newInstance() {
        fragment = new AccountsFragment();
        return fragment;
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

        return view;
    }


    View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivityForResult(new Intent(getActivity(), AddAccountActivity.class), REQUEST_CODE_ADD_ACCOUNT);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
