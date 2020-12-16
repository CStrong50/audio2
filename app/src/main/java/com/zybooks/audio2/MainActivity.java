package com.zybooks.audio2;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private Button startButton, stopButton, playButton, stopPlay;
    private MediaRecorder myRecorder;
    private MediaPlayer myPlayer;
    private static final String LOG_TAG = "AudioRecording";
    private static String myFileName = null;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start recording button
        startButton = (Button)findViewById(R.id.button_Record);
        //stop recording button
        stopButton = (Button)findViewById(R.id.button_Stop);
        //playback button
        playButton = (Button)findViewById(R.id.button_Play);
        //stops playback
        stopPlay = (Button)findViewById(R.id.button_StopPlay);
        //sets stop button to false
        stopButton.setEnabled(false);
        //sets play button to false
        playButton.setEnabled(false);
        //sets stop playback to false
        stopPlay.setEnabled(false);

        //where the recording is being stored
        myFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        myFileName += "/AudioRecording.3gp";

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //once clicked it is checking to see if it has the permission needed to continue
                if(CheckPermissions()) {
                    //sets stop button to true
                    stopButton.setEnabled(true);
                    //sets start button to false
                    startButton.setEnabled(false);
                    //sets play button to false
                    playButton.setEnabled(false);
                    //sets stop playback button to false
                    stopPlay.setEnabled(false);
                    //get a new media recorder
                    myRecorder = new MediaRecorder();
                    //the mic is chose as the audio source
                    myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    // chooses the output format
                    //three  gpp
                    myRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_2_TS);
                    //chooses the audio encoder
                    myRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    //sets the fileName
                    myRecorder.setOutputFile(myFileName);
                    try {
                        //prepares the recorded content
                        myRecorder.prepare();
                    } catch (IOException e) {
                        //if prepared was unsuccessful this IOexception will appear
                        Log.e(LOG_TAG, "prepare() failed");
                    }
                    myRecorder.start();
                    //if it is successful recording will start
                    Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_LONG).show();
                }
                else
                {
                    RequestPermissions();
                }
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sets stop button to false
                stopButton.setEnabled(false);
                //sets start button to true
                startButton.setEnabled(true);
                //sets play button to true
                playButton.setEnabled(true);
                //sets stop playback button to true
                stopPlay.setEnabled(true);
                //stops recording
                myRecorder.stop();
                //releases the recording
                myRecorder.release();
                myRecorder = null;
                //lets user know the recording has stopped
                Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_LONG).show();
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sets stop button to false
                stopButton.setEnabled(false);
                //sets start button to true
                startButton.setEnabled(true);
                //sets play button to true
                playButton.setEnabled(false);
                //sets stop playback button to true
                stopPlay.setEnabled(true);
                //new mediaPlayer is created
                myPlayer = new MediaPlayer();
                try {
                    //sets myFileName
                    myPlayer.setDataSource(myFileName);
                    //prepares the media player
                    myPlayer.prepare();
                    //starts the media player
                    myPlayer.start();
                    //lets the user know the recording has started
                    Toast.makeText(getApplicationContext(), "Recording Started Playing", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    //of the prepared fails this message appears
                    Log.e(LOG_TAG, "prepare() failed");
                }
            }
        });
        stopPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myPlayer.release();
                myPlayer = null;
                //sets stop button to false
                stopButton.setEnabled(false);
                //sets start button to true
                startButton.setEnabled(true);
                //sets play button to true
                playButton.setEnabled(true);
                //sets stop playback button to false
                stopPlay.setEnabled(false);
                //lets user know the audio has stopped
                Toast.makeText(getApplicationContext(),"Playing Audio Stopped", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    //permission results
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            //request audio permission
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length> 0) {
                    //if permissions are given to record
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    //if permissions are given to store
                    boolean permissionToStore = grantResults[1] ==  PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        //lets user know permission is granted
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        //lets user know if permissions are denied
                        Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    //checks to see if permissions are granted
    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
    //requests the user to give permission
    private void RequestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }
}