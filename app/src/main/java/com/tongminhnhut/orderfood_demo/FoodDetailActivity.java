package com.tongminhnhut.orderfood_demo;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tongminhnhut.orderfood_demo.Common.Common;
import com.tongminhnhut.orderfood_demo.Database.Database;
import com.tongminhnhut.orderfood_demo.model.Foods;
import com.tongminhnhut.orderfood_demo.model.Order;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodDetailActivity extends AppCompatActivity {
    TextView txtName, txtPrice, txtDes ;
    ImageView imgFood ;
    String idFood = "";
    FirebaseDatabase mData ;
    DatabaseReference detailFood ;
    CollapsingToolbarLayout collapsingToolbarLayout ;
    ElegantNumberButton numberButton ;

    CounterFab btnCart ;
    Foods curentFood ;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //set Default font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fs.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setContentView(R.layout.activity_food_detail);
        mData = FirebaseDatabase.getInstance();
        detailFood= mData.getReference("Foods");

        initView();

        if (getIntent() != null)
            idFood = getIntent().getStringExtra("IDFood");
        if (!idFood.isEmpty() && idFood!=null){
            loadFoods(idFood);
        }

        btnCart.setColorFilter(R.color.colorPrimary);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(FoodDetailActivity.this).addToCart(new Order(
                        idFood,
                        curentFood.getName(),
                        numberButton.getNumber(),
                        curentFood.getPrice(),
                        curentFood.getDiscount(),
                        curentFood.getImage()
                ));
                Toast.makeText(FoodDetailActivity.this, "Added to Cart !", Toast.LENGTH_SHORT).show();
            }
        });

        btnCart.setCount(new Database(this).getCountCart());
    }

    private void loadFoods(String idFood) {
        detailFood.child(idFood).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                curentFood = dataSnapshot.getValue(Foods.class);

                Picasso.with(getBaseContext()).load(curentFood.getImage())
                        .into(imgFood);

                collapsingToolbarLayout.setTitle(curentFood.getName());
                txtName.setText(curentFood.getName());
                DecimalFormat formatter = new DecimalFormat("#,###,###");
//                String price = formatter.format(curentFood.getPrice());
                txtPrice.setText(curentFood.getPrice());
                txtDes.setText(curentFood.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initView() {
        txtName = findViewById(R.id.txtName_FoodDetail);
        txtPrice = findViewById(R.id.txtPrice_FoodDetail);
        txtDes =findViewById(R.id.txtDecription_FoodDetail);
        imgFood =findViewById(R.id.imgFoodDetail);
        numberButton = findViewById(R.id.numer_button);
        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapseAppbar);
        btnCart = findViewById(R.id.btnCart_FoodDetail);
    }
}
