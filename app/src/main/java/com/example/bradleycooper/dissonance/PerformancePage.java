package com.example.bradleycooper.dissonance;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class PerformancePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle extras = getIntent().getExtras();
        final Uri songUri = Uri.parse(extras.getString("songUri"));
        final String songTitle = extras.getString("songTitle");

        TextView textViewSongTitle = (TextView)findViewById(R.id.textViewSongTitle);
        textViewSongTitle.setText("Currently Playing = " + songTitle);

        final ImageView imageViewPlay = (ImageView)findViewById(R.id.imageViewPlay);
        final ImageView imageViewPause = (ImageView)findViewById(R.id.imageViewPause);

        imageViewPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewPause.setVisibility(View.VISIBLE);
                imageViewPlay.setVisibility(View.INVISIBLE);
                play(songUri);
            }
        });
        imageViewPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewPause.setVisibility(View.INVISIBLE);
                imageViewPlay.setVisibility(View.VISIBLE);
                pause();
            }
        });


    }
    private void play(Uri song){
        MediaPlayer mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(getApplicationContext(), song);
        } catch (IllegalArgumentException e) {
        } catch (SecurityException e) {
        } catch (IllegalStateException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mPlayer.prepare();
        } catch (IllegalStateException e) {
        } catch (IOException e) {
        }
        mPlayer.start();
    }
    private void pause(){

    }
}
