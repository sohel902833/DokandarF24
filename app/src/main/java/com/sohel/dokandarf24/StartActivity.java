package com.sohel.dokandarf24;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sohel.dokandarf24.Local.Database.UserDb;
import com.sohel.dokandarf24.Rider.RiderLoginActivity;
import com.sohel.dokandarf24.Rider.RiderMainActivity;
import com.sohel.dokandarf24.Seller.SellerLoginActivity;
import com.sohel.dokandarf24.Seller.SellerMainActivity;
import com.sohel.dokandarf24.User.UserLoginActivity;
import com.sohel.dokandarf24.User.UserMainActivity;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Button sellerButton,userButton,riderButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
            init();
            sellerButton.setOnClickListener(this);
            userButton.setOnClickListener(this);
            riderButton.setOnClickListener(this);
    }

    private void init() {

        toolbar=findViewById(R.id.appBariId);
        setSupportActionBar(toolbar);
        this.setTitle("dokander24");
        //<------------Layout Finidg-------------->

        sellerButton=findViewById(R.id.sellerButtonid);
        userButton=findViewById(R.id.customerButtonid);
        riderButton=findViewById(R.id.riderButtonid);


    }

    @Override
    protected void onStart() {
        super.onStart();
        UserDb userDb=new UserDb(StartActivity.this);
        String userType=userDb.getUserType();
        if(userType.equals("user")){
            senduserToUserMainActivity();
        }  if(userType.equals("seller")){
            sendUserToSellerMainActivity();
        }  if(userType.equals("rider")){
            sendUserToRiderMainActivity();
        }
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.sellerButtonid){
            sendUserToSellerLoginActivity();
        }
        if(v.getId()==R.id.customerButtonid){
            sendUserToUserLoginActivity();
        }
        if(v.getId()==R.id.riderButtonid){
            sendUserToRiderLoginActivity();
        }
    }
    private void sendUserToSellerLoginActivity() {
            startActivity(new Intent(StartActivity.this, SellerLoginActivity.class));

    }
    private void sendUserToUserLoginActivity() {
            startActivity(new Intent(StartActivity.this, UserLoginActivity.class));

    }
    private void senduserToUserMainActivity() {
            startActivity(new Intent(StartActivity.this, UserMainActivity.class));
            finish();
    }  private void sendUserToSellerMainActivity() {
            startActivity(new Intent(StartActivity.this, SellerMainActivity.class));
            finish();
    } private void sendUserToRiderMainActivity() {
            startActivity(new Intent(StartActivity.this, RiderMainActivity.class));
            finish();
    }
    private void sendUserToRiderLoginActivity() {
            startActivity(new Intent(StartActivity.this, RiderLoginActivity.class));

    }
}