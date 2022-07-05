package com.dodgeballhq.protect.messages;

import java.util.Map;

public class Event{
    public Event(String ip, Map<String, Object> data){
        this.ip = ip;
        this.data  = data;
    }

    public String ip;
    public Map<String, Object> data;
}
