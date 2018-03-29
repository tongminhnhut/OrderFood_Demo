package com.tongminhnhut.orderfood_demo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.tongminhnhut.orderfood_demo.Common.Common;
import com.tongminhnhut.orderfood_demo.Interface.ItemClickListener;
import com.tongminhnhut.orderfood_demo.ViewHolder.OrderViewHolder;
import com.tongminhnhut.orderfood_demo.ViewHolder.TableViewHolder;
import com.tongminhnhut.orderfood_demo.model.Requests;
import com.tongminhnhut.orderfood_demo.model.Table_Status;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TableActivity extends AppCompatActivity {
    RecyclerView recyclerView ;
    RecyclerView.LayoutManager layoutManager ;
    FirebaseRecyclerAdapter<Table_Status, TableViewHolder> adapter ;
    FirebaseDatabase mData ;
    DatabaseReference table ;
    MaterialSpinner spinner ;
    ImageButton btnDate, btnTime ;
    TextView txtDate, txtTime ;
    View view ;
    String idTable = "" ;

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

        setContentView(R.layout.activity_table);

        mData = FirebaseDatabase.getInstance() ;
        table = mData.getReference("Table");

        recyclerView = findViewById(R.id.recylerTab);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        loadTable();
    }

    private void loadTable() {
        FirebaseRecyclerOptions<Table_Status> options = new FirebaseRecyclerOptions.Builder<Table_Status>()
                .setQuery(table, Table_Status.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Table_Status, TableViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final TableViewHolder viewHolder, final int position, @NonNull Table_Status model) {
                viewHolder.txtBan.setText("Bàn số "+adapter.getRef(position).getKey());
                viewHolder.txtStatus.setText(Common.convertStatusTable(model.getStatus()+""));
                viewHolder.txtNgay.setText(model.getNgay()+"");
                viewHolder.txtGio.setText(model.getGio()+"");

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(getApplicationContext(), CashActivity.class);
//                        Common.currentRe = model ;
                        intent.putExtra("Table", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
                final DatabaseReference cash_tab = FirebaseDatabase.getInstance().getReference("Table");

                viewHolder.btncash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.btncash.setText("Complete");
                        adapter.getItem(position).setCash_Status("1");
                        adapter.notifyDataSetChanged(); // Them vao để cập nhật item size
                        cash_tab.child(adapter.getRef(position).getKey()).setValue(adapter.getItem(position));

                    }
                });
                viewHolder.btnReset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.getItem(position).setCash_Status("0");
                        adapter.notifyDataSetChanged(); // Them vao để cập nhật item size
                        cash_tab.child(adapter.getRef(position).getKey()).setValue(adapter.getItem(position));

                    }
                });
            }

            @Override
            public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_item, parent, false);

                return new TableViewHolder(item);
            }
        };


        adapter.notifyDataSetChanged();
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)){
            showUpDateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        return super.onContextItemSelected(item);
    }

    private void showUpDateDialog(final String key, final Table_Status item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Cập nhật trạng thái");
        alertDialog.setMessage("Vui lòng cập nhật lại thông tin");

        LayoutInflater inflater = this.getLayoutInflater();

        view = inflater.inflate(R.layout.table_update, null);

        btnDate = view.findViewById(R.id.btnDate);
        btnTime = view.findViewById(R.id.btnTime);
        txtDate = view.findViewById(R.id.txtNgay_update);
        txtTime = view.findViewById(R.id.txtGio_Update);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate();

            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTime();
            }
        });


        spinner = view.findViewById(R.id.spinnerStatus);
        spinner.setItems("Bàn còn trống", "Bàn đã đặt", "Bàn VIP");

        alertDialog.setView(view);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
                item.setNgay(txtDate.getText().toString()+"");
                item.setGio(txtTime.getText().toString()+"");
                if (spinner.getSelectedIndex()==1){

                }

                table.child(key).setValue(item);
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void chooseTime() {
        final Calendar calendar = Calendar.getInstance();
        int gio = calendar.get(Calendar.HOUR_OF_DAY);
        int phut = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(0,0,0,hourOfDay,minute);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                txtTime.setText(String.valueOf(sdf.format(calendar.getTime())));
            }
        },gio, phut, true);
        timePickerDialog.show();
    }

    private void chooseDate() {
        final Calendar calendar = Calendar.getInstance();
        int ngay = calendar.get(Calendar.DATE);
        int thang = calendar.get(Calendar.MONTH);
        int nam = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year,month,dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                txtDate.setText(String.valueOf(simpleDateFormat.format(calendar.getTime())));

            }
        },nam,thang,ngay);
        datePickerDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.startListening();
    }
}
