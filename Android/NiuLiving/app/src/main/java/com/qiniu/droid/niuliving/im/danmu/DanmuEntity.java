package com.qiniu.droid.niuliving.im.danmu;


import android.net.Uri;

import com.orzangleli.xdanmuku.Model;


public class DanmuEntity extends Model {
    private String content;
    private String name;
    private Uri portrait;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getPortrait() {
        return portrait;
    }

    public void setPortrait(Uri portrait) {
        this.portrait = portrait;
    }
}