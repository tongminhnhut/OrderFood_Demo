package com.tongminhnhut.orderfood_demo.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tongminhnhut.orderfood_demo.Interface.ItemClickListener;
import com.tongminhnhut.orderfood_demo.R;

/**
 * Created by nhut on 2/17/2018.
 */

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuName ;
    public ImageView imgHinh ;
    private ItemClickListener itemClickListener ;


    public MenuViewHolder(View itemView) {
        super(itemView);

        txtMenuName = (TextView) itemView.findViewById(R.id.txtMenu_name);
        imgHinh = (ImageView)itemView.findViewById(R.id.imgMenu_image);

        itemView.setOnClickListener(this);


    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);

    }
}
