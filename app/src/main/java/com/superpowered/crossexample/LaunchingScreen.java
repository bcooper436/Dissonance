package com.superpowered.crossexample;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


public class LaunchingScreen extends AppCompatActivity {
    boolean isPaired = false, isPlaylist = false, isTwitter = false, isReturningFromSet = false;
    String headphoneColor = "White";
    Uri selectedSong;
    String songTitle;
    String m_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launching_screen);

        initButtonReactions();

        TextView textViewLogo = (TextView) findViewById(R.id.textViewLogo);
        TextView textViewLogo2 = (TextView) findViewById(R.id.textViewLogo2);
        TextView textViewPair = (TextView) findViewById(R.id.textViewPair);
        TextView textViewPlaylist = (TextView) findViewById(R.id.textViewPlaylist);
        TextView textViewStart = (TextView) findViewById(R.id.textViewStart);
        TextView textViewPairButton = (TextView) findViewById(R.id.textViewPairButton);
        TextView textViewPlaylistButton = (TextView) findViewById(R.id.textViewSongTitle);
        TextView textViewAppDescription = (TextView) findViewById(R.id.textViewAppDescription);
        TextView textViewGetStarted = (TextView)findViewById(R.id.textViewGetStarted);
        Typeface font = Typeface.createFromAsset(getAssets(), "bat.ttf");
        Typeface font2 = Typeface.createFromAsset(getAssets(), "bato.ttf");
        Typeface font3 = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");

        textViewLogo.setTypeface(font);
        textViewLogo2.setTypeface(font);
        textViewPair.setTypeface(font);
        textViewPlaylist.setTypeface(font);
        textViewStart.setTypeface(font3);
        textViewPairButton.setTypeface(font3);
        textViewPlaylistButton.setTypeface(font3);
        textViewAppDescription.setTypeface(font3);
        textViewGetStarted.setTypeface(font);

        ImageView imageViewHeadphones = (ImageView)findViewById(R.id.imageViewHeadphones);
        Random r = new Random();
        int randomInt = r.nextInt(3 - 1) + 1;
        if(randomInt == 1){
            imageViewHeadphones.setImageResource(R.drawable.headphoneswhitealt);
        }
        else if(randomInt == 2){
            imageViewHeadphones.setImageResource(R.drawable.headphonesblackalt);
        }




    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if(isReturningFromSet) {
            isReturningFromSet = false;
            reset();
        }
    }

    private void initButtonReactions(){
        final RelativeLayout relativeLayoutPair = (RelativeLayout) findViewById(R.id.relativeLayoutPair);
        final RelativeLayout relativeLayoutPlaylist = (RelativeLayout) findViewById(R.id.relativeLayoutPlaylist);
        final ImageView imageViewRedo = (ImageView)findViewById(R.id.imageViewRedo);
        relativeLayoutPair.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                relativeLayoutPair.setBackground(getResources().getDrawable(R.drawable.button_circle_full));
                imageViewRedo.setBackground(getResources().getDrawable(R.drawable.startoveralt));
                pairHeadphones();
            }
        });
        relativeLayoutPlaylist.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                relativeLayoutPlaylist.setBackground(getResources().getDrawable(R.drawable.button_circle_full));
                imageViewRedo.setBackground(getResources().getDrawable(R.drawable.startoveralt));
                createPlaylist();
            }
        });
        ImageView imageViewHeadphones = (ImageView)findViewById(R.id.imageViewHeadphones);
        final GestureDetector gd = new GestureDetector(LaunchingScreen.this, new GestureDetector.SimpleOnGestureListener(){
            //here is the method for double tap
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                //your action here for double tap e.g.
                Log.d("OnDoubleTapListener", "onDoubleTap");
                changeHeadphonesColor();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
            }
            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return true;
            }
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });


        imageViewHeadphones.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gd.onTouchEvent(event);
            }
        });

        ImageView imageViewInfo = (ImageView)findViewById(R.id.imageViewInfo);
        imageViewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(LaunchingScreen.this)
                        .setTitle("About Dissonance")
                        .setMessage("This app is designed to work with the JBL Everest Elite 700 Headphones.  Would you like to visit JBL's official site for more information?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.jbl.com/bluetooth-headphones/EVEREST+700+ELITE.html"));
                                startActivity(browserIntent);
                                // continue with delete
                                //
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(R.drawable.jbl)
                        .show();
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateStatusOfStartup(){
        ImageView imageViewRedo = (ImageView)findViewById(R.id.imageViewRedo);
        imageViewRedo.setBackground(getResources().getDrawable(R.drawable.startoveralt));
        imageViewRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        RelativeLayout relativeLayoutStartSet = (RelativeLayout)findViewById(R.id.relativeLayoutStartSet);
        ImageView imageViewPairCheck = (ImageView)findViewById(R.id.imageViewPairCheck);
        ImageView imageViewPlaylistCheck = (ImageView)findViewById(R.id.imageViewPlaylistCheck);

        TextView textViewPair = (TextView) findViewById(R.id.textViewPair);
        TextView textViewPlaylist = (TextView) findViewById(R.id.textViewPlaylist);

        int colorGray = ContextCompat.getColor(getApplicationContext(), R.color.colorMaterialGrey900);


        if(isPaired){
            imageViewPairCheck.setVisibility(View.VISIBLE);
            textViewPair.setTextColor(colorGray);
        }
        if(isPlaylist){
            imageViewPlaylistCheck.setVisibility(View.VISIBLE);
            textViewPlaylist.setTextColor(colorGray);
        }
        if(isPaired && isPlaylist){
            relativeLayoutStartSet.setVisibility(View.VISIBLE);
            relativeLayoutStartSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(LaunchingScreen.this, MainActivity.class);
                    //myIntent.putExtra("songUri", selectedSong.toString());
                    //myIntent.putExtra("songTitle", songTitle);
                    myIntent.putExtra("djName", m_Text);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    isReturningFromSet = true;
                    LaunchingScreen.this.startActivity(myIntent);
                }
            });
        }
    }
    private void changeHeadphonesColor(){
        ImageView imageViewHeadphones = (ImageView)findViewById(R.id.imageViewHeadphones);
        if(headphoneColor.equals("Black")) {
            imageViewHeadphones.setImageResource(R.drawable.headphoneswhitealt);
            headphoneColor = "White";
        }else {
            imageViewHeadphones.setImageResource(R.drawable.headphonesblackalt);
            headphoneColor = "Black";
        }

    }
    private void pairHeadphones(){
        isPaired = true;

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        Toast.makeText(getApplicationContext(), "Bluetooth is now enabled.  Check your Bluetooth settings to verify connection.", Toast.LENGTH_SHORT).show();
        updateStatusOfStartup();
    }
    private void createPlaylist(){
        /*
        isPlaylist = true;

        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload,1); */

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                isPlaylist = true;
                updateStatusOfStartup();
            }
        });


        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                Uri uri = data.getData();
                selectedSong = uri;
                TextView textViewSongTitle = (TextView)findViewById(R.id.textViewSongTitle);
                songTitle = getFileName(selectedSong);
                textViewSongTitle.setText("Song = " + songTitle);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        updateStatusOfStartup();
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void reset(){
        final RelativeLayout relativeLayoutPair = (RelativeLayout) findViewById(R.id.relativeLayoutPair);
        final RelativeLayout relativeLayoutPlaylist = (RelativeLayout) findViewById(R.id.relativeLayoutPlaylist);
        relativeLayoutPair.setBackground(getResources().getDrawable(R.drawable.button_circle));
        relativeLayoutPlaylist.setBackground(getResources().getDrawable(R.drawable.button_circle));

        isPlaylist = false;
        isPaired = false;
        isTwitter = false;

        RelativeLayout relativeLayoutStartSet = (RelativeLayout)findViewById(R.id.relativeLayoutStartSet);
        relativeLayoutStartSet.setVisibility(View.INVISIBLE);

        ImageView imageViewPairCheck = (ImageView)findViewById(R.id.imageViewPairCheck);
        ImageView imageViewPlaylistCheck = (ImageView)findViewById(R.id.imageViewPlaylistCheck);

        imageViewPairCheck.setVisibility(View.INVISIBLE);
        imageViewPlaylistCheck.setVisibility(View.INVISIBLE);

        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        TextView textViewPair = (TextView) findViewById(R.id.textViewPair);
        TextView textViewPlaylist = (TextView) findViewById(R.id.textViewPlaylist);

        textViewPair.setTextColor(colorPrimary);
        textViewPlaylist.setTextColor(colorPrimary);


        TextView textViewSongTitle = (TextView)findViewById(R.id.textViewSongTitle);
        textViewSongTitle.setText("Select a Song");

        ImageView imageViewRedo = (ImageView)findViewById(R.id.imageViewRedo);
        imageViewRedo.setBackground(getResources().getDrawable(R.drawable.startovergrey2));
        imageViewRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
