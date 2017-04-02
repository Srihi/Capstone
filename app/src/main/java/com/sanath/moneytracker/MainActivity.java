package com.sanath.moneytracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.sanath.moneytracker.common.Constant;
import com.sanath.moneytracker.data.DataContract;
import com.sanath.moneytracker.ui.activities.AddTransactionActivity;
import com.sanath.moneytracker.ui.fragments.AccountsFragment;
import com.sanath.moneytracker.ui.fragments.CategoriesFragment;
import com.sanath.moneytracker.ui.fragments.SettingFragment;
import com.sanath.moneytracker.ui.fragments.SummaryFragment;
import com.sanath.moneytracker.ui.fragments.TransactionsFragment;

import java.util.concurrent.ThreadLocalRandom;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ActionBarDrawerToggle toggle;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manageAppShortCutsLinks();
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            //load initial fragment and select menu item
            replaceFragment(TransactionsFragment.newInstance());
            navigationView.getMenu().getItem(0).setChecked(true);
        }

    }

    private void manageAppShortCutsLinks() {
        String action = getIntent().getAction();
        if (action != null) {
            if (action.equals(Constant.ACTION_ADD_EXPENSE)) {
                startActivity(
                        new Intent(this, AddTransactionActivity.class)
                                .putExtra(AddTransactionActivity.KEY_TRANSACTION_TYPE,
                                        DataContract.TransactionTypes.EXPENSES));
            } else if (action.equals(Constant.ACTION_ADD_INCOME)) {
                startActivity(
                        new Intent(this, AddTransactionActivity.class)
                                .putExtra(AddTransactionActivity.KEY_TRANSACTION_TYPE,
                                        DataContract.TransactionTypes.INCOME));
            } else if (action.equals(Constant.ACTION_ADD_TRANSFER)) {
                startActivity(
                        new Intent(this, AddTransactionActivity.class)
                                .putExtra(AddTransactionActivity.KEY_TRANSACTION_TYPE,
                                        DataContract.TransactionTypes.TRANSFER));
            }
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
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Fragment fragment = null;
                            switch (id) {
                                case R.id.nav_transactions:
                                    fragment = TransactionsFragment.newInstance();
                                    setTitle(R.string.title_nav_transactions);
                                    break;
                                case R.id.nav_summary:
                                    fragment = SummaryFragment.newInstance();
                                    setTitle(R.string.title_nav_summary);
                                    break;
                                case R.id.nav_accounts:
                                    fragment = AccountsFragment.newInstance();
                                    setTitle(R.string.title_nav_accounts);
                                    break;
                                case R.id.nav_categories:
                                    fragment = CategoriesFragment.newInstance();
                                    setTitle(R.string.title_nav_categories);
                                    break;
                                case R.id.nav_settings:
                                    fragment = SettingFragment.newInstance();
                                    setTitle(R.string.title_nav_settings);
                                    break;
                            }
                            replaceFragment(fragment);
                        } catch (Exception e) {
                            Log.e(TAG, "Fragment can not be instantiated ", e);
                        }
                    }
                });

            }
        }).start();


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setTitle(int titleId) {
        toolbar.setTitle(getString(titleId));
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
    }
}
