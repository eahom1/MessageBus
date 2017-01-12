package com.eahom.messagebuslib.thread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by eahom on 17/1/11.
 */

public final class HandlerPoster extends Handler {

    private final int ASYNC = 1;
    private final int SYNC = 2;
    private final Queue<Runnable> asyncPool;
    private final Queue<SyncPost> syncPool;
    private final int maxMillisInsideHandleMessage;
    private boolean asyncActive;
    private boolean syncActive;

    public HandlerPoster(Looper looper, int maxMillisInsideHandleMessage) {
        super(looper);
        this.maxMillisInsideHandleMessage = maxMillisInsideHandleMessage;
        asyncPool = new LinkedBlockingQueue<>();
        syncPool = new LinkedBlockingQueue<>();
    }

    public void release() {
        this.removeCallbacksAndMessages(null);
        this.asyncPool.clear();
        this.syncPool.clear();
    }

    public void async(Runnable runnable) {
        synchronized (asyncPool) {
            asyncPool.offer(runnable);
            if (!asyncActive) {
                asyncActive = true;
                if (!sendMessage(obtainMessage(ASYNC))) {
                    throw new RuntimeException("Could not send handler message");
                }
            }
        }
    }

    public void sync(SyncPost post) {
        synchronized (syncPool) {
            syncPool.offer(post);
            if (!syncActive) {
                syncActive = true;
                if (!sendMessage(obtainMessage(SYNC))) {
                    throw new RuntimeException("Could not send handler message");
                }
            }
        }
    }

    @Override
    public void handleMessage(Message msg) {
        int what = msg.what;
        if (what == ASYNC) {
            boolean rescheduled = false;
            try {
                long started = SystemClock.uptimeMillis();
                while (true) {
                    Runnable runnable = asyncPool.poll();
                    if (runnable == null) {
                        synchronized (asyncPool) {
                            runnable = asyncPool.poll();
                            if (runnable == null) {
                                asyncActive = false;
                                return;
                            }
                        }
                    }
                    runnable.run();
                    long timeInMethod = SystemClock.uptimeMillis() - started;
                    if (timeInMethod >= maxMillisInsideHandleMessage) {
                        if (!sendMessage(obtainMessage(ASYNC))) {
                            throw new RuntimeException("Could not send handler message");
                        }
                        rescheduled = true;
                        return;
                    }
                }
            }
            finally {
                asyncActive = rescheduled;
            }
        }
        else if (what == SYNC) {
            boolean rescheduled = false;
            long started = SystemClock.uptimeMillis();
            try {
                while (true) {
                    SyncPost post = syncPool.poll();
                    if (post == null) {
                        synchronized (syncPool) {
                            post = syncPool.poll();
                            if (post == null) {
                                syncActive = false;
                                return;
                            }
                        }
                    }
                    post.run();
                    long timeInMethod = SystemClock.uptimeMillis() - started;
                    if (timeInMethod >= maxMillisInsideHandleMessage) {
                        if (!sendMessage(obtainMessage(SYNC))) {
                            throw new RuntimeException("Could not send handler message");
                        }
                        rescheduled = true;
                        return;
                    }
                }
            }
            finally {
                syncActive = rescheduled;
            }
        }
        else {
            super.handleMessage(msg);
        }
    }
}
