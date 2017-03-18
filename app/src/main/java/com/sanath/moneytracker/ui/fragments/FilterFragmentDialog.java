package com.sanath.moneytracker.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.ImageView;

import com.sanath.moneytracker.R;
import com.sanath.moneytracker.data.DataContract;
import com.sanath.moneytracker.data.DataContract.TransactionTypes;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by sanathnandasiri on 2/28/17.
 */

public class FilterFragmentDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    public static final java.lang.String KEY_SELECTED_FILTER = "key_selected_filter";
    private Unbinder unbinder;

    @BindView(R.id.imageButtonAll)
    ImageView imageButtonAll;
    @BindView(R.id.imageButtonIncome)
    ImageView imageButtonIncome;
    @BindView(R.id.imageButtonExpenses)
    ImageView imageButtonExpenses;
    @BindView(R.id.imageButtonTransfer)
    ImageView imageButtonTransfer;

    private FilterDismissListener dismissListener;
    private FilterSelectedListener filterSelectedListener;

    private int selectedFilterType = TransactionTypes.ALL;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedFilterType = getArguments().getInt(KEY_SELECTED_FILTER, TransactionTypes.ALL);
    }

    @SuppressWarnings("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_filter, null);
        unbinder = ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        imageButtonAll.setOnClickListener(this);
        imageButtonIncome.setOnClickListener(this);
        imageButtonExpenses.setOnClickListener(this);
        imageButtonTransfer.setOnClickListener(this);

        setSelectedFilter();
    }

    private void setSelectedFilter() {
        switch (selectedFilterType) {
            case TransactionTypes.ALL:
                toggleButtons(imageButtonAll);
                break;
            case TransactionTypes.INCOME:
                toggleButtons(imageButtonIncome);
                break;
            case TransactionTypes.EXPENSES:
                toggleButtons(imageButtonExpenses);
                break;
            case TransactionTypes.TRANSFER:
                toggleButtons(imageButtonTransfer);
                break;
            default:
                toggleButtons(imageButtonAll);
                break;
        }
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.onDismiss();
        }
        unbinder.unbind();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButtonAll:
                selectedFilterType = TransactionTypes.ALL;
                break;
            case R.id.imageButtonIncome:
                selectedFilterType = TransactionTypes.INCOME;
                break;
            case R.id.imageButtonExpenses:
                selectedFilterType = TransactionTypes.EXPENSES;
                break;
            case R.id.imageButtonTransfer:
                selectedFilterType = TransactionTypes.TRANSFER;
                break;
            default:
                selectedFilterType = TransactionTypes.ALL;
        }
        filter(selectedFilterType);
        toggleButtons(v);
    }

    private void toggleButtons(View v) {
        ImageView[] imageButtons = new ImageView[]{imageButtonTransfer, imageButtonExpenses, imageButtonIncome, imageButtonAll};
        for (ImageView imageButton : imageButtons) {
            if (imageButton.getId() == v.getId()) {
                imageButton.setBackgroundResource(R.drawable.selected_filter_circle_background);
            } else {
                imageButton.setBackgroundResource(R.drawable.gray_circle_background);
            }
        }
    }

    private void filter(int transactionTypes) {
        if (this.filterSelectedListener != null) {
            this.filterSelectedListener.onFilterSelected(transactionTypes);
        }
    }

    public void setFilterDismissListener(FilterDismissListener filterDismissListener) {
        dismissListener = filterDismissListener;
    }

    public void setFilterSelectedListener(FilterSelectedListener filterSelectedListener) {
        this.filterSelectedListener = filterSelectedListener;
    }

    public interface FilterDismissListener {
        void onDismiss();
    }

    public interface FilterSelectedListener {
        void onFilterSelected(int transactionTypes);
    }
}
