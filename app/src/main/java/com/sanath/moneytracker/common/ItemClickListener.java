package com.sanath.moneytracker.common;

import android.net.Uri;

/**
 * Created by sna on 3/3/2017.
 */

public interface ItemClickListener<T> {
    void onItemClick(Uri item);
}
