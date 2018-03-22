package com.tongminhnhut.orderfood_demo;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tongminhnhut.orderfood_demo.Common.Common;
import com.tongminhnhut.orderfood_demo.ViewHolder.CashViewHolder;
import com.tongminhnhut.orderfood_demo.model.Requests;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CashActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase mData ;
    DatabaseReference tab_request, cash ;
    String id_table = "";

    FButton btnCash ;
    TextView txtTotal ;
    FirebaseRecyclerAdapter<Requests, CashViewHolder> adapter ;
    Requests newRequest ;
    ViewGroup parent ;

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


        setContentView(R.layout.activity_cash);
        mData = FirebaseDatabase.getInstance();
        tab_request = mData.getReference("Requests");
        cash = mData.getReference("Cash");

        recyclerView = findViewById(R.id.recylerCash);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        btnCash = findViewById(R.id.btnThanhToan_Cash);

        if (getIntent() != null)
            id_table = getIntent().getStringExtra("Table");
        if (!id_table.isEmpty() && id_table!=null){
            loadOrder(id_table);
        }

        tab_request.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int total = 0;
                Requests a = dataSnapshot.getValue(Requests.class);
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
//                    total += a;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        txtTotal = findViewById(R.id.txtTotal_cash);
//        int total = 0;
//        List<Requests> requests = new FirebaseRecyclerAdapter<Requests, CashViewHolder>();
//        for (Requests item:requests){
//            total += Integer.parseInt(item.getTotal());
//        }
//        txtTotal.setText(total+"");

        btnCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



//                tab_request.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        tab_request.child(adapter.getRef(Integer.parseInt(id_table)).getKey()).removeValue();
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
            }
        });


    }

    private void loadOrder(String idTable) {
        Query query = cash.orderByChild("address").equalTo(idTable);
        FirebaseRecyclerOptions<Requests> options = new FirebaseRecyclerOptions.Builder<Requests>()
                .setQuery(query, Requests.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Requests, CashViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CashViewHolder viewHolder, final int position, @NonNull final Requests model) {
                viewHolder.txtBan.setText("Bàn số "+model.getAddress());
                viewHolder.txtPhone.setText(model.getPhone());
                viewHolder.txtStatus.setText(Common.convertCodeStatus(""+model.getStatus()));
                viewHolder.txtTotal.setText(model.getTotal());
                viewHolder.txtDate.setText(model.getDate());



                viewHolder.btnDetail_Cash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), CashDetailActivity.class);
                        Common.currentRequest=model;
                        intent.putExtra("Detail", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });

                viewHolder.btnXoa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleDialog(adapter.getRef(position).getKey());
                    }
                });
            }

            @Override
            public CashViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cash_item, parent, false);
                return new CashViewHolder(view);
            }
        };


        adapter.notifyDataSetChanged();
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void deleDialog(String key) {
        cash.child(key).removeValue();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.startListening();
    }
}
