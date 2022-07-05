package com.dodgeballhq.protect;

import com.dodgeballhq.protect.api.ClientCheckpointData;
import com.dodgeballhq.protect.api.DodgeballServices;
import com.sun.javaws.exceptions.InvalidArgumentException;
import com.sun.javaws.exceptions.MissingFieldException;
import org.apache.commons.lang3.StringUtils;

import com.dodgeballhq.protect.messages.*;
import org.omg.CORBA.portable.ApplicationException;

import java.util.concurrent.CompletableFuture;

public class Dodgeball {
    Dodgeball(
            String apiKey,
            String url){
        this.apiKey = apiKey;
        this.baseUrl = url;
    }

    public static Builder builder(){
        return new Builder();
    }

    public CompletableFuture<CheckpointResponse> checkpoint(
            CheckpointRequest request){
        Delegate toCall = this.new Delegate(request);
        return CompletableFuture.supplyAsync(toCall::call);
    }

    public class Delegate{
        public final int BASE_CHECKPOINT_TIMEOUT_MS = 100;
        public final int MAX_TIMEOUT = 10000;
        public final int MAX_RETRY_COUNT = 3;

        public Delegate(CheckpointRequest request){
            this.request = request;
        }

        public CheckpointResponse call() {
            boolean isTimeout = false;
            try {
                CheckpointRequest request = this.request;
                if (request == null) {
                    throw new Exception("Must provide a request to execute");
                }

                String checkpointName = request.checkpointName;
                // Validate required parameters are present
                if (checkpointName == null) {
                    throw new InvalidArgumentException(
                            new String[]{"checkpointName: must not be null" }
                    );
                }

                if (request.event == null) {
                    throw new IllegalArgumentException("event: must not be null");
                } else if (StringUtils.isEmpty(request.event.ip)) {
                    throw new InvalidArgumentException(
                            new String[]{"event.ip must be provided" });
                }

                if (StringUtils.isEmpty(request.dodgeballId)) {
                    throw new InvalidArgumentException(
                            new String[]{"dodgeballId: must be provided" });
                }

                CheckpointRequest.Options options = request.options;

                boolean trivialTimeout = options == null ||
                        options.timeout <= 0;

                boolean largeTimeout = ((options != null) && (options.timeout > 5 * BASE_CHECKPOINT_TIMEOUT_MS));
                boolean mustPoll = trivialTimeout || largeTimeout;
                int activeTimeout = mustPoll
                        ? BASE_CHECKPOINT_TIMEOUT_MS
                        : options.timeout;

                int maximalTimeout = MAX_TIMEOUT;

                CheckpointRequest.Options internalOptions =
                        new CheckpointRequest.Options(
                                options == null ?
                                        true :
                                        options.sync,
                                activeTimeout,
                                options == null ? null : options.webhook
                        );

                CheckpointResponse response = null;
                int numRepeats = 0;
                int numFailures = 0;
                int cumulativeTime = 0;

                while ((response == null || !response.success) && numRepeats < 3) {
                    CheckpointRequest internalRequest = new CheckpointRequest(
                            request.event,
                            checkpointName,
                            request.dodgeballId,
                            request.userId,
                            internalOptions);

                    response = DodgeballServices.executeSynchronous(
                            Dodgeball.this.baseUrl,
                            Dodgeball.this.apiKey,
                            request
                    );
                    numRepeats += 1;
                    cumulativeTime += activeTimeout;
                }

                if (response == null) {
                    throw new Exception("Unknown server error");
                } else if (!response.success) {
                    return response;
                }

                boolean isResolved = (response.verification != null &&
                        response.verification.status != Constants.VerificationStatus.PENDING);
                String verificationId = (response.verification == null) ?
                        null :
                        response.verification.id;


                int maxTimeout = BASE_CHECKPOINT_TIMEOUT_MS;
                if (options != null) {
                    maxTimeout = options.timeout;
                }

                while ((trivialTimeout ||
                        !isTimeout) &&
                        !isResolved &&
                        numFailures < MAX_RETRY_COUNT) {
                    Thread.sleep(activeTimeout);
                    cumulativeTime += activeTimeout;

                    activeTimeout =
                            activeTimeout < maximalTimeout ? 2 * activeTimeout : activeTimeout;

                    response = DodgeballServices.executeSynchronous(
                            Dodgeball.this.baseUrl,
                            Dodgeball.this.apiKey,
                            request
                    );
                    cumulativeTime += activeTimeout;

                    if (response != null && response.success) {
                        String status = response.verification == null ?
                                null :
                                response.verification.status;
                        if (StringUtils.isEmpty(status)) {
                            numFailures += 1;
                        } else {
                            isResolved = (status != Constants.VerificationStatus.PENDING);
                            numRepeats += 1;
                        }
                    } else {
                        numFailures += 1;
                    }

                    isTimeout = (maxTimeout <= cumulativeTime);
                }

                if (numFailures >= MAX_RETRY_COUNT) {
                    throw new Exception("Service Unavailable: Maximum retry count exceeded");
                }

                if(!isResolved){
                    throw new Exception("Verification unresolved");
                }

                return response;
            }
            catch (Exception exc){
                CheckpointResponse toReturn =  new CheckpointResponse(exc);
                toReturn.isTimeout = isTimeout;
                return toReturn;
            }
        }

        CheckpointRequest request;
    }


    static class Builder{
        private static final String DEFAULT_DB_URL = "https://api.dodgeballhq.com";

        public Dodgeball build(){
            if(StringUtils.isEmpty(this.apiKey)){
                throw new IllegalStateException("API Keys must be set");
            }
            return new Dodgeball(
                    this.apiKey,
                    StringUtils.isEmpty(this.dbUrl)? DEFAULT_DB_URL: this.dbUrl
            );
        }

        public Builder setApiKeys(String apiKey){
            this.apiKey = apiKey;
            return this;
        }

        public Builder setDbUrl(String dbUrl){
            this.dbUrl = dbUrl;
            return this;
        }

        private String dbUrl;
        private String apiKey;
    }

    String baseUrl;
    String apiKey;
}
