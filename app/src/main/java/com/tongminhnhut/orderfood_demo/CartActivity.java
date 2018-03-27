package com.tongminhnhut.orderfood_demo;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tongminhnhut.orderfood_demo.Common.Common;
import com.tongminhnhut.orderfood_demo.Database.Database;
import com.tongminhnhut.orderfood_demo.Helper.RecylerItemTouchHelper;
import com.tongminhnhut.orderfood_demo.Interface.RecylerItemTouchHeplerListener;
import com.tongminhnhut.orderfood_demo.Remote.APIService;
import com.tongminhnhut.orderfood_demo.ViewHolder.CartAdapter;
import com.tongminhnhut.orderfood_demo.ViewHolder.CartViewHolder;
import com.tongminhnhut.orderfood_demo.model.MyResponse;
import com.tongminhnhut.orderfood_demo.model.Notification;
import com.tongminhnhut.orderfood_demo.model.Order;
import com.tongminhnhut.orderfood_demo.model.Requests;
import com.tongminhnhut.orderfood_demo.model.Sender;
import com.tongminhnhut.orderfood_demo.model.Token;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CartActivity extends AppCompatActivity implements RecylerItemTouchHeplerListener {
    RecyclerView recyclerView ;
    public TextView txtTotal ;
    FButton btnOrder ;
    FirebaseDatabase mData ;
    DatabaseReference request ;
    RecyclerView.LayoutManager layoutManager ;
    List<Order> orderList = new ArrayList<>();
    CartAdapter adapter ;
    View view ;

    APIService mService ;

    RelativeLayout relativeLayout ;
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

        setContentView(R.layout.activity_cart);
        mData = FirebaseDatabase.getInstance();
        request = mData.getReference("Requests");

        mService = Common.getFCMService();

        recyclerView = findViewById(R.id.recylerCart);
        txtTotal =findViewById(R.id.txtTotal);
        btnOrder =findViewById(R.id.btnOrder);

        //init Layout
        relativeLayout = findViewById(R.id.relativeLayout_Cart);

        // init RecylerView
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Swiped
        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecylerItemTouchHelper(0, ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);



        loadListFood();


        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });


    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Your Order");
        alertDialog.setMessage("Nhập vào bàn order");


        LayoutInflater inflater = this.getLayoutInflater();

        view = inflater.inflate(R.layout.cart_dialog, null);
        alertDialog.setView(view);

        final EditText ban = view.findViewById(R.id.edtBanso_cartdialog);
        final EditText note = view.findViewById(R.id.edtNote_cartdialog);

        //Calender
        final Calendar calendar = Calendar.getInstance();
        int ngay = calendar.get(Calendar.DATE);
        int thang = calendar.get(Calendar.MONTH);
        int nam = calendar.get(Calendar.YEAR);
        calendar.set(ngay, thang, nam);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        //set Time
        int gio = calendar.get(Calendar.HOUR_OF_DAY);
        int phut = calendar.get(Calendar.MINUTE);
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        calendar.set(0,0,0,gio,phut);

        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Requests requests = new Requests(
                        Common.curentUser.getPhone(),
                        Common.curentUser.getName(),
                        ban.getText().toString(),
                        txtTotal.getText().toString(),
                        note.getText().toString(),
                        String.valueOf(simpleDateFormat.format(calendar.getTime())),
                        String.valueOf(sdf.format(calendar.getTime())),
                        orderList
                );

                String order_number = String.valueOf(System.currentTimeMillis());
                DatabaseReference cash = FirebaseDatabase.getInstance().getReference("Cash");
                cash.child(order_number).setValue(requests);
                request.child(order_number).setValue(requests);
                new Database(CartActivity.this).cleanCart();

                sendNotificationOrder(order_number);


//                Toast.makeText(CartActivity.this, "Thank you!"+"\n"+ "Order Success !!!", Toast.LENGTH_SHORT).show();
//                finish();
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

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("serverToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postDataSnapShot:dataSnapshot.getChildren()){
                    Token serverToken = postDataSnapShot.getValue(Token.class);

                    //Create
                    Notification notification = new Notification("Polaris VN", "You have new order " +order_number);
                    Sender content = new Sender(serverToken.getToken(), notification);

                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                        if (response.body().success == 1){
                                            Toast.makeText(CartActivity.this, "Thank you. Order Success", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }else {
                                            Toast.makeText(CartActivity.this, "Failed !!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }



                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR", t.getMessage());

                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadListFood() {
        orderList = new Database(this).getCart();
        adapter = new CartAdapter(orderList,this);
        recyclerView.setAdapter(adapter);

        // Tính tông giá
        int total = 0 ;
        for (Order order:orderList){
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuanlity()));
        }
//        Locale locale = new Locale("vn", "VN");
//        NumberFormat fm = NumberFormat.getCurrencyInstance(locale);
        DecimalFormat fm = new DecimalFormat("#,###,###");
        txtTotal.setText(fm.format(total));


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)){
//            showUpDateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int positon) {
        if (viewHolder instanceof CartViewHolder){
            String name = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();
            final Order deleteItem = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());

            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
//            new Database(getBaseContext()).removeFromCart(deleteItem.getProductID());

            // Tính tông giá
            int total = 0 ;
            List<Order> orders = new Database(getBaseContext()).getCart();
            for (Order item:orders){
                total += (Integer.parseInt(deleteItem.getPrice())) * (Integer.parseInt(item.getQuanlity()));
            }
            Locale locale = new Locale("vn", "VN");
            NumberFormat fm = NumberFormat.getCurrencyInstance(locale);
            txtTotal.setText(fm.format(total));

            Snackbar snackBar = Snackbar.make(relativeLayout, name + " removed from Cart", Snackbar.LENGTH_LONG);
            snackBar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addToCart(deleteItem);
                    // Tính tông giá
                    int total = 0 ;
                    List<Order> orders = new Database(getBaseContext()).getCart();
                    for (Order item:orders){
                        total += (Integer.parseInt(deleteItem.getPrice())) * (Integer.parseInt(item.getQuanlity()));
                    }
                    Locale locale = new Locale("vn", "VN");
                    NumberFormat fm = NumberFormat.getCurrencyInstance(locale);
                    txtTotal.setText(fm.format(total));

                }
            });
            snackBar.setActionTextColor(Color.YELLOW);
            snackBar.show();

        }
    }
}
