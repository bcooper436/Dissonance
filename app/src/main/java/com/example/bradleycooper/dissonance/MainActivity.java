package com.example.bradleycooper.dissonance;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

    private static Context context;
    Bluetooth connector;

    Button button;
    Button button1;
    File root = android.os.Environment.getExternalStorageDirectory();
    //tv.append("\nExternal file system root: "+root);

    // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mainData

        MainActivity.context = getApplicationContext();
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.btn_pair);
        button1 = (Button) findViewById(R.id.button1);
        // Remember to check runtime permissions for 6.0+
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 50);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d("EVEREST", "clicked");
                    connector = new Bluetooth(new EverestConnector(MainActivity.this), MainActivity.this, true);
                    connector.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

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

    public static void setStart(String data) {
        if (count < 10) {
            long seconds = SystemClock.currentThreadTimeMillis();
            start.addStartingValue(data, seconds);
            count++;
        }
        if (count == 10) {
            aveStart = start.getAverage();
            count++;
        }
        if (count > 10) {
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
