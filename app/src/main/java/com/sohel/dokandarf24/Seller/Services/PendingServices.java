package com.sohel.dokandarf24.Seller.Services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sohel.dokandarf24.Local.ApiKey.ApiKey;
import com.sohel.dokandarf24.Local.Database.UserDb;
import com.sohel.dokandarf24.Local.Model.PendingBalance;
import com.sohel.dokandarf24.Rider.Model.RiderModel;
import com.sohel.dokandarf24.Seller.Model.SellerModel;

import java.util.HashMap;

public class PendingServices {
    Activity activity;
    private ProgressDialog progressDialog;
    private PendingBalance pendingBalance;
    private UserDb userDb;
    public PendingServices(Activity activity) {
        this.activity = activity;
        progressDialog=new ProgressDialog(activity);
        userDb=new UserDb(activity);
    }

    public void findSettingAndStart(PendingBalance pendingBalance) {
        this.pendingBalance=pendingBalance;
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiKey.getSettingRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    try {
                        int rPercentage = snapshot.child("riderP").getValue(Integer.class);
                        acceptPendingBalance(pendingBalance,rPercentage);
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

    private void acceptPendingBalance(PendingBalance pendingBalance,int percentage){
        int riderBalance=pendingBalance.getAmount()*percentage/100;
        int sellerBalance=pendingBalance.getAmount()-riderBalance;
        updateRiderBalance(riderBalance,sellerBalance);

    }

    private void updateRiderBalance(int riderBalance, int sellerBalance) {
        ApiKey.getRiderRef()
                .child(pendingBalance.getRiderId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            RiderModel rider=snapshot.getValue(RiderModel.class);
                            HashMap<String,Object> riderMap=new HashMap<>();
                            riderMap.put("balance",riderBalance+rider.getBalance());
                            ApiKey.getRiderRef()
                                    .child(pendingBalance.getRiderId())
                                    .updateChildren(riderMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                updateSellerBalance(sellerBalance);
                                            }else{
                                                Toast.makeText(activity, "Error.", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });


                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(activity, "Error.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });

    }

    private void updateSellerBalance(int sellerBalance) {
        ApiKey.getSellerRef()
                .child(userDb.getSellerPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            SellerModel seller=snapshot.getValue(SellerModel.class);
                            HashMap<String,Object> sellerMap=new HashMap<>();
                            sellerMap.put("balance",sellerBalance+seller.getBalance());
                            ApiKey.getSellerRef()
                                    .child(userDb.getSellerPhone())
                                    .updateChildren(sellerMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                removePendingItem();
                                             }else{
                                                Toast.makeText(activity, "Error.", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });


                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(activity, "Error.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void removePendingItem() {
        ApiKey.getPendingBalance()
                .child(userDb.getSellerPhone())
                .child(pendingBalance.getKey())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                          progressDialog.dismiss();
                            Toast.makeText(activity, "Balance Added To Your Account", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(activity, "Error.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });

    }


}
