package com.sohel.dokandarf24.Local.Model;

public class PendingBalance {
    String key,productId,riderId;
    int amount;
    public PendingBalance(){}


    public PendingBalance(String key, String productId, String riderId, int amount) {
        this.key = key;
        this.productId = productId;
        this.riderId = riderId;
        this.amount = amount;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }
}
