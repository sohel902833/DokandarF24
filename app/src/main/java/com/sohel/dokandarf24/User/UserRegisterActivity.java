package com.sohel.dokandarf24.User;

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
import com.sohel.dokandarf24.Seller.SellerLoginActivity;
import com.sohel.dokandarf24.Seller.SellerRegisterActivity;
import com.sohel.dokandarf24.User.Model.UserModel;

public class UserRegisterActivity extends AppCompatActivity {

    private Button locationSelectButton,registerButton;
    private EditText nameEt,phoneEt,passwordEt,houseNameEt,floorNoEt;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private TextView login_link;

    private double lat=0,lng=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        init();

        login_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserRegisterActivity.this, UserLoginActivity.class));
                finish();
            }
        });

        locationSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserRegisterActivity.this, MapsActivity.class);
                startActivityForResult(intent,1212);
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name =nameEt.getText().toString();
                String phone=phoneEt.getText().toString().trim();
                String password=passwordEt.getText().toString().trim();
                String houseName=houseNameEt.getText().toString();
                String floorNo=floorNoEt.getText().toString().trim();

                if(lat==0 && lng==0){
                    Toast.makeText(UserRegisterActivity.this, "Please Select Your Location", Toast.LENGTH_SHORT).show();
                }
                else if(name.isEmpty()){
                    showError(nameEt,"Write Your Name");
                }else if(houseName.isEmpty()){
                    showError(houseNameEt,"Write Your House name");
                }else if(floorNo.isEmpty()){
                    showError(floorNoEt,"Write Your Floor No");
                }else if(phone.isEmpty()){
                    showError(phoneEt,"Please enter Your Phone Number with country Code.");
                }else if(password.isEmpty()){
                    showError(passwordEt,"Please Enter Password");
                }else{
                    checkUserExists(name,phone,password,houseName,floorNo);
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
        this.setTitle("Register user");


        login_link=findViewById(R.id.login_link);
        locationSelectButton=findViewById(R.id.locationSelectBt);
        registerButton=findViewById(R.id.registerButton);
        nameEt=findViewById(R.id.nameEt);
        houseNameEt=findViewById(R.id.houseNameEt);
        floorNoEt=findViewById(R.id.floorNoEt);

        phoneEt=findViewById(R.id.phoneEt);
        passwordEt=findViewById(R.id.passwordEt);
        progressBar=findViewById(R.id.progressBar);
    }

    private void checkUserExists(String name, String phone, String password, String houseName, String floorNo) {
       progressBar.setVisibility(View.VISIBLE);

        ApiKey.getUserRef().child(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(UserRegisterActivity.this, "This Phone Number Have Already An Account", Toast.LENGTH_SHORT).show();
                        }else{
                            registerUser(name,phone,password,houseName,floorNo);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);

                    }
                });

    }

    private void registerUser(String name, String phone, String password, String houseName, String floorNo) {
        LocationModel locationModel=new LocationModel(lat,lng);
        UserModel userModel=new UserModel(
                name,phone,password,floorNo,houseName,"none","none",0,locationModel
        );
        ApiKey.getUserRef().child(phone)
                .setValue(userModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            UserDb userDb=new UserDb(UserRegisterActivity.this);
                            userDb.setUserType("user");
                            userDb.setUserData(userModel);
                            sendUserToUserMainActivity();
                        }else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(UserRegisterActivity.this, "Register Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void sendUserToUserMainActivity() {
        startActivity(new Intent(UserRegisterActivity.this,UserMainActivity.class));
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