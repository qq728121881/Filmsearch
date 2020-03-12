package com.zzn.filmsearch;

/**
 * @author
 * @date 2020/3/11.
 * Created by：郑振楠
 */
public class PlayurlBean {

    private String url;
    private String name;
    private boolean ischeck;

    public boolean isIscheck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
