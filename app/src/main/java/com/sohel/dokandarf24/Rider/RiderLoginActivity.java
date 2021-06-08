package com.sohel.dokandarf24.Rider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sohel.dokandarf24.Local.ApiKey.ApiKey;
import com.sohel.dokandarf24.Local.Database.UserDb;
import com.sohel.dokandarf24.R;
import com.sohel.dokandarf24.Rider.Model.RiderModel;
import com.sohel.dokandarf24.Seller.SellerLoginActivity;
import com.sohel.dokandarf24.Seller.SellerRegisterActivity;
import com.sohel.dokandarf24.User.Model.UserModel;
import com.sohel.dokandarf24.User.UserLoginActivity;
import com.sohel.dokandarf24.User.UserMainActivity;

public class RiderLoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText phoneEt,passwordEt;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private TextView registerLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_login);
        init();

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RiderLoginActivity.this, RiderRegisterActivity.class));
                finish();
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone=phoneEt.getText().toString().trim();
                String password=passwordEt.getText().toString().trim();
                if(phone.isEmpty()){
                    showError(phoneEt,"Please enter Your Phone Number with country Code.");
                }else if(password.isEmpty()){
                    showError(passwordEt,"Please Enter Password");
                }else{
                    checkUserExists(phone,password);
                }
            }
        });


    }
    private void showError(EditText editText, String message){
        editText.setError(message);
        editText.requestFocus();
    }
    private void init() {
        toolbar=findViewById(R.id.appBariId);
        setSupportActionBar(toolbar);
        this.setTitle("Login Rider");
        registerLink=findViewById(R.id.register_link);
        loginButton=findViewById(R.id.loginButton);
        phoneEt=findViewById(R.id.phoneEt);
        passwordEt=findViewById(R.id.passwordEt);
        progressBar=findViewById(R.id.progressBar);
    }

    private void checkUserExists(String phone, String password) {
        progressBar.setVisibility(View.VISIBLE);

        ApiKey.getRiderRef().child(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            RiderModel riderModel=snapshot.getValue(RiderModel.class);
                            if(riderModel.getPhone().equals(phone) && riderModel.getPassword().equals(password)){
                                progressBar.setVisibility(View.GONE);
                                UserDb userDb=new UserDb(RiderLoginActivity.this);
                                userDb.setUserType("rider");
                                userDb.setRiderData(riderModel);
                                sendUserToRiderMainActivity();
                                Toast.makeText(RiderLoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                            }else{
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(RiderLoginActivity.this, "Phone or Password is Wrong", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RiderLoginActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);

                    }
                });

    }

    private void sendUserToRiderMainActivity() {
        startActivity(new Intent(RiderLoginActivity.this, RiderMainActivity.class));
        finish();
    }
}