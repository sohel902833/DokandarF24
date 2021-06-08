package com.sohel.dokandarf24.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sohel.dokandarf24.Local.ApiKey.ApiKey;
import com.sohel.dokandarf24.Local.Database.UserDb;
import com.sohel.dokandarf24.Local.Model.OrderModel;
import com.sohel.dokandarf24.Local.Model.OrderState;
import com.sohel.dokandarf24.R;
import com.sohel.dokandarf24.User.Adapter.OrderListAdapter;
import com.sohel.dokandarf24.User.OrderStateActivity;
import com.sohel.dokandarf24.User.USuccessActivity;

import java.util.ArrayList;
import java.util.List;

public class SellerSuccessOrderActivity extends AppCompatActivity {
    private OrderListAdapter orderAdapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private List<OrderModel> orderList=new ArrayList<>();
    private UserDb userDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_success_order);

        init();

        progressDialog=new ProgressDialog(this);
        userDb=new UserDb(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter=new OrderListAdapter(this,orderList,"not");
        recyclerView.setAdapter(orderAdapter);

        orderAdapter.setOnItemClickListner(new OrderListAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {
                OrderModel orderModel=orderList.get(position);
                Intent intent=new Intent(SellerSuccessOrderActivity.this, OrderStateActivity.class);
                intent.putExtra("orderId",orderModel.getOrderId());
                startActivity(intent);
            }
            @Override
            public void onDelete(int position) {
                Toast.makeText(SellerSuccessOrderActivity.this, "No Permission", Toast.LENGTH_SHORT).show();
            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        ApiKey.getOrderRef()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            orderList.clear();
                            for(DataSnapshot snapshot1:snapshot.getChildren()){
                                OrderModel orderModel=snapshot1.getValue(OrderModel.class);
                                if(orderModel.getPickedSellerId().equals(userDb.getSellerPhone()) &&  orderModel.getOrderState().equals(OrderState.step5)){
                                    orderList.add(orderModel);
                                    orderAdapter.notifyDataSetChanged();
                                }
                            }
                            progressDialog.dismiss();
                        }else{
                            Toast.makeText(SellerSuccessOrderActivity.this, "No Success Order Found", Toast.LENGTH_SHORT).show();
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
        this.setTitle("Success Orders");

        recyclerView=findViewById(R.id.recyclerViewid);
    }
}