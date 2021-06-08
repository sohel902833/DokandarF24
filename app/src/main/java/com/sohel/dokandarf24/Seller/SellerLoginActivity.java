package com.sohel.dokandarf24.Seller;

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
import com.sohel.dokandarf24.Rider.RiderLoginActivity;
import com.sohel.dokandarf24.Rider.RiderMainActivity;
import com.sohel.dokandarf24.Seller.Model.SellerModel;
import com.sohel.dokandarf24.User.UserLoginActivity;
import com.sohel.dokandarf24.User.UserRegisterActivity;

public class SellerLoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText phoneEt,passwordEt;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private TextView registerLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        init();


        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerLoginActivity.this, SellerRegisterActivity.class));
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
    private void init() {
        toolbar=findViewById(R.id.appBariId);
        setSupportActionBar(toolbar);
        this.setTitle("Login Seller");

        registerLink=findViewById(R.id.register_link);
        loginButton=findViewById(R.id.loginButton);
        phoneEt=findViewById(R.id.phoneEt);
        passwordEt=findViewById(R.id.passwordEt);
        progressBar=findViewById(R.id.progressBar);
    }
    private void showError(EditText editText, String message){
        editText.setError(message);
        editText.requestFocus();
    }

    private void checkUserExists(String phone, String password) {
        progressBar.setVisibility(View.VISIBLE);

        ApiKey.getSellerRef().child(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            SellerModel sellerModel=snapshot.getValue(SellerModel.class);
                            if(sellerModel.getPhone().equals(phone) && sellerModel.getPassword().equals(password)){
                                progressBar.setVisibility(View.GONE);
                                UserDb userDb=new UserDb(SellerLoginActivity.this);
                                userDb.setUserType("seller");
                                userDb.setSellerData(sellerModel);
                                sendUserToSellerMainActivity();
                                Toast.makeText(SellerLoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                            }else{
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SellerLoginActivity.this, "Phone or Password is Wrong", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SellerLoginActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);

                    }
                });

    }

    private void sendUserToSellerMainActivity() {
        startActivity(new Intent(SellerLoginActivity.this, SellerMainActivity.class));
        finish();
    }

    
}