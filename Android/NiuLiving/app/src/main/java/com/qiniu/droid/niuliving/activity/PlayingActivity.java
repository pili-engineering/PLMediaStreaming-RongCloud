package com.qiniu.droid.niuliving.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.orzangleli.xdanmuku.DanmuContainerView;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLOnBufferingUpdateListener;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.PLOnInfoListener;
import com.pili.pldroid.player.PLOnVideoSizeChangedListener;
import com.pili.pldroid.player.widget.PLVideoView;
import com.qiniu.droid.niuliving.R;
import com.qiniu.droid.niuliving.StreamingApplication;
import com.qiniu.droid.niuliving.im.ChatroomKit;
import com.qiniu.droid.niuliving.im.DataInterface;
import com.qiniu.droid.niuliving.im.adapter.ChatListAdapter;
import com.qiniu.droid.niuliving.im.danmu.DanmuAdapter;
import com.qiniu.droid.niuliving.im.danmu.DanmuEntity;
import com.qiniu.droid.niuliving.im.gift.GiftSendModel;
import com.qiniu.droid.niuliving.im.gift.GiftView;
import com.qiniu.droid.niuliving.im.like.HeartLayout;
import com.qiniu.droid.niuliving.im.message.ChatroomBarrage;
import com.qiniu.droid.niuliving.im.message.ChatroomGift;
import com.qiniu.droid.niuliving.im.message.ChatroomLike;
import com.qiniu.droid.niuliving.im.message.ChatroomUserQuit;
import com.qiniu.droid.niuliving.im.message.ChatroomWelcome;
import com.qiniu.droid.niuliving.im.model.ChatroomInfo;
import com.qiniu.droid.niuliving.im.model.NeedLoginEvent;
import com.qiniu.droid.niuliving.im.panel.BottomPanelFragment;
import com.qiniu.droid.niuliving.im.panel.InputPanel;
import com.qiniu.droid.niuliving.utils.Config;
import com.qiniu.droid.niuliving.utils.DialogUtils;
import com.qiniu.droid.niuliving.utils.LogUtils;
import com.qiniu.droid.niuliving.utils.StreamingSettings;
import com.qiniu.droid.niuliving.utils.ToastUtils;

import java.util.Random;

import de.greenrobot.event.EventBus;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

import static com.qiniu.droid.niuliving.im.DataInterface.DEfALUT_AVATAR;
import static com.qiniu.droid.niuliving.im.DataInterface.getUri;
import static com.qiniu.droid.niuliving.utils.StreamingSettings.DEFAULT_CACHE;
import static com.qiniu.droid.niuliving.utils.StreamingSettings.MAX_CACHE;

public class PlayingActivity extends AppCompatActivity implements Handler.Callback {
    public static final String TAG = "PlayingActivity";

    private TextView mLogText;
    private ProgressBar mProgressBar;
    private PLVideoView mVideoView;
    private int mBufferingCount = 0;
    private int mVideoWidth = 0;
    private int mVideoHeight = 0;
    private int mVideoBitrate = 0;
    private int mVideoFps = 0;
    private String mRoomName;

    protected BottomPanelFragment bottomPanel;
    private ImageView btnHeart;
    private ListView chatListView;
    private HeartLayout heartLayout;
    private ChatListAdapter chatListAdapter;

    private DanmuContainerView danmuContainerView;
    private GiftView giftView;

    protected ChatroomInfo mInfo;
    protected String roomId;
    protected Handler handler = new Handler(this);

