package com.qiniu.droid.niuliving.im.model;

/**
 * Created by duanliuyi on 2018/5/28.
 */

public class NeedLoginEvent {

    private boolean needLogin;

    public NeedLoginEvent(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }
}
