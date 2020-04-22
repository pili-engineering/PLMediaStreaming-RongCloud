package com.qiniu.droid.niuliving.im.messageview;

import android.content.Context;
import android.widget.RelativeLayout;

import io.rong.imlib.model.MessageContent;

public abstract class BaseMsgView extends RelativeLayout {

    protected MessageContent mMsgCongtent;
    protected String mSendUserId;

    public BaseMsgView(Context context) {
        super(context);
    }

    public void bindContent(MessageContent msgContent, String sendUserId){
        mMsgCongtent = msgContent;
        mSendUserId = sendUserId;
        onBindContent(mMsgCongtent,sendUserId);
    }

    protected abstract void onBindContent(MessageContent msgContent, String sendUserId);

    protected String getSendUserName(){
        if (mMsgCongtent == null)
            return mSendUserId;
        if (mMsgCongtent.getUserInfo() != null)
            return mMsgCongtent.getUserInfo().getName();
        return mSendUserId;
    }
}
