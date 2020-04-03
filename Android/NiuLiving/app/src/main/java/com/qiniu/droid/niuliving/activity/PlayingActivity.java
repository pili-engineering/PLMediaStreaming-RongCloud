package com.qiniu.droid.niuliving.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLOnBufferingUpdateListener;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.PLOnInfoListener;
import com.pili.pldroid.player.PLOnVideoSizeChangedListener;
import com.pili.pldroid.player.widget.PLVideoView;
import com.qiniu.droid.niuliving.R;
import com.qiniu.droid.niuliving.utils.Config;
import com.qiniu.droid.niuliving.utils.StreamingSettings;
import com.qiniu.droid.niuliving.utils.ToastUtils;

import static com.qiniu.droid.niuliving.utils.StreamingSettings.DEFAULT_CACHE;
import static com.qiniu.droid.niuliving.utils.StreamingSettings.MAX_CACHE;

public class PlayingActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_playing);

        mVideoView = (PLVideoView) findViewById(R.id.pl_video_view);
        mLogText = (TextView) findViewById(R.id.playing_log_text);
        mProgressBar = (ProgressBar) findViewById(R.id.playing_progress_bar);

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
}
