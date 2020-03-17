package com.zzn.filmsearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

import com.liulishuo.filedownloader.FileDownloader;
import com.lzy.okgo.OkGo;
import com.lzy.okserver.OkDownload;

import java.io.File;

public class MyApplication extends MultiDexApplication {


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        OkGo.getInstance().init(this);


        Init();

        FileDownloader.setupOnApplicationOnCreate(this);

    }

    private void Init() {

        OkDownload okDownload = OkDownload.getInstance();
        okDownload.setFolder(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Video" + File.separator); //设置全局下载目录
        okDownload.getThreadPool().setCorePoolSize(3); //设置同时下载数量

    }
}
