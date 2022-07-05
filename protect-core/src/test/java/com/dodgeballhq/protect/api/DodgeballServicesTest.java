package com.dodgeballhq.protect.api;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

import com.dodgeballhq.protect.messages.*;
import com.dodgeballhq.protect.api.DodgeballServices;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class DodgeballServicesTest {
    @Test
    public void testBasicSend(){
        String testSecret = "1c29d5d6593011ec9412470128c0fd71";
        String testDBSourceId = "5c8e04c1-21aa-4af6-aaae-2a2be3c1bd2f";
        String checkpointName = "DONATION";

        Map<String, Object> hm = new HashMap<String, Object>();
        hm.put("amount", 10);
        hm.put("currency", "USD");

        Event event = new Event("127.0.0.1", hm);

        CheckpointRequest request = new CheckpointRequest(
                event,
                checkpointName,
                testDBSourceId,
                "test@dodgeballhq.com",
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
        String testSecret = "1c29d5d6593011ec9412470128c0fd71";
        String testDBSourceId = "5c8e04c1-21aa-4af6-aaae-2a2be3c1bd2f";
        String checkpointName = "DONATION";

        Map<String, Object> hm = new HashMap<String, Object>();
        hm.put("amount", 30000);
        hm.put("currency", "USD");
        hm.put("mfaPhoneNumbers", "+16178174021");

        Event event = new Event("127.0.0.1", hm);

        CheckpointRequest request = new CheckpointRequest(
                event,
                checkpointName,
                testDBSourceId,
                "test@dodgeballhq.com",
                null
        );


        CompletableFuture<CheckpointResponse> responseFuture = DodgeballServices.executeAsync(
                "http://localhost:3001",
                testSecret,
                request);

        CheckpointResponse response = responseFuture.join();
        assertTrue(response.success);
    }
}
