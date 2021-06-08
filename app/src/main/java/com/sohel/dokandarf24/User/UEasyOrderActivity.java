package com.sohel.dokandarf24.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sohel.dokandarf24.Local.ApiKey.ApiKey;
import com.sohel.dokandarf24.Local.Database.UserDb;
import com.sohel.dokandarf24.R;
import com.sohel.dokandarf24.StartActivity;
import com.sohel.dokandarf24.User.Model.UserModel;
import com.sohel.dokandarf24.User.Services.OrderServices;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UEasyOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Button orderButton,pendingOrderButton,successOrderButton,playButton,pauseButton;
    private Chronometer chronometer;
    private TextView balanceTv;

    private  boolean mStartRecording=true;
    private  boolean mPauseRecording=true;
    private long timeWhenPaused=0;
    MediaRecorder mediaRecorder;
    long mStartingTimeMillis=0;
    long mEllapsedMillis=0;
    File file;
    String fileName;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private UserDb userDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_easy_order);

        progressDialog=new ProgressDialog(this);
        storageReference= FirebaseStorage.getInstance().getReference().child("OrderMp3");
        init();

        userDb=new UserDb(this);
        pendingOrderButton.setOnClickListener(this);
        successOrderButton.setOnClickListener(this);
        orderButton.setOnClickListener(this);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserPermission();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        ApiKey.getUserRef().child(userDb.getUserPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            progressDialog.dismiss();
                            UserModel user=snapshot.getValue(UserModel.class);
                            balanceTv.setText("Balance: "+user.getBalance()+" tk");
                        }else{
                            progressDialog.dismiss();
                           logout();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void startRecording(){

        playButton.setEnabled(false);

        Toast.makeText(this, "Recording Started", Toast.LENGTH_SHORT).show();
        File folder=new File(Environment.getExternalStorageDirectory()+"/MySoundRecord");
        if(!folder.exists()){
            folder.mkdir();
        }

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        playButton.setText("Recording....");


        Long tsLong=System.currentTimeMillis()/1000;
        String ts=tsLong.toString();
        fileName="audio_"+ts;
        file=new File(Environment.getExternalStorageDirectory()+"/MySoundRecord/"+fileName+".mp3");
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(file.getAbsolutePath());
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioChannels(1);

        try{
            mediaRecorder.prepare();
            mediaRecorder.start();
            mStartingTimeMillis=System.currentTimeMillis();
        }catch (IOException e){
            Toast.makeText(getApplicationContext(), ""+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }
    private void stopRecording() {

        if(mediaRecorder!=null){
            playButton.setText("Record");
            playButton.setEnabled(true);



            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused=0;

            mediaRecorder.stop();
            mEllapsedMillis=(System.currentTimeMillis()-mStartingTimeMillis);
            mediaRecorder.release();
            String fileName=System.currentTimeMillis()+userDb.getUserPhone()+".mp3";
            StorageReference filePath=storageReference.child(fileName);
            Uri uri=Uri.fromFile(new File(file.getAbsolutePath()));

            progressDialog.setMessage("Uploading Order...");
            progressDialog.setTitle("Please Wait..");
            progressDialog.setCancelable(false);
            progressDialog.show();
            filePath.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> urlTask=taskSnapshot.getStorage().getDownloadUrl();
                            while(!urlTask.isSuccessful());
                            Uri downloaduri=urlTask.getResult();
                            progressDialog.dismiss();
                            OrderServices orderServices=new OrderServices(UEasyOrderActivity.this);
                            orderServices.saveNewOrder(downloaduri.toString(),mEllapsedMillis);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }


    }
    private void checkUserPermission() {
        Dexter.withContext(UEasyOrderActivity.this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            startRecording();
                        }else{
                            Toast.makeText(UEasyOrderActivity.this, "Please Allow The Permissions", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check();
    }
    private void logout() {
        UserDb userDb=new UserDb(this);
        userDb.setUserType("");
        startActivity(new Intent(UEasyOrderActivity.this, StartActivity.class));
        finish();
    }
    private void init() {
        toolbar=findViewById(R.id.appBariId);
        setSupportActionBar(toolbar);
        this.setTitle("Dokandar24");

        orderButton=findViewById(R.id.ordersButton);
        balanceTv=findViewById(R.id.balanceTv);
        pendingOrderButton=findViewById(R.id.pendingButton);
        successOrderButton=findViewById(R.id.successButton);
        playButton=findViewById(R.id.startButton);
        pauseButton=findViewById(R.id.stopButton);
        chronometer=findViewById(R.id.chronometer);




    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ordersButton){
            sendUserToOrderListActivity();
        } if(v.getId()==R.id.pendingButton){
            sendUserToPendingOrderActivity();
        }if(v.getId()==R.id.successButton){
            sendUserToSuccessOrderActivity();
        }
    }
    private void sendUserToOrderListActivity() {
        startActivity(new Intent(UEasyOrderActivity.this,UOrdersActivity.class));
    }
    private void sendUserToPendingOrderActivity() {
        startActivity(new Intent(UEasyOrderActivity.this,UPendingActivity.class));
    }
    private void sendUserToSuccessOrderActivity() {
        startActivity(new Intent(UEasyOrderActivity.this,USuccessActivity.class));
    }
}