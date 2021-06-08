package com.sohel.dokandarf24.Local.Model;

import java.util.List;

public class OrderModel {
    public OrderModel(){}
    String  orderId,userId,userName,filePath;
    long time;
    long length;
    boolean picked;
    boolean pickedRider;

    String pickedSellerId;
    String orderState;
    String pickedRiderId;
    String productType;
    private List<ProductItem> productItems;

    public OrderModel(String orderId, String userId,String userName, String filePath, long time, long length, boolean picked, boolean pickedRider, String pickedSellerId, String orderState, String pickedRiderId, String productType, List<ProductItem> productItems) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName=userName;
        this.filePath = filePath;
        this.time = time;
        this.length = length;
        this.picked = picked;
        this.pickedRider = pickedRider;
        this.pickedSellerId = pickedSellerId;
        this.orderState = orderState;
        this.pickedRiderId = pickedRiderId;
        this.productType = productType;
        this.productItems = productItems;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public boolean isPicked() {
        return picked;
    }

    public void setPicked(boolean picked) {
        this.picked = picked;
    }

    public boolean isPickedRider() {
        return pickedRider;
    }

    public void setPickedRider(boolean pickedRider) {
        this.pickedRider = pickedRider;
    }

    public String getPickedSellerId() {
        return pickedSellerId;
    }

    public void setPickedSellerId(String pickedSellerId) {
        this.pickedSellerId = pickedSellerId;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getPickedRiderId() {
        return pickedRiderId;
    }

    public void setPickedRiderId(String pickedRiderId) {
        this.pickedRiderId = pickedRiderId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public List<ProductItem> getProductItems() {
        return productItems;
    }

    public void setProductItems(List<ProductItem> productItems) {
        this.productItems = productItems;
    }
}
