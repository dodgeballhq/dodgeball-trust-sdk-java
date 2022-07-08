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
        String testSecret = TestValues.TEST_SECRET;
        String testDBSourceId = TestValues.TEST_DB_SOURCE_ID;
        String checkpointName = TestValues.TEST_CHECKPOINT_NAME;

        Map<String, Object> hm = new HashMap<String, Object>();
        hm.put("amount", 30000);
        hm.put("currency", "USD");
        hm.put("mfaPhoneNumbers", TestValues.MFA_PHONE_NUMBERS);

        Event event = new Event("127.0.0.1", hm);

        CheckpointRequest request = new CheckpointRequest(
                event,
                checkpointName,
                testDBSourceId,
                "test@dodgeballhq.com"
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
