package com.eahom.messagebuslib.subscribe;

import com.eahom.messagebuslib.message.IMessage;

/**
 * Created by eahom on 17/1/10.
 */

public class Subscriber<T extends IMessage> {

    public final Class<? extends T> messageType;
    public final OnMessageReceivedListener<T> listener;

    public Subscriber(Class<? extends T> messageType, OnMessageReceivedListener<T> listener) {
        this.messageType = messageType;
        this.listener = listener;
    }
}
