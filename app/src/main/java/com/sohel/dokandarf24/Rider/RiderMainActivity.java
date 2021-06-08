package com.sohel.dokandarf24.Rider;

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
import com.sohel.dokandarf24.Rider.Model.RiderModel;
import com.sohel.dokandarf24.Seller.Model.SellerModel;
import com.sohel.dokandarf24.Seller.SellerMainActivity;
import com.sohel.dokandarf24.StartActivity;
import com.sohel.dokandarf24.User.Adapter.OrderListAdapter;
import com.sohel.dokandarf24.User.OrderStateActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RiderMainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView profileImage;
    private TextView userNameTv,balanceTv,sellerListBtn;
    private Button successBtn,cashoutBtn;
    private RecyclerView recyclerView;
    private OrderListAdapter orderListAdapter;
    private ProgressDialog progressDialog;

    private List<OrderModel> orderList=new ArrayList<>();
    private List<OrderModel> allOrderList=new ArrayList<>();
    private UserDb userDb;
    private RiderModel riderModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_main);
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
                Intent intent=new Intent(RiderMainActivity.this, OrderStateActivity.class);
                intent.putExtra("orderId",order.getOrderId());
                intent.putExtra("userType","rider");
                startActivity(intent);
            }

            @Override
            public void onDelete(int position) {
                Toast.makeText(RiderMainActivity.this, "No Permission", Toast.LENGTH_SHORT).show();
            }
        });

         successBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RiderMainActivity.this,RiderSuccessOrderActivity.class));
            }
        });
         cashoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RiderMainActivity.this,RiderCashoutActivity.class));
            }
        });   sellerListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RiderMainActivity.this,Rider_SellerActivity.class));
            }
        });

    }

    private void init() {
        toolbar=findViewById(R.id.appBariId);
        setSupportActionBar(toolbar);
        this.setTitle("Dokandar24");

        sellerListBtn=findViewById(R.id.sellerListBtn);
        cashoutBtn=findViewById(R.id.cashOutBtn);
        progressDialog=new ProgressDialog(this);
        profileImage=findViewById(R.id.main_UserProfileImage);
        userNameTv=findViewById(R.id.main_UserNameTv);
        balanceTv=findViewById(R.id.balanceTv);
        successBtn=findViewById(R.id.orderSuccessBtn);
        recyclerView=findViewById(R.id.recyclerViewid);
    }
    @Override
    protected void onStart() {
        super.onStart();

        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();

        loadAppSetting();
        loadRiderData();
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
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(RiderMainActivity.this);
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

    private void loadRiderData() {
        ApiKey.getRiderRef()
                .child(userDb.getRiderPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            riderModel=snapshot.getValue(RiderModel.class);
                            userNameTv.setText(riderModel.getName());
                            balanceTv.setText(""+riderModel.getBalance()+" tk");
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
                                if(orderModel.getOrderState().equals(OrderState.step4)){
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
        ApiKey.getRiderOrders()
                .child(userDb.getRiderPhone())
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
        startActivity(new Intent(RiderMainActivity.this, StartActivity.class));
        finish();
    }
}