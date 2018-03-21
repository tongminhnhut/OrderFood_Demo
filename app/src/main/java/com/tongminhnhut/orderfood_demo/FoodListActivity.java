package com.tongminhnhut.orderfood_demo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.tongminhnhut.orderfood_demo.Common.Common;
import com.tongminhnhut.orderfood_demo.Database.Database;
import com.tongminhnhut.orderfood_demo.Interface.ItemClickListener;
import com.tongminhnhut.orderfood_demo.ViewHolder.FoodViewHolder;
import com.tongminhnhut.orderfood_demo.model.Foods;
import com.tongminhnhut.orderfood_demo.model.Order;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodListActivity extends AppCompatActivity {
    RecyclerView recyclerViewFood ;
    RecyclerView.LayoutManager layoutManager ;
    FirebaseDatabase mData ;
    DatabaseReference food ;
    String categoryID = "";
    FirebaseRecyclerAdapter<Foods, FoodViewHolder> adapter ;

    // Search Bar
    FirebaseRecyclerAdapter<Foods, FoodViewHolder> searchAdapter ;
    List<String> list = new ArrayList<>();
    MaterialSearchBar materialSearchBar ;

    SwipeRefreshLayout swipeRefreshLayout ;

    Database localDB ;

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


        setContentView(R.layout.activity_food_list);
        mData = FirebaseDatabase.getInstance();
        food = mData.getReference("Foods");

        recyclerViewFood = findViewById(R.id.recylerView_FoodList);
        recyclerViewFood.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewFood.setLayoutManager(layoutManager);

        // local DB
        localDB = new Database(this);

        //view Swipe refresh
        swipeRefreshLayout = findViewById(R.id.swipe_foodlist);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getIntent() != null)
                    categoryID = getIntent().getStringExtra("ID");
                if (!categoryID.isEmpty() && categoryID!=null){
                    loadFoods(categoryID);
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (getIntent() != null)
                    categoryID = getIntent().getStringExtra("ID");
                if (!categoryID.isEmpty() && categoryID!=null){
                    if (Common.isConnectedInternet(getBaseContext())){
                        loadFoods(categoryID);
                    }else {
                        Toast.makeText(FoodListActivity.this, "Vui lòng kiểm tra internet", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

                materialSearchBar = findViewById(R.id.searchBar);
                materialSearchBar.setHint("Enter your foods");
                loadSearchBar();

                materialSearchBar.setLastSuggestions(list);
                materialSearchBar.setCardViewElevation(10);
                materialSearchBar.addTextChangeListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        List<String> suggest = new ArrayList<>();
                        for (String search:list){
                            if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                                suggest.add(search);
                        }
                        materialSearchBar.setLastSuggestions(suggest);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                    @Override
                    public void onSearchStateChanged(boolean enabled) {
                        if (!enabled)
                            recyclerViewFood.setAdapter(adapter);
                    }

                    @Override
                    public void onSearchConfirmed(CharSequence text) {
                        startSearch(text);
                    }

                    @Override
                    public void onButtonClicked(int buttonCode) {

                    }
                });


            }
        });





    }

    private void startSearch(CharSequence text) {
        Query query = food.orderByChild("name").equalTo(text.toString());
        FirebaseRecyclerOptions<Foods> options = new FirebaseRecyclerOptions.Builder<Foods>()
                .setQuery(query, Foods.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<Foods, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder viewHolder, int position, @NonNull Foods model) {
                viewHolder.txtName.setText(model.getName());

                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imgFood);
                Foods itemClick = model ;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodIntent = new Intent(getApplicationContext(), FoodDetailActivity.class);
                        foodIntent.putExtra("IDFood", searchAdapter.getRef(position).getKey() );
                        startActivity(foodIntent);
                    }
                });
            }

            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);

                return new FoodViewHolder(itemView);
            }
        };


        searchAdapter.notifyDataSetChanged();
        searchAdapter.startListening();
        recyclerViewFood.setAdapter(searchAdapter);

    }

    private void loadSearchBar() {
        food.orderByChild("menuId").equalTo(categoryID).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    Foods item = postSnapshot.getValue(Foods.class);
                    list.add(item.getName());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadFoods(String ID) {
        Query query = food.orderByChild("menuId").equalTo(ID);
        FirebaseRecyclerOptions<Foods> options = new FirebaseRecyclerOptions.Builder<Foods>()
                .setQuery(query, Foods.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Foods, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, final int position, @NonNull final Foods model) {
                viewHolder.txtName.setText(model.getName());
                viewHolder.txtPrice.setText(String.format("VND %s", model.getPrice().toString()));

//                final boolean isExists = new Database(getApplicationContext()).checkFoodExists(adapter.getRef(position).getKey(), Common.curentUser.getPhone()) ;
                    viewHolder.btnCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                new Database(FoodListActivity.this).addToCart(new Order(
                                        adapter.getRef(position).getKey(),
                                        model.getName(),
                                        "1",
                                        model.getPrice(),
                                        model.getDiscount(),
                                        model.getImage()
                                ));
                                Toast.makeText(FoodListActivity.this, "Added to Cart !", Toast.LENGTH_SHORT).show();


                        }
                    });
                    Picasso.with(getBaseContext()).load(model.getImage())
                            .into(viewHolder.imgFood);
                    Foods itemClick = model ;
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Intent foodIntent = new Intent(getApplicationContext(), FoodDetailActivity.class);
                            foodIntent.putExtra("IDFood", adapter.getRef(position).getKey() );
                            startActivity(foodIntent);
                        }
                    });

                    // Add Favorite
                if (localDB.isFavorite(adapter.getRef(position).getKey()))
                    viewHolder.imgFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);

                //Click thay đổi trạng thái Favorite
                viewHolder.imgFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!localDB.isFavorite(adapter.getRef(position).getKey())){
                            localDB.addFavorite(adapter.getRef(position).getKey());
                            viewHolder.imgFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodListActivity.this, ""+model.getName()+" was added to Favorites", Toast.LENGTH_SHORT).show();
                        }else {
                            localDB.removeFavorite(adapter.getRef(position).getKey());
                            viewHolder.imgFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FoodListActivity.this, ""+model.getName()+" was remove from Favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }

            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(itemView);
            }
        };
//
        adapter.notifyDataSetChanged();
        adapter.startListening();
        recyclerViewFood.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        searchAdapter.startListening();
        adapter.startListening();
        if (searchAdapter!=null)
            searchAdapter.stopListening();

    }
}
