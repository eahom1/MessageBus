package com.eahom.messagebuslib.subscribe;

import com.eahom.messagebuslib.message.IMessage;

/**
 * Created by eahom on 17/1/10.
 */

public interface OnMessageReceivedListener<T extends IMessage> {

    void onMessageReceived(boolean isUiThread, T message);

}
