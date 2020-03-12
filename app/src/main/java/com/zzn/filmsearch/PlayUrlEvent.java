package com.zzn.filmsearch;

/**
 * @author
 * @date 2020/3/12.
 * Created by：郑振楠
 */
public class PlayUrlEvent {

    private  String url;
    private  int  position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
