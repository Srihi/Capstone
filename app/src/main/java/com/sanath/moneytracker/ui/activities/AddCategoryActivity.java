package com.sanath.moneytracker.ui.activities;

import android.app.ActionBar;
import android.content.ContentValues;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.sanath.moneytracker.data.DataContract;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        unbinder = ButterKnife.bind(this);

        accountType = getIntent().getIntExtra(KEY_CATEGORY_TYPE, AccountTypes.EXPENSES);
        setActivityTitle();
        fillDrawableCache();
        setColorSelectorColor();
        addDefaultIcon();

        buttonColorSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ColorChooserDialog.Builder(AddCategoryActivity.this,
                        R.string.title_dialog_category_color)
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
            setTitle(R.string.activity_title_add_expense);
        } else {
            setTitle(R.string.activity_title_add_income);
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
                .title(R.string.dialog_title_categoty_icon)
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.ic_action_done) {
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
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.AccountEntry.COLUMN_NAME, editTextCategoryName.getText().toString().trim());
        contentValues.put(DataContract.AccountEntry.COLUMN_TYPE, accountType);
        contentValues.put(DataContract.AccountEntry.COLUMN_ICON, selectedIcon.ordinal());
        contentValues.put(DataContract.AccountEntry.COLUMN_COLOR, selectedColor);
        getContentResolver().insert(DataContract.AccountEntry.CONTENT_URI, contentValues);
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
