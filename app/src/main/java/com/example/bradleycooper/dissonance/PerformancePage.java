package com.example.bradleycooper.dissonance;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.harman.everestelite.Bluetooth;

import java.io.File;
import java.io.IOException;

public class PerformancePage extends AppCompatActivity {
    public static DataArray mainData = new DataArray();
    public static StartingValue start = new StartingValue();
    public static int count = 0;
    public static DataPoint aveStart;

    private static Context context;
    Bluetooth connector;

    Button button;
    Button button1;
    File root = android.os.Environment.getExternalStorageDirectory();

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
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        try {
            Bluetooth connector = new Bluetooth(new EverestConnector(this), this, true);
            connector.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void setData(String data) {
        // Log.d("Data to object", data);
        //Calendar c = Calendar.getInstance();
        //int seconds = c.get(Calendar.SECOND);
        long seconds = SystemClock.currentThreadTimeMillis();
        mainData.addData(data, seconds);

    }
    public static void setStart(String data) {
        if (count < 50) {
            long seconds = SystemClock.currentThreadTimeMillis();
            start.addStartingValue(data, seconds);
            count++;
        }
        if (count == 50) {
            aveStart = start.getAverage();
            count++;
        }
        if (count > 50) {
            String avAxis = aveStart.getAxisInfo();
            String[] dataPointArray = avAxis.split(",");

            DataPoint current = new DataPoint(data, SystemClock.currentThreadTimeMillis());
            String[] currentPointArray = current.getAxisInfo().split(",");
            for (int i = 0; i < dataPointArray.length; i++) {
                switch (i) {
                    case 0:
                        double gyX = Double.parseDouble(dataPointArray[0]);
                        double cgyX = Double.parseDouble(currentPointArray[0]);
                        if (cgyX > gyX + 3 || cgyX < gyX - 3) {
                            Log.d("Change Gyro X ", cgyX - gyX + "");
                        }
                    case 1:
                        double gyY = Double.parseDouble(dataPointArray[1]);
                        double cgyY = Double.parseDouble(currentPointArray[1]);
                        if (cgyY > gyY + 3 || cgyY < gyY - 3) {
                            Log.d("Change Gyro Y ", cgyY - gyY + "");
                        }

                    case 2:
                        double gyZ = Double.parseDouble(dataPointArray[2]);
                        double cgyZ = Double.parseDouble(currentPointArray[2]);
                        if (cgyZ > gyZ + 3 || cgyZ < gyZ - 3) {
                            Log.d("Change Gyro Z ", cgyZ - gyZ + "");
                        }

                    case 3:
                        double acX = Double.parseDouble(dataPointArray[3]);
                        double cacX = Double.parseDouble(currentPointArray[3]);
                        if (cacX > acX + .3 || cacX < acX - .3) {
                            Log.d("Change Accel X ", cacX - acX + "");
                        }

                    case 4:
                        double acY = Double.parseDouble(dataPointArray[4]);
                        double cacY = Double.parseDouble(currentPointArray[4]);
                        if (cacY > acY + .3 || cacY < acY - .3) {
                            Log.d("Change Accel Y ", cacY - acY + "");
                        }

                    case 5:
                        double acZ = Double.parseDouble(dataPointArray[5]);
                        double cacZ = Double.parseDouble(currentPointArray[5]);
                        if (cacZ > acZ + .3 || cacZ < acZ - .3) {
                            Log.d("Change Accel Z ", cacZ - acZ + "");
                        }

                    case 6:
                        double maX = Double.parseDouble(dataPointArray[6]);
                        double cmaX = Double.parseDouble(currentPointArray[6]);
                        if (cmaX > maX + 3500 || cmaX < maX - 3600) {
                            Log.d("Change Magna X ", cmaX - maX + "");
                        }

                    case 7:
                        double maY = Double.parseDouble(dataPointArray[6]);
                        double cmaY = Double.parseDouble(currentPointArray[6]);
                        if (cmaY > maY + 3500 || cmaY < maY - 3600) {
                            Log.d("Change Magna Y ", cmaY - maY + "");
                        }

                    case 8:
                        double maZ = Double.parseDouble(dataPointArray[6]);
                        double cmaZ = Double.parseDouble(currentPointArray[6]);
                        if (cmaZ > maZ + 3500 || cmaZ < maZ - 3600) {
                            Log.d("Change Magna Z ", cmaZ - maZ + "");
                        }
                }

            }
        }
    }

}
