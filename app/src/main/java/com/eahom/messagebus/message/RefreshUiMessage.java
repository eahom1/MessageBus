package com.eahom.messagebus.message;

import android.graphics.Bitmap;

import com.eahom.messagebuslib.message.IMessage;

/**
 * Created by eahom on 17/1/10.
 */

public class RefreshUiMessage extends IMessage {

    private String text;
    private Bitmap bitmap;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
