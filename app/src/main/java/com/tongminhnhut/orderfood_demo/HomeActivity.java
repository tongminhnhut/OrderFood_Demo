package com.tongminhnhut.orderfood_demo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.tongminhnhut.orderfood_demo.Common.Common;
import com.tongminhnhut.orderfood_demo.Database.Database;
import com.tongminhnhut.orderfood_demo.Interface.ItemClickListener;
import com.tongminhnhut.orderfood_demo.Service.ListenServer;
import com.tongminhnhut.orderfood_demo.ViewHolder.MenuViewHolder;
import com.tongminhnhut.orderfood_demo.model.Banner;
import com.tongminhnhut.orderfood_demo.model.Category;
import com.tongminhnhut.orderfood_demo.model.Token;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    CounterFab btnCart;
    FirebaseDatabase firebaseDatabase ;
    DatabaseReference tab_cate;
    TextView txtFullname ;

    RecyclerView recyclerView ;
    RecyclerView.LayoutManager layoutManager ;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    SwipeRefreshLayout swipeRefreshLayout ;

    HashMap<String, String> list_image ;
    SliderLayout sliderLayout ;

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


        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        Paper.init(this);

        // init Firsbase

        firebaseDatabase = FirebaseDatabase.getInstance();
        tab_cate = firebaseDatabase.getReference("Category");





        btnCart = findViewById(R.id.btnCart_Home);



        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedInternet(HomeActivity.this)){
                    loadMenu();
                }else {
                    Toast.makeText(HomeActivity.this, "Vui lòng kiểm tra internet", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedInternet(HomeActivity.this)){
                    loadMenu();
                }else {
                    Toast.makeText(HomeActivity.this, "Vui lòng kiểm tra internet", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });





        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication(), CartActivity.class));
            }
        });
        btnCart.setCount(new Database(this).getCountCart());


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set name for User
        View headerView = navigationView.getHeaderView(0);
        txtFullname = headerView.findViewById(R.id.txtFullName);
        txtFullname.setText(Common.curentUser.getName());


        // Load Menu
        recyclerView = findViewById(R.id.recyler_menu);
        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),
                R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);


        // check internet for load Menu
        if (Common.isConnectedInternet(getApplicationContext())){
            loadMenu();
        }else {
            Toast.makeText(this, "Vui lòng kiểm internet !", Toast.LENGTH_SHORT).show();
            return;
        }


        Intent server = new Intent(getApplicationContext(), ListenServer.class);
        startService(server);

//        updateToken(FirebaseInstanceId.getInstance().getToken());

        //Slider
        setupSlider();
    }

    private void setupSlider() {
        sliderLayout = findViewById(R.id.slider_banner);
        list_image = new HashMap<>();

        final DatabaseReference banner = FirebaseDatabase.getInstance().getReference("Banner");

        banner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postDataSnapShot:dataSnapshot.getChildren()){
                    Banner banner1 = postDataSnapShot.getValue(Banner.class);

                    list_image.put(banner1.getName()+"_"+banner1.getId(), banner1.getImage());

                }

                for (String key:list_image.keySet()){
                    String[] keySplit = key.split("_");
                    String nameOfFood = keySplit[0];
                    String idOfFood = keySplit[1];

//                    TextSliderView

                    final TextSliderView textSliderView = new TextSliderView(getBaseContext());
                    textSliderView.description(nameOfFood)
                            .image(list_image.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent = new Intent(getApplicationContext(), FoodDetailActivity.class);
                                    intent.putExtras(textSliderView.getBundle());
                                    startActivity(intent);
                                }
                            });

                    // Add Extras Bundle
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("IDFood", idOfFood);

                    sliderLayout.addSlider(textSliderView);

                    // remove event after finish
                    banner.removeEventListener(this);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnCart.setCount(new Database(this).getCountCart());
    }

    private void updateToken(String token) {
        FirebaseDatabase mData = FirebaseDatabase.getInstance();
        DatabaseReference tokens = mData.getReference("Tokens");
        Token data = new Token(token, false); // false because token send from Client App
        tokens.child(Common.curentUser.getPhone()).setValue(data);
    }

    private void loadMenu() {

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(tab_cate, Category.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, int position, @NonNull Category model) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imgHinh);
                final Category itemClick = model ;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
//                        Toast.makeText(HomeActivity.this, ""+itemClick.getName(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), FoodListActivity.class);
                        intent.putExtra("ID",adapter.getRef(position).getKey() );
                        startActivity(intent);
                    }
                });
            }

            @Override
            public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();



    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.startListening();
        sliderLayout.stopAutoCycle();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {
            startActivity(new Intent(getApplicationContext(), CartActivity.class));
        } else if (id == R.id.nav_Logout) {
            // delete User & pass
            Paper.book().destroy();
            Intent logoutItent = new Intent(getApplicationContext(), SignInActivity.class);
            logoutItent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutItent);
        } else if (id == R.id.nav_order) {
            startActivity(new Intent(getApplicationContext(), OrderStatusActivity.class));
        } else if (id == R.id.nav_table){
            startActivity(new Intent(getApplicationContext(), TableActivity.class));
        }else if (id == R.id.nav_changepass){
            showChangePassDialog();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showChangePassDialog() {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle("CHANGE PASSWORD");
        mDialog.setMessage("Vui lòng điền đầy đủ thông tin !");

        LayoutInflater inflater = LayoutInflater.from(this);
        View chang_layout = inflater.inflate(R.layout.change_password_layout, null);

        final MaterialEditText edtPass = chang_layout.findViewById(R.id.edtPass_ChangePass);
        final MaterialEditText edtRepass = chang_layout.findViewById(R.id.edtRePass_ChangePass);
        final MaterialEditText edtNewpass = chang_layout.findViewById(R.id.edtNewPass_ChangePass);

        mDialog.setView(chang_layout);

        mDialog.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // AlertDialog sử dụng thư viện android.app
                final android.app.AlertDialog waitingDialog = new SpotsDialog(HomeActivity.this);
                waitingDialog.show();

                if (edtPass.getText().toString().equals(Common.curentUser.getPassword())){
                    if (edtNewpass.getText().toString().equals(edtRepass.getText().toString())){
                        Map<String, Object> passUpdate = new HashMap<>();
                        passUpdate.put("password", edtNewpass.getText().toString());

                        DatabaseReference user = FirebaseDatabase.getInstance().getReference("User");
                        user.child(Common.curentUser.getPhone())
                                .updateChildren(passUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        waitingDialog.dismiss();
                                        Toast.makeText(HomeActivity.this, "Thay đổi password thành công", Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        waitingDialog.dismiss();
                        Toast.makeText(HomeActivity.this, "Repeat Password không khớp", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    waitingDialog.dismiss();
                    Toast.makeText(HomeActivity.this, "Sai mật khẩu ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mDialog.show();
    }
}
