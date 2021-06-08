package com.sohel.dokandarf24.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sohel.dokandarf24.Local.ApiKey.ApiKey;
import com.sohel.dokandarf24.Local.Model.OrderModel;
import com.sohel.dokandarf24.Local.Model.OrderState;
import com.sohel.dokandarf24.Local.Model.ProductItem;
import com.sohel.dokandarf24.R;
import com.sohel.dokandarf24.User.Services.OrderServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BalanceAddActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout linearLayout;
    private Button addBalanceButton;
    private String orderId,riderId;
    private OrderModel orderModel;
    private ProgressDialog progressDialog;

    private List<View> viewList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_add);
        orderId=getIntent().getStringExtra("orderId");
        riderId=getIntent().getStringExtra("riderId");
        progressDialog=new ProgressDialog(this);
        init();

        addBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllItemData();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiKey.getOrderRef().child(orderId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            orderModel=snapshot.getValue(OrderModel.class);
                            addView(orderModel);
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(BalanceAddActivity.this, "Order Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });
    }
    private void init(){
        toolbar=findViewById(R.id.appBariId);
        setSupportActionBar(toolbar);
        this.setTitle("Add Order Balance");

        linearLayout=findViewById(R.id.linearLayout);
        addBalanceButton=findViewById(R.id.addBalanceButton);
    }
    private void addView(OrderModel orderModel) {
        List<ProductItem> productItems=orderModel.getProductItems();

        if(productItems!=null) {
            for(int i=0; i<productItems.size(); i++){
                View view = getLayoutInflater().inflate(R.layout.view_item_layout, null, false);
                viewList.add(view);

                Button closeBtn = view.findViewById(R.id.close_ViewId);
                TextView textView=view.findViewById(R.id.view_TextView);
                textView.setText(productItems.get(i).getProductName());
                closeBtn.setOnClickListener(v -> {
                    viewList.remove(view);
                    removeView(view);
                });
                linearLayout.addView(view);
            }
            progressDialog.dismiss();
        }else{
            Toast.makeText(this, "No Item Added", Toast.LENGTH_SHORT).show();
        }
    }
    private  void removeView(View view){
        linearLayout.removeView(view);
    }
    private void getAllItemData() {
        List<ProductItem> productItems=orderModel.getProductItems();
        int count=0;
        for(int i=0; i<productItems.size(); i++){
            count=i;
            View view=viewList.get(i);
            EditText editText=view.findViewById(R.id.view_BalanceEt);
            String itemBalance=editText.getText().toString().trim();
            if(itemBalance.isEmpty()){
                editText.setError("Enter Item Price");
                editText.requestFocus();
                break;
            }else{
                productItems.get(i).setPrice(Integer.parseInt(itemBalance));
            }
        }

        if(count==productItems.size()-1){
            progressDialog.setMessage("Loading..");
            progressDialog.setCancelable(false);
            progressDialog.show();

           HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("productItems",productItems);
            hashMap.put("orderState", OrderState.step3);
            OrderServices orderServices=new OrderServices(BalanceAddActivity.this);
            orderServices.updateOrder(hashMap,orderId);
                           
        /*    ApiKey.getRiderOrders()
                    .child(riderId)
                    .child(orderId)
                    .child("take")
                    .setValue(true)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                OrderServices orderServices=new OrderServices(BalanceAddActivity.this);
                                orderServices.updateOrder(hashMap,orderId);
                            }else{
                                progressDialog.dismiss();
                            }
                        }
                    });
       */ }



    }

}