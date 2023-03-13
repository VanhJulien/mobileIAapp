package com.example.spotiflop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.File;
import java.io.IOException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private MediaRecorder recorder;
    private String outputFile;
    private boolean isRecording = false;
    private Button recordButton;
    private Button playButton;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordButton = findViewById(R.id.recordButton);
        playButton = findViewById(R.id.playButton);

        // Set click listener for record button
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRecording) {
                    startRecording();
                } else {
                    stopRecording();
                }
            }
        });

        // Set click listener for play button
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player == null) {
                    startPlaying();
                } else {
                    stopPlaying();
                }
            }
        });
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
            return result == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        outputFile = getExternalCacheDir().getAbsolutePath() + "/recording.3gp";
        recorder.setOutputFile(outputFile);
        try {
            recorder.prepare();
            recorder.start();
            isRecording = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update record button text
        recordButton.setText("Stop Recording");

        // Disable play button
        playButton.setEnabled(false);
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        isRecording = false;

        // Display "Recording stopped" message
        Toast.makeText(getApplicationContext(), "Recording stopped", Toast.LENGTH_SHORT).show();

        // Update record button text
        recordButton.setText("Start Recording");

        // Enable play button
        playButton.setEnabled(true);

        uploadFile(outputFile);
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(outputFile);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update play button text
        playButton.setText("Stop Playing");

        // Disable record button
        recordButton.setEnabled(false);
    }

    private void stopPlaying() {
        player.release();
        player = null;

        // Update play button text
        playButton.setText("Start Playing");

        // Enable record button
        recordButton.setEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Release media player and recorder resources when the activity is stopped
        if (player != null) {
            player.release();
            player = null;
        }
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

    private void uploadFile(String filePath) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", "recording.3gp",
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File(filePath)))
                .build();
        Request request = new Request.Builder()
                .url("http://your-api-url.com/upload")
                .method("POST", body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                // File uploaded successfully
            } else {
                // File upload failed
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

