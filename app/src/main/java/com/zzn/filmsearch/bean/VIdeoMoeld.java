package com.zzn.filmsearch.bean;

import java.io.Serializable;

/**
 * @author
 * @date 2020/3/17.
 * Created by：郑振楠
 */
public class VIdeoMoeld implements Serializable {
    private String name;
    private String url;
    private String iconurl;

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

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }
}
