package com.tongminhnhut.orderfood_demo.Helper;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.tongminhnhut.orderfood_demo.Interface.RecylerItemTouchHeplerListener;
import com.tongminhnhut.orderfood_demo.ViewHolder.CartViewHolder;

/**
 * Created by tongminhnhut on 16/03/2018.
 */

public class RecylerItemTouchHelper extends ItemTouchHelper.SimpleCallback{
    private RecylerItemTouchHeplerListener listener;

    public RecylerItemTouchHelper(int dragDirs, int swipeDirs, RecylerItemTouchHeplerListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (listener !=null)
            listener.onSwiped(viewHolder,direction, viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        View foreGroundView = ((CartViewHolder)viewHolder).view_foreground;
        getDefaultUIUtil().clearView(foreGroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foreGroundView = ((CartViewHolder)viewHolder).view_foreground;
        getDefaultUIUtil().onDraw(c, recyclerView,foreGroundView, dX,dY,actionState, isCurrentlyActive);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder !=null){
            View foreGroundView = ((CartViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().onSelected(foreGroundView);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foreGroundView = ((CartViewHolder)viewHolder).view_foreground;
        getDefaultUIUtil().onDrawOver(c, recyclerView,foreGroundView, dX,dY,actionState, isCurrentlyActive);
    }
}
