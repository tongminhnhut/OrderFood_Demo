package com.tongminhnhut.orderfood_demo.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tongminhnhut.orderfood_demo.R;
import com.tongminhnhut.orderfood_demo.model.Order;

import java.util.List;

/**
 * Created by tongminhnhut on 10/03/2018.
 */
class MyViewholder extends RecyclerView.ViewHolder {
    public TextView txtName, txtSoluong, txtPrice, txtDiscount ;

    public MyViewholder(View itemView, TextView txtName, TextView txtSoluong, TextView txtPrice, TextView txtDiscount) {
        super(itemView);
        this.txtName = txtName;
        this.txtSoluong = txtSoluong;
        this.txtPrice = txtPrice;
        this.txtDiscount = txtDiscount;
    }

    public MyViewholder(View itemView) {
        super(itemView);
        txtName = itemView.findViewById(R.id.txtName_CashDetailItem);
        txtSoluong = itemView.findViewById(R.id.txtSoluong_CashDetailItem);
        txtPrice = itemView.findViewById(R.id.txtPrice_CashDetailItem);
        txtDiscount = itemView.findViewById(R.id.txtDiscount_CashDetailItem);
    }
}
public class CashAdapter extends RecyclerView.Adapter<MyViewholder> {
    List<Order> listFood ;

    public CashAdapter(List<Order> listFood) {
        this.listFood = listFood;
    }

    @Override
    public MyViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_cash_item, parent, false);
        return new MyViewholder(view);
    }

    @Override
    public void onBindViewHolder(MyViewholder holder, int position) {
        Order order = listFood.get(position);
        holder.txtName.setText(String.format("Tên món ăn : %s ",order.getProductName()));
        holder.txtSoluong.setText(String.format("Số lượng : %s",order.getQuanlity()));
        holder.txtPrice.setText(String.format("Giá : %s",order.getPrice()));
        holder.txtDiscount.setText(String.format("Giảm giá : %s ", order.getDiscount()));

    }

    @Override
    public int getItemCount() {
        return listFood.size();
    }
}
