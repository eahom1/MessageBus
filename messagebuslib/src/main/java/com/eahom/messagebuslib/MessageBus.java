package com.eahom.messagebuslib;

import android.os.Looper;

import com.eahom.messagebuslib.message.IMessage;
import com.eahom.messagebuslib.subscribe.OnMessageReceivedListener;
import com.eahom.messagebuslib.subscribe.Subscriber;
import com.eahom.messagebuslib.thread.AsyncPost;
import com.eahom.messagebuslib.thread.HandlerPoster;
import com.eahom.messagebuslib.thread.SyncPost;

import java.util.Iterator;
import java.util.LinkedList;


public class MessageBus {

    private static MessageBus mInstance;

    private HandlerPoster mUiThreadPoster;
    private LinkedList<Subscriber<?>> mSubscribers = new LinkedList<>();

    private MessageBus() {
        mUiThreadPoster = new HandlerPoster(Looper.getMainLooper(), 16);
    }

    public static synchronized MessageBus getInstance() {
        if (mInstance == null)
            mInstance = new MessageBus();
        return mInstance;
    }

    public <T extends IMessage> boolean register(Class<? extends T> messageType, OnMessageReceivedListener<T> onMessageReceivedListener) {
        if (messageType == null || onMessageReceivedListener == null)
            return false;
        synchronized (mSubscribers) {
            mSubscribers.add(new Subscriber(messageType, onMessageReceivedListener));
        }
        return true;
    }

    public int unregister(OnMessageReceivedListener onMessageReceivedListener) {
        int removeCount = 0;
        if (onMessageReceivedListener == null)
            return removeCount;
        synchronized (mSubscribers) {
            for (Iterator<Subscriber<?>> it = mSubscribers.iterator(); it.hasNext(); ) {
                Subscriber<?> register = it.next();
                if (register.listener == onMessageReceivedListener) {
                    it.remove();
                    removeCount++;
                }
            }
        }
        return removeCount;
    }

    public <T extends IMessage> int sendMessage(final T message) {
        boolean isUiThread = Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
        int sendCount = 0;
        for (Subscriber<?> register : mSubscribers) {
            if (register.messageType.isInstance(message)) {
                ((Subscriber<T>)register).listener.onMessageReceived(isUiThread, message);
                sendCount++;
            }
        }
        return sendCount;
    }

    public <T extends IMessage> int postMessageToUiThreadAsync(final T message) {
//        final boolean isUiThread = Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
        int sendCount = 0;
        for (final Subscriber<?> register : mSubscribers) {
            if (register.messageType.isInstance(message)) {
                mUiThreadPoster.async(new AsyncPost() {
                    @Override
                    public void run() {
                        ((Subscriber<T>)register).listener.onMessageReceived(true, message);
                    }
                });
                sendCount++;
            }
        }
        return sendCount;
    }

    public <T extends IMessage> int postMessageToUiThreadSync(final T message) {
//        final boolean isUiThread = Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
        int sendCount = 0;
        for (final Subscriber<?> register : mSubscribers) {
            if (register.messageType.isInstance(message)) {
                SyncPost syncPost = new SyncPost(new Runnable() {
                    @Override
                    public void run() {
                        ((Subscriber<T>)register).listener.onMessageReceived(true, message);
                    }
                });
                mUiThreadPoster.sync(syncPost);
                syncPost.waitRun();
                sendCount++;
            }
        }
        return sendCount;
    }

}
