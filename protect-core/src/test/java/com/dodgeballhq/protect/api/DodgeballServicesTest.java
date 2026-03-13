package com.dodgeballhq.protect.api;

import com.dodgeballhq.protect.TestValues;
import com.dodgeballhq.protect.messages.*;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class DodgeballServicesTest {

    private static final String CHECKPOINT_OK =
            "{\"success\":true,\"verification\":{\"id\":\"test-id\",\"status\":\"COMPLETE\",\"outcome\":\"APPROVED\"}}";
    private static final String NOTIFICATION_OK = "{\"success\":true}";

    private MockWebServer mockWebServer;

    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    private String mockBaseUrl() {
        return mockWebServer.url("/").toString();
    }

    @Test
    public void testBasicCheckpointSend() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(CHECKPOINT_OK));

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

        CheckpointResponse callResponse = DodgeballServices.createCheckpoint(
                mockBaseUrl(), testSecret, request);

        assertTrue(callResponse.success);
    }

    @Test
    public void testBasicNotificationSend() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(NOTIFICATION_OK));

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
                mockBaseUrl(), testSecret, request);

        assertTrue(callResponse.success);
    }

    @Test
    public void testBasicCheckpointVerify() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(CHECKPOINT_OK));

        String testSecret = TestValues.TEST_SECRET;
        String verificationId = "test-verification-id";
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

        CheckpointResponse callResponse = DodgeballServices.verifyCheckpoint(
                mockBaseUrl(), testSecret, verificationId, request);

        assertTrue(callResponse.success);
    }
}
