package com.esafirm.kotlin.playground.sam;

public class ClickHandler {

    private OnClickListenerJava clickListener;
    private OnClickKotlin longClickListener;

    public void setOnClickListener(OnClickListenerJava listener) {
        this.clickListener = listener;
    }

    public void setOnClickListener(OnClickKotlin listener) {
        this.longClickListener = listener;
    }

    public void sendEvent() {
        if (clickListener != null) {
            clickListener.onClick("test");
        }
        if (longClickListener != null) {
            longClickListener.onClick(123);
        }
    }
}
