package com.sohel.dokandarf24.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sohel.dokandarf24.Local.ApiKey.ApiKey;
import com.sohel.dokandarf24.Local.GFG;
import com.sohel.dokandarf24.Local.Model.LocationModel;
import com.sohel.dokandarf24.Local.Model.OrderModel;
import com.sohel.dokandarf24.Local.Model.OrderState;
import com.sohel.dokandarf24.Local.Model.ProductItem;
import com.sohel.dokandarf24.Local.Services.VerificationServices;
import com.sohel.dokandarf24.R;
import com.sohel.dokandarf24.Rider.Model.RiderModel;
import com.sohel.dokandarf24.Seller.Model.SellerModel;
import com.sohel.dokandarf24.User.Adapter.ProductItemAdapter;
import com.sohel.dokandarf24.User.Model.UserModel;
import com.sohel.dokandarf24.User.Services.OrderServices;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class OrderStateActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String orderId;

    private CardView orderDetailsCard,sellerDetailsCard,riderDetailsCard,orderControlCard,riderManagementCard;

    private TextView orderUserNameTv,orderUserPhoneTv,orderStateTv,totalAmountTv;
    private TextView sellerNameTv,sellerPhoneTv,sellerDistanceTv;
    private TextView riderNameTv,riderPhoneTv,riderDistanceTv;
    private RecyclerView recyclerView;
    private EditText verificationCodeEt;
    private Button sendCodeBtn,verifyBtn;

    private ProgressDialog progressDialog;
    private Button payOnDeliveryBtn,cancelBtn;
    private VerificationServices verificationServices;

    private SellerModel seller;
    private UserModel user;
    private RiderModel rider;
    private OrderModel order;
    private ProductItemAdapter productItemAdapter;
    int totalCount=0;
    String userType="user";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_state);
        orderId=getIntent().getStringExtra("orderId");
        try {
            userType = getIntent().getStringExtra("userType");
        }catch(Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        init();

        payOnDeliveryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrder();
            }
        });

        sendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNo=order.getUserId();
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                        verificationServices=new VerificationServices(OrderStateActivity.this,user);
                        verificationServices.sendMessage(phoneNo,order);

                    }else{
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS},1);
                    }
                }
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=verificationCodeEt.getText().toString();
                if(code.isEmpty()){
                    verificationCodeEt.setError("Enter Verification Code.");
                    verificationCodeEt.requestFocus();
                }else{
                    verificationServices=new VerificationServices(OrderStateActivity.this,user);
                    verificationServices.verifyCode(code,order);
                }
            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        getOrder();
    }
    private void init() {

        progressDialog=new ProgressDialog(this);

        toolbar=findViewById(R.id.appBariId);
        setSupportActionBar(toolbar);
        this.setTitle("Order Details");

        payOnDeliveryBtn=findViewById(R.id.payonDeliveryButton);
        cancelBtn=findViewById(R.id.orderCancelButton);
        sendCodeBtn=findViewById(R.id.sendVerificationCodeBtn);
        verifyBtn=findViewById(R.id.verifyButton);
        verificationCodeEt=findViewById(R.id.verificationCodeEt);
        orderUserNameTv=findViewById(R.id.orderUesrNameTv);
        orderUserPhoneTv=findViewById(R.id.orderUserPhoneTv);
        orderStateTv=findViewById(R.id.orderStateTv);
        orderDetailsCard=findViewById(R.id.orderDetailsCard);
        riderManagementCard=findViewById(R.id.riderManagmentCard);
        recyclerView=findViewById(R.id.orderDetailsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        totalAmountTv=findViewById(R.id.totalAmountTv);
        sellerDetailsCard=findViewById(R.id.sellerDetailsCard);
        orderControlCard=findViewById(R.id.orderControlCard);
        riderDetailsCard=findViewById(R.id.riderDetailsCard);
        sellerNameTv=findViewById(R.id.sellerName);
        sellerPhoneTv=findViewById(R.id.sellerPhone);
        sellerDistanceTv=findViewById(R.id.sellerDistance);
        riderNameTv=findViewById(R.id.riderNameTv);
        riderPhoneTv=findViewById(R.id.riderPhoneTv);
        riderDistanceTv=findViewById(R.id.rider_DistanceTv);
    }


    private void getOrder() {
        ApiKey.getOrderRef().child(orderId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            order=snapshot.getValue(OrderModel.class);
                            setOrderState();
                            getOrderUserData();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(OrderStateActivity.this, "Order Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                    }
                });
    }
    private void getOrderUserData() {
        ApiKey.getUserRef()
                .child(order.getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            user=snapshot.getValue(UserModel.class);
                            orderUserNameTv.setText("User Name: "+user.getName());
                            orderUserPhoneTv.setText("User Phone: "+user.getPhone());
                           if(order.getOrderState().equals(OrderState.step3) || order.getOrderState().equals(OrderState.step4) || order.getOrderState().equals(OrderState.step5)){
                               if(order.getOrderState().equals(OrderState.step3)){
                                   orderControlCard.setVisibility(View.VISIBLE);
                               }
                               setTotalAmount();
                              orderDetailsCard.setVisibility(View.VISIBLE);
                                List<ProductItem> productItemList=order.getProductItems();
                                if(productItemList!=null){
                                    productItemAdapter=new ProductItemAdapter(OrderStateActivity.this,productItemList);
                                    recyclerView.setAdapter(productItemAdapter);
                                }

                                getSeller();
                            }else{
                                progressDialog.dismiss();
                            }

                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(OrderStateActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });
    }
    private void getSeller() {
        ApiKey.getSellerRef()
                .child(order.getPickedSellerId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            seller=snapshot.getValue(SellerModel.class);
                            sellerDetailsCard.setVisibility(View.VISIBLE);
                            sellerNameTv.setText("Seller Name: "+seller.getName());
                            sellerPhoneTv.setText("Seller Phone: "+seller.getPhone());

                            LocationModel userLocation=user.getLocation();
                            LocationModel sellerLocation=seller.getLocation();

                          double distacne = GFG.distance(userLocation.getLatitude(), sellerLocation.getLatitude(), userLocation.getLongitude(), sellerLocation.getLongitude());
                          sellerDistanceTv.setText("Distance: "+String.format("%.2f",distacne)+"km");

                          if(order.getOrderState().equals(OrderState.step4) || order.getOrderState().equals(OrderState.step5)){
                            if(userType==null){
                                riderDetailsCard.setVisibility(View.VISIBLE);
                                getRider();
                            }else{
                                if(order.getOrderState().equals(OrderState.step4) && userType!=null && userType.equals("rider")){
                                    riderManagementCard.setVisibility(View.VISIBLE);
                                    progressDialog.dismiss();
                                }else{
                                    progressDialog.dismiss();
                                }
                            }
                          }else{
                              progressDialog.dismiss();
                          }


                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(OrderStateActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });
    }
    private void getRider() {
        ApiKey.getRiderRef()
                .child(order.getPickedRiderId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            progressDialog.dismiss();
                            rider=snapshot.getValue(RiderModel.class);
                            riderNameTv.setText("Rider Name: "+rider.getName());
                            riderPhoneTv.setText("Rider Phone: "+rider.getPhone());

                            LocationModel userLocation=user.getLocation();
                            LocationModel riderLocation=rider.getLocation();

                            double distacne = GFG.distance(userLocation.getLatitude(), riderLocation.getLatitude(), userLocation.getLongitude(), riderLocation.getLongitude());
                            riderDistanceTv.setText("Distance: "+String.format("%.2f",distacne)+" km");
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(OrderStateActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });
    }
    private void setOrderState() {
        if(order.getOrderState().equals(OrderState.step1)){
            orderStateTv.setText("Pending For Unvoice");
        }else if(order.getOrderState().equals(OrderState.step2)){
            orderStateTv.setText("Pending For Balance Add");
        }else if(order.getOrderState().equals(OrderState.step3)){
            orderStateTv.setText("Need To Accept");
        }else if(order.getOrderState().equals(OrderState.step4)){
            orderStateTv.setText("On Rider");
        }else if(order.getOrderState().equals(OrderState.step5)){
            orderStateTv.setText("Success");
        }
    }
    private void setTotalAmount(){
        totalCount=0;
        List<ProductItem> productItems=order.getProductItems();

        if(productItems!=null){
            for(int i=0; i<productItems.size(); i++){
                ProductItem productItem=productItems.get(i);
                totalCount+=productItem.getPrice();
            }
            totalAmountTv.setText("Total Amount: "+totalCount+" tk");
        }else{
            totalAmountTv.setText("Total Amount: "+0+" tk");
        }


    }
    private void updateOrder() {
        updateProfileAmount();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("orderState", OrderState.step4);
        hashMap.put("pickedRider", true);
        hashMap.put("pickedRiderId", seller.getRiderPhone());

        ApiKey.getRiderOrders()
                .child(seller.getRiderPhone())
                .child(orderId)
                .child("take")
                .setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            orderControlCard.setVisibility(View.GONE);
                            progressDialog.dismiss();
                            OrderServices orderServices=new OrderServices(OrderStateActivity.this);
                            orderServices.updateOrder2(hashMap,orderId);
                            onStart();
                        }else{
                            progressDialog.dismiss();
                        }
                    }
                });
    }
    private void updateProfileAmount(){
        int newBalance=user.getBalance()+totalCount;
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("balance",newBalance);
        ApiKey.getUserRef().child(user.getPhone())
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                        }else{
                            progressDialog.dismiss();
                        }
                    }
                });

    }

}