package com.zzn.filmsearch;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.SeekParameters;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.Bind;
import butterknife.ButterKnife;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager;

public class VideoDetailActivity extends AppCompatActivity implements OnRefreshListener {

    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.name_tv)
    TextView nameTv;
    @Bind(R.id.name_rl)
    RelativeLayout nameRl;
    @Bind(R.id.content_tv)
    TextView contentTv;
    @Bind(R.id.recyclerview)
    XRecyclerView recyclerView;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.date_ll)
    LinearLayout dateLl;
    private String url;
    private VideoPlyaListAdapter plyaListAdapter;

    private List<PlayurlBean> playurlBeans = new ArrayList<>();


    StandardGSYVideoPlayer detailPlayer;

    private boolean isPlay;
    private boolean isPause;

    private OrientationUtils orientationUtils;
    private String name;
    private int posstion;//多少集

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        EventBus.getDefault().register(this);
        detailPlayer = (StandardGSYVideoPlayer) findViewById(R.id.detail_player);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        //EXOPlayer内核，支持格式更多
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
        //exo缓存模式，支持m3u8，只支持exo
        CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
        inview();


    }

    private void inview() {
        refreshLayout.setOnRefreshListener(this);

        refreshLayout.setEnableAutoLoadMore(false);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);
        plyaListAdapter = new VideoPlyaListAdapter(this);
        recyclerView.setAdapter(plyaListAdapter);

        refreshLayout.autoRefresh(100);

//        plyaListAdapter.setClickCallBack(new VideoPlyaListAdapter.ItemClickCallBack() {
//            @Override
//            public void onItemClick(int pos, PlayurlBean bean) {
//                Intent intent = new Intent(new Intent(VideoDetailActivity.this, PlayvideoActivity.class));
//                intent.putExtra("Url", bean.getUrl());
//                startActivity(intent);
//
//
//            }
//        });


    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        Log.e("zzn", "刷新");
        ProgressUtil.show(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(url).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (doc == null) {
                    return;
                }
                Element body = doc.body();

                getAnalysis(body);

            }
        }).start();
    }


    private void getAnalysis(Element body) {

        Elements vodImg = body.getElementsByClass("lazy");
        Elements info = body.getElementsByClass("vodh");
        Elements more = body.getElementsByClass("more");

        String Imagesrc = vodImg.attr("src");//图片地址
        Elements h2 = info.select("h2");
        //片名
        name = h2.text();
        String context = more.text();//简介


        Element play_2 = body.getElementById("play_1");

        //获取HTML页面中的所有链接
        Elements links = play_2.select("li");

        for (int a = 0; a < links.size(); a++) {
            Log.e("zzn", "link : " + "text :" + links.get(a).text());
            String text = links.get(a).text();
            String playurl = text.substring(text.indexOf("$") + 1);

            PlayurlBean bean = new PlayurlBean();
            if (a == 0) {
                bean.setIscheck(true);
            } else {
                bean.setIscheck(false);
            }
            bean.setName(name);
            bean.setUrl(playurl);
            playurlBeans.add(bean);


        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(VideoDetailActivity.this).load(Imagesrc).into(image);
                nameTv.setText(name);
                contentTv.setText(context);

                plyaListAdapter.setDatas(playurlBeans);

                posstion = 1;
                String url = playurlBeans.get(0).getUrl();
                PlayVideo(url);
            }
        });

        onLoadFinish();


    }

    private void PlayVideo(String url) {
        //自动执行点击事件
        detailPlayer.getStartButton().post(new Runnable() {
            @Override
            public void run() {
                detailPlayer.getStartButton().performClick();
            }
        });

        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Glide.with(VideoDetailActivity.this).load(url).into(imageView);
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
                .setShowFullAnimation(false)
                .setStartAfterPrepared(true)
                .setNeedLockFull(true)
                .setUrl(url)
                .setCacheWithPlay(true)
                .setVideoTitle(name + "第" + posstion + "集")
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
                detailPlayer.startWindowFullscreen(VideoDetailActivity.this, true, true);
            }
        });

    }


    private void onLoadFinish() {
        ProgressUtil.hide();
        refreshLayout.finishLoadMore(500);
        refreshLayout.finishRefresh(500);
    }

    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        detailPlayer.getCurrentPlayer().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        detailPlayer.getCurrentPlayer().onVideoResume(false);
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        if (isPlay) {
            detailPlayer.getCurrentPlayer().release();
        }
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            detailPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Plya(PlayUrlEvent playUrlEvent) {
        posstion = playUrlEvent.getPosition();

        for (int a = 0; a < playurlBeans.size(); a++) {
            if (a == (posstion - 1)) {
                playurlBeans.get(a).setIscheck(true);
            } else {
                playurlBeans.get(a).setIscheck(false);
            }

        }

        plyaListAdapter.notifyDataSetChanged();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlayVideo(playUrlEvent.getUrl());


            }
        });

    }


}
