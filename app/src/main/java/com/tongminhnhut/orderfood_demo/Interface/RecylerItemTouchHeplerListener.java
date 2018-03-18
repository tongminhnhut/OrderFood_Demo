package com.tongminhnhut.orderfood_demo.Interface;

import android.support.v7.widget.RecyclerView;

/**
 * Created by tongminhnhut on 16/03/2018.
 */

public interface RecylerItemTouchHeplerListener {
    void onSwiped (RecyclerView.ViewHolder viewHolder, int direction, int positon);
}
