package com.sanath.moneytracker.ui.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.sanath.moneytracker.data.DataContract.AccountEntry;
import com.sanath.moneytracker.data.DataContract.AccountTypes;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder.IconValue;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AddCategoryActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback {

    public static final String TAG = AddCategoryActivity.class.getSimpleName();
    public static final String KEY_CATEGORY_TYPE = "CATEGORY_TYPE";
    private Unbinder unbinder;

    @BindView(R.id.editTextCategoryName)
    EditText editTextCategoryName;
    @BindView(R.id.buttonIconSelector)
    ImageView buttonIconSelector;
    @BindView(R.id.buttonColorSelector)
    ImageView buttonColorSelector;


    private MaterialSimpleListAdapter adapter;

    private ArrayList<IconValue> drawableCache = new ArrayList<>(3);
    private int selectedColor = Color.GRAY;
    private IconValue selectedIcon = IconValue.MORE;

    private int accountType = AccountTypes.EXPENSES;

    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        unbinder = ButterKnife.bind(this);

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri uri = intent.getData();
        if (action != null && action.equals(Intent.ACTION_EDIT) && uri != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                editTextCategoryName.setText(cursor.getString(cursor.getColumnIndex(AccountEntry.COLUMN_NAME)));
                selectedColor = cursor.getInt(cursor.getColumnIndex(AccountEntry.COLUMN_COLOR));
                selectedIcon = IconValue.values()[cursor.getInt(cursor.getColumnIndex(AccountEntry.COLUMN_ICON))];
                accountType = cursor.getInt(cursor.getColumnIndex(AccountEntry.COLUMN_TYPE));
            }
            isEdit = true;
        } else {
            accountType = intent.getIntExtra(KEY_CATEGORY_TYPE, AccountTypes.EXPENSES);
        }
        setActivityTitle();
        fillDrawableCache();
        setColorSelectorColor();
        addDefaultIcon();

        buttonColorSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ColorChooserDialog.Builder(AddCategoryActivity.this,
                        R.string.dialog_title_category_color)
                        .build()
                        .show(AddCategoryActivity.this);
            }
        });
        adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
            @Override
            public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                dialog.dismiss();
                selectedIcon = drawableCache.get(index);
                MaterialDrawableBuilder builder = getMaterialDrawableBuilder(AddCategoryActivity.this.selectedIcon);
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

    private void setActivityTitle() {
        if (accountType == AccountTypes.EXPENSES) {
            if (isEdit) {
                setTitle(R.string.activity_title_edit_expense_category);
            } else {
                setTitle(R.string.activity_title_add_expense_category);
            }
        } else {
            if (isEdit) {
                setTitle(R.string.activity_title_edit_income_category);
            } else {
                setTitle(R.string.activity_title_add_income_category);
            }
        }
    }

    private MaterialDrawableBuilder getMaterialDrawableBuilder(IconValue selectedIcon) {
        return MaterialDrawableBuilder.with(AddCategoryActivity.this) // provide a context
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
        if (accountType == AccountTypes.EXPENSES) {
            addExpensesIcons();
        } else {
            addIncomeIcons();
        }
    }


    private void showIconSelectorDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.dialog_title_category_icon)
                .adapter(adapter, null)
                .show();
    }

    private void addAccountIcons(MaterialSimpleListAdapter adapter) {
        adapter.clear();
        for (IconValue iconValue : drawableCache) {
            adapter.add(new MaterialSimpleListItem.Builder(this)
                    .icon(getMaterialDrawableBuilder(iconValue).setColor(Color.WHITE).build())
                    .iconPaddingDp(8)
                    .backgroundColor(selectedColor)
                    .build());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateAccount() {
        getContentResolver().update(getIntent().getData(), getContentValues(), null, null);
    }

    private void saveAccount() {
        getContentResolver().insert(AccountEntry.CONTENT_URI, getContentValues());
    }

    @NonNull
    private ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AccountEntry.COLUMN_NAME, editTextCategoryName.getText().toString().trim());
        contentValues.put(AccountEntry.COLUMN_TYPE, accountType);
        contentValues.put(AccountEntry.COLUMN_ICON, selectedIcon.ordinal());
        contentValues.put(AccountEntry.COLUMN_COLOR, selectedColor);
        return contentValues;
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

    private void addExpensesIcons() {
        drawableCache.add(IconValue.MORE);
        drawableCache.add(IconValue.FOOD);
        drawableCache.add(IconValue.AIRPLANE);
        drawableCache.add(IconValue.CAR);
        drawableCache.add(IconValue.CAR_WASH);
        drawableCache.add(IconValue.GAS_STATION);
        drawableCache.add(IconValue.WATER);
        drawableCache.add(IconValue.BEACH);
        drawableCache.add(IconValue.GIFT);
        drawableCache.add(IconValue.TRAIN);
        drawableCache.add(IconValue.BUS);
        drawableCache.add(IconValue.PHONE);
        drawableCache.add(IconValue.HEADPHONES);
        drawableCache.add(IconValue.CART);
        drawableCache.add(IconValue.BEER);
        drawableCache.add(IconValue.HEART);
        drawableCache.add(IconValue.HOSPITAL_BUILDING);
        drawableCache.add(IconValue.STETHOSCOPE);
        drawableCache.add(IconValue.HOME);
        drawableCache.add(IconValue.MARTINI);
        drawableCache.add(IconValue.MOTORBIKE);
        drawableCache.add(IconValue.MOVIE);
        drawableCache.add(IconValue.OIL);
        drawableCache.add(IconValue.SCHOOL);
        drawableCache.add(IconValue.SHOPPING);
        drawableCache.add(IconValue.RUN);
        drawableCache.add(IconValue.SUBWAY);
        drawableCache.add(IconValue.COFFEE);
    }

    private void addIncomeIcons() {
        drawableCache.add(IconValue.MORE);
        drawableCache.add(IconValue.TRENDING_UP);
        drawableCache.add(IconValue.BRIEFCASE);
        drawableCache.add(IconValue.CASH);
        drawableCache.add(IconValue.GIFT);
        drawableCache.add(IconValue.TROPHY_AWARD);
    }
}
