package com.dodgeballhq.protect.api;


import com.dodgeballhq.protect.TestValues;
import com.dodgeballhq.protect.messages.*;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static org.junit.Assert.assertTrue;

public class DodgeballServicesTest {
    @Test
    public void testBasicCheckpointSend(){
        String testSecret = TestValues.TEST_SECRET;
        String checkpointName = "DONATION";

        Map<String, Object> hm = new HashMap<String, Object>();
        hm.put("amount", 10);
        hm.put("currency", "USD");

        Event event = new Event("127.0.0.1", hm);

        CheckpointRequest request = new CheckpointRequest(
                event,
                checkpointName,
                TestValues.TEST_SOURCE_TOKEN,
                TestValues.TEST_SESSION_ID,
                TestValues.TEST_CUSTOMER_ID,
                null
        );

        CheckpointResponse callResponse = DodgeballServices.executeSynchronous(
                "https://api.dev.dodgeballhq.com",
                testSecret,
                request);


        assertTrue(callResponse.success);
    }

    @Test
    public void testBasicNotificationSend(){
        String testSecret = TestValues.TEST_SECRET;
        String eventName = "DONATION";

        Map<String, Object> hm = new HashMap<String, Object>();
        hm.put("amount", 10);
        hm.put("currency", "USD");

        Event event = new Event("127.0.0.1", hm);

        NotificationRequest request = new NotificationRequest(
                event,
                eventName,
                TestValues.TEST_SESSION_ID,
                TestValues.TEST_CUSTOMER_ID
        );

        NotificationResponse callResponse = DodgeballServices.executeNotification(
                "https://api.dev.dodgeballhq.com",
                testSecret,
                request);


        assertTrue(callResponse.success);
    }
}
