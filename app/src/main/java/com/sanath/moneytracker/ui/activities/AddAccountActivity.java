package com.sanath.moneytracker.ui.activities;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.sanath.moneytracker.R;
import com.sanath.moneytracker.common.Utils;
import com.sanath.moneytracker.data.DataContract;
import com.sanath.moneytracker.data.DataContract.TransactionTypes;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder.IconValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.sanath.moneytracker.R.id.editTextDescription;

public class AddAccountActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback {

    public static final String TAG = AddAccountActivity.class.getSimpleName();
    private Unbinder unbinder;

    @BindView(R.id.editTextAccountName)
    EditText editTextAccountName;
    @BindView(R.id.editTextBalance)
    EditText editTextBalance;
    @BindView(R.id.buttonIconSelector)
    ImageView buttonIconSelector;
    @BindView(R.id.buttonColorSelector)
    ImageView buttonColorSelector;


    private MaterialSimpleListAdapter adapter;

    private ArrayList<IconValue> drawableCache = new ArrayList<>(3);
    private int selectedColor = Color.GRAY;
    private IconValue selectedIcon = IconValue.BANK;

    private SimpleDateFormat sdfPeriod = new SimpleDateFormat("MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        unbinder = ButterKnife.bind(this);
        setTitle(getString(R.string.activity_title_add_account));
        fillDrawableCache();
        setColorSelectorColor();
        addDefaultIcon();

        buttonColorSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ColorChooserDialog.Builder(AddAccountActivity.this,
                        R.string.dialog_title_account_color)
                        .build()
                        .show(AddAccountActivity.this);
            }
        });

        adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
            @Override
            public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                dialog.dismiss();
                selectedIcon = drawableCache.get(index);
                MaterialDrawableBuilder builder = getMaterialDrawableBuilder(AddAccountActivity.this.selectedIcon);
                setIcon(builder);
                buttonIconSelector.setTag(builder);
            }
        });

        addAccountIcons(adapter);

        buttonIconSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAccountIcons(adapter);
                showIconSelectorDialog();
            }
        });

    }

    private MaterialDrawableBuilder getMaterialDrawableBuilder(IconValue selectedIcon) {
        return MaterialDrawableBuilder.with(AddAccountActivity.this) // provide a context
                .setIcon(selectedIcon)
                .setColor(Color.WHITE)
                .setToActionbarSize();
    }

    private void addDefaultIcon() {
        MaterialDrawableBuilder builder = getMaterialDrawableBuilder(selectedIcon);
        setIcon(builder);
        buttonIconSelector.setTag(builder);
    }

    private void fillDrawableCache() {
        drawableCache.add(IconValue.BANK);
        drawableCache.add(IconValue.WALLET);
        drawableCache.add(IconValue.CASH);
    }

    private void showIconSelectorDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.dialog_title_account_icon)
                .adapter(adapter, null)
                .show();
    }

    private void addAccountIcons(MaterialSimpleListAdapter adapter) {
        adapter.clear();
        adapter.add(new MaterialSimpleListItem.Builder(this)
                .icon(getMaterialDrawableBuilder(drawableCache.get(0)).setColor(Color.WHITE).build())
                .content(R.string.account_bank)
                .iconPaddingDp(8)
                .backgroundColor(selectedColor)
                .build());


        adapter.add(new MaterialSimpleListItem.Builder(this)
                .icon(getMaterialDrawableBuilder(drawableCache.get(1)).setColor(Color.WHITE).build())
                .content(R.string.account_wallet)
                .iconPaddingDp(8)
                .backgroundColor(selectedColor)
                .build());

        adapter.add(new MaterialSimpleListItem.Builder(this)
                .icon(getMaterialDrawableBuilder(drawableCache.get(2)).setColor(Color.WHITE).build())
                .content(R.string.account_other)
                .iconPaddingDp(8)
                .backgroundColor(selectedColor)
                .build());
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
        getMenuInflater().inflate(R.menu.menu_add_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            try {
                saveAccount();
                finish();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAccount() {

        double amount = Double.parseDouble(editTextBalance.getText().toString());
        long transactionDateTime = new Date().getTime();

        ContentValues accountValues = new ContentValues();
        accountValues.put(DataContract.AccountEntry.COLUMN_NAME, editTextAccountName.getText().toString().trim());
        accountValues.put(DataContract.AccountEntry.COLUMN_TYPE, DataContract.AccountTypes.TRANSFER);
        accountValues.put(DataContract.AccountEntry.COLUMN_ICON, selectedIcon.ordinal());
        accountValues.put(DataContract.AccountEntry.COLUMN_COLOR, selectedColor);

        ContentValues journalValues = new ContentValues();
        journalValues.put(DataContract.JournalEntry.COLUMN_TYPE, TransactionTypes.BALANCE);
        journalValues.put(DataContract.JournalEntry.COLUMN_PERIOD, sdfPeriod.format(transactionDateTime));
        journalValues.put(DataContract.JournalEntry.COLUMN_DESCRIPTION, "balance");
        journalValues.put(DataContract.JournalEntry.COLUMN_DATE_TIME, transactionDateTime);

        ContentValues postingValuesDestination = new ContentValues();
        postingValuesDestination.put(DataContract.PostingEntry.COLUMN_AMOUNT, amount);
        postingValuesDestination.put(DataContract.PostingEntry.COLUMN_DATE_TIME, transactionDateTime);
        postingValuesDestination.put(DataContract.PostingEntry.COLUMN_CREDIT_DEBIT, DataContract.CreditType.DEBIT);

        ArrayList<ContentProviderOperation> operations = new
                ArrayList<>();

        operations.add(ContentProviderOperation.newInsert(DataContract.AccountEntry.CONTENT_URI).withValues(accountValues).build());

        operations.add(ContentProviderOperation.newInsert(DataContract.JournalEntry.CONTENT_URI).withValues(journalValues).build());

        operations.add(ContentProviderOperation.newInsert(DataContract.PostingEntry.CONTENT_URI).
                withValues(postingValuesDestination)
                .withValueBackReference(DataContract.PostingEntry.COLUMN_ACCOUNT_ID, 0)
                .withValueBackReference(DataContract.PostingEntry.COLUMN_JOURNAL_ID, 1)
                .build());

        try {
            getContentResolver().applyBatch(DataContract.CONTENT_AUTHORITY, operations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        MaterialDrawableBuilder builder = (MaterialDrawableBuilder) buttonIconSelector.getTag();
        this.selectedColor = selectedColor;
        setIcon(builder);
        setColorSelectorColor();
    }

    private void setColorSelectorColor() {
        Drawable background = buttonColorSelector.getBackground();
        Utils.setBackgroundColor(background, selectedColor);
    }

    private void setIcon(MaterialDrawableBuilder builder) {
        builder.setColor(Color.WHITE);
        buttonIconSelector.setImageDrawable(builder.build());
        Drawable background = buttonIconSelector.getBackground();
        Utils.setBackgroundColor(background, selectedColor);
    }
}
