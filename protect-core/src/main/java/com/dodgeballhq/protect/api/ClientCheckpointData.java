package com.dodgeballhq.protect.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

import com.dodgeballhq.protect.messages.CheckpointRequest;
import com.dodgeballhq.protect.messages.Event;


/**
 *
 */
public class ClientCheckpointData {
    /**
     *
     */
    public static class ApiEvent{
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

    public static class WebhookData{
        public WebhookData(){
        }

        /**
         *
         * @param url
         */
        public WebhookData(String url){
            this.url = url;
        }

        public String url;
    }

    public static class ApiOptions{
        public ApiOptions(){
            this.sync = true;
        }

        /**
         *
         * @param sync
         * @param timeout
         * @param webhook
         */
        public ApiOptions(
                boolean sync,
                int timeout,
                String webhook
        ){
            this.sync = sync;
            this.timeout = timeout;
            if(!StringUtils.isEmpty(webhook)){
                this.webhook = new WebhookData(webhook);
            }
        }

        /**
         *
         * @param options
         */
        public ApiOptions( CheckpointRequest.Options options){
            this.sync = options.sync;
            this.timeout = options.timeout;
            if(!StringUtils.isEmpty(options.webhook)){
                this.webhook = new WebhookData(options.webhook);
            }
        }

        public boolean sync;
        public Integer timeout;
        public WebhookData webhook;
    };

    public ClientCheckpointData(){
        this.options = null;
    }

    /**
     *
     * @param eventName
     * @param event
     */
    public ClientCheckpointData(
            String eventName,
            Event event
    ){
        this.transferEventData(eventName, event);
        this.options = null;
    }

    /**
     *
     * @param request
     */
    public ClientCheckpointData(
            CheckpointRequest request
    ){
        this.transferEventData(request.checkpointName, request.event);
        this.options =   (request.options == null)?
                new ApiOptions():
                new ApiOptions(request.options);
    }

    private void transferEventData(String eventType, Event event){
        HashMap<String, Object> eventData = new HashMap<>();
        if(event.data != null){
            for(Map.Entry<String, Object> existing: event.data.entrySet()){
                eventData.put(existing.getKey(), existing.getValue());
            }
            if(!StringUtils.isEmpty(event.ip)){
                eventData.put("ip", event.ip);
            }
        }
        ApiEvent apiEvent = new ApiEvent(eventType, eventData);
        this.event = apiEvent;
    }

    public ApiEvent event;
    public ApiOptions  options;
}
