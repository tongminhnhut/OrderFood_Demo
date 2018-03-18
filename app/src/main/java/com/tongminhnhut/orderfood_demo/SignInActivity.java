package com.tongminhnhut.orderfood_demo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;
import com.tongminhnhut.orderfood_demo.Common.Common;
import com.tongminhnhut.orderfood_demo.model.User;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignInActivity extends AppCompatActivity {
    MaterialEditText edtPhone, edtPass;
    Button btnSignIn ;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference tab_user;
    CheckBox cb ;
    TextView txtForgot ;


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

        setContentView(R.layout.activity_sign_in);
        Paper.init(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        tab_user = firebaseDatabase.getReference("User");
        initView();
        addEvents();

    }
    private void addEvents() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = new SpotsDialog(SignInActivity.this, "Loading . . .");

                if (Common.isConnectedInternet(getBaseContext())) {

                    if (cb.isChecked()){
                        Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                        Paper.book().write(Common.PMW_KEY, edtPass.getText().toString());
                    }

                    dialog.show();
                    tab_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child(edtPhone.getText().toString().trim()).exists()) {
                                dialog.dismiss();
                                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                user.setPhone(edtPhone.getText().toString());
                                if (user.getPassword().equals(edtPass.getText().toString().trim())) {
//                                dialog.dismiss();
//                                Toast.makeText(getApplicationContext(), "Sign In Successfully !", Toast.LENGTH_SHORT).show();
                                    Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                                    Common.curentUser = user;
                                    startActivity(homeIntent);
                                    finish();

                                    tab_user.removeEventListener(this);

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
                    Toast.makeText(SignInActivity.this, "Vui lòng kiểm tra internet !", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        txtForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotDialog();
            }
        });
    }

    private void showForgotDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Forgot Password");
        alertDialog.setMessage("Enter your secure code");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgot_view = inflater.inflate(R.layout.forgot_pass_item, null);

        alertDialog.setView(forgot_view);
        alertDialog.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText edtPhone = forgot_view.findViewById(R.id.edtPhoneNumber_forgotItem);
        final MaterialEditText edtSecure = forgot_view.findViewById(R.id.edtSecureCode_forgotItem);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tab_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child(edtPhone.getText().toString())
                                .getValue(User.class);

                        if (user.getSecureCode().equals(edtSecure.getText().toString())){
                            Toast.makeText(SignInActivity.this, "Your password :"+ user.getPassword(), Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(SignInActivity.this, "Wrong secure code", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();


    }

    private void initView() {
        txtForgot = findViewById(R.id.txtForgotPass);
        cb = findViewById(R.id.cbRemember);
        edtPhone = findViewById(R.id.edtPhonenumber_SignIp);
        edtPass = findViewById(R.id.edtPass_SignIp);
        btnSignIn = findViewById(R.id.btnSignIn_SignIp);
    }
}
