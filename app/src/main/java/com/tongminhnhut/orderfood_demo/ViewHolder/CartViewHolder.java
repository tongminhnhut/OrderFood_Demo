package com.tongminhnhut.orderfood_demo.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.tongminhnhut.orderfood_demo.Common.Common;
import com.tongminhnhut.orderfood_demo.Interface.ItemClickListener;
import com.tongminhnhut.orderfood_demo.R;

/**
 * Created by tongminhnhut on 17/03/2018.
 */

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener
{

    public ImageView imgFood ;
    public TextView txtName, txtPrice ;
    public ImageView imgCount ;
    public ElegantNumberButton btnNumber;

    public RelativeLayout view_background ;
    public LinearLayout view_foreground ;

    private ItemClickListener itemClickListener ;
    public CartViewHolder(View itemView) {
        super(itemView);
        txtName = (TextView) itemView.findViewById(R.id.txtName_CardItem);
        txtPrice = (TextView)itemView.findViewById(R.id.txtPrice_CardItem);
//        imgCount = (ImageView) itemView.findViewById(R.id.imgCount_CardItem);
        btnNumber = itemView.findViewById(R.id.numer_button_cartItem);
        imgFood = itemView.findViewById(R.id.imgFood_cartItem);
        view_background = itemView.findViewById(R.id.view_backGroud);
        view_foreground = itemView.findViewById(R.id.layout_View_CartItem);

        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Vui lòng chọn ");

        menu.add(0,0,getAdapterPosition(), Common.DELETE);
    }
}
