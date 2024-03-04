package com.example.be_perfectly_safe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Exercise extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner exerciseSpinner;
    private Button startButton;
    private TextView timerTextView;
    private VideoView videoView;

    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;

    private String[] exercises = {
            "躺姿─雙腿彎曲抬臀運動",
            "躺姿─大腿伸直抬高(左右腳輪流)",
            "坐姿─直膝抬小腿(左右腳輪流)",
            "坐姿─大腿輪流上提抬高左右輪流",
            "站姿─腳尖踮起放下",
            "站姿─坐下/起立"
    };

    private int[] exerciseVideos = {
            R.raw.img_2136_0,
            R.raw.img_2137_1,
            R.raw.img_2138_2,
            R.raw.img_2139_3,
            R.raw.img_2140_4,
            R.raw.img_2141_5
    };

    private int selectedExerciseIndex;
    EditText time;

    private PoseDetector poseDetector;
    private Executor executor = Executors.newSingleThreadExecutor();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        exerciseSpinner = findViewById(R.id.exerciseSpinner);
        startButton = findViewById(R.id.startButton);
        timerTextView = findViewById(R.id.timerTextView);
        videoView = findViewById(R.id.videoView);
        time = findViewById(R.id.timeEditText);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, exercises);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSpinner.setAdapter(adapter);

        exerciseSpinner.setOnItemSelectedListener(this);

        // Initialize PoseDetector
        PoseDetectorOptions options =
                new PoseDetectorOptions.Builder()
                        .setDetectorMode(PoseDetectorOptions.SINGLE_IMAGE_MODE)
                        .build();
        poseDetector = PoseDetection.getClient(options);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeText = time.getText().toString();

                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                SharedPreferences.Editor editor = getPrefs.edit();
                editor.putString("time", timeText);
                editor.apply();

                Intent intent = new Intent(Exercise.this, exercise_pose.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void startExercise() {
        if (isTimerRunning) {
            return;
        }
        // Add your logic to start the exercise
    }

    private void showExerciseCompletion() {
        // Show exercise completion message
    }

    private void playExerciseVideo() {
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + exerciseVideos[selectedExerciseIndex]);
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.start();
            }
        });
    }

    private void stopExerciseVideo() {
        videoView.stopPlayback();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedExerciseIndex = position;
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        SharedPreferences.Editor editor = getPrefs.edit();
        editor.putInt("video", selectedExerciseIndex);

        // Apply changes, make sure to use apply for it to take effect
        editor.apply();
        if (isTimerRunning) {
            // Reset the timer if needed
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }
}
