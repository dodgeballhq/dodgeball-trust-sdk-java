package com.dodgeballhq.protect;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

import com.dodgeballhq.protect.api.DodgeballServices;
import com.dodgeballhq.protect.messages.CheckpointRequest;
import com.dodgeballhq.protect.messages.CheckpointResponse;
import com.dodgeballhq.protect.messages.Event;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DodgeballTest {
    @Test
    public void emptyKeyThrows() {
        try {
            Dodgeball db = Dodgeball.builder().build();
            fail();
        } catch (IllegalStateException exc) {
            assertNotNull(exc.getMessage());
            assertNotEquals(exc.getMessage(), "");
        }
    }

    @Test
    public void defaultUrlProd() {
        Dodgeball db = Dodgeball.builder().setApiKeys("NotValidated").build();
        assertEquals(db.baseUrl, "https://api.dodgeballhq.com");
    }

    @Test
    public void testSimpleCheckpoint() throws Exception{
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

        Dodgeball db = Dodgeball.builder().
                setApiKeys(testSecret).
                setDbUrl("http://localhost:3001").
                build();

        CompletableFuture<CheckpointResponse> responseFuture = db.checkpoint(request);
        CheckpointResponse response = responseFuture.join();
        assertTrue(response.success);
    }

}
