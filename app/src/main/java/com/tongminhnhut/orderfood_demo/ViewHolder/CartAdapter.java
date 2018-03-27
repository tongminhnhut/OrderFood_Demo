package com.tongminhnhut.orderfood_demo.ViewHolder;


import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Picasso;
import com.tongminhnhut.orderfood_demo.CartActivity;
import com.tongminhnhut.orderfood_demo.Common.Common;
import com.tongminhnhut.orderfood_demo.Database.Database;
import com.tongminhnhut.orderfood_demo.Interface.ItemClickListener;
import com.tongminhnhut.orderfood_demo.R;
import com.tongminhnhut.orderfood_demo.model.Order;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by nhut on 2/18/2018.
 */


public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    List<Order> list = new ArrayList<>();
    CartActivity cartActivity;

    public CartAdapter(List<Order> list, CartActivity cartActivity) {
        this.list = list;
        this.cartActivity = cartActivity;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cartActivity);
        View view = inflater.inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, final int position) {
//        TextDrawable textDrawable = (TextDrawable) TextDrawable.builder()
//                .buildRound(""+list.get(position).getQuanlity(), Color.BLACK);
//
//        holder.imgCount.setImageDrawable(textDrawable);

        holder.btnNumber.setNumber(list.get(position).getQuanlity());
        holder.btnNumber.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = list.get(position);
                order.setQuanlity(String.valueOf(newValue));
                new Database(cartActivity).updateCart(order);

                // Tính tông giá
                int total = 0 ;
                List<Order> orders = new Database(cartActivity).getCart();
                for (Order item:orders){
                    total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuanlity()));
                }
//                Locale locale = new Locale("en", "US");
//                NumberFormat fm = NumberFormat.getCurrencyInstance(locale);
                DecimalFormat fm = new DecimalFormat("#,###,###");
                cartActivity.txtTotal.setText(fm.format(total));
            }
        });
//        Locale locale = new Locale("vn", "VN");
//        NumberFormat fm = NumberFormat.getCurrencyInstance(locale);
        DecimalFormat fm = new DecimalFormat("#,###,###");
        int pirce = (Integer.parseInt(list.get(position).getPrice())) * (Integer.parseInt(list.get(position).getQuanlity()));
        holder.txtPrice.setText(fm.format(pirce));
        holder.txtName.setText(list.get(position).getProductName());
        Picasso.with(cartActivity)
                .load(list.get(position).getImage())
                .resize(70, 70)
                .into(holder.imgFood);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Order getItem (int position){
        return list.get(position);
    }

    public void removeItem (int position){
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem (Order item, int position){
        list.add(position, item);
        notifyItemInserted(position);
    }

}
