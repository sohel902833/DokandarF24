package com.sohel.dokandarf24.Seller.Model;

import com.sohel.dokandarf24.Local.Model.LocationModel;

public class SellerModel {
    String name,phone,password,profileImage,coverPhoto,riderPhone;
    int balance;
    private LocationModel location;

    public SellerModel(){}

    public SellerModel(String name, String phone, String password, String profileImage, String coverPhoto, String riderPhone, int balance, LocationModel location) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.profileImage = profileImage;
        this.coverPhoto = coverPhoto;
        this.riderPhone = riderPhone;
        this.balance = balance;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getRiderPhone() {
        return riderPhone;
    }

    public void setRiderPhone(String riderPhone) {
        this.riderPhone = riderPhone;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }
}
