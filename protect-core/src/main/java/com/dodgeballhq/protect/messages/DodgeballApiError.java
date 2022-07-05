package com.dodgeballhq.protect.messages;

public class DodgeballApiError {
    public DodgeballApiError(String message){
        this.message = message;
    }

    public DodgeballApiError(String message, String stack){
        this.message = message;
        this.stack = stack;
    }

    public String message;
    public String stack;
}
