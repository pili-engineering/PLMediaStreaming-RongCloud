package com.qiniu.droid.niuliving.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.qiniu.droid.niuliving.R;
import com.qiniu.droid.niuliving.im.DataInterface;
import com.qiniu.droid.niuliving.im.model.ChatroomInfo;
import com.qiniu.droid.niuliving.utils.Config;
import com.qiniu.droid.niuliving.utils.QNAppServer;
import com.qiniu.droid.niuliving.utils.StreamingSettings;
import com.qiniu.droid.niuliving.utils.ToastUtils;

import io.rong.imlib.RongIMClient;

public class AddressConfigActivity extends AppCompatActivity {
    private static final String PLAY_URL_REGEX = "(rtmp|http)://[-a-zA-Z0-9._?=/%&+~]+";
    private static final String PLAY_ROOMNAME_REGEX = "[-a-zA-Z0-9_]+";

    private EditText mAddressConfigEditText;
    private EditText mUserNameConfigEditText;
    private Button mStartLivingButton;
    private RadioButton mProtocolRadioButton;
    private LinearLayout mProtocolLayout;

    private int mOpenType;
    private boolean mIsProtocolAgreed;
    private String mPlayingUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setContentView(R.layout.activity_address_config);
        mAddressConfigEditText = (EditText) findViewById(R.id.address_config_edit_text);
        mUserNameConfigEditText = (EditText) findViewById(R.id.username_config_edit_text);
        mStartLivingButton = (Button) findViewById(R.id.start_living_button);
        mProtocolLayout = (LinearLayout) findViewById(R.id.protocol_layout);
        mProtocolRadioButton = (RadioButton) findViewById(R.id.protocol_radio_button);
        mProtocolRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsProtocolAgreed = !mIsProtocolAgreed;
                mProtocolRadioButton.setChecked(mIsProtocolAgreed);
            }
        });

        mOpenType = getIntent().getIntExtra(Config.MODE, 0);

        if (isStreamingType()) {
            mProtocolLayout.setVisibility(View.VISIBLE);
        }

        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        String roomName = preferences.getString(isStreamingType() ?
                StreamingSettings.STREAMING_ROOMNAME : StreamingSettings.PLAYING_ROOMNAME, "");
        mAddressConfigEditText.setText(roomName);

        String userName = preferences.getString(StreamingSettings.USERNAME, "");
        mUserNameConfigEditText.setText(userName);

        if (isStreamingType()) {
            mAddressConfigEditText.setHint(R.string.streaming_mode_hint);
            mStartLivingButton.setText(R.string.streaming_mode_button_text);
        } else {
            mAddressConfigEditText.setHint(R.string.playing_mode_hint);
            mStartLivingButton.setText(R.string.playing_mode_button_text);
        }

        mStartLivingButton.setOnClickListener(mOnStartLivingClickListener);
    }

    public void onClickBack(View v) {
        finish();
    }

    private View.OnClickListener mOnStartLivingClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!QNAppServer.isNetworkAvailable(AddressConfigActivity.this)) {
                ToastUtils.s(AddressConfigActivity.this, getString(R.string.network_disconnected));
                return;
            }
            if (isStreamingType() && !mProtocolRadioButton.isChecked()) {
                ToastUtils.s(AddressConfigActivity.this, getString(R.string.niuliving_protocol_tips));
                return;
            }
            final String roomName = mAddressConfigEditText.getText().toString().trim();
            if ("".equals(roomName)) {
                ToastUtils.s(AddressConfigActivity.this, getString(R.string.null_room_name_toast));
                return;
            }
            final String userName = mUserNameConfigEditText.getText().toString().trim();
            if (TextUtils.isEmpty(userName)) {
                ToastUtils.s(AddressConfigActivity.this, getString(R.string.null_user_name_toast));
                return;
            }
            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).edit();
            editor.putString(StreamingSettings.USERNAME, userName);

            if (isStreamingType()) {
                editor.putString(StreamingSettings.STREAMING_ROOMNAME, roomName);
                editor.apply();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String url = QNAppServer.getInstance().requestPublishUrl(roomName);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (url == null) {
                                    ToastUtils.s(AddressConfigActivity.this, getString(R.string.get_url_failed));
                                    return;
                                }

                                DataInterface.connectIM(new RongIMClient.ConnectCallback() {
                                    @Override
                                    public void onTokenIncorrect() {
                                        DataInterface.connectIM(this);
                                    }

                                    @Override
                                    public void onSuccess(String s) {
                                        DataInterface.setLogin(userName);
                                        Intent intent = new Intent(AddressConfigActivity.this, StreamingActivity.class);
                                        ChatroomInfo chatroomInfo = new ChatroomInfo(roomName, roomName, null, DataInterface.getUserId(), 0);
                                        intent.putExtra("roominfo", chatroomInfo);
                                        intent.putExtra(Config.STREAMING_URL, url);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {
                                        ToastUtils.s(AddressConfigActivity.this, getString(R.string.im_connect_error));
                                    }
                                });
                            }
                        });
                    }
                }).start();
            } else {
                editor.putString(StreamingSettings.PLAYING_ROOMNAME, roomName);
                editor.apply();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (roomName.matches(PLAY_ROOMNAME_REGEX)) {
                            mPlayingUrl = QNAppServer.getInstance().requestPlayUrl(roomName);
                        } else if (roomName.matches(PLAY_URL_REGEX)) {
                            mPlayingUrl = roomName;
                        } else {
                            mPlayingUrl = null;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mPlayingUrl == null) {
                                    ToastUtils.s(AddressConfigActivity.this,
                                            roomName.matches(PLAY_ROOMNAME_REGEX)
                                                    ? getString(R.string.get_url_failed)
                                                    : getString(R.string.illegal_play_url));
                                    return;
                                }

                                DataInterface.connectIM(new RongIMClient.ConnectCallback() {
                                    @Override
                                    public void onTokenIncorrect() {
                                        DataInterface.connectIM(this);
                                    }

                                    @Override
                                    public void onSuccess(String s) {
                                        DataInterface.setLogin(userName);
                                        Intent intent = new Intent(AddressConfigActivity.this, PlayingActivity.class);
                                        ChatroomInfo chatroomInfo = new ChatroomInfo(roomName, roomName, null, DataInterface.getUserId(), 0);
                                        intent.putExtra("roominfo", chatroomInfo);
                                        intent.putExtra(Config.PLAYING_URL, mPlayingUrl);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {
                                        ToastUtils.s(AddressConfigActivity.this, getString(R.string.im_connect_error));
                                    }
                                });
                            }
                        });
                    }
                }).start();
            }
        }
    };

    public void onClickProtocol(View v) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_protocol);
        WebView webView = (WebView) dialog.findViewById(R.id.protocol_web_view);
        webView.loadUrl("file:///android_asset/user_declare.html");
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);
        dialog.show();
    }

    private boolean isStreamingType() {
        return mOpenType == Config.OPEN_TYPE_STREAMING;
    }
}
