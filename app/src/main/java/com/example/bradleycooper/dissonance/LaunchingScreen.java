package com.example.bradleycooper.dissonance;

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
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class LaunchingScreen extends AppCompatActivity {
    boolean isPaired = false, isPlaylist = false, isTwitter = false, isReturningFromSet = false;
    String headphoneColor = "White";
    Uri selectedSong;
    String songTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launching_screen);

        initButtonReactions();

        TextView textViewLogo = (TextView) findViewById(R.id.textViewLogo);
        TextView textViewPair = (TextView) findViewById(R.id.textViewPair);
        TextView textViewPlaylist = (TextView) findViewById(R.id.textViewPlaylist);
        TextView textViewTwitter = (TextView) findViewById(R.id.textViewTwitter);
        TextView textViewStart = (TextView) findViewById(R.id.textViewStart);
        TextView textViewPairButton = (TextView) findViewById(R.id.textViewPairButton);
        TextView textViewPlaylistButton = (TextView) findViewById(R.id.textViewSongTitle);
        TextView textViewTwitterButton = (TextView) findViewById(R.id.textViewTwitterButton);
        TextView textViewAppDescription = (TextView) findViewById(R.id.textViewAppDescription);
        TextView textViewGetStarted = (TextView)findViewById(R.id.textViewGetStarted);
        Typeface font = Typeface.createFromAsset(getAssets(), "bat.ttf");
        Typeface font2 = Typeface.createFromAsset(getAssets(), "bato.ttf");
        Typeface font3 = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");

        textViewLogo.setTypeface(font);
        textViewPair.setTypeface(font);
        textViewPlaylist.setTypeface(font);
        textViewTwitter.setTypeface(font);
        textViewStart.setTypeface(font3);
        textViewPairButton.setTypeface(font3);
        textViewPlaylistButton.setTypeface(font3);
        textViewTwitterButton.setTypeface(font3);
        textViewAppDescription.setTypeface(font3);
        textViewGetStarted.setTypeface(font);
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
        final RelativeLayout relativeLayoutTwitter = (RelativeLayout) findViewById(R.id.relativeLayoutTwitter);
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
        relativeLayoutTwitter.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                relativeLayoutTwitter.setBackground(getResources().getDrawable(R.drawable.button_circle_full));
                imageViewRedo.setBackground(getResources().getDrawable(R.drawable.startoveralt));
                connectTwitter();
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
        ImageView imageViewTwitterCheck = (ImageView)findViewById(R.id.imageViewTwitterCheck);

        TextView textViewPair = (TextView) findViewById(R.id.textViewPair);
        TextView textViewPlaylist = (TextView) findViewById(R.id.textViewPlaylist);
        TextView textViewTwitter = (TextView) findViewById(R.id.textViewTwitter);

        int colorGray = ContextCompat.getColor(getApplicationContext(), R.color.colorMaterialGrey900);

        /*
        ImageView imageViewPairX = (ImageView)findViewById(R.id.imageViewPairX);
        ImageView imageViewPlaylistX = (ImageView)findViewById(R.id.imageViewPlaylistX);
        ImageView imageViewTwitterX = (ImageView)findViewById(R.id.imageViewTwitterX);  */

        if(isPaired){
            //imageViewPairX.setVisibility(View.INVISIBLE);
            imageViewPairCheck.setVisibility(View.VISIBLE);
            textViewPair.setTextColor(colorGray);
        }
        if(isPlaylist){
            //imageViewPlaylistX.setVisibility(View.INVISIBLE);
            imageViewPlaylistCheck.setVisibility(View.VISIBLE);
            textViewPlaylist.setTextColor(colorGray);
        }
        if(isTwitter){
            //imageViewTwitterX.setVisibility(View.INVISIBLE);
            imageViewTwitterCheck.setVisibility(View.VISIBLE);
            textViewTwitter.setTextColor(colorGray);
        }
        if(isPaired && isPlaylist && isTwitter){
            relativeLayoutStartSet.setVisibility(View.VISIBLE);
            relativeLayoutStartSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(LaunchingScreen.this, MainActivity.class);
                    myIntent.putExtra("songUri", selectedSong.toString());
                    myIntent.putExtra("songTitle", songTitle);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    isReturningFromSet = true;
                    LaunchingScreen.this.startActivity(myIntent);
                }
            });
        }
        ImageView imageViewInfo = (ImageView)findViewById(R.id.imageViewInfo);
        imageViewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(LaunchingScreen.this).create();
                alertDialog.setTitle("How to Use Dissonance");
                alertDialog.setMessage("Dissonance is designed for the JBL Everest Elite 700 Headphones. Move your head to control audio effects. Click on the side button to change the active effect.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
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
        updateStatusOfStartup();
    }
    private void createPlaylist(){
        isPlaylist = true;

        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload,1);
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
    private void connectTwitter(){
        isTwitter = true;
        updateStatusOfStartup();
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void reset(){
        final RelativeLayout relativeLayoutPair = (RelativeLayout) findViewById(R.id.relativeLayoutPair);
        final RelativeLayout relativeLayoutPlaylist = (RelativeLayout) findViewById(R.id.relativeLayoutPlaylist);
        final RelativeLayout relativeLayoutTwitter = (RelativeLayout) findViewById(R.id.relativeLayoutTwitter);
        relativeLayoutPair.setBackground(getResources().getDrawable(R.drawable.button_circle));
        relativeLayoutPlaylist.setBackground(getResources().getDrawable(R.drawable.button_circle));
        relativeLayoutTwitter.setBackground(getResources().getDrawable(R.drawable.button_circle));

        isPlaylist = false;
        isPaired = false;
        isTwitter = false;

        RelativeLayout relativeLayoutStartSet = (RelativeLayout)findViewById(R.id.relativeLayoutStartSet);
        relativeLayoutStartSet.setVisibility(View.INVISIBLE);

        ImageView imageViewPairCheck = (ImageView)findViewById(R.id.imageViewPairCheck);
        ImageView imageViewPlaylistCheck = (ImageView)findViewById(R.id.imageViewPlaylistCheck);
        ImageView imageViewTwitterCheck = (ImageView)findViewById(R.id.imageViewTwitterCheck);

        imageViewPairCheck.setVisibility(View.INVISIBLE);
        imageViewPlaylistCheck.setVisibility(View.INVISIBLE);
        imageViewTwitterCheck.setVisibility(View.INVISIBLE);

        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        TextView textViewPair = (TextView) findViewById(R.id.textViewPair);
        TextView textViewPlaylist = (TextView) findViewById(R.id.textViewPlaylist);
        TextView textViewTwitter = (TextView) findViewById(R.id.textViewTwitter);

        textViewPair.setTextColor(colorPrimary);
        textViewPlaylist.setTextColor(colorPrimary);
        textViewTwitter.setTextColor(colorPrimary);

        //ImageView imageViewPairX = (ImageView)findViewById(R.id.imageViewPairX);
        //ImageView imageViewPlaylistX = (ImageView)findViewById(R.id.imageViewPlaylistX);
        //ImageView imageViewTwitterX = (ImageView)findViewById(R.id.imageViewTwitterX);
        //imageViewPairX.setVisibility(View.VISIBLE);
        //imageViewPlaylistX.setVisibility(View.VISIBLE);
        //imageViewTwitterX.setVisibility(View.VISIBLE);

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
