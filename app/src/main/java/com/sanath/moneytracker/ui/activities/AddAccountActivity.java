package com.sanath.moneytracker.ui.activities;

import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.sanath.moneytracker.R;
import com.sanath.moneytracker.data.DataContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AddAccountActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @BindView(R.id.editTextAccountName)
    EditText editTextAccountName;
    @BindView(R.id.editTextBalance)
    EditText editTextBalance;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.ic_action_done){
            saveAccount();
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveAccount() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.AccountEntry.COLUMN_NAME,editTextAccountName.getText().toString().trim());
        contentValues.put(DataContract.AccountEntry.COLUMN_TYPE, DataContract.AccountTypes.TRANSFER);
        getContentResolver().insert(DataContract.AccountEntry.CONTENT_URI,contentValues);
    }
}
