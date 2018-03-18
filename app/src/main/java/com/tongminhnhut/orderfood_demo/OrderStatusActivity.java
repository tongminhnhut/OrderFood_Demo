package com.tongminhnhut.orderfood_demo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.tongminhnhut.orderfood_demo.Common.Common;
import com.tongminhnhut.orderfood_demo.Interface.ItemClickListener;
import com.tongminhnhut.orderfood_demo.ViewHolder.OrderViewHolder;
import com.tongminhnhut.orderfood_demo.model.Requests;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderStatusActivity extends AppCompatActivity {
    RecyclerView recyclerView ;
    RecyclerView.LayoutManager layoutManager ;

    FirebaseRecyclerAdapter<Requests, OrderViewHolder> adapter ;
    FirebaseDatabase database ;
    DatabaseReference request ;


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

        setContentView(R.layout.activity_order_status);

        database=FirebaseDatabase.getInstance();
        request = database.getReference("Requests");

        recyclerView = findViewById(R.id.recylerOrderStatus);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (Common.isConnectedInternet(getApplicationContext())) {
            loadOrderStatus(Common.curentUser.getPhone());
        }else {
            Toast.makeText(this, "Vui lòng kiểm internet !", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadOrderStatus(String phone) {
        Query query = request.orderByChild("phone")
                .equalTo(phone);
        FirebaseRecyclerOptions<Requests> options = new FirebaseRecyclerOptions.Builder<Requests>()
                .setQuery(query, Requests.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Requests, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, int position, @NonNull Requests model) {
                viewHolder.txtTen.setText("Bàn số "+model.getAddress());
                viewHolder.txtStatus.setText(Common.convertCodeStatus(model.getStatus()+""));
                viewHolder.txtPhone.setText(model.getPhone());
                viewHolder.txtAddress.setText(model.getAddress());
            }

            @Override
            public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
                return new OrderViewHolder(view);
            }
        };

       adapter.notifyDataSetChanged();
       adapter.startListening();
       recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
