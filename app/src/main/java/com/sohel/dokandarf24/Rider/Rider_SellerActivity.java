package com.sohel.dokandarf24.Rider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sohel.dokandarf24.Local.ApiKey.ApiKey;
import com.sohel.dokandarf24.Local.Database.UserDb;
import com.sohel.dokandarf24.R;
import com.sohel.dokandarf24.Rider.Adapter.SellerAdapter;
import com.sohel.dokandarf24.Seller.Model.SellerModel;
import com.sohel.dokandarf24.User.Model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class Rider_SellerActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private SellerAdapter sellerAdapter;
    private List<SellerModel> sellerList=new ArrayList<>();
    private UserDb userDb;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider__seller);
        init();

        sellerAdapter=new SellerAdapter(this,sellerList);
        recyclerView.setAdapter(sellerAdapter);

        sellerAdapter.setOnItemClickListner(new SellerAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDelete(int position) {

            }

            @Override
            public void onUpdate(int position) {

            }
        });


    }
    private void init() {
        toolbar=findViewById(R.id.appBariId);
        setSupportActionBar(toolbar);
        this.setTitle("Seller List");

        userDb=new UserDb(this);
        progressDialog=new ProgressDialog(this);
        recyclerView=findViewById(R.id.userRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    @Override
    public void onStart() {
        super.onStart();
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiKey.getSellerRef()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            sellerList.clear();
                            for(DataSnapshot snapshot1:snapshot.getChildren()){
                                SellerModel seller=snapshot1.getValue(SellerModel.class);
                                if(seller.getRiderPhone().equals(userDb.getRiderPhone())){
                                    sellerList.add(seller);
                                    sellerAdapter.notifyDataSetChanged();
                                }
                             }
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
}