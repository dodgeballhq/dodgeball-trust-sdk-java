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
    public void testBasicSend(){
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
                "http://localhost:3001",
                testSecret,
                request);


        assertTrue(callResponse.success);
    }

    @Test
    public void testBasicAsyncSend() throws Exception{
        String testSecret = TestValues.TEST_SECRET;
        String checkpointName = "DONATION";

        Map<String, Object> hm = new HashMap<String, Object>();
        hm.put("amount", 30000);
        hm.put("currency", "USD");
        hm.put("mfaPhoneNumbers", TestValues.MFA_PHONE_NUMBERS);

        Event event = new Event("127.0.0.1", hm);
        CheckpointRequest.Options options = new CheckpointRequest.Options();
        options.sync = false;
        options.timeout = 50;
        CheckpointRequest request = new CheckpointRequest(
                event,
                checkpointName,
                TestValues.TEST_SOURCE_TOKEN,
                TestValues.TEST_SESSION_ID,
                TestValues.TEST_CUSTOMER_ID,
                options
        );


        CompletableFuture<CheckpointResponse> responseFuture = DodgeballServices.executeAsync(
                "http://localhost:3001",
                testSecret,
                request);

        CheckpointResponse response = responseFuture.join();
        assertTrue(response.success);
    }
}
