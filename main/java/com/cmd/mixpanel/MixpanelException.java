package com.cmd.mixpanel;

public class MixpanelException extends Throwable {
    public MixpanelException(Exception e) {
        super(e);
    }

    public MixpanelException(String message) {
        super(message);
    }
}