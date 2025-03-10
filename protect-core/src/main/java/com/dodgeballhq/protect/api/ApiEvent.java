package com.dodgeballhq.protect.api;

import java.util.Date;
import java.util.Map;

public class ApiEvent{
    /**
     *
     * @param eventType
     * @param data
     */
    public ApiEvent(String eventType, Map<String, Object> data){
        this.type = eventType;
        this.data = data;
    }

    public String type;
    public Map<String, Object> data;
    public Date eventTime;
}
