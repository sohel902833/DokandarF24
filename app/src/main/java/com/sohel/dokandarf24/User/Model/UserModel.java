package com.sohel.dokandarf24.User.Model;

import com.sohel.dokandarf24.Local.Model.LocationModel;

public class UserModel {
    String name,phone,password,floorNo,houseName,profileImage,coverPhoto;
    int balance;
    private LocationModel location;

    public UserModel(){

    }

    public UserModel(String name, String phone, String password, String floorNo, String houseName, String profileImage, String coverPhoto, int balance, LocationModel location) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.floorNo = floorNo;
        this.houseName = houseName;
        this.profileImage = profileImage;
        this.coverPhoto = coverPhoto;
        this.balance = balance;
        this.location = location;
    }

    public String getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(String floorNo) {
        this.floorNo = floorNo;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
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
