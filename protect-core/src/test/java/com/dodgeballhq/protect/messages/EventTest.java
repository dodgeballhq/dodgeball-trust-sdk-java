package com.dodgeballhq.protect.messages;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;


import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class EventTest {
    @Test
    public void eventParses(){
        Map<String, Object> hm = new HashMap<String, Object>();
        hm.put("help", 1);
        hm.put("anotherEvent", new Event("127.0.0.2", new HashMap<String, Object>()));

        Event event = new Event("127.0.0.1",
                hm);

        Gson gson = new Gson();
        String serialized = gson.toJson(event);
        Event backEvent = gson.fromJson(serialized, Event.class);
        assertEquals(event.ip, backEvent.ip);

    }
}
