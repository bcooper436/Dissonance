package com.superpowered.crossexample;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import com.harman.everestelite.Bluetooth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    boolean playing = false;
    private Timer fakeTouchTimer;

    public static DataArray mainData = new DataArray();
    public static StartingValue start = new StartingValue();
    public static int count = 0;
    public static DataPoint aveStart;

    //public static TextView textViewEffectPercentage;

    public static TextView textViewFlangerPercentage;
    public static TextView textViewHiPassPercentage;
    public static TextView textViewRollPercentage;
    public static TextView textViewVolumePercentage;

    public static RelativeLayout relativeLayoutFlanger;
    public static RelativeLayout relativeLayoutHiPass;
    public static RelativeLayout relativeLayoutRoll;
    public static RelativeLayout relativeLayoutVolume;

    public static ImageView imageViewFlangerClear;
    public static ImageView imageViewHiPassClear;
    public static ImageView imageViewRollClear;
    public static ImageView imageViewVolumeClear;

    public static int flangerPercentage = 0;
    public static int hiPassValue = 0;
    public static int rollValue = 0;
    public static int volumeValue = 100;

    public static int colorNormal;
    public static int colorSelected;
    public static int colorSelectedText;
    public static int colorPrimary;

    public static String currentEffect = "flanger";
    public static int currentEffectPercentage;

    static boolean effectIsEnabled = false;

    static boolean isWaitPeriod = false;
    public static boolean isCrossfadePeriod = false;
    public static int numberOfSmartTaps = 0;

    public static Switch switchEffects;

    static ProgressDialog progress;


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
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progress = new ProgressDialog(MainActivity.this);
        Bundle extras = getIntent().getExtras();
        //final Uri songUri = Uri.parse(extras.getString("songUri"));
        //final String songTitle = extras.getString("songTitle");
        final Uri djName = Uri.parse(extras.getString("djName"));

        switchEffects = (Switch)findViewById(R.id.switchGesture);
        switchEffects.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    changeCurrentEffect();
                }else{
                    clearEffects();
                }
            }
        });

        Typeface font = Typeface.createFromAsset(getAssets(), "bat.ttf");
        TextView textViewDJName = (TextView)findViewById(R.id.textViewDJName);
        textViewDJName.setText(String.valueOf(djName));
        textViewDJName.setTypeface(font);

        colorNormal = ContextCompat.getColor(getApplicationContext(), R.color.colorMaterialGrey800);
        colorSelected = ContextCompat.getColor(getApplicationContext(), R.color.colorMaterialGrey600);
        colorSelectedText = ContextCompat.getColor(getApplicationContext(), R.color.colorMaterialGrey100);


        progress.setTitle("Calibrating Headphones");
        progress.setMessage("Stay still and down move your head in an upright position for a few seconds.");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        //TextView textViewSongTitle = (TextView)findViewById(R.id.textViewSongTitle);
        //textViewSongTitle.setText("Currently Playing = " + songTitle);


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
                    //Log.d("EVEREST", "clicked");
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
                    //Log.d("Axis from Object: ", allData.get(i).getAxisInfo());
                    //Log.d("Time from Object: ", (Long.toString(allData.get(i).getTimeInfo())));
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
                    //Log.d("******* File not found.", " Add a WRITE_EXTERNAL_STORAGE permission to the manifest?");
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        final ImageView imageViewPlay = (ImageView)findViewById(R.id.imageViewPlay);
        final ImageView imageViewPause = (ImageView)findViewById(R.id.imageViewPause);

        imageViewPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewPause.setVisibility(View.VISIBLE);
                imageViewPlay.setVisibility(View.INVISIBLE);
                play();
            }
        });
        imageViewPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewPause.setVisibility(View.INVISIBLE);
                imageViewPlay.setVisibility(View.VISIBLE);
                play();
            }
        });

        textViewFlangerPercentage = (TextView)findViewById(R.id.textViewFlangerPercentage);
        textViewHiPassPercentage = (TextView)findViewById(R.id.textViewHiPassPercentage);
        textViewRollPercentage = (TextView)findViewById(R.id.textViewRollPercentage);
        textViewVolumePercentage = (TextView)findViewById(R.id.textViewVolumePercentage);

        relativeLayoutFlanger = (RelativeLayout)findViewById(R.id.relativeLayoutFlanger);
        relativeLayoutHiPass = (RelativeLayout)findViewById(R.id.relativeLayoutHiPass);
        relativeLayoutRoll = (RelativeLayout)findViewById(R.id.relativeLayoutRoll);
        relativeLayoutVolume = (RelativeLayout)findViewById(R.id.relativeLayoutVolume);


        relativeLayoutFlanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentEffect.equalsIgnoreCase("flanger") && effectIsEnabled){
                    effectIsEnabled = false;
                    relativeLayoutFlanger.setBackgroundColor(colorNormal);
                    textViewFlangerPercentage.setTextColor(colorSelected);
                }
                else {
                    currentEffect = "flanger";
                    changeCurrentEffect();
                }
            }
        });
        relativeLayoutHiPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentEffect.equalsIgnoreCase("hipass") && effectIsEnabled){
                    effectIsEnabled = false;
                    relativeLayoutHiPass.setBackgroundColor(colorNormal);
                    textViewHiPassPercentage.setTextColor(colorSelected);
                }
                else {
                    currentEffect = "hipass";
                    changeCurrentEffect();
                }
            }
        });
        relativeLayoutRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentEffect.equalsIgnoreCase("roll") && effectIsEnabled){
                    effectIsEnabled = false;
                    relativeLayoutRoll.setBackgroundColor(colorNormal);
                    textViewRollPercentage.setTextColor(colorSelected);
                }
                else {
                    currentEffect = "roll";
                    changeCurrentEffect();
                }
            }
        });
        relativeLayoutVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentEffect.equalsIgnoreCase("volume") && effectIsEnabled){
                effectIsEnabled = false;
                relativeLayoutVolume.setBackgroundColor(colorNormal);
                textViewVolumePercentage.setTextColor(colorSelected);
            }
            else {
                currentEffect = "volume";
                changeCurrentEffect();
            }
            }
        });

        imageViewFlangerClear = (ImageView)findViewById(R.id.imageViewFlangerClear);
        imageViewHiPassClear = (ImageView)findViewById(R.id.imageViewHiPassClear);
        imageViewRollClear = (ImageView)findViewById(R.id.imageViewRollClear);
        imageViewVolumeClear = (ImageView)findViewById(R.id.imageViewVolumeClear);




        // Get the device's sample rate and buffer size to enable low-latency Android audio output, if available.
        String samplerateString = null, buffersizeString = null;
        if (Build.VERSION.SDK_INT >= 17) {
            AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            samplerateString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            buffersizeString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        }
        if (samplerateString == null) samplerateString = "44100";
        if (buffersizeString == null) buffersizeString = "512";

        // Files under res/raw are not zipped, just copied into the APK. Get the offset and length to know where our files are located.
        AssetFileDescriptor fd0 = getResources().openRawResourceFd(R.raw.lycka), fd1 = getResources().openRawResourceFd(R.raw.nuyorica);
        int fileAoffset = (int)fd0.getStartOffset(), fileAlength = (int)fd0.getLength(), fileBoffset = (int)fd1.getStartOffset(), fileBlength = (int)fd1.getLength();
        try {
            fd0.getParcelFileDescriptor().close();
            fd1.getParcelFileDescriptor().close();
        } catch (IOException e) {
            android.util.Log.d("", "Close error.");
        }

        // Arguments: path to the APK file, offset and length of the two resource files, sample rate, audio buffer size.
        SuperpoweredExample(Integer.parseInt(samplerateString), Integer.parseInt(buffersizeString), getPackageResourcePath(), fileAoffset, fileAlength, fileBoffset, fileBlength);

        // crossfader events
        final SeekBar crossfader = (SeekBar)findViewById(R.id.crossFader);
        if (crossfader != null) crossfader.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onCrossfader(progress);
                Log.d("Crossfade progress =  ", progress + "");
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        /*
        // fx fader events
        final SeekBar fxfader = (SeekBar)findViewById(R.id.fxFader);
        if (fxfader != null) fxfader.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onFxValue(progress);
                updateCurrentEffectValue(progress);
                //Log.d("FX Value =  ", progress + "");

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                onFxValue(seekBar.getProgress());
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                onFxOff();
            }
        });

        // fx select event
        final RadioGroup group = (RadioGroup)findViewById(R.id.radioGroup1);
        if (group != null) group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(checkedId);
                onFxSelect(radioGroup.indexOfChild(checkedRadioButton));
                Log.d("FX Index Value =  ", radioGroup.indexOfChild(checkedRadioButton) + "");
            }
        }); */



    }
