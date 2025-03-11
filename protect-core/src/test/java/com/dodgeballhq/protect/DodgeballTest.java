package com.dodgeballhq.protect;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

import com.dodgeballhq.protect.api.DodgeballServices;
import com.dodgeballhq.protect.messages.*;
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
        String checkpointName = TestValues.TEST_CHECKPOINT_NAME;

        Map<String, Object> hm = new HashMap<String, Object>();
        hm.put("amount", 30000);
        hm.put("currency", "USD");
        hm.put("mfaAddresses", TestValues.MFA_EMAIL_ADDRESSES);

        Event event = new Event("127.0.0.1", hm);

        CheckpointRequest request = new CheckpointRequest(
                event,
                checkpointName,
                null,
                TestValues.TEST_SESSION_ID,
                TestValues.TEST_CUSTOMER_ID
        );

        Dodgeball db = Dodgeball.builder().
                setApiKeys(testSecret).
                setDbUrl("https://api.dev.dodgeballhq.com").
                build();

        CompletableFuture<CheckpointResponse> responseFuture = db.checkpoint(request);
        CheckpointResponse response = responseFuture.join();
        assertTrue(response.success);
    }

    @Test
    public void testSimpleNotification() throws Exception{
        String testSecret = TestValues.TEST_SECRET;
        String eventName = TestValues.TEST_CHECKPOINT_NAME;

        Map<String, Object> hm = new HashMap<String, Object>();
        hm.put("amount", 30000);
        hm.put("currency", "USD");
        hm.put("mfaAddresses", TestValues.MFA_EMAIL_ADDRESSES);

        Event event = new Event(hm);

        NotificationRequest request = new NotificationRequest(
                event,
                eventName,
                TestValues.TEST_SESSION_ID,
                TestValues.TEST_CUSTOMER_ID
        );

        Dodgeball db = Dodgeball.builder().
                setApiKeys(testSecret).
                setDbUrl("https://api.dev.dodgeballhq.com").
                build();

        CompletableFuture<NotificationResponse> responseFuture = db.notify(request);
        NotificationResponse response = responseFuture.join();
        assertTrue(response.success);
    }

}
