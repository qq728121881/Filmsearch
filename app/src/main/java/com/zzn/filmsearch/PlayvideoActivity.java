package com.zzn.filmsearch;

import androidx.appcompat.app.AppCompatActivity;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.SeekParameters;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

public class PlayvideoActivity extends AppCompatActivity {

    private String playurl;
    StandardGSYVideoPlayer detailPlayer;

    OrientationUtils orientationUtils;
    private String name;

    private boolean isPlay;
    private boolean isPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playvideo);

        Intent intent = getIntent();
        playurl = intent.getStringExtra("Url");
        name = intent.getStringExtra("Name");
        init();

    }

    private void init() {
        detailPlayer = (StandardGSYVideoPlayer) findViewById(R.id.video_player);

        //自动执行点击事件
        detailPlayer.getStartButton().post(new Runnable() {
            @Override
            public void run() {
                detailPlayer.getStartButton().performClick();
//                detailPlayer.getFullscreenButton().performClick();
            }
        });
//        //自动执行点击事件
//        detailPlayer.getFullscreenButton().post(new Runnable() {
//            @Override
//            public void run() {
//                detailPlayer.getFullscreenButton().performClick();
//            }
//        });



        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Glide.with(PlayvideoActivity.this).load(playurl).into(imageView);
        //增加title
        detailPlayer.getTitleTextView().setVisibility(View.GONE);
        detailPlayer.getBackButton().setVisibility(View.GONE);

        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, detailPlayer);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);


        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption.setThumbImageView(imageView)
                .setIsTouchWiget(true)
                .setRotateViewAuto(true)
                .setNeedShowWifiTip(false)
                .setShowFullAnimation(false)
                .setStartAfterPrepared(true)
                .setNeedLockFull(true)
                .setUrl(playurl)
                .setCacheWithPlay(true)
                .setVideoTitle(name)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        //开始播放了才能旋转和全屏
                        orientationUtils.setEnable(true);
                        isPlay = true;
                        //设置 seek 的临近帧。
                        if (detailPlayer.getGSYVideoManager().getPlayer() instanceof Exo2PlayerManager) {
                            ((Exo2PlayerManager) detailPlayer.getGSYVideoManager().getPlayer()).setSeekParameter(SeekParameters.NEXT_SYNC);
                        }


                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
                    }
                }).setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                if (orientationUtils != null) {
                    //配合下方的onConfigurationChanged
                    orientationUtils.setEnable(!lock);
                }
            }
        }).build(detailPlayer);

        detailPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                detailPlayer.startWindowFullscreen(PlayvideoActivity.this, true, true);
            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();
        detailPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        detailPlayer.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onBackPressed() {
        //先返回正常状态
        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            detailPlayer.getFullscreenButton().performClick();
            return;
        }
        //释放所有
        detailPlayer.setVideoAllCallBack(null);
        super.onBackPressed();
    }
}
