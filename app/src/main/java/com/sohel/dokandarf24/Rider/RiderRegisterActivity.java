package com.sohel.dokandarf24.Rider;

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
import com.sohel.dokandarf24.Seller.SellerRegisterActivity;
import com.sohel.dokandarf24.User.Model.UserModel;
import com.sohel.dokandarf24.User.UserRegisterActivity;

import org.w3c.dom.Text;

public class RiderRegisterActivity extends AppCompatActivity {

    private Button locationSelectButton,registerButton;
    private EditText nameEt,phoneEt,passwordEt;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private TextView login_link;

    private double lat=0,lng=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_register);
        init();

        login_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RiderRegisterActivity.this,RiderLoginActivity.class));
                finish();
            }
        });


        locationSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RiderRegisterActivity.this, MapsActivity.class);
                startActivityForResult(intent,1212);
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name =nameEt.getText().toString();
                String phone=phoneEt.getText().toString().trim();
                String password=passwordEt.getText().toString().trim();

                if(lat==0 && lng==0){
                    Toast.makeText(RiderRegisterActivity.this, "Please Select Your Location", Toast.LENGTH_SHORT).show();
                }
                else if(name.isEmpty()){
                    showError(nameEt,"Write Your Name");
                }else if(phone.isEmpty()){
                    showError(phoneEt,"Please enter Your Phone Number with country Code.");
                }else if(password.isEmpty()){
                    showError(passwordEt,"Please Enter Password");
                }else{
                    checkUserExists(name,phone,password);
                }



            }
        });



    }
    private void init() {
        toolbar=findViewById(R.id.appBariId);
        setSupportActionBar(toolbar);
        this.setTitle("Register Rider");


        login_link=findViewById(R.id.login_link);
        locationSelectButton=findViewById(R.id.locationSelectBt);
        registerButton=findViewById(R.id.registerButton);
        nameEt=findViewById(R.id.nameEt);
        phoneEt=findViewById(R.id.phoneEt);
        passwordEt=findViewById(R.id.passwordEt);
        progressBar=findViewById(R.id.progressBar);
    }

    private void showError(EditText editText, String message){
        editText.setError(message);
        editText.requestFocus();
    }



    private void checkUserExists(String name, String phone, String password) {
        progressBar.setVisibility(View.VISIBLE);

        ApiKey.getRiderRef().child(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RiderRegisterActivity.this, "This Phone Number Have Already An Account", Toast.LENGTH_SHORT).show();
                        }else{
                            registerUser(name,phone,password);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);

                    }
                });


    }

    private void registerUser(String name, String phone, String password) {
        LocationModel locationModel=new LocationModel(lat,lng);
        RiderModel userModel=new RiderModel(
               name,phone,password,"none","none",0,locationModel
        );
        ApiKey.getRiderRef().child(phone)
                .setValue(userModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            UserDb userDb=new UserDb(RiderRegisterActivity.this);
                            userDb.setUserType("rider");
                            userDb.setRiderData(userModel);
                            sendUserToRiderMainActivity();
                        }else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RiderRegisterActivity.this, "Register Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendUserToRiderMainActivity() {
        startActivity(new Intent(RiderRegisterActivity.this,RiderMainActivity.class));
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