package com.sohel.dokandarf24.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sohel.dokandarf24.BuildConfig;
import com.sohel.dokandarf24.Local.ApiKey.ApiKey;
import com.sohel.dokandarf24.Local.Database.UserDb;
import com.sohel.dokandarf24.Local.Model.OrderModel;
import com.sohel.dokandarf24.Local.Model.OrderState;
import com.sohel.dokandarf24.R;
import com.sohel.dokandarf24.Rider.RiderMainActivity;
import com.sohel.dokandarf24.Seller.Model.SellerModel;
import com.sohel.dokandarf24.StartActivity;
import com.sohel.dokandarf24.User.Adapter.OrderListAdapter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerMainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView profileImage;
    private TextView userNameTv,balanceTv;
    private Button cashOutBtn,pendingBtn,successBtn,pendingBalance;
    private RecyclerView recyclerView;
    private OrderListAdapter orderListAdapter;
    private ProgressDialog progressDialog;

    private List<OrderModel> orderList=new ArrayList<>();
    private List<OrderModel> allOrderList=new ArrayList<>();
    private UserDb userDb;
    private SellerModel sellerModel;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_main);
        init();
        userDb=new UserDb(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderListAdapter=new OrderListAdapter(this,orderList,"not");
        recyclerView.setAdapter(orderListAdapter);


        orderListAdapter.setOnItemClickListner(new OrderListAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {
                OrderModel order=orderList.get(position);
                Intent intent=new Intent(SellerMainActivity.this,BalanceAddActivity.class);
                intent.putExtra("orderId",order.getOrderId());
                intent.putExtra("riderId",sellerModel.getRiderPhone());
               startActivity(intent);


            }

            @Override
            public void onDelete(int position) {
                Toast.makeText(SellerMainActivity.this, "No Permission", Toast.LENGTH_SHORT).show();
            }
        });

        pendingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerMainActivity.this,SellerPendingActivity.class));
            }
        });
        successBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerMainActivity.this,SellerSuccessOrderActivity.class));
            }
        });
      cashOutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SellerMainActivity.this,CashoutActivity.class));
                }
            });
        pendingBalance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SellerMainActivity.this,PendingBalanceActivity.class));
                }
            });
    }

    private void init() {
        toolbar=findViewById(R.id.appBariId);
        setSupportActionBar(toolbar);
        this.setTitle("Dokandar24");

        progressDialog=new ProgressDialog(this);
        profileImage=findViewById(R.id.main_UserProfileImage);
        userNameTv=findViewById(R.id.main_UserNameTv);
        balanceTv=findViewById(R.id.balanceTv);
        pendingBtn=findViewById(R.id.orderPendingBtn);
        successBtn=findViewById(R.id.orderSuccessBtn);
        cashOutBtn=findViewById(R.id.cashOutBtn);
        pendingBalance=findViewById(R.id.pendingBalanceBtn);
        recyclerView=findViewById(R.id.recyclerViewid);
    }
    @Override
    protected void onStart() {
        super.onStart();

        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();
        loadAppSetting();
        loadSellerData();
        getAllOrderList();
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
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SellerMainActivity.this);
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

    private void loadSellerData() {
        ApiKey.getSellerRef()
                .child(userDb.getSellerPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                             sellerModel=snapshot.getValue(SellerModel.class);
                            userNameTv.setText(sellerModel.getName());
                            balanceTv.setText(""+sellerModel.getBalance()+" tk");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });
    }

    public void getAllOrderList(){
        ApiKey.getOrderRef()
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    allOrderList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        OrderModel orderModel=snapshot1.getValue(OrderModel.class);
                        if(orderModel.getOrderState().equals(OrderState.step2)){
                            allOrderList.add(orderModel);
                        }
                    }
                    getAllPickedOrder();

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
    private void getAllPickedOrder() {
        ApiKey.getSellerOrders()
                .child(userDb.getSellerPhone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            orderList.clear();
                            for(DataSnapshot snapshot1:snapshot.getChildren()){
                                String key=snapshot1.getKey();
                                if(isIt(key)){
                                    OrderModel orderModel=getSpecificOrder(key);
                                      orderList.add(orderModel);
                                      orderListAdapter.notifyDataSetChanged();
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
    public boolean isIt(String orderKey){
        boolean isIt=false;
        for(int i=0; i<allOrderList.size(); i++){
            OrderModel order=allOrderList.get(i);
            if(order.getOrderId().equals(orderKey)){
                isIt=true;
                break;
            }
        }
        return isIt;
    }
    public OrderModel getSpecificOrder(String key){
        OrderModel getOrderModel=null;
        for(int i=0; i<allOrderList.size(); i++){
            OrderModel order=allOrderList.get(i);
            if(order.getOrderId().equals(key)){
                getOrderModel=order;
                break;
            }
        }
        return getOrderModel;
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
        startActivity(new Intent(SellerMainActivity.this, StartActivity.class));
        finish();
    }
}