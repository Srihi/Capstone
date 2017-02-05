package com.sanath.moneytracker.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.sanath.moneytracker.R;
import com.sanath.moneytracker.data.DataContract;
import com.sanath.moneytracker.ui.activities.AddAccountActivity;
import com.sanath.moneytracker.ui.activities.AddCategoryActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment {
    private static final String TAG = CategoriesFragment.class.getSimpleName();
    public static final int REQUEST_CODE_ADD_CATEGORY = 0x000002;

    private Unbinder unbinder;

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.menuActionAdd)
    FloatingActionMenu floatingActionMenu;

    private int accountType = DataContract.AccountTypes.EXPENSES;

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    public CategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        unbinder = ButterKnife.bind(this, view);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.expenses));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.income));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager.setAdapter(new CategoryFragmentAdapter(getChildFragmentManager()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                accountType = position == 0 ?
                        DataContract.AccountTypes.EXPENSES : DataContract.AccountTypes.INCOME;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        floatingActionMenu.setOnMenuButtonClickListener(fabClickListener);
        return view;
    }

    View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), AddCategoryActivity.class);
            intent.putExtra(AddCategoryActivity.KEY_CATEGORY_TYPE, accountType);
            startActivityForResult(intent, REQUEST_CODE_ADD_CATEGORY);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public class CategoryFragmentAdapter extends FragmentStatePagerAdapter {

        public CategoryFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return CategoryChildFragment.newInstance(DataContract.AccountTypes.EXPENSES);
                case 1:
                    return CategoryChildFragment.newInstance(DataContract.AccountTypes.INCOME);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
