package com.sanath.moneytracker.ui.fragments;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.ArrayMap;

import com.sanath.moneytracker.R;
import com.sanath.moneytracker.common.Constant;
import com.sanath.moneytracker.data.DataContract;
import com.sanath.moneytracker.data.DataContract.AccountEntry;

import java.util.ArrayList;

/**
 * Created by sanathnandasiri on 3/18/17.
 */

public class SettingFragment extends PreferenceFragmentCompat {


    private ArrayList<CharSequence> expenseAccountEntries = new ArrayList<>();
    private ArrayList<CharSequence> expenseAccountEntryValues = new ArrayList();

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.app_preferences);

        final ListPreference listPreferenceExpenseAccount = new ListPreference(getActivity());
        final PreferenceCategory preferenceCategoryExpenseAccount = (PreferenceCategory) findPreference(Constant
                .DEFAULT_EXPENSE_ACCOUNT_CATEGORY);


        listPreferenceExpenseAccount.setTitle(getString(R.string.pref_title_default_expense_account));
        listPreferenceExpenseAccount.setSummary(getString(R.string.pref_summary_default_expense_account));
        listPreferenceExpenseAccount.setKey(Constant.DEFAULT_EXPENSE_ACCOUNT);
        listPreferenceExpenseAccount.setPersistent(true);

        new AsyncTask<Void, Void, ArrayMap<Integer, String>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                expenseAccountEntries.clear();
                expenseAccountEntryValues.clear();
            }

            @Override
            protected ArrayMap<Integer, String> doInBackground(Void... params) {
                Cursor cursor = getActivity().getContentResolver().query(AccountEntry.CONTENT_URI,
                        new String[]{AccountEntry._ID, AccountEntry.COLUMN_NAME},
                        AccountEntry.COLUMN_TYPE + " =?",
                        new String[]{String.valueOf(DataContract.AccountTypes.TRANSFER)},
                        null,
                        null);

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        expenseAccountEntryValues.add(String.valueOf(cursor.getInt(0)));
                        expenseAccountEntries.add(cursor.getString(1));
                    } while (cursor.moveToNext());
                    cursor.close();
                }

                return null;
            }

            @Override
            protected void onPostExecute(ArrayMap<Integer, String> integerStringArrayMap) {
                super.onPostExecute(integerStringArrayMap);
                listPreferenceExpenseAccount.setEntries(expenseAccountEntries
                        .toArray(new CharSequence[expenseAccountEntries.size()]));
                listPreferenceExpenseAccount.setEntryValues(expenseAccountEntryValues
                        .toArray(new CharSequence[expenseAccountEntryValues.size()]));

                preferenceCategoryExpenseAccount.addPreference(listPreferenceExpenseAccount);
            }
        }.execute();

    }
}
