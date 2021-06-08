package com.sohel.dokandarf24.Local.Services;

import android.app.Activity;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sohel.dokandarf24.Local.ApiKey.ApiKey;
import com.sohel.dokandarf24.Local.Model.OrderModel;
import com.sohel.dokandarf24.Local.Model.OrderState;
import com.sohel.dokandarf24.Local.Model.PendingBalance;
import com.sohel.dokandarf24.Local.Model.ProductItem;
import com.sohel.dokandarf24.User.Model.UserModel;
import com.sohel.dokandarf24.User.OrderStateActivity;
import com.sohel.dokandarf24.User.Services.OrderServices;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class VerificationServices {
    private Activity activity;
    private UserModel user;
    private  int totalCount=0;
    public VerificationServices(Activity activity,UserModel user) {
        this.activity = activity;
        this.user=user;
    }

    public void sendMessage(String phoneNo, OrderModel order){
        String code=generateRandomCode();
        String message="Your Order Verification Code Is: "+code+" ";
        try{
            SmsManager smsManager=SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo,null,message,null,null);
            check(order,code);
        }catch(Exception e){
            Toast.makeText(activity, "Failed To Send Message: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void check(OrderModel order,String code){
        ApiKey.getVerificationRef()
                .child(order.getPickedRiderId())
                .child(order.getOrderId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(snapshot.hasChild("code")){
                                setCodeIntoDb(order,code,false);
                            }else{
                                setCodeIntoDb(order,code,true);
                            }
                        }else{
                            setCodeIntoDb(order,code,true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void setCodeIntoDb(OrderModel order,String code,boolean checker){
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("code",code);

        ApiKey.getVerificationRef()
                .child(order.getPickedRiderId())
                .child(order.getOrderId())
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            if(checker){
                                updateUserAmount(order);
                            }else{
                                Toast.makeText(activity, "Verification Code Sent to: "+order.getUserId(), Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(activity, "Failed To Send Message: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void verifyCode(String pCode,OrderModel order){
        ApiKey.getVerificationRef()
                .child(order.getPickedRiderId())
                .child(order.getOrderId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String code=snapshot.child("code").getValue().toString();
                            if(code.equals(pCode)){
                                removeVerification(order);
                              }else{
                                Toast.makeText(activity, "Verification Code not matched.", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(activity, "Verification Failed.Send Code Again.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void removeVerification(OrderModel order) {
            ApiKey.getVerificationRef()
                    .child(order.getPickedRiderId())
                    .child(order.getOrderId())
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("orderState", OrderState.step5);
                        OrderServices orderServices=new OrderServices(activity);
                        orderServices.updateOrder2(hashMap,order.getOrderId());
                        Toast.makeText(activity, "Verification Success", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(activity, "Verification Failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });



    }

    private int getTotalAmount(OrderModel order){
        totalCount=0;
        List<ProductItem> productItems=order.getProductItems();

        if(productItems!=null){
            for(int i=0; i<productItems.size(); i++){
                ProductItem productItem=productItems.get(i);
                totalCount+=productItem.getPrice();
            }
        }else{
            totalCount=0;
         }

    return totalCount;
    }
    private void updateUserAmount(OrderModel order){
        int orderPrice=getTotalAmount(order);
        HashMap<String,Object> hashMap=new HashMap<>();
        if(user.getBalance()>orderPrice){
            int newBalance=user.getBalance()-orderPrice;
            hashMap.put("balance",newBalance);
        }else{
            int newBalance=0;
            hashMap.put("balance",newBalance);
        }

            ApiKey.getUserRef().child(user.getPhone())
                    .updateChildren(hashMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                setPendingBalance(order);
                            }else{
                                Toast.makeText(activity, "Code Sent Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



    }
    private  void setPendingBalance(OrderModel order){
       String key=ApiKey.getPendingBalance().push().getKey();
       PendingBalance pendingBalance=new PendingBalance(key,order.getOrderId(),order.getPickedRiderId(),getTotalAmount(order));

       ApiKey.getPendingBalance()
               .child(order.getPickedSellerId())
               .child(key)
               .setValue(pendingBalance)
               .addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()){
                           Toast.makeText(activity, "Code Sent To:"+order.getUserId(), Toast.LENGTH_SHORT).show();
                       }else{
                           Toast.makeText(activity, "Code Sent Failed", Toast.LENGTH_SHORT).show();
                       }
                   }
               });

    }
    private String generateRandomCode() {
        String txt="";
        int[] nmbr={1,2,3,4,5,6,7,8,9,0};
        for(int i=0; i<5; i++){
            int randomNumber=new Random().nextInt(9);
            txt=txt+nmbr[randomNumber];
        }
        return txt.trim();
    }
}
