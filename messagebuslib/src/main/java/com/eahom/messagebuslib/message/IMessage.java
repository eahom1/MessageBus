package com.eahom.messagebuslib.message;

/**
 * Created by eahom on 17/1/10.
 */

public abstract class IMessage {

    public int id;
    public String tag;

    public IMessage() {

    }

    public IMessage(int id, String tag) {
        this.id = id;
        this.tag = tag;
    }
}
