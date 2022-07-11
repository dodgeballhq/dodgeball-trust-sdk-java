# DodgeBall-TrustSDK-Java

DodgeBall SDK for server side java engineers.

## How to Use
Quick Example:

```java
import com.dodgeballhq.protect.Dodgeball;
import com.dodgeballhq.protect.messages.*;
import com.dodgeballhq.protect.TestValues;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


// Build up the dictionary of Event Properties
Map<String, Object> hm = new HashMap<String, Object>();
hm.put("amount", 30000);
hm.put("currency", "USD");
hm.put("mfaPhoneNumbers", TestValues.MFA_PHONE_NUMBERS);

 // Populate an Event
Event event = new Event("127.0.0.1", hm);
CheckpointRequest request = new CheckpointRequest(
 event,
 "CHECKPOINT NAME",
 "Your DB Session ID",
 "User ID");

Dodgeball dodgeball= Dodgeball.
 builder().
 setApiKeys(TestValues.TEST_SECRET).
 build();

CompletableFuture<CheckpointResponse> responseFuture = dodgeball.checkpoint(request);
```

To handle the results inline:

```java

CheckpointResponse checkpointResponse = responseFuture.join();

if (dodgeball.isAllowed(checkpointResponse)) {
   // Handle good case where the operation is allowed
}
else if (dodgeball.isRunning(checkpointResponse)) {
   if(checkpointResponse.isTimeout){
      // The checkpoint is still running at timeout, so resubmit, etc.
   }
   else{
      // We're stopped for MFA.  Return to the client per code samples
   }
}
else if (dodgeball.isDenied(checkpointResponse)) {
    // Handle workflow rejection
}else{
    // Handle errors
}
```