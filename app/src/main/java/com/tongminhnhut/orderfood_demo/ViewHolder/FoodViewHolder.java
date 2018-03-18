package com.tongminhnhut.orderfood_demo.ViewHolder;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tongminhnhut.orderfood_demo.Interface.ItemClickListener;
import com.tongminhnhut.orderfood_demo.R;

/**
 * Created by nhut on 2/17/2018.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtName, txtPrice ;
    public ImageView imgFood, imgFavorite ;
    private ItemClickListener itemClickListener ;
    public ImageView btnCart;
    public FoodViewHolder(View itemView) {
        super(itemView);

        txtName = (TextView) itemView.findViewById(R.id.txtFood_name);
        imgFood = (ImageView) itemView.findViewById(R.id.imgFood_image);
        txtPrice = itemView.findViewById(R.id.txtFood_Price);
        btnCart = itemView.findViewById(R.id.btnCart_foodItem);
        imgFavorite = itemView.findViewById(R.id.imgFavorite);


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
