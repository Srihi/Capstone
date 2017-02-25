package com.sanath.moneytracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanath.moneytracker.R;
import com.sanath.moneytracker.common.CursorRecyclerAdapter;
import com.sanath.moneytracker.common.Utils;
import com.sanath.moneytracker.data.DataContract.AccountEntry;
import com.sanath.moneytracker.data.DataContract.JournalEntry;
import com.sanath.moneytracker.data.DataContract.PostingEntry;

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

    public TransactionAdapter(Context context, Cursor c) {
        super(c);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(TransactionsVH holder, Cursor cursor) {

        holder.textViewTransactionDescription.setText(
                cursor.getString(cursor.getColumnIndex(JournalEntry.COLUMN_DESCRIPTION)));
        holder.textViewTransactionAmount.setText(String.valueOf(
                cursor.getDouble(cursor.getColumnIndex(PostingEntry.COLUMN_AMOUNT))));
        holder.textViewTransactionDetails.setText(String.valueOf(
                cursor.getString(cursor.getColumnIndex(AccountEntry.COLUMN_NAME))));

        int icon = cursor.getInt(cursor.getColumnIndex(AccountEntry.COLUMN_ICON));
        int selectedColor = cursor.getInt(cursor.getColumnIndex(AccountEntry.COLUMN_COLOR));
        MaterialDrawableBuilder builder = Utils.getMaterialDrawableBuilder(context, icon, Color.WHITE);
        holder.imageViewTransactionIcon.setImageDrawable(builder.build());
        Drawable background = holder.imageViewTransactionIcon.getBackground();
        Utils.setBackgroundColor(background, selectedColor);

        boolean needHeader = false;
        final int position = cursor.getPosition();
        holder.header = getDateText(cursor.getLong(cursor.getColumnIndex(JournalEntry.COLUMN_DATE_TIME)));

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

                    header = getDateText(cursor.getLong(cursor.getColumnIndex(JournalEntry.COLUMN_DATE_TIME)));
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

    private String getDateText(Long dateInMillis) {
        calendar.setTimeInMillis(dateInMillis);
        int dateOfMonthToday = calendarToday.get(Calendar.DAY_OF_MONTH);
        if(dateOfMonthToday == calendar.get(Calendar.DAY_OF_MONTH)){
            return context.getString(R.string.today);
        }else if (--dateOfMonthToday == calendar.get(Calendar.DAY_OF_MONTH)){
            return context.getString(R.string.yesterday);
        }

        return sdfPeriod.format(calendar.getTime());
    }

    @Override
    public TransactionsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.list_item_transactions, parent, false);
        return new TransactionsVH(root);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        cellStates = newCursor == null ? null : new int[newCursor.getCount()];
        return super.swapCursor(newCursor);
    }
}
