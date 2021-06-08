package com.sohel.dokandarf24.Rider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sohel.dokandarf24.Local.ApiKey.ApiKey;
import com.sohel.dokandarf24.Local.Database.UserDb;
import com.sohel.dokandarf24.Local.Model.CashoutModel;
import com.sohel.dokandarf24.R;
import com.sohel.dokandarf24.Rider.Model.RiderModel;
import com.sohel.dokandarf24.Seller.CashoutActivity;
import com.sohel.dokandarf24.Seller.Model.SellerModel;

import java.util.HashMap;

public class RiderCashoutActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Spinner spinner;
    private EditText amountEt,accountNoEt;
    private Button cashoutBtn;
    private ProgressDialog progressDialog;
    private UserDb userDb;


    ArrayAdapter spinnerAdapter;
    String[] paymentTypes={"Bkash","Rocket","Nogod"};

    String selectedAccountType="none";

    private RiderModel rider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_cashout);


        init();

        cashoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount=amountEt.getText().toString();
                String accountNo=accountNoEt.getText().toString().trim();
                selectedAccountType=spinner.getSelectedItem().toString();

                if(accountNo.isEmpty()){
                    accountNoEt.setError("Enter Account No");
                    accountNoEt.requestFocus();
                }else if(amount.isEmpty()){
                    amountEt.setError("Enter Some Amount.");
                    amountEt.requestFocus();
                }else if(selectedAccountType.equals("none")){
                    Toast.makeText(RiderCashoutActivity.this, "Select Payment Type", Toast.LENGTH_SHORT).show();
                }else{
                    cashOut(Integer.parseInt(amount),accountNo);
                }


            }
        });



    }

    private void cashOut(int amount, String accountNo) {
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiKey.getRiderRef().child(userDb.getRiderPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            rider=snapshot.getValue(RiderModel.class);
                            int riderAmount=rider.getBalance()-10;
                            if(riderAmount<amount){
                                progressDialog.dismiss();
                                Toast.makeText(RiderCashoutActivity.this, "Insufficient Balance", Toast.LENGTH_SHORT).show();
                            }else{
                                removeAmount(amount,accountNo);
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
    private void removeAmount(int amount,String accountNo){
        int newBalance=rider.getBalance()-amount;
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("balance",newBalance);
        ApiKey.getRiderRef().child(userDb.getRiderPhone())
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            sendCashoutRequest(amount,accountNo);
                        }else{
                            progressDialog.dismiss();
                        }
                    }
                });

    }

    private void sendCashoutRequest(int amount,String accountNo) {
        String key=System.currentTimeMillis()+userDb.getUserPhone();
        CashoutModel cashoutModel=new CashoutModel(key,"rider","pending",userDb.getSellerPhone(),amount,selectedAccountType,accountNo);
        ApiKey.getCashOutRef()
                .child(key)
                .setValue(cashoutModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(RiderCashoutActivity.this, "Cashout Request Sent", Toast.LENGTH_SHORT).show();
                            finish();

                        }else{
                            progressDialog.dismiss();
                        }
                    }
                });

    }

    private void init() {
        toolbar=findViewById(R.id.appBariId);
        setSupportActionBar(toolbar);
        this.setTitle("Cashout");

        progressDialog=new ProgressDialog(this);
        userDb=new UserDb(this);
        spinner=findViewById(R.id.payment_TypeSinner);
        amountEt=findViewById(R.id.amount_EditText);
        accountNoEt=findViewById(R.id.account_no_Et);
        cashoutBtn=findViewById(R.id.cashOutBtn);


        spinnerAdapter=new ArrayAdapter(RiderCashoutActivity.this,R.layout.item_layout,R.id.spinnerHeaderTExt,paymentTypes);
        spinner.setAdapter(spinnerAdapter);


    }
}