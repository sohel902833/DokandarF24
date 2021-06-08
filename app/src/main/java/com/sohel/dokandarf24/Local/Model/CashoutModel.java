package com.sohel.dokandarf24.Local.Model;

public class CashoutModel {
    String id,userType,userId,accountType,accountNo,state;
    int amount;

    public CashoutModel(){}
    public CashoutModel(String id,String userType,String state, String userId, int amount, String accountType, String accountNo) {
        this.id = id;
        this.userType=userType;
        this.userId = userId;
        this.amount = amount;
        this.accountType = accountType;
        this.accountNo = accountNo;
        this.state=state;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
}
