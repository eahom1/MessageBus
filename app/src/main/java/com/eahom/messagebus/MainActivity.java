package com.eahom.messagebus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eahom.messagebus.message.RefreshUiMessage;
import com.eahom.messagebuslib.MessageBus;
import com.eahom.messagebuslib.subscribe.OnMessageReceivedListener;
import com.eahom.messagebuslib.utils.Logger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mGoto_btn;
    private TextView mText_tv;
    private ImageView mImage_iv;

    private OnMessageReceivedListener<RefreshUiMessage> mRefreshTextListener = new OnMessageReceivedListener<RefreshUiMessage>() {
        @Override
        public void onMessageReceived(boolean isUiThread, RefreshUiMessage message) {
            Logger.e(this.getClass().getName() + ", 收到消息了");
            try {
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isUiThread) {
                mText_tv.setText(message.getText());
                mImage_iv.setImageBitmap(message.getBitmap());
                Toast.makeText(getApplicationContext(), "MainActivity，收到消息了", Toast.LENGTH_SHORT).show();
            }
            else
                Logger.e("could not refresh ui because method not invoke in ui thread");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        MessageBus.getInstance().register(RefreshUiMessage.class, mRefreshTextListener);
    }

    private void findView() {
        mGoto_btn = (Button) findViewById(R.id.main_goto_btn);
        mGoto_btn.setOnClickListener(this);
        mText_tv = (TextView) findViewById(R.id.main_text_tv);
        mImage_iv = (ImageView) findViewById(R.id.main_image_iv);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.main_goto_btn) {
            Intent intent = new Intent(this, SecondActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageBus.getInstance().unregister(mRefreshTextListener);
    }
}
