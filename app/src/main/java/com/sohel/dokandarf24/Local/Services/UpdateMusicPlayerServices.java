package com.sohel.dokandarf24.Local.Services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.sohel.dokandarf24.R;

public class UpdateMusicPlayerServices {
    private Context context;
    private ImageView imagePlayPause;
    private TextView textCurrentTime,textTotalDuration;
    private SeekBar playerSeekBar;
    private MediaPlayer mediaPlayer;
    private Handler handler=new Handler();
    private AlertDialog dialog;

    public UpdateMusicPlayerServices(Context context,ImageView imagePlayPause, TextView textCurrentTime, TextView textTotalDuration, SeekBar playerSeekBar) {
        this.context=context;
        this.imagePlayPause = imagePlayPause;
        this.textCurrentTime = textCurrentTime;
        this.textTotalDuration = textTotalDuration;
        this.playerSeekBar = playerSeekBar;
    }
    public UpdateMusicPlayerServices(Context context, ImageView imagePlayPause, TextView textCurrentTime, TextView textTotalDuration, SeekBar playerSeekBar, AlertDialog dialog) {
        this.context=context;
        this.imagePlayPause = imagePlayPause;
        this.textCurrentTime = textCurrentTime;
        this.textTotalDuration = textTotalDuration;
        this.playerSeekBar = playerSeekBar;
        this.dialog=dialog;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void playMusic(String filePath){
        mediaPlayer=new MediaPlayer();

        playerSeekBar.setMax(100);
        imagePlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    imagePlayPause.setImageResource(R.drawable.n_play_icon);
                }else{
                    mediaPlayer.start();
                    imagePlayPause.setImageResource(R.drawable.n_pause_icon);
                    updateSeekBar();
                }
            }
        });
        prepareMediaPlayer(filePath);
        playerSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                SeekBar seekBar=(SeekBar)view;
                int playPosition=(mediaPlayer.getDuration()/100)*seekBar.getProgress();
                mediaPlayer.seekTo(playPosition);
                textCurrentTime.setText((milliSecondsToTimer(mediaPlayer.getCurrentPosition())));

                return false;
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                playerSeekBar.setSecondaryProgress(percent);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playerSeekBar.setProgress(0);
                imagePlayPause.setImageResource(R.drawable.n_play_icon);
                textCurrentTime.setText("0:0");
                textTotalDuration.setText("0:0");
                mediaPlayer.reset();
                prepareMediaPlayer(filePath);
            }
        });

        if(dialog!=null){
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.stop();
                    }
                }
            });

        }



    }





    private void prepareMediaPlayer(String filePath){
        try{
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            textTotalDuration.setText(milliSecondsToTimer(mediaPlayer.getDuration()));




        }catch(Exception e){
            Toast.makeText(context, "Error"+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }
    private  Runnable updater= () -> {
        updateSeekBar();
        long currentDuration=mediaPlayer.getCurrentPosition();
        textCurrentTime.setText(milliSecondsToTimer(currentDuration));
    };
    private  void updateSeekBar(){
        if(mediaPlayer.isPlaying()){
            playerSeekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration())*100));
            handler.postDelayed(updater,1000);
        }
    }
    private String milliSecondsToTimer(long milliSeconds){
        String timerString="";
        String secondsString;
        int hours=(int)(milliSeconds/(1000*60*60));
        int minutes=(int)(milliSeconds%(1000*60*60))/(1000*60);
        int seconds=(int)(milliSeconds%(1000*60*60))%(1000*60)/1000;

        if(hours>0){
            timerString=hours+":";
        }
        if(seconds<10){
            secondsString="0"+seconds;
        }else{
            secondsString=""+seconds;
        }

        timerString=timerString+minutes+":"+secondsString;
        return timerString;




    }

}
