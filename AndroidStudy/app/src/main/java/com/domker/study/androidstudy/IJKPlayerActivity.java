package com.domker.study.androidstudy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.domker.study.androidstudy.player.VideoPlayerIJK;
import com.domker.study.androidstudy.player.VideoPlayerListener;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 使用开源IjkPlayer播放视频
 */
public class IJKPlayerActivity extends AppCompatActivity implements View.OnClickListener{

    private VideoPlayerIJK ijkPlayer;
    private MediaPlayer player;
    private SurfaceHolder holder;
    private Handler handler;
    private ImageButton btnPuase,btnFullScreen;
    private SeekBar seekBar;
    private TextView tvTime,tvLoading;
    private ProgressBar pbLoading;
    private RelativeLayout rlLoading,rlPlayer;
    LinearLayout llProgressBar;
    private boolean isFinish = false;
    private boolean isPlay = false;
    private boolean isVisible = true;
    public static final int MSG_REFRESH = 777;
    private int mVideoWidth = 0;
    private int mVideoHeight = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ijkplayer);
        setTitle("ijkPlayer");
        getInit();
        //加载native库
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }
        ijkPlayer = findViewById(R.id.ijkPlayer);
        ijkPlayer.setListener(new VideoPlayerListener());
        ijkPlayer.setVideoResource(R.raw.bytedance);
        //ijkPlayer.setVideoPath(getVideoPath(R.raw.bytedance));
        ijkPlayer.setListener(new VideoPlayerListener() {

            @Override
            public void onCompletion(IMediaPlayer mp) {
                seekBar.setProgress(100);
                btnPuase.setBackgroundResource(R.drawable.play);
            }

            @Override
            public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
                mVideoWidth = mp.getVideoWidth();
                mVideoHeight = mp.getVideoHeight();
            }

            @Override
            public void onPrepared(IMediaPlayer mp) {
                updateTime();
                handler.sendEmptyMessageDelayed(MSG_REFRESH, 100);
                isFinish = false;
                isPlay = true;
                mVideoWidth = mp.getVideoWidth();
                mVideoHeight = mp.getVideoHeight();
                ijkPlayer.pause();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                DisplayMetrics metric = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metric);
                float width = metric.widthPixels;
                float height = metric.heightPixels;
                float ratio = 0;
                if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    //竖屏模式下按视频宽度计算放大倍数值
                    ratio = Math.max((float) mVideoWidth / (float) width, (float) mVideoHeight / (float) height);
                } else {
                    //横屏模式下按视频高度计算放大倍数值
                    ratio = Math.max(((float) mVideoWidth / (float) width), (float) mVideoHeight / (float) height);
                }
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rlPlayer.getLayoutParams();
                layoutParams.width = (int) Math.ceil((float) mVideoWidth / ratio);
                layoutParams.height = (int) Math.ceil((float) mVideoHeight / ratio);
                rlPlayer.setLayoutParams(layoutParams);
                ijkPlayer.start();
                mp.start();
                rlLoading.setVisibility(View.GONE);
            }
        });
    }

    private String getVideoPath(int resId) {
        return "http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8";
//        return "android.resource://" + this.getPackageName() + "/" + resId;
    }

    private void getInit() {

        btnPuase = findViewById(R.id.btn_pause);
        btnFullScreen = findViewById(R.id.btn_full_screen);
        seekBar = findViewById(R.id.seekbar);
        rlLoading = findViewById(R.id.rl_loading);
        rlPlayer = findViewById(R.id.rl_player);
        llProgressBar = findViewById(R.id.progress_bar);
        pbLoading = findViewById(R.id.pb_loading);
        tvLoading = findViewById(R.id.tv_loading);
        tvTime = findViewById(R.id.tv_time);
        btnPuase.setOnClickListener(this);
        btnFullScreen.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) {
                    ijkPlayer.seekTo(ijkPlayer.getDuration() * i / 100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacksAndMessages(null);
                ijkPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ijkPlayer.seekTo(ijkPlayer.getDuration() * seekBar.getProgress() / 100);
                ijkPlayer.start();
                handler.sendEmptyMessageDelayed(MSG_REFRESH, 100);
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_REFRESH:
                        if (ijkPlayer.isPlaying()) {
                            updateTime();
                            handler.sendEmptyMessageDelayed(MSG_REFRESH, 100);
                        }
                        break;
                }
            }
        };
    }

    private void updateTime() {
        long current = ijkPlayer.getCurrentPosition() / 1000;
        long duration = ijkPlayer.getDuration() / 1000;
        long current_second = current % 60;
        long current_minute = current / 60;
        long total_second = duration % 60;
        long total_minute = duration / 60;
        String time = current_minute + ":" + current_second + "/" + total_minute + ":" + total_second;
        tvTime.setText(time);
        if (duration != 0) {
            seekBar.setProgress((int) (current * 100 / duration));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.sendEmptyMessageDelayed(MSG_REFRESH, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ijkPlayer.isPlaying()) {
            ijkPlayer.stop();
        }
        IjkMediaPlayer.native_profileEnd();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        if (ijkPlayer != null) {
            ijkPlayer.stop();
            ijkPlayer.release();
            ijkPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ijkPlayer:
                if (!isVisible) {
                    llProgressBar.setVisibility(View.VISIBLE);
                    Animation animation  = new AlphaAnimation(0, 1);
                    animation.setDuration(500);
                    llProgressBar.startAnimation(animation);
                    isVisible = true;
                } else {
                    llProgressBar.setVisibility(View.INVISIBLE);
                    Animation animation  = new AlphaAnimation(1, 0);
                    animation.setDuration(500);
                    llProgressBar.startAnimation(animation);
                    llProgressBar.setVisibility(View.INVISIBLE);
                    isVisible = false;
                }
                break;
            case R.id.btn_full_screen:
                // TODO fix it in final project
                break;
            case R.id.btn_pause:
                if (isPlay) {
                    ijkPlayer.pause();
                    handler.removeCallbacksAndMessages(null);
                    btnPuase.setBackgroundResource(R.drawable.play);
                    isPlay = false;
                } else {
                    ijkPlayer.start();
                    btnPuase.setBackgroundResource(R.drawable.puase);
                    handler.sendEmptyMessageDelayed(MSG_REFRESH, 100);
                    isPlay = true;
                }
                break;
        }
    }
}
