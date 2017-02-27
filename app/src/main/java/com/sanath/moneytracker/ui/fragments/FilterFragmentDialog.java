package com.sanath.moneytracker.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;

import com.sanath.moneytracker.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by sanathnandasiri on 2/28/17.
 */

public class FilterFragmentDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private Unbinder unbinder;

    private FilterDismissListener dismissListener;

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

   /* @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //filterTransactionListener = (FilterTransactionListener) activity;
        dismissListener = (FilterDismissListener) activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //filterTransactionListener = (FilterTransactionListener) context;
    }*/

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

    }

    public interface FilterDismissListener{
        void onDismiss();
    }
}
