package com.tongminhnhut.orderfood_demo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tongminhnhut.orderfood_demo.Common.Common;
import com.tongminhnhut.orderfood_demo.model.User;

import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    Button btnSignUp, btnSignIn;
    TextView txtLogan;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference tab_user;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set Default font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("restaurant_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setContentView(R.layout.activity_main);
        firebaseDatabase = FirebaseDatabase.getInstance();
        tab_user = firebaseDatabase.getReference("User");
        initView();
        addEvents();
        Paper.init(this);
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PMW_KEY);
        if (user !=null && pwd !=null){
            if (!user.isEmpty() && !pwd.isEmpty()){
                login(user,pwd);
            }
        }



    }

    private void addEvents() {
//        Typeface typeface = Typeface.createFromAsset(getAssets(), "Barmbrack.ttf");
//        txtLogan.setTypeface(typeface);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplication(), SignUpActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplication(), SignInActivity.class));
            }
        });

        // check remember


    }

    private void login(final String phone, final String pwd) {
        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Loading . . .");
        if (Common.isConnectedInternet(getBaseContext())) {

            dialog.show();
            tab_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(phone).exists() ){
                        dialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);
                        if (user.getPassword().equals(pwd)) {
//                                dialog.dismiss();
//                                Toast.makeText(getApplicationContext(), "Sign In Successfully !", Toast.LENGTH_SHORT).show();
                            Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                            Common.curentUser = user;
                            startActivity(homeIntent);
                            finish();

                        } else {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Sign In failed !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed !!!", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            Toast.makeText(MainActivity.this, "Vui lòng kiểm tra internet !", Toast.LENGTH_SHORT).show();
            return;
        }


    }

    private void initView() {
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);
    }
}
