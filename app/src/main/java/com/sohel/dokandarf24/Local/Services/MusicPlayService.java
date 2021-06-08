package com.sohel.dokandarf24.Local.Services;

import android.app.Activity;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sohel.dokandarf24.Local.Model.OrderModel;
import com.sohel.dokandarf24.Local.Model.RecordingItem;
import com.sohel.dokandarf24.R;

import java.util.concurrent.TimeUnit;

public class MusicPlayService {
    Activity context;
    TextView fileNameTv,currentProgressTv,lengthTv;
    SeekBar seekBar;
    FloatingActionButton playButton;
    UpdateMusicPlayerServices musicPlayerServices;


    public MusicPlayService(Activity context){
        this.context=context;
    }




    public void showRecordPlayDialoug(String filePath) {

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view=context.getLayoutInflater().inflate(R.layout.fragment_playback_layout,null);
        builder.setView(view);

        fileNameTv=view.findViewById(R.id.file_name_textViewid);
        currentProgressTv=view.findViewById(R.id.current_progress_textViewid);
        lengthTv=view.findViewById(R.id.file_length_text_view);
        playButton=view.findViewById(R.id.d_playButtonid);
        seekBar=view.findViewById(R.id.seekBar);


        final AlertDialog dialog=builder.create();
        musicPlayerServices=new UpdateMusicPlayerServices(context,playButton,currentProgressTv,lengthTv,seekBar,dialog);
        musicPlayerServices.playMusic(filePath);

        dialog.show();
    }
}
