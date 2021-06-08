package com.sohel.dokandarf24.Local.ApiKey;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ApiKey {

    private  static  final DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Users");
    private  static  final DatabaseReference orderRef= FirebaseDatabase.getInstance().getReference().child("Orders");
    private  static  final DatabaseReference riderRef= FirebaseDatabase.getInstance().getReference().child("Riders");
    private  static  final DatabaseReference sellerRef= FirebaseDatabase.getInstance().getReference().child("Sellers");
    private  static  final DatabaseReference sellerOrders= FirebaseDatabase.getInstance().getReference().child("SellerOrders");
    private  static  final DatabaseReference riderOrders= FirebaseDatabase.getInstance().getReference().child("riderOrders");
    private  static  final DatabaseReference cashOutRef= FirebaseDatabase.getInstance().getReference().child("Admin").child("Cashout");
    private  static  final DatabaseReference verificationRef= FirebaseDatabase.getInstance().getReference().child("VerificationCodes");
    private  static  final DatabaseReference pendingBalance= FirebaseDatabase.getInstance().getReference().child("PendingBalance");
    private  static  final DatabaseReference settingRef= FirebaseDatabase.getInstance().getReference().child("Admin").child("AppSettings");


    public static DatabaseReference getOrderRef(){
        return orderRef;
    }
    public static DatabaseReference getRiderRef() {
        return riderRef;
    }
    public static DatabaseReference getUserRef() {
        return userRef;
    }
    public static DatabaseReference getSellerRef() {
        return sellerRef;
    }
    public static DatabaseReference getSellerOrders() {
        return sellerOrders;
    }
    public static DatabaseReference getRiderOrders() {
        return riderOrders;
    }
    public static DatabaseReference getCashOutRef() {
        return cashOutRef;
    }
    public static DatabaseReference getVerificationRef() {
        return verificationRef;
    }
    public static DatabaseReference getPendingBalance() {
        return pendingBalance;
    }

    public static DatabaseReference getSettingRef() {
        return settingRef;
    }
}
