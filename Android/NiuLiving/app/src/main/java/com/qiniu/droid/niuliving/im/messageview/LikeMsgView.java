package com.qiniu.droid.niuliving.im.messageview;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.qiniu.droid.niuliving.R;
import com.qiniu.droid.niuliving.im.message.ChatroomLike;

import io.rong.imlib.model.MessageContent;

/**
 * Created by duanliuyi on 2018/5/23.
 */

public class LikeMsgView extends BaseMsgView {

    private TextView username;
    private TextView infoText;

    public LikeMsgView(Context context) {
        super(context);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.msg_info_view, this);
        username = (TextView) view.findViewById(R.id.username);
        infoText = (TextView) view.findViewById(R.id.info_text);
    }

    @Override
    protected void onBindContent(MessageContent msgContent, String senderUserId) {
        String name = getSendUserName();
        username.setText(name + "  ");
        infoText.setText("给主播点了" + ((ChatroomLike) msgContent).getCounts() + "个赞");

    }
}
