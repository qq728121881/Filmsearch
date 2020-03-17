package com.zzn.filmsearch.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.task.XExecutor;
import com.zzn.filmsearch.R;
import com.zzn.filmsearch.adapter.DownloadAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AllDownActivity extends AppCompatActivity implements XExecutor.OnAllTaskEndListener{

    @Bind(R.id.removeAll)
    Button removeAll;
    @Bind(R.id.pauseAll)
    Button pauseAll;
    @Bind(R.id.startAll)
    Button startAll;
    @Bind(R.id.recyclerView)
    XRecyclerView recyclerView;
    private DownloadAdapter adapter;
    private OkDownload okDownload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_down);
        ButterKnife.bind(this);

        okDownload = OkDownload.getInstance();
        adapter = new DownloadAdapter(this);
        adapter.updateData(DownloadAdapter.TYPE_ALL);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        okDownload.addOnAllTaskEndListener(this);


    }

    @OnClick({R.id.removeAll, R.id.pauseAll, R.id.startAll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.removeAll:
                break;
            case R.id.pauseAll:
                break;
            case R.id.startAll:
                break;
        }
    }

    @Override
    public void onAllTaskEnd() {
        //所有都下载完毕

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        okDownload.removeOnAllTaskEndListener(this);
        adapter.unRegister();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.removeAll)
    public void removeAll(View view) {
        okDownload.removeAll();
        adapter.updateData(DownloadAdapter.TYPE_ALL);
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.pauseAll)
    public void pauseAll(View view) {
        okDownload.pauseAll();
    }

    @OnClick(R.id.startAll)
    public void startAll(View view) {
        okDownload.startAll();
    }
}
