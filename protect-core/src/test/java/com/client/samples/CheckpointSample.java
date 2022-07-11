package com.client.samples;
import com.dodgeballhq.protect.Dodgeball;
import com.dodgeballhq.protect.messages.*;
import com.dodgeballhq.protect.TestValues;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CheckpointSample {
   public void performCheckpoint(){

       // Build up the dictionary of Event Properties
       Map<String, Object> hm = new HashMap<String, Object>();
       hm.put("amount", 30000);
       hm.put("currency", "USD");
       hm.put("mfaPhoneNumbers", TestValues.MFA_PHONE_NUMBERS);

        // Populate an Event
       Event event = new Event("127.0.0.1", hm);
       CheckpointRequest request = new CheckpointRequest(
               event,
               TestValues.TEST_CHECKPOINT_NAME,
               TestValues.TEST_SOURCE_TOKEN,
               TestValues.TEST_SESSION_ID,
               TestValues.TEST_CUSTOMER_ID);

       Dodgeball dodgeball= Dodgeball.
               builder().
               setApiKeys(TestValues.TEST_SECRET).
               setDbUrl("http://localhost:3001").
               build();

       CompletableFuture<CheckpointResponse> responseFuture = dodgeball.checkpoint(request);
   }

}
