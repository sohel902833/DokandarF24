package com.sohel.dokandarf24.User.Services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.sohel.dokandarf24.Local.ApiKey.ApiKey;
import com.sohel.dokandarf24.Local.Database.UserDb;
import com.sohel.dokandarf24.Local.Model.OrderModel;
import com.sohel.dokandarf24.Local.Model.OrderState;
import com.sohel.dokandarf24.Seller.SellerMainActivity;

import java.util.HashMap;

public class OrderServices {
    private Activity activity;
    private ProgressDialog progressDialog;
    private UserDb userDb;


    public OrderServices(Activity activity){
        this.activity=activity;
        progressDialog=new ProgressDialog(activity);
        userDb=new UserDb(activity);
    }
    public void saveNewOrder(String filePath,long length){
        progressDialog.setMessage("Sending Order.");
        progressDialog.setCancelable(false);
        progressDialog.show();

         String orderid=System.currentTimeMillis()+ApiKey.getOrderRef().push().getKey();
         String userId=userDb.getUserPhone();
         String userName=userDb.getUserName();
        OrderModel orderModel=new OrderModel(
            orderid,userId,userName,filePath,System.currentTimeMillis(),length,false,false,"none", OrderState.step1,"none","notset",null
        );
        ApiKey.getOrderRef().child(orderid)
                .setValue(orderModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                           progressDialog.dismiss();
                            Toast.makeText(activity, "Order Sent", Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(activity, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateOrder(HashMap<String,Object> hashMap, String orderId){
        progressDialog.setMessage("Updating..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiKey.getOrderRef().child(orderId)
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(activity, "Update Successful", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            activity.startActivity(new Intent(activity, SellerMainActivity.class));
                        }else{
                            Toast.makeText(activity, "Update Failed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
    public void updateOrder2(HashMap<String,Object> hashMap, String orderId){
        progressDialog.setMessage("Updating..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiKey.getOrderRef().child(orderId)
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(activity, "Update Successful", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }else{
                            Toast.makeText(activity, "Update Failed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }



}
