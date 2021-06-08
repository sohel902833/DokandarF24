package com.sohel.dokandarf24.Rider.Model;

import com.sohel.dokandarf24.Local.Model.LocationModel;

public class RiderModel {
    String name,phone,password,profileImage,coverPhoto;
    int balance;
    private LocationModel location;


    public RiderModel(){}

    public RiderModel(String name, String phone, String password, String profileImage, String coverPhoto, int balance, LocationModel location) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.profileImage = profileImage;
        this.coverPhoto = coverPhoto;
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
