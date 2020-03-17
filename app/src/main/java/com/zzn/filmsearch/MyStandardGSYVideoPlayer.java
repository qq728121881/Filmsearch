package com.zzn.filmsearch;

import android.content.Context;
import android.util.AttributeSet;

import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

/**
 * @author
 * @date 2020/3/14.
 * Created by：郑振楠
 */
public class MyStandardGSYVideoPlayer extends StandardGSYVideoPlayer {
    public MyStandardGSYVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public MyStandardGSYVideoPlayer(Context context) {
        super(context);
    }

    public MyStandardGSYVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public int getLayoutId() {


        return R.layout.play_layout;
    }
}
