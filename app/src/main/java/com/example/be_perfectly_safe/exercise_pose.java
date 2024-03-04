package com.example.be_perfectly_safe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class exercise_pose extends AppCompatActivity {


    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    int PERMISSION_REQUESTS = 1;

    PreviewView previewView;

    // Base pose detector with streaming frames, when depending on the pose-detection sdk
    PoseDetectorOptions options =
            new PoseDetectorOptions.Builder()
                    .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                    .build();

    PoseDetector poseDetector = PoseDetection.getClient(options);

    Canvas canvas;

    Paint mPaint = new Paint();

    Display display;

    Bitmap bitmap4Save;

    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    ArrayList<Bitmap> bitmap4DisplayArrayList = new ArrayList<>();

    ArrayList<Pose> poseArrayList = new ArrayList<>();

    boolean isRunning = false;

    private Spinner exerciseSpinner;
    private Button startButton;
    private TextView timerTextView;
    private VideoView videoView;

    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;


    private int[] exerciseVideos = {
            R.raw.img_2136_0,
            R.raw.img_2137_1,
            R.raw.img_2138_2,
            R.raw.img_2139_3,
            R.raw.img_2140_4,
            R.raw.img_2141_5
    };

    private int selectedExerciseIndex;

    private static final float straightArmThreshold = 0.1f;

    private boolean areArmsStraight(PointF shoulder, PointF elbow, PointF wrist) {
        float shoulderToElbowDistance = calculateDistance(shoulder, elbow);
        float elbowToWristDistance = calculateDistance(elbow, wrist);

        float straightArmThreshold = 0.1f;

        return elbowToWristDistance > shoulderToElbowDistance - straightArmThreshold;
    }

    private float calculateDistance(PointF point1, PointF point2) {
        return (float) Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2));
    }

    private boolean areBothWristsAboveHead = false;
    private static final int RESET_THRESHOLD = 100;

    private int consecutiveFramesWithRightWristAboveHead = 0;
    private static final int THRESHOLD_CONSECUTIVE_FRAMES = 5;


    @SuppressLint("MissingInflatedId")
    @ExperimentalGetImage
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_pose);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        previewView = findViewById(R.id.previewView);
        display = findViewById(R.id.displayOverlay);
        timerTextView = findViewById(R.id.timerTextView);
        videoView = findViewById(R.id.videoView);

        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(10);


        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        selectedExerciseIndex = getPrefs.getInt("video", 0);

        startExercise();

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        previewView = findViewById(R.id.previewView);

        display = findViewById(R.id.displayOverlay);

        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(10);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }


    }



    private void startExercise() {
        if (isTimerRunning) {
            return;
        }

        long exerciseTimeMinutes = getExerciseTimeMinutes();

        if (exerciseTimeMinutes <= 0) {
            return;
        }

        long exerciseTimeMillis = exerciseTimeMinutes * 60 * 1000;

        countDownTimer = new CountDownTimer(exerciseTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                String time = String.format("%02d:%02d", minutes, seconds);
                timerTextView.setText(time);
            }

            @Override
            public void onFinish() {
                timerTextView.setText("00:00");
                stopExerciseVideo();
                showExerciseCompletion();
                Toast.makeText(exercise_pose.this, "運動完成！", Toast.LENGTH_SHORT).show();
                isTimerRunning = false;
            }
        };

        countDownTimer.start();
        isTimerRunning = true;

        playExerciseVideo();
    }

    private long getExerciseTimeMinutes() {

        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        String timeText = getPrefs.getString("time", null);


        if (timeText.isEmpty()) {
            return 0;
        }

        try {
            return Long.parseLong(timeText);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void showExerciseCompletion() {
        // 在此显示运动完成的消息
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



    Runnable RunMlkit = new Runnable() {
        @Override
        public void run() {
            poseDetector.process(InputImage.fromBitmap(bitmapArrayList.get(0),0)).addOnSuccessListener(new OnSuccessListener<Pose>() {
                @Override
                public void onSuccess(Pose pose) {
                    poseArrayList.add(pose);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    };//處理

    @ExperimentalGetImage
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
//                         enable the following line if RGBA output is needed.
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
//                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ActivityCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                // insert your code here.
                // after done, release the ImageProxy object

                if (poseArrayList.size() >= 1) {
                    Pose pose = poseArrayList.get(0);

                    // 獲取左肩、左肘、左腕的 PoseLandmark
                    PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
                    PoseLandmark leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
                    PoseLandmark leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);

                    // Draw joint points and lines for pose detection
                    drawPoseOnCanvas(pose, bitmapArrayList.get(0));

                    // 獲取右肩、右肘、右腕的 PoseLandmark
                    PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
                    PoseLandmark rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
                    PoseLandmark rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);

                    // 獲取左腳踝的 PoseLandmark
                    PoseLandmark leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE);

                    // 獲取右腳踝的 PoseLandmark
                    PoseLandmark rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE);

                    // 獲取左腳後跟、左腳尖、右腳後跟和右腳尖的 PoseLandmark
                    PoseLandmark leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL);
                    PoseLandmark rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL);

                    // 設定墊起腳尖的閾值，這裡假設腳尖的 y 軸位置高於腳後跟
                    float toeUpThreshold = 0.1f;

                    // 設定腳抬起的閾值，這裡假設腳抬起時 y 軸的差值大於一個閾值
                    float footUpThreshold = 0.1f;

                    // 檢查雙手臂是否在同一平面上
                    if (leftShoulder != null && rightShoulder != null &&
                            Math.abs(leftShoulder.getPosition().x - rightShoulder.getPosition().x) < straightArmThreshold &&
                            Math.abs(leftShoulder.getPosition().y - rightShoulder.getPosition().y) < straightArmThreshold) {

                        // 檢查左手臂和右手臂的方向是否形成一條直線
                        if (areArmsStraight(leftShoulder.getPosition(), leftElbow.getPosition(), leftWrist.getPosition()) &&
                                areArmsStraight(rightShoulder.getPosition(), rightElbow.getPosition(), rightWrist.getPosition())) {

                            // 雙手臂伸直的動作
                            Log.d("PoseDetection", "Both arms are straight!");
                        }
                    }

                    float horizontalLineThreshold = 0.1f;

                    // 檢查雙手是否舉成水平線
                    if (leftElbow != null && leftWrist != null && rightElbow != null && rightWrist != null) {
                        float leftArmHeight = Math.abs(leftElbow.getPosition().y - leftWrist.getPosition().y);
                        float rightArmHeight = Math.abs(rightElbow.getPosition().y - rightWrist.getPosition().y);

                        if (Math.abs(leftArmHeight - rightArmHeight) < horizontalLineThreshold) {
                            // 雙手舉成水平線的動作
                            Log.d("PoseDetection", "Both arms are raised horizontally!");
                        }
                    }

                    // 檢查左手臂是否伸直
                    if (leftShoulder != null && leftElbow != null && leftWrist != null) {
                        float shoulderToElbowDistance = calculateDistance(leftShoulder.getPosition(), leftElbow.getPosition());
                        float elbowToWristDistance = calculateDistance(leftElbow.getPosition(), leftWrist.getPosition());

                        float straightArmThreshold = 0.1f;

                        if (elbowToWristDistance > shoulderToElbowDistance - straightArmThreshold) {
                            // 左手臂伸直的動作
                            Log.d("PoseDetection", "Left arm is straight!");
                        }
                    }

                    // 檢查右手臂是否伸直
                    if (rightShoulder != null && rightElbow != null && rightWrist != null) {
                        float shoulderToElbowDistance = calculateDistance(rightShoulder.getPosition(), rightElbow.getPosition());
                        float elbowToWristDistance = calculateDistance(rightElbow.getPosition(), rightWrist.getPosition());

                        float straightArmThreshold = 0.1f;

                        if (elbowToWristDistance > shoulderToElbowDistance - straightArmThreshold) {
                            // 右手臂伸直的動作
                            Log.d("PoseDetection", "Right arm is straight!");
                        }
                    }
                }

                ByteBuffer byteBuffer = imageProxy.getImage().getPlanes()[0].getBuffer();
                byteBuffer.rewind();
                Bitmap bitmap = Bitmap.createBitmap(imageProxy.getWidth(), imageProxy.getHeight(), Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(byteBuffer);

                Matrix matrix = new Matrix();
                matrix.postRotate(270);
                matrix.postScale(-1,1);
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap,0,0,imageProxy.getWidth(), imageProxy.getHeight(),matrix,false);

                bitmapArrayList.add(rotatedBitmap);

                if (poseArrayList.size() >= 1) {//座?
                    canvas = new Canvas(bitmapArrayList.get(0));

                    for (PoseLandmark poseLandmark : poseArrayList.get(0).getAllPoseLandmarks()) {
                        canvas.drawCircle(poseLandmark.getPosition().x, poseLandmark.getPosition().y,5,mPaint);
                    }

                    bitmap4DisplayArrayList.clear();
                    bitmap4DisplayArrayList.add(bitmapArrayList.get(0));
                    bitmap4Save = bitmapArrayList.get(bitmapArrayList.size()-1);
                    bitmapArrayList.clear();
                    bitmapArrayList.add(bitmap4Save);
                    poseArrayList.clear();
                    isRunning = false;
                }

                if (poseArrayList.size() == 0 && bitmapArrayList.size() >= 1 && !isRunning) {
                    RunMlkit.run();
                    isRunning = true;
                }

                if (bitmap4DisplayArrayList.size() >= 1) {
                    display.getBitmap(bitmap4DisplayArrayList.get(0));
                }

                imageProxy.close();

            }
        });

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis, preview);
    }

    private void drawPoseOnCanvas(Pose pose, Bitmap bitmap) {
        canvas = new Canvas(bitmap);

        // Draw joint points
        for (PoseLandmark poseLandmark : pose.getAllPoseLandmarks()) {
            float x = poseLandmark.getPosition().x;
            float y = poseLandmark.getPosition().y;

            // Draw a circle at each joint point
            canvas.drawCircle(x, y, 10, mPaint);
        }

        // Draw lines connecting joint points
        drawLineBetweenLandmarks(pose, PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_ELBOW);
        drawLineBetweenLandmarks(pose, PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_WRIST);
        // ... (add more lines as needed)

        // Refresh the display with the modified bitmap
        bitmap4DisplayArrayList.clear();
        bitmap4DisplayArrayList.add(bitmap);
        display.getBitmap(bitmap4DisplayArrayList.get(0));
    }

    // Method to draw a line between two PoseLandmarks
    private void drawLineBetweenLandmarks(Pose pose, int startLandmark, int endLandmark) {
        PoseLandmark start = pose.getPoseLandmark(startLandmark);
        PoseLandmark end = pose.getPoseLandmark(endLandmark);

        if (start != null && end != null) {
            canvas.drawLine(start.getPosition().x, start.getPosition().y,
                    end.getPosition().x, end.getPosition().y, mPaint);
        }
    }


    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }
}