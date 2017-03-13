package com.sanath.moneytracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanath.moneytracker.R;
import com.sanath.moneytracker.common.CursorRecyclerAdapter;
import com.sanath.moneytracker.common.ItemClickListener;
import com.sanath.moneytracker.common.Utils;
import com.sanath.moneytracker.data.DataContract;
import com.sanath.moneytracker.data.DataContract.AccountEntry;
import com.sanath.moneytracker.data.DataContract.JournalEntry;
import com.sanath.moneytracker.data.DataContract.TransactionEntry;
import com.sanath.moneytracker.data.DataContract.TransactionTypes;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by sanathnandasiri on 2/4/17.
 */

@SuppressWarnings("WrongConstant")
public class TransactionAdapter extends CursorRecyclerAdapter<TransactionsVH> {

    private final Context context;

    /**
     * State of ListView item that has never been determined.
     */
    private static final int STATE_UNKNOWN = 0;

    /**
     * State of a ListView item that is sectioned. A sectioned item must
     * display the separator.
     */
    private static final int STATE_SECTIONED_CELL = 1;

    /**
     * State of a ListView item that is not sectioned and therefore does not
     * display the separator.
     */
    private static final int STATE_REGULAR_CELL = 2;

    private int[] cellStates;
    private String header = null;

    Calendar calendar = Calendar.getInstance();
    Calendar calendarToday = Calendar.getInstance();

    private SimpleDateFormat sdfPeriod = new SimpleDateFormat("E, d", Locale.getDefault());

    private ItemClickListener<Uri> itemClickListener;

    public TransactionAdapter(Context context, Cursor c, ItemClickListener<Uri> itemClickListener) {
        super(c);
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(TransactionsVH holder, Cursor cursor) {

        holder.textViewTransactionDescription.setText(
                cursor.getString(cursor.getColumnIndex(JournalEntry.COLUMN_DESCRIPTION)));


        holder.textViewTransactionDetails.setText(String.valueOf(
                cursor.getString(cursor.getColumnIndex(AccountEntry.COLUMN_NAME))));

        int icon = cursor.getInt(cursor.getColumnIndex(AccountEntry.COLUMN_ICON));
        int selectedColor = cursor.getInt(cursor.getColumnIndex(AccountEntry.COLUMN_COLOR));
        MaterialDrawableBuilder builder = Utils.getMaterialDrawableBuilder(context, icon, Color.WHITE);
        holder.imageViewTransactionIcon.setImageDrawable(builder.build());
        Drawable background = holder.imageViewTransactionIcon.getBackground();
        Utils.setBackgroundColor(background, selectedColor);

        int transactionType = cursor.getInt(cursor.getColumnIndex(TransactionEntry.TRANSACTION_TYPE));

        double amount = cursor.getDouble(cursor.getColumnIndex(TransactionEntry.COLUMN_AMOUNT));
        SpannableString spannableAmount = Utils.getAmountWithCurrency(amount);
        if (amount < 0 && transactionType == TransactionTypes.INCOME) {
            // income
            setColor(spannableAmount, R.color.colorGreen500);
        } else if (amount > 0 && transactionType == TransactionTypes.EXPENSES) {
            //expense
            setColor(spannableAmount, R.color.colorRed500);
        } else {
            setColor(spannableAmount, R.color.colorBlue500);
        }
        holder.textViewTransactionAmount.setText(spannableAmount);

        boolean needHeader = false;
        final int position = cursor.getPosition();
        holder.header = getDateText(cursor.getLong(cursor.getColumnIndex(TransactionEntry.COLUMN_DATE_TIME)));

        switch (cellStates[position]) {
            case STATE_SECTIONED_CELL:
                needHeader = true;
                break;

            case STATE_REGULAR_CELL:
                needHeader = false;
                break;

            case STATE_UNKNOWN:
            default:
                // A separator is needed if it's the first itemview of the
                // ListView or if the group of the current cell is different
                // from the previous itemview.
                if (position == 0) {
                    needHeader = true;
                } else {
                    cursor.moveToPosition(position - 1);

                    header = getDateText(cursor.getLong(cursor.getColumnIndex(TransactionEntry.COLUMN_DATE_TIME)));
                    if (header != null && holder.header != null && !header.equalsIgnoreCase(holder.header)) {
                        needHeader = true;
                    }

                    cursor.moveToPosition(position);
                }

                // Cache the result
                cellStates[position] = needHeader ? STATE_SECTIONED_CELL : STATE_REGULAR_CELL;
                break;
        }

        if (needHeader) {
            holder.textViewHeader.setText(holder.header);
            holder.textViewHeader.setVisibility(View.VISIBLE);
        } else {
            holder.textViewHeader.setVisibility(View.GONE);
        }
    }

    private void setColor(SpannableString amountSpann, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            amountSpann.setSpan(new ForegroundColorSpan(context.getColor(color)), 0, amountSpann.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            amountSpann.setSpan(new ForegroundColorSpan(context.getResources().getColor(color)), 0, amountSpann.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private String getDateText(Long dateInMillis) {
        calendar.setTimeInMillis(dateInMillis);
        int dateOfMonthToday = calendarToday.get(Calendar.DAY_OF_MONTH);
        if (dateOfMonthToday == calendar.get(Calendar.DAY_OF_MONTH)) {
            return context.getString(R.string.today);
        } else if (--dateOfMonthToday == calendar.get(Calendar.DAY_OF_MONTH)) {
            return context.getString(R.string.yesterday);
        }

        return sdfPeriod.format(calendar.getTime());
    }

    @Override
    public TransactionsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.list_item_transactions, parent, false);
        final TransactionsVH vh = new TransactionsVH(root);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(
                            JournalEntry.buildAccountUri(getItemId(vh.getAdapterPosition())));
                }
            }
        });
        return vh;
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        cellStates = newCursor == null ? null : new int[newCursor.getCount()];
        return super.swapCursor(newCursor);
    }
}
