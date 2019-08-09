package com.example.runtimepermission;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayersActivity extends AppCompatActivity {
    Button btn_next,btn_previous,btn_pause;
    TextView songTextLabel;
    SeekBar songSeekbar;



    //Handle incoming phone calls
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;


    static MediaPlayer myMediaPlayer;
    int position;
    String sname;

    ArrayList<File> mySongs;
    Thread updateseekBar;



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);


        btn_next = (Button) findViewById(R.id.next);
        btn_previous = (Button) findViewById(R.id.previous);
        btn_pause = (Button) findViewById(R.id.pause);

        songTextLabel = (TextView) findViewById(R.id.songLabel);
        songSeekbar = (SeekBar) findViewById(R.id.seekBar);


        updateseekBar = new Thread() {
            @Override
            public void run() {
                int totalDuration = myMediaPlayer.getDuration();
                int currentPosition = 0;
                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = myMediaPlayer.getCurrentPosition();
                        songSeekbar.setProgress(currentPosition);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        if(myMediaPlayer!=null){
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }
        Intent i=getIntent();
        Bundle bundle=i.getExtras();

        mySongs=(ArrayList) bundle.getParcelableArrayList("songs");

        sname=mySongs.get(position).getName().toString();

        String songName=i.getStringExtra("songname");

        songTextLabel.setText(songName);
        songTextLabel.setSelected(true);
        position=bundle.getInt("pos",0);
        Uri u=Uri.parse(mySongs.get(position).toString());

        myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);

        myMediaPlayer.start();

        songSeekbar.setMax(myMediaPlayer.getDuration());
        updateseekBar.start();

        songSeekbar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        songSeekbar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN );

        songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myMediaPlayer.seekTo(seekBar.getProgress());

            }
        });
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPause();
            }
        });


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previous();
            }
        });

        callStateListener();/*this function is called for calls received by cell phone.if there is a call,then for that time the
                                     media will stop and as the call ends,the music will be played again*/
                              /*this function is made below at last and is called under inCreate method*/


    }

    public void playPause()   /*created functon for play pause button*/
    {
        songSeekbar.setMax(myMediaPlayer.getDuration());
        if(myMediaPlayer.isPlaying()) {
            btn_pause.setBackgroundResource(R.drawable.icon_play);
            myMediaPlayer.pause();
        } else {
            btn_pause.setBackgroundResource(R.drawable.icon_pause);
            myMediaPlayer.start();
        }
    }

    public void next()/*created function for next button*/
    {
        myMediaPlayer.stop();
        myMediaPlayer.release();
        position= ((position+1)%mySongs.size());
        Uri u=Uri.parse(mySongs.get(position).toString());
        myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);
        sname =mySongs.get(position).getName().toString();
        songTextLabel.setText(sname);
        myMediaPlayer.start();
    }
    public void previous()   /*created function for previous button*/
    {
        myMediaPlayer.stop();
        myMediaPlayer.release();
        position=((position-1)<0)?(mySongs.size()-1):(position-1);
        Uri u=Uri.parse(mySongs.get(position).toString());
        myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);
        sname =mySongs.get(position).getName().toString();
        songTextLabel.setText(sname);
        myMediaPlayer.start();
    }
    //Handle incoming phone calls
    private void callStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (myMediaPlayer != null) {
                            myMediaPlayer.pause();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (myMediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                playPause();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
