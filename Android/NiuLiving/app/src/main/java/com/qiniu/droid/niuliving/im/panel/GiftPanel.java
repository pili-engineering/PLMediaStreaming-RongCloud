package com.qiniu.droid.niuliving.im.panel;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qiniu.droid.niuliving.R;
import com.qiniu.droid.niuliving.im.ChatroomKit;
import com.qiniu.droid.niuliving.im.DataInterface;
import com.qiniu.droid.niuliving.im.adapter.GiftAdapter;
import com.qiniu.droid.niuliving.im.message.ChatroomGift;
import com.qiniu.droid.niuliving.im.model.Gift;

import java.util.ArrayList;

/**
 * Created by duanliuyi on 2018/5/11.
 */

public class GiftPanel extends LinearLayout {

    private GridView gvGift;
    private GiftAdapter adapter;
    private TextView tvGiftSend;
    private EditText editNum;

    private ArrayList<Gift> gifts;


    private int currentPosition = -1;


    public GiftPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    private void initView(final Context context) {
        final View layout = LayoutInflater.from(getContext()).inflate(R.layout.widget_gift_panel, this);

        gvGift = (GridView) findViewById(R.id.gv_gift);
        tvGiftSend = (TextView) findViewById(R.id.tv_gift_send);
        editNum = (EditText) findViewById(R.id.edit_gift_num);

        gifts = DataInterface.getGiftList();
        adapter = new GiftAdapter(context, gifts);

        gvGift.setAdapter(adapter);

        tvGiftSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editNum.getText()) || Integer.parseInt(editNum.getText().toString()) == 0) {
                    Toast.makeText(context, "礼物数不能为0", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(editNum.getText().toString()) > 99) {
                    Toast.makeText(context, "礼物数不能超过99", Toast.LENGTH_SHORT).show();
                } else {
                    Gift gift = gifts.get(currentPosition);
                    ChatroomGift giftMessage = new ChatroomGift();
                    giftMessage.setId(gift.getGiftId());
                    giftMessage.setNumber(Integer.parseInt(editNum.getText().toString()));
                    ChatroomKit.sendMessage(giftMessage);
                    layout.setVisibility(GONE);
                }
            }
        });


        gvGift.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                for (int i = 0; i < adapterView.getCount(); i++) {
                    View v = adapterView.getChildAt(i);
                    if (position == i) {//当前选中的Item改变背景颜色
                        view.setSelected(true);
                        currentPosition = position;
                    } else {
                        v.setSelected(false);
                    }
                }
                tvGiftSend.setEnabled(true);
            }
        });

    }
}
