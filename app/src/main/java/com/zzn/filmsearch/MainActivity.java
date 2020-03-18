package com.zzn.filmsearch;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.KeyboardUtils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zzn.filmsearch.ui.AllDownActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements TextWatcher, OnRefreshListener {

    @Bind(R.id.ed_text)
    EditText edText;
    @Bind(R.id.iv_no)
    ImageView ivNo;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.text)
    TextView text;
    @Bind(R.id.no_data)
    RelativeLayout noData;

    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.recyclerview)
    XRecyclerView recyclerView;
    @Bind(R.id.date_ll)
    LinearLayout dateLl;
    @Bind(R.id.my_but)
    Button myBut;
    private VideoListAdapter videoListAdapter;
    private AlphaAnimation alphaAniShow;
    private AlphaAnimation alphaAniHide;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        MainActivityPermissionsDispatcher.getLocationWithCheck(this);

        refreshLayout.setOnRefreshListener(this);

        refreshLayout.setEnableAutoLoadMore(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoListAdapter = new VideoListAdapter(this);
        recyclerView.setAdapter(videoListAdapter);

        refreshLayout.autoRefresh(100);

        edText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {


                //防止触发两次
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    ProgressUtil.show(MainActivity.this);
                    GloableParams.filmBeans.clear();
                    videoListAdapter.notifyDataSetChanged();
                    String context = edText.getText().toString().trim();
                    try {
                        getData(context);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }


                KeyboardUtils.hideSoftInput(v);
                return false;
            }
        });

        onClick();


//
//        String url="http://zuida.downzuida.com/2001/A情公寓5-30.mp4";
//
//        GetRequest<File> request = OkGo.<File>get(url); //构建下载请求
//        DownloadTask task = OkDownload.request(url, request); //创建下载任务，tag为一个任务的唯一标示
//        task.register(new com.lzy.okserver.download.DownloadListener("66") {
//            @Override
//            public void onStart(Progress progress) {
//            }
//
//            @Override
//            public void onProgress(Progress progress) {
//
//                Log.e("zzn", "totalSize:"+(progress.currentSize * 100 / progress.totalSize));//百分比
//
//                Log.e("zzn", "totalSize:"+(int)progress.totalSize/100);
//
//                Log.e("zzn", progress.currentSize+"");
//            }
//
//            @Override
//            public void onError(Progress progress) {
//            }
//
//            @Override
//            public void onFinish(File file, Progress progress) {
//
//            }
//
//            @Override
//            public void onRemove(Progress progress) {
//            }
//        }).save();
//        task.fileName("情公寓5-30.mp4"); //设置下载的文件名
//        task.start(); //开始或继续下载

//        task.restart(); //重新下载
//        task.pause(); //暂停下载
//        task.remove(); //删除下载，只删除记录，不删除文件
//        task.remove(true); //删除下载，同时删除记录和文件


    }


    private void onClick() {
        videoListAdapter.setClickCallBack(new VideoListAdapter.ItemClickCallBack() {
            @Override
            public void onItemClick(int pos, FilmBean bean) {

                Intent intent = new Intent(new Intent(MainActivity.this, VideoDetailActivity.class));
                intent.putExtra("url", bean.getUrl());
                startActivity(intent);


            }
        });


        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e("xxx", "onScrollStateChanged");
                myBut.  clearAnimation();
                myBut.setVisibility(View.VISIBLE);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.e("xxx", "onScrolled");


                        //更改UI；
                        alphaAnim(myBut,true);

            }
        });

//        recyclerView.setOnTouchListener(new View.OnTouchListener() {
////            @Override
////            public boolean onTouch(View v, MotionEvent event) {
////
////                switch (event.getAction()) {
////                    case MotionEvent.ACTION_DOWN:
////                        Log.e("xxx", "按下");
////                        myBut.setVisibility(View.VISIBLE);
////                        break;
////                    case MotionEvent.ACTION_UP:
////                        Log.e("xxx", "抬起");
////
////                        alphaAnim(myBut,false);
////
////                        break;
////                    case MotionEvent.ACTION_MOVE:
////
////
////                        alphaAnim(myBut,true);
////                        Log.e("xxx", "移动");
////                        break;
////                }
////
////                return false;
////            }
////        });

    }

    private void getData(String context) throws IOException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(HttpUrls.url + "/index.php?m=vod-search&wd=" + context + "&submit=search").post();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Element body = doc.body();

                getAnalysis(body);

            }
        }).start();


    }

    private void getAnalysis(Element body) {

        Elements xing_vb = body.getElementsByClass("xing_vb");

        //获取HTML页面中的所有链接
        Elements links = xing_vb.select("a[href]");


        for (Element link : links) {
            Log.e("zzn", "link : " + link.attr("href"));
            Log.e("zzn", "link : " + "text :" + link.text());
            if (link.attr("href").startsWith("/?m=vod-index-pg-") || link.attr("href").startsWith("/index.php?m=vod-search-pg")) {
                break;
            }
            FilmBean bean = new FilmBean();
            bean.setUrl(HttpUrls.url + link.attr("href"));
            bean.setName(link.text());
            GloableParams.filmBeans.add(bean);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //更改UI；
                videoListAdapter.setDatas(GloableParams.filmBeans);
                if (links.size() > 0) {
                    noData.setVisibility(View.GONE);
                    dateLl.setVisibility(View.VISIBLE);
                } else {
                    noData.setVisibility(View.VISIBLE);
                    dateLl.setVisibility(View.GONE);

                    Toast.makeText(MainActivity.this, "暂时没数据", Toast.LENGTH_LONG).show();
                }

            }
        });

        onLoadFinish();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {


    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {


        Log.e("zzn", "刷新");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(HttpUrls.url).get();
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

    private void onLoadFinish() {
        ProgressUtil.hide();
        refreshLayout.finishLoadMore(500);
        refreshLayout.finishRefresh(500);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
    })
    public void getLocation() {

    }

    @OnClick({R.id.date_ll, R.id.my_but})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.date_ll:
                break;
            case R.id.my_but:
                Intent intent = new Intent(new Intent(this, AllDownActivity.class));
                startActivity(intent);

                break;
        }
    }

    /**
     * View 渐变显示与隐藏
     */
    private void alphaAnim(final View view, boolean show){
        if(show) {
            view.setAlpha(0);
            view.setVisibility(View.VISIBLE);
            view.animate()
                    .alpha(0.8f)
                    .setDuration(3000)
                    .setListener(null);
        }else {
            view.animate()
                    .alpha(0f)
                    .setDuration(3000)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            view.setVisibility(View.GONE);
                        }
                    });
        }
    }
}
