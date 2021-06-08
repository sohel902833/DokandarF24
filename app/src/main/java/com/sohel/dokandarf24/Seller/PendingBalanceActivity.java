package com.sohel.dokandarf24.Seller;

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
import com.sohel.dokandarf24.Local.Model.PendingBalance;
import com.sohel.dokandarf24.R;
import com.sohel.dokandarf24.Seller.Adapter.PendingBalanceAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class PendingBalanceActivity extends AppCompatActivity {

    private List<PendingBalance> pendingBalanceList=new ArrayList<>();
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;

    private PendingBalanceAdapter pendingAdapter;
    private UserDb userDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_balance);
        init();

        userDb=new UserDb(this);
        pendingAdapter=new PendingBalanceAdapter(this,pendingBalanceList);
        recyclerView.setAdapter(pendingAdapter);


    }

    private void init() {

        toolbar=findViewById(R.id.appBariId);
        setSupportActionBar(toolbar);
        this.setTitle("Pending Balance");

        progressDialog=new ProgressDialog(this);
        recyclerView=findViewById(R.id.recyclerViewid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



    }

    @Override
    public void onStart() {
        super.onStart();
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiKey.getPendingBalance()
                .child(userDb.getSellerPhone())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    pendingBalanceList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        PendingBalance pending=snapshot1.getValue(PendingBalance.class);
                           pendingBalanceList.add(pending);
                            pendingAdapter.notifyDataSetChanged();
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