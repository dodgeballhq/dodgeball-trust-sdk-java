# Dodgeball Server Trust SDK for Java

## Table of Contents
- [Purpose](#purpose)
- [Prerequisites](#prerequisites)
- [Related](#related)
- [Installation](#installation)
- [Usage](#usage)
- [API](#api)

## Purpose
[Dodgeball](https://dodgeballhq.com) enables developers to decouple security logic from their application code. This has several benefits including:
- The ability to toggle and compare security services like fraud engines, MFA, KYC, and bot prevention.
- Faster responses to new attacks. When threats evolve and new vulnerabilities are identified, your application's security logic can be updated without changing a single line of code.
- The ability to put in placeholders for future security improvements while focussing on product development.
- A way to visualize all application security logic in one place.

The Dodgeball Server Trust SDK for Java makes integration with the Dodgeball API easy and is maintained by the Dodgeball team.

## Prerequisites
You will need to obtain an API key for your application from the [Dodgeball developer center](https://app.dodgeballhq.com/developer).

## Related
Check out the [Dodgeball Trust Client SDK](https://npmjs.com/package/@dodgeball/trust-sdk-client) for how to integrate Dodgeball into your frontend applications.

## Installation
Add as a dependency from git.

## Usage
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
 "Your App Session ID",
 "Your User ID");

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

## API

### Utility Methods
___
There are several utility methods available to help interpret the checkpoint response. It is strongly advised to use them rather than directly interpreting the checkpoint response.

#### `dodgeball.isAllowed(checkpointResponse)`
The `isAllowed` method takes in a checkpoint response and returns `true` if the request is allowed to proceed.

#### `dodgeball.isDenied(checkpointResponse)`
The `isDenied` method takes in a checkpoint response and returns `true` if the request is denied and should not be allowed to proceed.

#### `dodgeball.isRunning(checkpointResponse)`
The `isRunning` method takes in a checkpoint response and returns `true` if no determination has been reached on how to proceed. The verification should be returned to the frontend application to gather additional input from the user. See the [useVerification](#useverification) section for more details on use and an end-to-end example.

#### `dodgeball.isUndecided(checkpointResponse)`
The `isUndecided` method takes in a checkpoint response and returns `true` if the verification has finished and no determination has been reached on how to proceed. See [undecided](#undecided) for more details.

#### `dodgeball.hasError(checkpointResponse)`
The `hasError` method takes in a checkpoint response and returns `true` if it contains an error.

#### `dodgeball.isTimeout(checkpointResponse)`
The `isTimeout` method takes in a checkpoint response and returns `true` if the verification has timed out. At which point it is up to the application to decide how to proceed. 

### useVerification
___
Sometimes additional input is required from the user before making a determination about how to proceed. For example, if a user should be required to perform 2FA before being allowed to proceed, the checkpoint response will contain a verification with `status` of `BLOCKED` and  outcome of `PENDING`. In this scenario, you will want to return the verification to your frontend application. Inside your frontend application, you can pass the returned verification directly to the `dodgeball.handleVerification()` method to automatically handle gathering additional input from the user. Continuing with our 2FA example, the user would be prompted to select a phone number and enter a code sent to that number. Once the additional input is received, the frontend application should simply send along the ID of the verification performed to your API. Passing that verification ID to the `useVerification` option will allow that verification to be used for this checkpoint instead of creating a new one. This prevents duplicate verifications being performed on the user. 

**Important Note:** To prevent replay attacks, each verification ID can only be passed to `useVerification` once.