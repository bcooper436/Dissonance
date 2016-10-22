package com.example.bradleycooper.dissonance;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.harman.everestelite.Bluetooth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


public class MainActivity extends Activity {
    //public static ArrayList <String> allData;
    public static DataArray mainData = new DataArray();
    public static StartingValue start = new StartingValue();
    public static int count = 0;
    public static DataPoint aveStart;
    static boolean effectIsEnabled = false;

    TextView textViewEffectName = (TextView)findViewById(R.id.textViewCurrentEffect);

    private static Context context;
    Bluetooth connector;

    Button button;
    Button button1;
    File root = android.os.Environment.getExternalStorageDirectory();
    //tv.append("\nExternal file system root: "+root);

    // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder
    static double newVolume = -9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mainData
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        MainActivity.context = getApplicationContext();
        setContentView(R.layout.activity_main);

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


        button = (Button) findViewById(R.id.btn_pair);
        button1 = (Button) findViewById(R.id.button1);
        // Remember to check runtime permissions for 6.0+
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 50);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*try {
                    Log.d("EVEREST", "clicked");
                    connector = new Bluetooth(new EverestConnector(MainActivity.this), MainActivity.this, true);
                    connector.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<DataPoint> allData = mainData.getDataArray();
                connector.close();
                File dir = new File(root.getAbsolutePath() + "/download");
                String location = dir.getAbsolutePath().toString();
                //dir.mkdirs();
                File file = new File(dir, "myData.txt");
                FileOutputStream f = null;
                String data1 = "";
                //Log.d("The Object ", allData.toString());
                for (int i = allData.size() - 1; i >= 0; i--) {
                    data1 = allData.get(i).getAxisInfo() + " " + allData.get(i).getTimeInfo() + "\n" + data1;
                   // Log.d("Axis from Object: ", allData.get(i).getAxisInfo());
                   // Log.d("Time from Object: ", (Long.toString(allData.get(i).getTimeInfo())));
                }
                try {
                    f = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                PrintWriter pw = new PrintWriter(f, true);
                try {

                    //pw.println(data);
                    pw.append(data1);
                    //pw.println("Hello");
                    pw.flush();
                    pw.close();
                    f.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.d("******* File not found.", " Add a WRITE_EXTERNAL_STORAGE permission to the manifest?");
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }
    public static void turnOnOrOffEffect(){
        if(effectIsEnabled){
            effectIsEnabled = false;
        }
        else{
            effectIsEnabled = true;
        }
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

    public static Context getContext() {
        return MainActivity.context;
    }

    public static void setData(String data) {
       // Log.d("Data to object", data);
        //Calendar c = Calendar.getInstance();
        //int seconds = c.get(Calendar.SECOND);
        long seconds = SystemClock.currentThreadTimeMillis();
        mainData.addData(data, seconds);

    }
    public static void changeVolume(double y){
        int newVolume = 0;
        int newNewVolume = 0;
        if(y > -10) {
            newVolume = 15;
        }
        else if (y <= -10 && y > -10.5){
            newVolume = 14;
        }
        else if (y <= -10.5 && y > -11){
            newVolume = 14;
        }
        else if (y <= -11 && y > -11.5){
            newVolume = 13;
        }
        else if (y <= -11.5 && y > -12){
            newVolume = 12;
        }
        else if (y <= -12 && y > -12.5){
            newVolume = 11;
        }
        else if (y <= -12.5 && y > -13){
            newVolume = 10;
        }
        else if (y <= -13 && y > -13.5){
            newVolume = 9;
        }
        else if (y <= -13.5 && y > -14){
            newVolume = 8;
        }
        else if (y <= -14 && y > -14.5){
            newVolume = 7;
        }
        else if (y <= -14.5 && y > -15){
            newVolume = 6;
        }
        else if (y <= -15 && y > -15.5){
            newVolume = 5;
        }
        else if (y <= -15.5 && y > -16){
            newVolume = 4;
        }
        else if (y <= -16 && y > -16.5){
            newVolume = 3;
        }
        else if (y <= -16.5 && y > -17){
            newVolume = 2;
        }
        else if (y <= -17 && y > -17.5){
            newVolume = 1;
        }
        else if (y <= -17.5){
            newVolume = 0;
        }
        AudioManager mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        newNewVolume = (int)(maxVolume * newVolume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
        Log.d("max =  " , maxVolume + "");
        Log.d("volume =  " , newVolume + "");
        Log.d("y =  " , y + "");
        Log.d("newVolume =  " , newNewVolume + "");

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
                            //Log.d("Change Gyro X ", cgyX - gyX + "");
                        }
                    case 1:
                        double gyY = Double.parseDouble(dataPointArray[1]);
                        double cgyY = Double.parseDouble(currentPointArray[1]);
                        if (cgyY > gyY + 3 || cgyY < gyY - 3) {
                            Log.d("Change Gyro Y ", cgyY - gyY + "");
                            changeVolume(cgyY - gyY);
                        }

                    case 2:
                        double gyZ = Double.parseDouble(dataPointArray[2]);
                        double cgyZ = Double.parseDouble(currentPointArray[2]);
                        if (cgyZ > gyZ + 3 || cgyZ < gyZ - 3) {
                            //Log.d("Change Gyro Z ", cgyZ - gyZ + "");
                        }

                    case 3:
                        double acX = Double.parseDouble(dataPointArray[3]);
                        double cacX = Double.parseDouble(currentPointArray[3]);
                        if (cacX > acX + .3 || cacX < acX - .3) {
                            //Log.d("Change Accel X ", cacX - acX + "");
                        }

                    case 4:
                        double acY = Double.parseDouble(dataPointArray[4]);
                        double cacY = Double.parseDouble(currentPointArray[4]);
                        if (cacY > acY + .3 || cacY < acY - .3) {
                            //Log.d("Change Accel Y ", cacY - acY + "");
                        }

                    case 5:
                        double acZ = Double.parseDouble(dataPointArray[5]);
                        double cacZ = Double.parseDouble(currentPointArray[5]);
                        if (cacZ > acZ + .3 || cacZ < acZ - .3) {
                            //Log.d("Change Accel Z ", cacZ - acZ + "");
                        }

                    case 6:
                        double maX = Double.parseDouble(dataPointArray[6]);
                        double cmaX = Double.parseDouble(currentPointArray[6]);
                        if (cmaX > maX + 3500 || cmaX < maX - 3600) {
                            //Log.d("Change Magna X ", cmaX - maX + "");
                        }

                    case 7:
                        double maY = Double.parseDouble(dataPointArray[6]);
                        double cmaY = Double.parseDouble(currentPointArray[6]);
                        if (cmaY > maY + 3500 || cmaY < maY - 3600) {
                            //Log.d("Change Magna Y ", cmaY - maY + "");
                        }

                    case 8:
                        double maZ = Double.parseDouble(dataPointArray[6]);
                        double cmaZ = Double.parseDouble(currentPointArray[6]);
                        if (cmaZ > maZ + 3500 || cmaZ < maZ - 3600) {
                            //Log.d("Change Magna Z ", cmaZ - maZ + "");
                        }
                }

            }
        }
    }

}

