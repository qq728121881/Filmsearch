package com.zzn.filmsearch.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

import java.util.List;
import java.util.Random;

/**
 * @author
 * @date 2020/3/22.
 * Created by：郑振楠
 */
public class MessageService extends HmsMessageService {
    @Override
    public void onNewToken(String token) {
        Log.d("zzn", "Refreshed token: " + token);
        // send the token to your app server.
        if (!TextUtils.isEmpty(token)) {
            sendRegTokenToServer(token);
        }
    }

    private void sendRegTokenToServer(String token) {
        // TODO: Implement this method to send token to your app server.


    }


    private static final String TAG = "DemoHmsMessageService";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        Log.i(TAG, "onMessageReceived is called");


        if (message.getData().length() > 0) {
            Log.d(TAG, "Message data payload: " + message.getData());
            if (/* This method callback must be completed within 10s. */ true) {
                /* Otherwise, you need to start a new job for callback processing. */
                startNewJobProcess();
            } else {
                // Process message within 10s
                processNow();
            }
        }
        // Check if this message contains a notification payload.
        if (message.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + message.getNotification().getBody());
        }
        // TODO: your's other processing logic
    }

    private void startNewJobProcess() {
        Log.d(TAG, "Start new job processing.");
        //TODO:
    }

    private void processNow() {
        Log.d(TAG, "Processing now.");
    }

    public static int getRandom(int min, int max) {
        Random random = new Random();
        int i = random.nextInt(max) % (max - min + 1) + min;
        return i;
    }


    public static void openDing(String packageName, Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = packageManager.getPackageInfo("com.alibaba.android.rimet", 0);
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);
        List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0);
        ResolveInfo resolveInfo = apps.iterator().next();
        if (resolveInfo != null) {
            String className = resolveInfo.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }

}
