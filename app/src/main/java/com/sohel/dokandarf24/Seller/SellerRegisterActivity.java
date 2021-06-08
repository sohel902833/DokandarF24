package com.sohel.dokandarf24.Seller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sohel.dokandarf24.Local.ApiKey.ApiKey;
import com.sohel.dokandarf24.Local.Database.UserDb;
import com.sohel.dokandarf24.Local.Model.LocationModel;
import com.sohel.dokandarf24.MapsActivity;
import com.sohel.dokandarf24.R;
import com.sohel.dokandarf24.Rider.Model.RiderModel;
import com.sohel.dokandarf24.Rider.RiderLoginActivity;
import com.sohel.dokandarf24.Rider.RiderRegisterActivity;
import com.sohel.dokandarf24.Seller.Model.SellerModel;

import org.w3c.dom.Text;

public class SellerRegisterActivity extends AppCompatActivity {

    private Button locationSelectButton,registerButton;
    private EditText nameEt,phoneEt,passwordEt,riderIdEt;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    private TextView login_link;

    private double lat=0,lng=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_register);
        init();

        login_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerRegisterActivity.this, SellerLoginActivity.class));
                finish();
            }
        });

        locationSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SellerRegisterActivity.this, MapsActivity.class);
                startActivityForResult(intent,1212);
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name =nameEt.getText().toString();
                String phone=phoneEt.getText().toString().trim();
                String password=passwordEt.getText().toString().trim();
                String riderPhone=riderIdEt.getText().toString().trim();

                if(lat==0 && lng==0){
                    Toast.makeText(SellerRegisterActivity.this, "Please Select Your Location", Toast.LENGTH_SHORT).show();
                }
                else if(name.isEmpty()){
                    showError(nameEt,"Write Your Name");
                } else if(riderPhone.isEmpty()){
                    showError(riderIdEt,"Write Rider Phone");
                }else if(phone.isEmpty()){
                    showError(phoneEt,"Please enter Your Phone Number");
                }else if(password.isEmpty()){
                    showError(passwordEt,"Please Enter Password");
                }else{
                    checkRiderExists(name,phone,password,riderPhone);
                }



            }
        });





    }
    private void init() {
        toolbar=findViewById(R.id.appBariId);
        setSupportActionBar(toolbar);
        this.setTitle("Register Seller");


        login_link=findViewById(R.id.login_link);
        locationSelectButton=findViewById(R.id.locationSelectBt);
        registerButton=findViewById(R.id.registerButton);
        nameEt=findViewById(R.id.nameEt);
        phoneEt=findViewById(R.id.phoneEt);
        riderIdEt=findViewById(R.id.riderIdEt);
        passwordEt=findViewById(R.id.passwordEt);
        progressBar=findViewById(R.id.progressBar);

    }
    private void showError(EditText editText, String message){
        editText.setError(message);
        editText.requestFocus();
    }

    private void checkUserExists(String name, String phone, String password,String riderPhone) {
        progressBar.setVisibility(View.VISIBLE);

        ApiKey.getSellerRef().child(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SellerRegisterActivity.this, "This Phone Number Have Already An Account", Toast.LENGTH_SHORT).show();
                        }else{
                            registerUser(name,phone,password,riderPhone);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);

                    }
                });


    }


    private void checkRiderExists(String name, String phone, String password,String riderPhone) {
        progressBar.setVisibility(View.VISIBLE);

        ApiKey.getRiderRef().child(riderPhone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            checkUserExists(name,phone,password,riderPhone);
                        }else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SellerRegisterActivity.this, "Rider Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);

                    }
                });


    }

    private void registerUser(String name, String phone, String password, String riderPhone) {
        LocationModel locationModel=new LocationModel(lat,lng);
        SellerModel sellerModel=new SellerModel(
            name,phone,password,"none","none",riderPhone,0,locationModel
                );
        ApiKey.getSellerRef().child(phone)
                .setValue(sellerModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            UserDb userDb=new UserDb(SellerRegisterActivity.this);
                            userDb.setUserType("seller");
                            userDb.setSellerData(sellerModel);
                            sendUserToSellerMainActivity();
                        }else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SellerRegisterActivity.this, "Register Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void sendUserToSellerMainActivity() {
        startActivity(new Intent(SellerRegisterActivity.this,SellerMainActivity.class));
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1212){
            if(resultCode==RESULT_OK){
                lat=data.getDoubleExtra("latitude",0);
                lng=data.getDoubleExtra("longitude",0);
                if(lat!=0 && lng!=0){
                    locationSelectButton.setText("Location Selected");
                }
            }
        }

    }
}