package com.sanath.moneytracker.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sanath.moneytracker.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AddAccountActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
