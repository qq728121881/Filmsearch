package com.zzn.filmsearch;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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
import com.shuyu.gsyvideoplayer.player.PlayerFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.Bind;
import butterknife.ButterKnife;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

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
    private VideoListAdapter videoListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
}
