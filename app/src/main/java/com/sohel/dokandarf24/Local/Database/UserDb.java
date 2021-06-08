package com.sohel.dokandarf24.Local.Database;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.sohel.dokandarf24.Rider.Model.RiderModel;
import com.sohel.dokandarf24.Seller.Model.SellerModel;
import com.sohel.dokandarf24.User.Model.UserModel;

public class UserDb {
    private Activity activity;

    public UserDb(Activity activity) {
        this.activity = activity;
    }

    public void  setUserType(String type){
        SharedPreferences sharedPreferences=activity.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("userType",type);
        editor.commit();
    }
    public String getUserType(){
        SharedPreferences sharedPreferences=activity.getSharedPreferences("user", Context.MODE_PRIVATE);
        String type=sharedPreferences.getString("userType","");
        return  type;
    }

    public void setUserData(UserModel userModel) {
        SharedPreferences sharedPreferences=activity.getSharedPreferences("userDb", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("userType","user");
        editor.putString("name",userModel.getName());
        editor.putString("phone",userModel.getPhone());
        editor.putString("houseName",userModel.getHouseName());
        editor.putString("floorNo",userModel.getFloorNo());
        editor.commit();
    }

    public void setSellerData(SellerModel sellerModel) {
        SharedPreferences sharedPreferences=activity.getSharedPreferences("sellerData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("userType","seller");
        editor.putString("name",sellerModel.getName());
        editor.putString("pickedSellerId",sellerModel.getPhone());
        editor.commit();
    }

    public void setRiderData(RiderModel userModel) {
        SharedPreferences sharedPreferences=activity.getSharedPreferences("riderData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("userType","rider");
        editor.putString("name",userModel.getName());
        editor.putString("phone",userModel.getPhone());
        editor.commit();
    }



    public String getUserPhone(){
        SharedPreferences sharedPreferences=activity.getSharedPreferences("userDb", Context.MODE_PRIVATE);
        String phone=sharedPreferences.getString("phone","");
        return  phone;
    }
    public String getUserName(){
        SharedPreferences sharedPreferences=activity.getSharedPreferences("userDb", Context.MODE_PRIVATE);
        String name=sharedPreferences.getString("name","");
        return  name;
    } public String getSellerPhone(){
        SharedPreferences sharedPreferences=activity.getSharedPreferences("sellerData", Context.MODE_PRIVATE);
        String phone=sharedPreferences.getString("pickedSellerId","");
        return  phone;
    }
    public String getRiderPhone(){
        SharedPreferences sharedPreferences=activity.getSharedPreferences("riderData", Context.MODE_PRIVATE);
        String phone=sharedPreferences.getString("phone","");
        return  phone;
    }


}