private static void clearEffects(){
    switchEffects.setChecked(false);
    effectIsEnabled = false;
    relativeLayoutFlanger.setBackgroundColor(colorNormal);
    relativeLayoutHiPass.setBackgroundColor(colorNormal);
    relativeLayoutRoll.setBackgroundColor(colorNormal);
    relativeLayoutVolume.setBackgroundColor(colorNormal);

    textViewFlangerPercentage.setTextColor(colorSelected);
    textViewHiPassPercentage.setTextColor(colorSelected);
    textViewRollPercentage.setTextColor(colorSelected);
    textViewVolumePercentage.setTextColor(colorSelected);

}
    private static void changeCurrentEffect(){
        switchEffects.setChecked(true);
        effectIsEnabled = true;
        switch(currentEffect){
            case "flanger":
                onFxSelect(0);
                relativeLayoutFlanger.setBackgroundColor(colorSelected);
                relativeLayoutHiPass.setBackgroundColor(colorNormal);
                relativeLayoutRoll.setBackgroundColor(colorNormal);
                relativeLayoutVolume.setBackgroundColor(colorNormal);

                textViewFlangerPercentage.setTextColor(colorSelectedText);
                textViewHiPassPercentage.setTextColor(colorSelected);
                textViewRollPercentage.setTextColor(colorSelected);
                textViewVolumePercentage.setTextColor(colorSelected);

                textViewHiPassPercentage.setText("0%");
                textViewRollPercentage.setText("0%");

                break;
            case "hipass":
                onFxSelect(1);
                relativeLayoutFlanger.setBackgroundColor(colorNormal);
                relativeLayoutHiPass.setBackgroundColor(colorSelected);
                relativeLayoutRoll.setBackgroundColor(colorNormal);
                relativeLayoutVolume.setBackgroundColor(colorNormal);

                textViewFlangerPercentage.setTextColor(colorSelected);
                textViewHiPassPercentage.setTextColor(colorSelectedText);
                textViewRollPercentage.setTextColor(colorSelected);
                textViewVolumePercentage.setTextColor(colorSelected);

                textViewFlangerPercentage.setText("0%");
                textViewRollPercentage.setText("0%");


                break;
            case "roll":
                onFxSelect(2);
                relativeLayoutFlanger.setBackgroundColor(colorNormal);
                relativeLayoutHiPass.setBackgroundColor(colorNormal);
                relativeLayoutRoll.setBackgroundColor(colorSelected);
                relativeLayoutVolume.setBackgroundColor(colorNormal);

                textViewFlangerPercentage.setTextColor(colorSelected);
                textViewHiPassPercentage.setTextColor(colorSelected);
                textViewRollPercentage.setTextColor(colorSelectedText);
                textViewVolumePercentage.setTextColor(colorSelected);


                textViewFlangerPercentage.setText("0%");
                textViewHiPassPercentage.setText("0%");


                break;
            case "volume":
                relativeLayoutFlanger.setBackgroundColor(colorNormal);
                relativeLayoutHiPass.setBackgroundColor(colorNormal);
                relativeLayoutRoll.setBackgroundColor(colorNormal);
                relativeLayoutVolume.setBackgroundColor(colorSelected);


                textViewFlangerPercentage.setTextColor(colorSelected);
                textViewHiPassPercentage.setTextColor(colorSelected);
                textViewRollPercentage.setTextColor(colorSelected);
                textViewVolumePercentage.setTextColor(colorSelectedText);


                textViewFlangerPercentage.setText("0%");
                textViewHiPassPercentage.setText("0%");
                textViewRollPercentage.setText("0%");
                break;
            default:
                break;
        }
    }
    private static void updateCurrentEffectValue(int value){
        switch(currentEffect){
            case "flanger":
                textViewFlangerPercentage.setText(String.valueOf(value) + "%");
                break;
            case "hipass":
                textViewHiPassPercentage.setText(String.valueOf(value) + "%");
                break;
            case "roll":
                textViewRollPercentage.setText(String.valueOf(value) + "%");
                break;
            case "volume":
                textViewVolumePercentage.setText(String.valueOf(value) + "%");
                break;
            default:
                break;
        }
    }
    private void play(){
        playing = !playing;
        onPlayPause(playing);
    }
    private void pause(){
        playing = !playing;
        onPlayPause(playing);
    }
    public static void dismissDialogue(){
        progress.hide();
    }
    private static void changeEffectGesture(){
        isWaitPeriod = true;
        Log.d("Wait Period Activated", "");

        if(currentEffect.equals("flanger")){
            currentEffect = "hipass";
            Log.d("Effect changed to = ", currentEffect);
        }
        else if(currentEffect.equals("hipass")){
            currentEffect = "flanger";
            Log.d("Effect changed to = ", currentEffect);
        }
        changeCurrentEffect();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isWaitPeriod = false;
                Log.d("Wait Period Closed", "");
            }
        }, 5000);

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
    public static void changeVolume(double y){
        if(effectIsEnabled) {
            y = -y;
            y = y - 10;
            if(y < 0)
                y = 0;

            String yString = String.format("%.1f", y);
            y = Double.valueOf(yString);

            int maxValueForEffect = 0;
            int inputRangeSize = 10;
            double result;

            switch(currentEffect){
                case "flanger":
                    maxValueForEffect = 230;
                    break;
                case "hipass":
                    maxValueForEffect = 60;
                    break;
                case "roll":
                    maxValueForEffect = 40;
                    break;
                default:
                    break;
            }


            result = (double)(maxValueForEffect/inputRangeSize) * y;
            if(result > maxValueForEffect){
                result = maxValueForEffect;
            }
            int resultInt = (int)result;


            double percentageDouble = (result/maxValueForEffect)*100;
            int percentage = (int)percentageDouble;
            if(percentage < 0)
                percentage = 0;


            //AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            //mAudioManager.setSpeakerphoneOn(true);
            //int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

            //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);

            updateCurrentEffectValue(percentage);
            onFxValue(resultInt);
            //Log.d("Percent =  ", percentage + "");
            //Log.d("Y Value =  ", y + "");
            //Log.d("Effect Value =  ", resultInt + "");
        }
    }
    public static void setStart(String data) {
        if (count < 50) {
            //Log.d("SOMETHING", 1 + "");

            long seconds = SystemClock.currentThreadTimeMillis();
            start.addStartingValue(data, seconds);
            count++;
        }
        if (count == 50) {
            //Dismiss Progress Bar Here
            dismissDialogue();
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
                        if (cgyX > gyX + .3 || cgyX < gyX - .3) {
                            //Log.d("Change Gyro X ", cgyX - gyX + "");

                        }
                    case 1:
                        double gyY = Double.parseDouble(dataPointArray[1]);
                        double cgyY = Double.parseDouble(currentPointArray[1]);
                        if (cgyY > gyY + 3 || cgyY < gyY - 3) {
                            if(effectIsEnabled) {
                                //Log.d("Change Gyro Y ", cgyY - gyY + "");
                                changeVolume(cgyY - gyY);
                            }
                        }

                    case 2:
                        double gyZ = Double.parseDouble(dataPointArray[2]);
                        double cgyZ = Double.parseDouble(currentPointArray[2]);
                        if (cgyZ > gyZ + 1 || cgyZ < gyZ - 1) {
                            //Log.d("Change Gyro Z ", cgyZ - gyZ + "");
                            if(isCrossfadePeriod){
                                double y = cgyZ - gyZ;
                                y = y + 5;
                                int progress;
                                int maxValue = 100;
                                int dataRangeSize = 10;

                                double result = (maxValue/dataRangeSize)*y;
                                progress = (int)result;
                                if(progress < 0 ){
                                    progress = 0;
                                }
                                else if(progress > 100) {
                                    progress = 100;
                                }
                                onCrossfader(progress);
                                Log.d("Crossfade Progress = ", progress + "");
                            }
                        }

                    case 3:
                        double acX = Double.parseDouble(dataPointArray[3]);
                        double cacX = Double.parseDouble(currentPointArray[3]);
                        if (cacX > acX + .3 || cacX < acX - .3) {
                            //Log.d("Change Accel X ", cacX - acX + "");
                            if((cacX - acX) > 1.15 && !isWaitPeriod){
                                changeEffectGesture();
                                Log.d("Triggered! Effect", " Change Gesture");
                            }

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
    public static void registerSmartButtonClick(){
        if(isCrossfadePeriod){
            isCrossfadePeriod = false;
            Log.d("Crossfade Period", "Closed");
        }
        else {
            numberOfSmartTaps++;
            if (numberOfSmartTaps > 1) {
                Log.d("Crossfade Period", "Activated");
                clearEffects();
                Log.d("EFFECT TOGGLE", "Turn Off");
                isCrossfadePeriod = true;
            }
            else{
                turnOnOrOffEffect();
            }
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    numberOfSmartTaps = 0;
                    Log.d("Timer finished, ", "# of Smart Taps = 0");

                }
            }, 2000);
        }
    }
    public static void turnOnOrOffEffect(){
        if(effectIsEnabled){
            clearEffects();
            Log.d("EFFECT TOGGLE", "Turn Off");
        }
        else{
            changeCurrentEffect();
            Log.d("EFFECT TOGGLE", "Turn On");
        }
    }
    /*
    public void SuperpoweredExample_PlayPause(View button) {  // Play/pause.
        playing = !playing;

        // Sending fake touches every second helps sustaining CPU rate.
        // This is not necessary for this little app, but might be helpful for projects with big audio processing needs.
        if (playing) {
            TimerTask fakeTouchTask = new TimerTask() {
                public void run() {
                    try {
                        Instrumentation instrumentation = new Instrumentation();
                        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACKSLASH);
                    } catch(java.lang.Exception e) {
                        assert true;
                    }
                }
            };
            fakeTouchTimer = new Timer();
            fakeTouchTimer.schedule(fakeTouchTask, 1000, 1000);
        } else {
            fakeTouchTimer.cancel();
            fakeTouchTimer.purge();
        }

        onPlayPause(playing);
        Button b = (Button) findViewById(R.id.playPause);
        if (b != null) b.setText(playing ? "Pause" : "Play");
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private native void SuperpoweredExample(int samplerate, int buffersize, String apkPath, int fileAoffset, int fileAlength, int fileBoffset, int fileBlength);
    private native void onPlayPause(boolean play);
    private static native void onCrossfader(int value);
    private static native void onFxSelect(int value);
    private native void onFxOff();
    private static native void onFxValue(int value);

    static {
        System.loadLibrary("SuperpoweredExample");
    }
}
