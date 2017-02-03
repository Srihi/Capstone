package com.sanath.moneytracker.ui.activities;

import android.content.ContentValues;
import android.graphics.Color;
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
import com.sanath.moneytracker.data.DataContract;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.ArrayList;

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

    private ArrayList<MaterialDrawableBuilder> drawableCache = new ArrayList<>(3);
    private int selectedColor = Color.BLUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        unbinder = ButterKnife.bind(this);
        fillDrawableCache();
        setIconSelectorColor();
        addDefaultIcon();

        buttonColorSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ColorChooserDialog.Builder(AddAccountActivity.this,
                        R.string.title_dialog_account_color)
                        .build()
                        .show(AddAccountActivity.this);
            }
        });

        adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
            @Override
            public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                Log.d(TAG, "onMaterialListItemSelected() called with: dialog = [" + dialog + "], index = [" + index + "], item = [" + item + "]");
                dialog.dismiss();
                MaterialDrawableBuilder builder = drawableCache.get(index);
                setIconAndColor(builder, AddAccountActivity.this.buttonIconSelector);
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

    private void addDefaultIcon() {
        MaterialDrawableBuilder builder = drawableCache.get(0);
        setIconAndColor(builder, buttonIconSelector);
        buttonIconSelector.setTag(builder);
    }

    private void fillDrawableCache() {
        drawableCache.add(MaterialDrawableBuilder.with(this) // provide a context
                .setIcon(MaterialDrawableBuilder.IconValue.BANK)
                .setToActionbarSize());

        drawableCache.add(MaterialDrawableBuilder.with(this) // provide a context
                .setIcon(MaterialDrawableBuilder.IconValue.WALLET)// set the icon color
                .setToActionbarSize());

        drawableCache.add(MaterialDrawableBuilder.with(this) // provide a context
                .setIcon(MaterialDrawableBuilder.IconValue.CASH) // set the icon color
                .setToActionbarSize());
    }

    private void showIconSelectorDialog() {
        new MaterialDialog.Builder(this)
                .title("Select Account Icon")
                .adapter(adapter, null)
                .show();
    }

    private void addAccountIcons(MaterialSimpleListAdapter adapter) {
        adapter.clear();
        adapter.add(new MaterialSimpleListItem.Builder(this)
                .icon(drawableCache.get(0).setColor(selectedColor).build())
                .content("Bank")
                .iconPaddingDp(8)
                .build());


        adapter.add(new MaterialSimpleListItem.Builder(this)
                .icon(drawableCache.get(1).setColor(selectedColor).build())
                .content("Wallet")
                .iconPaddingDp(8)
                .build());

        adapter.add(new MaterialSimpleListItem.Builder(this)
                .icon(drawableCache.get(2).setColor(selectedColor).build())
                .content("Other")
                .iconPaddingDp(8)
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.ic_action_done) {
            saveAccount();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAccount() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.AccountEntry.COLUMN_NAME, editTextAccountName.getText().toString().trim());
        contentValues.put(DataContract.AccountEntry.COLUMN_TYPE, DataContract.AccountTypes.TRANSFER);
        getContentResolver().insert(DataContract.AccountEntry.CONTENT_URI, contentValues);
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        MaterialDrawableBuilder builder = (MaterialDrawableBuilder) buttonIconSelector.getTag();
        this.selectedColor = selectedColor;
        setIconAndColor(builder, buttonIconSelector);
        setIconSelectorColor();
    }

    private void setIconSelectorColor() {
        MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(this) // provide a context
                .setIcon(MaterialDrawableBuilder.IconValue.CHECKBOX_BLANK_CIRCLE)
                .setToActionbarSize();
        setIconAndColor(builder, buttonColorSelector);

    }

    private void setIconAndColor(MaterialDrawableBuilder builder, ImageView imageView) {
        builder.setColor(selectedColor);
        imageView.setImageDrawable(builder.build());
    }
}
