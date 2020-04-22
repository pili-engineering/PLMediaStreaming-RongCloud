package com.qiniu.droid.niuliving.im.panel;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qiniu.droid.niuliving.R;
import com.qiniu.droid.niuliving.im.utils.CommonUtils;


public class InputPanel extends LinearLayout {

    private final static String TAG = "InputPanel";

    private ViewGroup inputBar;
    private EditText textEditor;
    private ImageView emojiBtn;
    private TextView sendBtn;
    private EmojiBoard emojiBoard;

    private InputPanelListener listener;

    private int type;
    public static final int TYPE_TEXTMESSAGE = 1;
    public static final int TYPE_BARRAGE = 2;

    public void setType(int type) {
        this.type = type;
    }

    public interface InputPanelListener {
        void onSendClick(String text, int msgType);
    }

    public InputPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        final View layout = LayoutInflater.from(getContext()).inflate(R.layout.widget_input_panel, this);
        inputBar = (ViewGroup) findViewById(R.id.input_bar);
        textEditor = (EditText) findViewById(R.id.input_editor);
        emojiBtn = (ImageView) findViewById(R.id.input_emoji_btn);
        sendBtn = (TextView) findViewById(R.id.input_send);
        emojiBoard = (EmojiBoard) findViewById(R.id.input_emoji_board);

        textEditor.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                inputBar.setSelected(hasFocus);
                emojiBtn.setSelected(emojiBoard.getVisibility() == VISIBLE);
            }
        });
        textEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                sendBtn.setEnabled(!s.toString().isEmpty());
                int start = textEditor.getSelectionStart();
                int end = textEditor.getSelectionEnd();
                textEditor.removeTextChangedListener(this);
                CharSequence cs = EmojiManager.parse(s.toString(), textEditor.getTextSize());
                textEditor.setText(cs, TextView.BufferType.SPANNABLE);
                textEditor.setSelection(start, end);
                textEditor.addTextChangedListener(this);
            }
        });

        emojiBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiBoard.setVisibility(emojiBoard.getVisibility() == VISIBLE ? GONE : VISIBLE);
                if (emojiBoard.getVisibility() == VISIBLE) {
                    CommonUtils.hideInputMethod(getContext(), textEditor);
                }else {
                    CommonUtils.showInputMethod(getContext(), textEditor);
                }
                emojiBtn.setSelected(emojiBoard.getVisibility() == VISIBLE);
            }
        });

        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSendClick(textEditor.getText().toString(), type);
                }
                textEditor.getText().clear();
                layout.setVisibility(GONE);
                CommonUtils.hideInputMethod(getContext(),textEditor);
            }
        });

        emojiBoard.setItemClickListener(new EmojiBoard.OnEmojiItemClickListener() {
            @Override
            public void onClick(String code) {
                if (code.equals("/DEL")) {
                    textEditor.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                } else {
                    textEditor.getText().insert(textEditor.getSelectionStart(), code);
                }
            }
        });
    }

    public void setPanelListener(InputPanelListener l) {
        listener = l;
    }

    /**
     * back键或者空白区域点击事件处理
     *
     * @return 已处理true, 否则false
     */
    public boolean onBackAction() {
        if (emojiBoard.getVisibility() == VISIBLE) {
            emojiBoard.setVisibility(GONE);
            emojiBtn.setSelected(false);
            return true;
        }
        return false;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            textEditor.requestFocus();
            CommonUtils.showInputMethod(getContext(), textEditor);
        }else {
            emojiBoard.setVisibility(visibility);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
    }
}
