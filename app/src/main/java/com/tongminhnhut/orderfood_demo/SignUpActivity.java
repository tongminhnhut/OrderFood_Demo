package com.tongminhnhut.orderfood_demo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tongminhnhut.orderfood_demo.Common.Common;
import com.tongminhnhut.orderfood_demo.model.User;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUpActivity extends AppCompatActivity {
    MaterialEditText edtPhonenumber, edtFullname, edtPass, edtSecureCode;
    Button btnSignUp ;
    FirebaseDatabase mData ;
    DatabaseReference tab_User ;

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


        setContentView(R.layout.activity_sign_up);
        mData = FirebaseDatabase.getInstance();
        tab_User = mData.getReference("User");
        initView();
        addEvents();
    }

    private void addEvents() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedInternet(getBaseContext())) {

                    final ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);
                    dialog.setMessage("Loading . . .");
                    dialog.show();
                    tab_User.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(edtPhonenumber.getText().toString()).exists()) {
                                dialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "Phone number already ragister!", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.dismiss();
                                User user = new User(edtFullname.getText().toString().trim(),
                                        edtPass.getText().toString().trim(),
                                        edtSecureCode.getText().toString().trim());
                                tab_User.child(edtPhonenumber.getText().toString()).setValue(user);
                                Toast.makeText(SignUpActivity.this, "Sign Up successfully !", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else {
                    Toast.makeText(SignUpActivity.this, "Vui lòng kiểm tra internet !", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void initView() {
        edtPhonenumber = findViewById(R.id.edtPhoneNumber_SignUp);
        edtFullname = findViewById(R.id.edtFullName_SignUp);
        edtPass = findViewById(R.id.edtPass_SignUp);
        edtSecureCode = findViewById(R.id.edtSecureCode_SignUp);
        btnSignUp = findViewById(R.id.btnSignUp_SignUp);

    }
}
