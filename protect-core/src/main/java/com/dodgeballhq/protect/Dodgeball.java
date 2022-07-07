package com.dodgeballhq.protect;

import com.dodgeballhq.protect.api.ClientCheckpointData;
import com.dodgeballhq.protect.api.DodgeballServices;
import com.sun.javaws.exceptions.InvalidArgumentException;
import com.sun.javaws.exceptions.MissingFieldException;
import com.sun.tools.internal.jxc.ap.Const;
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
                                false,
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
                            internalRequest
                    );
                    numRepeats += 1;
                    cumulativeTime += activeTimeout;
                }

                if (response == null) {
                    throw new Exception("Unknown server error");
                } else if (!response.success) {
                    return response;
                }

                boolean isResolved = response.verification != null &&
                        !stringsEqual(response.verification.status,
                                Constants.VerificationStatus.PENDING);
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
                            isResolved = !stringsEqual(status,Constants.VerificationStatus.PENDING);
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

    public static boolean isRunning(CheckpointResponse  checkpointResponse){
        if (checkpointResponse.success && checkpointResponse.verification != null) {
            switch (checkpointResponse.verification.status) {
                case Constants.VerificationStatus.PENDING:
                case Constants.VerificationStatus.BLOCKED:
                    return true;
                default:
                    return false;
            }
        }

        return false;
    }

    public static boolean isAllowed(CheckpointResponse checkpointResponse) {
        return (
                checkpointResponse.success &&
                        checkpointResponse.verification != null &&
                        stringsEqual(
                                checkpointResponse.verification.status,
                                Constants.VerificationStatus.COMPLETE) &&
                        stringsEqual(checkpointResponse.verification.outcome,
                                Constants.VerificationOutcome.APPROVED));
    }

    public static boolean isDenied(CheckpointResponse checkpointResponse) {
        if (checkpointResponse.success && checkpointResponse.verification != null) {
            return stringsEqual(
                    checkpointResponse.verification.outcome,
                    Constants.VerificationOutcome.DENIED);
        }

        return false;
    }

    public static boolean isUndecided(
            CheckpointResponse checkpointResponse
    ) {
        return (
                checkpointResponse.success &&
                        checkpointResponse.verification != null &&
                        stringsEqual(
                                checkpointResponse.verification.status,
                                Constants.VerificationStatus.COMPLETE) &&
                        stringsEqual(
                                checkpointResponse.verification.outcome,
                                Constants.VerificationOutcome.PENDING)
        );
    }

    public static boolean hasError(CheckpointResponse checkpointResponse){
        return ((!checkpointResponse.success ||
                        checkpointResponse.verification == null) ||
                        (stringsEqual(
                                checkpointResponse.verification.status,
                                Constants.VerificationStatus.FAILED) &&
                                stringsEqual(
                                        checkpointResponse.verification.outcome,
                                        Constants.VerificationOutcome.ERROR)) ||
                                (checkpointResponse.errors != null && checkpointResponse.errors.length > 0));
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

    private static boolean stringsEqual(String lhs, String rhs){
        if(lhs == null || rhs == null){
            return lhs == null && rhs == null;
        }

        return lhs.equals(rhs);
    }

    String baseUrl;
    String apiKey;
}
