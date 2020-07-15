package com.example.chapter8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private Button btnPhoto,btnVideo;
    private SurfaceView surfaceView;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private ImageView imageView;
    private VideoView videoView;
    private MediaRecorder mMediaRecorder;
    private String mp4Path;
    private boolean isRecording;

    private final static int SET_CLICKABLE_VIDEO = 666;
    private final static int SET_CLICKABLE_IMAGE = 777;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPhoto = findViewById(R.id.btn_photo);
        btnVideo = findViewById(R.id.btn_video);
        imageView = findViewById(R.id.imageView);
        videoView = findViewById(R.id.videoView);

        surfaceView = findViewById(R.id.surfaceView);
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);

        initCamera();

        // get from PPT
        final Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                FileOutputStream fos = null;
                String filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "1.jpg";
                File file = new File(filePath);
                try {
                    fos = new FileOutputStream(file);
                    fos.write(bytes);
                    fos.close();
                    displayImage(imageView,filePath);
                    videoView.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    mCamera.startPreview();
                    if(fos != null){
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        @SuppressLint("HandlerLeak")
        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SET_CLICKABLE_VIDEO:
                        btnVideo.setClickable(true);
                        break;
                    case SET_CLICKABLE_IMAGE:
                        btnPhoto.setClickable(true);
                        break;
                    default:
                        break;
                }
            }
        };

        //设置监视器
        btnPhoto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPhoto.setClickable(false);
                new Timer("setAble").schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Message msg = mHandler.obtainMessage(SET_CLICKABLE_IMAGE);
                        mHandler.sendMessage(msg);
                    }}, 1000);
                mCamera.takePicture(null,null,mPictureCallback);
            }
        });

        //设置监视器
        btnVideo.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnVideo.setClickable(false);
                new Timer("setAble").schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Message msg = mHandler.obtainMessage(SET_CLICKABLE_VIDEO);
                        mHandler.sendMessage(msg);
                    }}, 1000);
                record();
            }
        });
    }

    // get from PPT
    @Override
    protected void onResume() {
        super.onResume();
        if(mCamera == null){
            initCamera();
        }
        mCamera.startPreview();
    }

    // get from PPT
    @Override
    protected void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }

    // get from PPT
    private void initCamera(){

        mCamera = Camera.open(0);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        if(parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        parameters.set("orientation","portrait");
        parameters.set("rotation",90);
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);
    }

    // get from PPT
    private boolean prepareVideoRecorder(){
        mMediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        mp4Path = getOutputMediaPath();
        mMediaRecorder.setOutputFile(mp4Path);

        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());
        mMediaRecorder.setOrientationHint(90);
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            mMediaRecorder.release();
            return false;
        }
        return true;
    }

    // get from PPT
    public void record(){
        if(isRecording){

            btnVideo.setText("录制");

            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();

            imageView.setVisibility(View.INVISIBLE);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoPath(mp4Path);
            videoView.start();
        }else{
            if(prepareVideoRecorder()){
                btnVideo.setText("暂停");
                mMediaRecorder.start();
            }
        }
        isRecording = !isRecording;
    }

    // get from PPT
    public String getOutputMediaPath(){
        File mediaDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaDir,"IMG_" + timeStamp + ".mp4");
        if(!mediaFile.exists()){
            mediaFile.getParentFile().mkdirs();
        }
        return mediaFile.getAbsolutePath();
    }

    // get from PPT
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // get from PPT
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if(mHolder.getSurface() == null) return;
        try {
            mCamera.stopPreview();
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // get from PPT
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    // get from PPT
    public static Bitmap rotateBitmap(Bitmap bitmap, String path) {
        try {
            ExifInterface srcExif = new ExifInterface(path);
            Matrix matrix = new Matrix();
            int angle = 0;
            int orientation = srcExif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_90:{
                    angle = 90;
                    break;
                }
                case ExifInterface.ORIENTATION_ROTATE_180:{
                    angle = 180;
                    break;
                }
                case ExifInterface.ORIENTATION_ROTATE_270:{
                    angle = 270;
                    break;
                }
                default:
                    break;
            }
            matrix.postRotate(angle);
            return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void displayImage(ImageView imageView,String path){
        int targetWidth = imageView.getWidth();
        int targetHeight = imageView.getHeight();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);
        int photoWidth = options.outWidth;
        int photoHeight = options.outHeight;
        int scaleFactor = Math.min(photoHeight/targetHeight,photoWidth/targetWidth);
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        Bitmap bitmap = rotateBitmap(BitmapFactory.decodeFile(path,options),path);
        imageView.setImageBitmap(bitmap);
    }
}