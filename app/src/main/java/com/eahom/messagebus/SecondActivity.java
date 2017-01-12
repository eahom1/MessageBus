package com.eahom.messagebus;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.eahom.messagebus.message.RefreshUiMessage;
import com.eahom.messagebuslib.MessageBus;
import com.eahom.messagebuslib.message.IMessage;
import com.eahom.messagebuslib.subscribe.OnMessageReceivedListener;
import com.eahom.messagebuslib.utils.Logger;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mMessageEdit_et;
    private Button mSend_btn;

    private OnMessageReceivedListener<IMessage> mMessageListener = new OnMessageReceivedListener<IMessage>() {
        @Override
        public void onMessageReceived(boolean isUiThread, IMessage message) {
            Logger.e(this.getClass().getName() + ", 收到消息了");
            mMessageEdit_et.setText(message.tag);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        findView();
        MessageBus.getInstance().register(IMessage.class, mMessageListener);
    }

    private void findView() {
        mMessageEdit_et = (EditText) findViewById(R.id.second_message_edit_et);
        mSend_btn = (Button) findViewById(R.id.second_send_btn);
        mSend_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.second_send_btn) {
            String text = mMessageEdit_et.getText().toString().trim();
            final RefreshUiMessage refreshTextMessage = new RefreshUiMessage();
            refreshTextMessage.tag = "1234567890";
            refreshTextMessage.setText(text);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.ic_launcher);
            refreshTextMessage.setBitmap(bitmapDrawable.getBitmap());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MessageBus.getInstance().postMessageToUiThreadAsync(refreshTextMessage);
                    Logger.e("发送消息完毕");
                }
            }).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageBus.getInstance().unregister(mMessageListener);
    }
}
