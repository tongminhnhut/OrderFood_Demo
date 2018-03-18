package com.tongminhnhut.orderfood_demo.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tongminhnhut.orderfood_demo.Interface.ItemClickListener;
import com.tongminhnhut.orderfood_demo.R;

/**
 * Created by nhut on 2/22/2018.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder  {
    public TextView txtTen, txtStatus, txtPhone, txtAddress;

    public OrderViewHolder(View itemView) {
        super(itemView);
        txtTen = (TextView) itemView.findViewById(R.id.txtName_OrderItem);
        txtStatus = (TextView) itemView.findViewById(R.id.txtStatus_OrderItem);
        txtPhone = (TextView) itemView.findViewById(R.id.txtPhone_OrderItem);
        txtAddress = (TextView) itemView.findViewById(R.id.txtAddress_OrderItem);

    }

}
