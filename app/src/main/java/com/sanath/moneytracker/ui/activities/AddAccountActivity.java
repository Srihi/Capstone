package com.sanath.moneytracker.ui.activities;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.sanath.moneytracker.R;
import com.sanath.moneytracker.common.Utils;
import com.sanath.moneytracker.data.DataContract;
import com.sanath.moneytracker.data.DataContract.AccountEntry;
import com.sanath.moneytracker.data.DataContract.JournalEntry;
import com.sanath.moneytracker.data.DataContract.PostingEntry;
import com.sanath.moneytracker.data.DataContract.TransactionTypes;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder.IconValue;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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

    private boolean isEdit = false;

    private double currentBalance = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        unbinder = ButterKnife.bind(this);

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri uri = intent.getData();
        if (action != null && action.equals(Intent.ACTION_EDIT) && uri != null) {
            getAccountDetails(uri);
            isEdit = true;
            setTitle(getString(R.string.activity_title_edit_account));
        } else {
            setTitle(getString(R.string.activity_title_add_account));
        }

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

    private void getAccountDetails(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            editTextAccountName.setText(cursor.getString(cursor.getColumnIndex(AccountEntry.COLUMN_NAME)));
            selectedColor = cursor.getInt(cursor.getColumnIndex(AccountEntry.COLUMN_COLOR));
            selectedIcon = IconValue.values()[cursor.getInt(cursor.getColumnIndex(AccountEntry.COLUMN_ICON))];
            cursor.close();
        }

        currentBalance = Utils.getBalance(this, Integer.valueOf(uri.getLastPathSegment()));

        editTextBalance.setText(String.valueOf(currentBalance));
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isEdit) {
            MenuItem item = menu.findItem(R.id.action_delete);
            if (item != null) {
                item.setVisible(true);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            try {
                if (isEdit) {
                    updateAccount();
                } else {
                    saveAccount();
                }
                finish();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        } else if (id == R.id.action_delete) {
            showDeleteAccountConfirmMessage();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAccountConfirmMessage() {
        new MaterialDialog.Builder(this).title(R.string.dialog_title_delete_account)
                .content(R.string.dialog_body_delete_account)
                .positiveText(R.string.dialog_positive_delete)
                .negativeText(R.string.dialog_negative_cancel)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            dialog.dismiss();
                            deleteAccount();
                        } else {
                            dialog.dismiss();
                        }
                    }
                }).show();
    }

    private void deleteAccount() {
        if (getContentResolver().delete(getIntent().getData(), null, null) > 0) {
            finish();
        }
    }

    private void updateAccount() {
        double amount = Double.parseDouble(editTextBalance.getText().toString());
        long transactionDateTime = new Date().getTime();
        ContentValues accountValues = getContentValuesForAccount();
        ArrayList<ContentProviderOperation> operations = new
                ArrayList<>();

        Uri uri = getIntent().getData();
        operations.add(ContentProviderOperation.newUpdate(uri).withValues(accountValues).build());

        if (Math.abs(getBalance(amount)) > 0) { // balance edited
            ContentValues journalValues = getContentValuesForJournal(transactionDateTime, "change balance");
            ContentValues postingValuesDestination = getContentValuesForPosting(getBalance(amount), transactionDateTime);
            postingValuesDestination.put(PostingEntry.COLUMN_ACCOUNT_ID, uri.getLastPathSegment());
            operations.add(ContentProviderOperation.newInsert(JournalEntry.CONTENT_URI).withValues(journalValues).build());
            operations.add(ContentProviderOperation.newInsert(PostingEntry.CONTENT_URI).
                    withValues(postingValuesDestination)
                    .withValueBackReference(PostingEntry.COLUMN_JOURNAL_ID, 1)
                    .build());
        }

        try {
            getContentResolver().applyBatch(DataContract.CONTENT_AUTHORITY, operations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private double getBalance(double amount) {
        return amount - currentBalance;
    }

    private void saveAccount() {
        double amount = Double.parseDouble(editTextBalance.getText().toString());
        long transactionDateTime = new Date().getTime();
        ContentValues accountValues = getContentValuesForAccount();
        ContentValues journalValues = getContentValuesForJournal(transactionDateTime, "initial balance");
        ContentValues postingValuesDestination = getContentValuesForPosting(amount, transactionDateTime);

        ArrayList<ContentProviderOperation> operations = new
                ArrayList<>();

        operations.add(ContentProviderOperation.newInsert(AccountEntry.CONTENT_URI).withValues(accountValues).build());
        operations.add(ContentProviderOperation.newInsert(JournalEntry.CONTENT_URI).withValues(journalValues).build());
        operations.add(ContentProviderOperation.newInsert(PostingEntry.CONTENT_URI).
                withValues(postingValuesDestination)
                .withValueBackReference(PostingEntry.COLUMN_ACCOUNT_ID, 0)
                .withValueBackReference(PostingEntry.COLUMN_JOURNAL_ID, 1)
                .build());

        try {
            getContentResolver().applyBatch(DataContract.CONTENT_AUTHORITY, operations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @NonNull
    private ContentValues getContentValuesForPosting(double amount, long transactionDateTime) {
        ContentValues postingValuesDestination = new ContentValues();
        postingValuesDestination.put(PostingEntry.COLUMN_AMOUNT, amount);
        postingValuesDestination.put(PostingEntry.COLUMN_DATE_TIME, transactionDateTime);
        postingValuesDestination.put(PostingEntry.COLUMN_CREDIT_DEBIT, DataContract.CreditType.DEBIT);
        return postingValuesDestination;
    }

    @NonNull
    private ContentValues getContentValuesForJournal(long transactionDateTime, String reason) {
        ContentValues journalValues = new ContentValues();
        journalValues.put(JournalEntry.COLUMN_TYPE, TransactionTypes.BALANCE);
        journalValues.put(JournalEntry.COLUMN_PERIOD, Utils.getPeriodTag(transactionDateTime));
        journalValues.put(JournalEntry.COLUMN_DESCRIPTION, reason);
        journalValues.put(JournalEntry.COLUMN_DATE_TIME, transactionDateTime);
        return journalValues;
    }

    @NonNull
    private ContentValues getContentValuesForAccount() {
        ContentValues accountValues = new ContentValues();
        accountValues.put(AccountEntry.COLUMN_NAME, editTextAccountName.getText().toString().trim());
        accountValues.put(AccountEntry.COLUMN_TYPE, DataContract.AccountTypes.TRANSFER);
        accountValues.put(AccountEntry.COLUMN_ICON, selectedIcon.ordinal());
        accountValues.put(AccountEntry.COLUMN_COLOR, selectedColor);
        return accountValues;
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
