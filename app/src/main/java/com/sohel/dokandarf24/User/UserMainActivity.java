package com.sohel.dokandarf24.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sohel.dokandarf24.BuildConfig;
import com.sohel.dokandarf24.Local.ApiKey.ApiKey;
import com.sohel.dokandarf24.Local.Database.UserDb;
import com.sohel.dokandarf24.R;
import com.sohel.dokandarf24.Rider.RiderMainActivity;
import com.sohel.dokandarf24.Seller.Model.SellerModel;
import com.sohel.dokandarf24.Seller.SellerMainActivity;
import com.sohel.dokandarf24.StartActivity;
import com.sohel.dokandarf24.User.Model.UserModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserMainActivity extends AppCompatActivity {
    private CardView easyOrder,goDokan;
    private Toolbar toolbar;
    private CircleImageView profileImage;
    private TextView userNameTv,balanceTv;
    private ProgressDialog progressDialog;
    UserDb userDb;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        init();
        userDb=new UserDb(this);


        easyOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    sendUserToEasyOrderActivity();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();
        loadAppSetting();
        ApiKey.getUserRef()
                .child(userDb.getUserPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            userModel=snapshot.getValue(UserModel.class);
                            userNameTv.setText(userModel.getName());
                            balanceTv.setText("Balance: "+userModel.getBalance()+" tk");
                            progressDialog.dismiss();
                        }else{
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void init() {
        toolbar=findViewById(R.id.appBariId);
        setSupportActionBar(toolbar);
        this.setTitle("Dokandar24");

        easyOrder=findViewById(R.id.easyOrderCard);
        goDokan=findViewById(R.id.goDokanCard);
        progressDialog=new ProgressDialog(this);
        profileImage=findViewById(R.id.main_UserProfileImage);
        userNameTv=findViewById(R.id.main_UserNameTv);
        balanceTv=findViewById(R.id.balanceTv);

    }
    private void loadAppSetting() {
        ApiKey.getSettingRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    try {
                        progressDialog.dismiss();
                        double appVersion = snapshot.child("versionNo").getValue(Double.class);
                        int priority = snapshot.child("priority").getValue(Integer.class);
                        double cVersion=Double.parseDouble(BuildConfig.VERSION_NAME);
                        if(appVersion>cVersion){
                            showUpdateDiolouge(String.valueOf(priority));
                        }
                    }catch(Exception e){
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                }else{
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }
    private void showUpdateDiolouge(String priority) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserMainActivity.this);
        alertDialog.setTitle("Warning...");
        alertDialog.setMessage("New Update Available On Playstore");
        if(priority.equals("1")){
            alertDialog.setCancelable(false);
        }else{
            alertDialog.setCancelable(true);
            alertDialog.setNegativeButton("Update Later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        alertDialog.setPositiveButton("Check Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        AlertDialog alertDialog1 = alertDialog.create();
        alertDialog1.show();
    }


    private void sendUserToEasyOrderActivity() {
        startActivity(new Intent(UserMainActivity.this,UEasyOrderActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout_menu){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        UserDb userDb=new UserDb(this);
        userDb.setUserType("");
        startActivity(new Intent(UserMainActivity.this, StartActivity.class));
        finish();
    }
}