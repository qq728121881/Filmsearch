package com.zzn.filmsearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
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

        getToken();
        FileDownloader.setupOnApplicationOnCreate(this);

    }

    private void getToken() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // read from agconnect-services.json
                    String appId = AGConnectServicesConfig.fromContext(MyApplication.this).getString("client/app_id");
                    String token = HmsInstanceId.getInstance(MyApplication.this).getToken(appId, "HCM");
                    Log.i("zzn", "get token:" + token);
                    if(!TextUtils.isEmpty(token)) {
                    }
                } catch (ApiException e) {
                    Log.e("zzn", "get token failed, " + e);
                }
            }
        }.start();
    }


    private void Init() {

        OkDownload okDownload = OkDownload.getInstance();
        okDownload.setFolder(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Video" + File.separator); //设置全局下载目录
        okDownload.getThreadPool().setCorePoolSize(3); //设置同时下载数量

    }
}