    private Random random = new Random();
    long currentTime = 0;
    int clickCount = 0;
    long banStartTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_playing);

        mVideoView = (PLVideoView) findViewById(R.id.pl_video_view);
        mLogText = (TextView) findViewById(R.id.playing_log_text);
        mProgressBar = (ProgressBar) findViewById(R.id.playing_progress_bar);

        chatListView = (ListView) findViewById(R.id.chat_listview);
        bottomPanel = (BottomPanelFragment) getSupportFragmentManager().findFragmentById(R.id.bottom_bar);
        btnHeart = (ImageView) bottomPanel.getView().findViewById(R.id.btn_heart);
        heartLayout = (HeartLayout) findViewById(R.id.heart_layout);
        chatListAdapter = new ChatListAdapter(this);
        chatListView.setAdapter(chatListAdapter);

        danmuContainerView = (DanmuContainerView) findViewById(R.id.danmuContainerView);
        danmuContainerView.setAdapter(new DanmuAdapter(this));

        giftView = (GiftView) findViewById(R.id.giftView);
        giftView.setViewCount(2);
        giftView.init();

        String videoPath = getIntent().getStringExtra(Config.PLAYING_URL);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        mRoomName = preferences.getString(StreamingSettings.PLAYING_ROOMNAME, "");
        int defaultCache = preferences.getInt(DEFAULT_CACHE, 100);
        int maxCache = preferences.getInt(MAX_CACHE, 200);
        boolean isSwEnable = preferences.getBoolean(StreamingSettings.SW_ENABLE, false);
        boolean isDebugEnable = preferences.getBoolean(StreamingSettings.DEBUG_MODE_ENABLED, false);

        AVOptions options = new AVOptions();
        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC,
                isSwEnable ? AVOptions.MEDIA_CODEC_SW_DECODE : AVOptions.MEDIA_CODEC_HW_DECODE);
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);
        // options.setString(AVOptions.KEY_DNS_SERVER, "127.0.0.1");
        options.setInteger(AVOptions.KEY_LOG_LEVEL, isDebugEnable ? 0 : 5);
        options.setInteger(AVOptions.KEY_CACHE_BUFFER_DURATION, defaultCache);
        options.setInteger(AVOptions.KEY_MAX_CACHE_BUFFER_DURATION, maxCache);
        mVideoView.setAVOptions(options);

        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mVideoView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mVideoView.setOnErrorListener(mOnErrorListener);

        mVideoView.setVideoPath(videoPath);

        btnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataInterface.isLogin()) {
                    heartLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            int rgb = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
                            heartLayout.addHeart(rgb);
                        }
                    });
                    clickCount++;
                    currentTime = System.currentTimeMillis();
                    checkAfter(currentTime);
                } else {
                    EventBus.getDefault().post(new NeedLoginEvent(true));
                }
            }
        });
        bottomPanel.setInputPanelListener(new InputPanel.InputPanelListener() {
            @Override
            public void onSendClick(String text, int type) {
                if (type == InputPanel.TYPE_TEXTMESSAGE) {
                    final TextMessage content = TextMessage.obtain(text);
                    ChatroomKit.sendMessage(content);
                } else if (type == InputPanel.TYPE_BARRAGE) {
                    ChatroomBarrage barrage = new ChatroomBarrage();
                    barrage.setContent(text);
                    ChatroomKit.sendMessage(barrage);
                }
            }
        });

        initChatRoom();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();

        ChatroomKit.quitChatRoom(new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                LogUtils.i(TAG, "quitChatRoom success");
                ChatroomKit.removeEventHandler(handler);
                if (DataInterface.isLogin()) {
//                    Toast.makeText(LiveShowActivity.this, "退出聊天室成功", Toast.LENGTH_SHORT).show();
                    ChatroomUserQuit userQuit = new ChatroomUserQuit();
                    userQuit.setId(ChatroomKit.getCurrentUser().getUserId());
                    ChatroomKit.sendMessage(userQuit);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                ChatroomKit.removeEventHandler(handler);
//                Toast.makeText(LiveShowActivity.this, "退出聊天室失败! errorCode = " + errorCode, Toast.LENGTH_SHORT).show();

                LogUtils.e(TAG, "quitChatRoom failed errorCode = " + errorCode);
            }
        });
    }

    public void onClickClosePlayer(View v) {
        mVideoView.stopPlayback();
        finish();
    }

    public void onClickLogButton(View v) {
        mLogText.setVisibility(mLogText.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
    }

    public void onClickCopyRoomName(View v) {
        copyToClipboard(mRoomName);
        TSnackbar snackBar = TSnackbar.make(findViewById(R.id.playing_layout),
                String.format(getString(R.string.copy_to_clipboard), mRoomName), TSnackbar.LENGTH_SHORT);
        View snackView = snackBar.getView();
        snackView.setBackgroundColor(getResources().getColor(R.color.backgroundEndColor));
        TextView textView = (TextView) snackView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        snackBar.show();
    }

    private void copyToClipboard(String content) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            ClipData clipData = ClipData.newPlainText("Label", content);
            cm.setPrimaryClip(clipData);
        }
    }

    private void updateStatInfo() {
        String logText = String.format(getString(R.string.playing_log_text), mBufferingCount, mVideoWidth, mVideoHeight,
                mVideoBitrate / 1024, mVideoFps);
        mLogText.setText(logText);
    }

    private PLOnInfoListener mOnInfoListener = new PLOnInfoListener() {
        @Override
        public void onInfo(int what, int extra) {
            Log.i(TAG, "OnInfo, what = " + what + ", extra = " + extra);
            switch (what) {
                case PLOnInfoListener.MEDIA_INFO_BUFFERING_START:
                    mBufferingCount++;
                    updateStatInfo();
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;
                case PLOnInfoListener.MEDIA_INFO_BUFFERING_END:
                    mProgressBar.setVisibility(View.GONE);
                    break;
                case PLOnInfoListener.MEDIA_INFO_VIDEO_RENDERING_START:
                    mProgressBar.setVisibility(View.GONE);
                    ToastUtils.s(PlayingActivity.this, "first video render time: " + extra + "ms");
                    break;
                case PLOnInfoListener.MEDIA_INFO_AUDIO_RENDERING_START:
                case PLOnInfoListener.MEDIA_INFO_VIDEO_FRAME_RENDERING:
                    Log.i(TAG, "video frame rendering, ts = " + extra);
                    break;
                case PLOnInfoListener.MEDIA_INFO_AUDIO_FRAME_RENDERING:
                    Log.i(TAG, "audio frame rendering, ts = " + extra);
                    break;
                case PLOnInfoListener.MEDIA_INFO_VIDEO_GOP_TIME:
                    Log.i(TAG, "Gop Time: " + extra);
                    break;
                case PLOnInfoListener.MEDIA_INFO_SWITCHING_SW_DECODE:
                    Log.i(TAG, "Hardware decoding failure, switching software decoding!");
                    break;
                case PLOnInfoListener.MEDIA_INFO_METADATA:
                    Log.i(TAG, mVideoView.getMetadata().toString());
                    break;
                case PLOnInfoListener.MEDIA_INFO_VIDEO_BITRATE:
                    mVideoBitrate = extra;
                    updateStatInfo();
                    break;
                case PLOnInfoListener.MEDIA_INFO_VIDEO_FPS:
                    mVideoFps = extra;
                    updateStatInfo();
                    break;
                case PLOnInfoListener.MEDIA_INFO_CONNECTED:
                    Log.i(TAG, "Connected !");
                    break;
                case PLOnInfoListener.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                    Log.i(TAG, "Rotation changed: " + extra);
                    break;
                default:
                    break;
            }
        }
    };

    private PLOnBufferingUpdateListener mOnBufferingUpdateListener = new PLOnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(int precent) {
            Log.i(TAG, "onBufferingUpdate: " + precent);
        }
    };

    private PLOnVideoSizeChangedListener mOnVideoSizeChangedListener = new PLOnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(int width, int height) {
            Log.i(TAG, "onVideoSizeChanged: width = " + width + ", height = " + height);
            mVideoWidth = width;
            mVideoHeight = height;
        }
    };

    private PLOnErrorListener mOnErrorListener = new PLOnErrorListener() {
        @Override
        public boolean onError(int errorCode) {
            Log.e(TAG, "Error happened, errorCode = " + errorCode);
            switch (errorCode) {
                case PLOnErrorListener.ERROR_CODE_IO_ERROR:
                    /**
                     * SDK will do reconnecting automatically
                     */
                    ToastUtils.s(PlayingActivity.this, getString(R.string.playing_io_error));
                    Log.e(TAG, "IO Error!");
                    break;
                case PLOnErrorListener.ERROR_CODE_OPEN_FAILED:
                    ToastUtils.s(PlayingActivity.this, getString(R.string.open_failed));
                    break;
                case PLOnErrorListener.ERROR_CODE_SEEK_FAILED:
                    break;
                default:
                    ToastUtils.s(PlayingActivity.this, "unknown error !");
                    break;
            }
            finish();
            return true;
        }
    };

    private void initChatRoom() {
        mInfo = getIntent().getParcelableExtra("roominfo");
        roomId = mInfo.getRoomId();
        ChatroomKit.addEventHandler(handler);
        DataInterface.setBanStatus(false);
        joinChatRoom();
    }

    //500毫秒后做检查，如果没有继续点击了，发消息
    public void checkAfter(final long lastTime) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (lastTime == currentTime) {
                    ChatroomLike likeMessage = new ChatroomLike();
                    likeMessage.setCounts(clickCount);
                    ChatroomKit.sendMessage(likeMessage);

                    clickCount = 0;
                }
            }
        }, 500);
    }

    protected void joinChatRoom() {
        ChatroomKit.joinChatRoom(roomId, -1, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                LogUtils.i(TAG, "加入聊天室成功！");
                onJoinChatRoom();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                ToastUtils.s(PlayingActivity.this, "聊天室加入失败! errorCode = " + errorCode);
            }
        });
    }

    protected void onJoinChatRoom() {
        if (ChatroomKit.getCurrentUser() == null)
            return;
        //发送欢迎信令
        ChatroomWelcome welcomeMessage = new ChatroomWelcome();
        welcomeMessage.setId(ChatroomKit.getCurrentUser().getUserId());
        ChatroomKit.sendMessage(welcomeMessage);
    }

    @Override
    public boolean handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case ChatroomKit.MESSAGE_ARRIVED:
            case ChatroomKit.MESSAGE_SENT: {
                MessageContent messageContent = ((Message) msg.obj).getContent();
                String sendUserId = ((Message) msg.obj).getSenderUserId();
                if (messageContent instanceof ChatroomBarrage) {
                    ChatroomBarrage barrage = (ChatroomBarrage) messageContent;
                    DanmuEntity danmuEntity = new DanmuEntity();
                    danmuEntity.setContent(barrage.getContent());
                    String name = sendUserId;
                    Uri uri = getUri(PlayingActivity.this, DEfALUT_AVATAR);
                    if (messageContent != null) {
                        name = messageContent.getUserInfo().getName();
                        uri = DataInterface.getAvatarUri(messageContent.getUserInfo().getPortraitUri());
                    }
                    danmuEntity.setPortrait(uri);
                    danmuEntity.setName(name);
                    danmuEntity.setType(barrage.getType());
                    danmuContainerView.addDanmu(danmuEntity);
                } else if (messageContent instanceof ChatroomGift) {
                    ChatroomGift gift = (ChatroomGift) messageContent;
                    if (gift.getNumber() > 0) {
                        GiftSendModel model = new GiftSendModel(gift.getNumber());
                        model.setGiftRes(DataInterface.getGiftInfo(gift.getId()).getGiftRes());
                        String name = sendUserId;
                        Uri uri = getUri(StreamingApplication.getContext(), DEfALUT_AVATAR);
                        if (messageContent != null) {
                            name = messageContent.getUserInfo().getName();
                            uri = DataInterface.getAvatarUri(messageContent.getUserInfo().getPortraitUri());
                        }
                        model.setSig("送出" + DataInterface.getGiftNameById(gift.getId()));
                        model.setNickname(name);
                        model.setUserAvatarRes(uri.toString());
                        giftView.addGift(model);
                    }
                } else if (((Message) msg.obj).getConversationType() == Conversation.ConversationType.CHATROOM) {
                    Message msgObj = (Message) msg.obj;
                    chatListAdapter.addMessage(msgObj);

                    if (messageContent instanceof ChatroomUserQuit) {
                        String senderUserId = msgObj.getSenderUserId();
                        if (TextUtils.equals(senderUserId, mInfo.getPubUserId())) {
                            DialogUtils.showDialog(this, "本次直播结束！", "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                        }
                    } else if (messageContent instanceof ChatroomLike) {
                        //出点赞的心
                        for (int i = 0; i < ((ChatroomLike) messageContent).getCounts(); i++) {
                            heartLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    int rgb = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
                                    heartLayout.addHeart(rgb);
                                }
                            });
                        }
                    }
                }
                break;
            }
            case ChatroomKit.MESSAGE_SEND_ERROR: {
                Log.d(TAG, "handleMessage Error: " + msg.arg1 + ", " + msg.obj);
                if (msg.arg1 == RongIMClient.ErrorCode.RC_CHATROOM_NOT_EXIST.getValue()) {
                    DialogUtils.showDialog(PlayingActivity.this, "1 小时内无人讲话，聊天室已被解散，请退出后重进");
                }
                break;
            }
            default:
        }
        chatListAdapter.notifyDataSetChanged();
        return false;
    }
}
