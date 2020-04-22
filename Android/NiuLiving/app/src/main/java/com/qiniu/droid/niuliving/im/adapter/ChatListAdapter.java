package com.qiniu.droid.niuliving.im.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qiniu.droid.niuliving.im.ChatroomKit;
import com.qiniu.droid.niuliving.im.messageview.BaseMsgView;
import com.qiniu.droid.niuliving.im.messageview.UnknownMsgView;

import java.util.ArrayList;

import io.rong.imlib.model.Message;

public class ChatListAdapter extends BaseAdapter {

    private ArrayList<Message> msgList;
    private Context context;

    public ChatListAdapter(Context context) {
        msgList = new ArrayList<>();
        this.context = context;
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseMsgView baseMsgView = (BaseMsgView) convertView;
        Message message = msgList.get(position);
        Class<? extends BaseMsgView> msgViewClass = ChatroomKit.getRegisterMessageView(message.getContent().getClass());
        if (msgViewClass == null) {
            baseMsgView = new UnknownMsgView(parent.getContext());
        } else if (baseMsgView == null || baseMsgView.getClass() != msgViewClass) {
            try {
                baseMsgView = msgViewClass.getConstructor(Context.class).newInstance(parent.getContext());
            } catch (Exception e) {
                throw new RuntimeException("baseMsgView newInstance failed.");
            }
        }
        baseMsgView.bindContent(message.getContent(), message.getSenderUserId());

//        ViewHolder viewHolder = null;
//        if (convertView == null) {
//            viewHolder = new ViewHolder();
//            convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_list, null);
//            viewHolder.tvInfo = convertView.findViewById(R.id.tv_info);
//             convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//
//        if (msgContent instanceof TextMessage) {
//            TextMessage textMessage = (TextMessage) msgContent;
//            viewHolder.tvInfo.setText(textMessage.getContent());
//
//        }
//
//        return convertView;
        return baseMsgView;
    }

    public void addMessage(Message msg) {
        msgList.add(msg);
    }


    class ViewHolder {
        TextView tvInfo;
    }
}
