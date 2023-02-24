package com.dodgeballhq.protect;
import org.apache.commons.lang3.StringUtils;
import java.util.concurrent.CompletableFuture;

import com.dodgeballhq.protect.api.ClientCheckpointData;
import com.dodgeballhq.protect.api.DodgeballServices;
import com.dodgeballhq.protect.messages.*;
/**
 * The primary service class in the protect-core library.  Clients will:
 *    - Initialize an instance through calls to Dodgeball.builder()
 *    - Configure API Keys through setApiKeys
 *    - Invoke Builder.build()
 *
 *  Once created, invoke using Dodgeball.checkpoint
 *
 * @author Andrew Schwartz
 *
 */
public class Dodgeball {

    /**
     *
     * @param apiKey: Client Server Side API Key
     * @param url: Builder-provided URL.
     */
    Dodgeball(
            String apiKey,
            String url){
        this.apiKey = apiKey;
        this.baseUrl = url;
    }

    /**
     * Static Constructor for a Builder instance
     * @return: Builder without API Keys targeting Prod Instances
     */
    public static Builder builder(){
        return new Builder();
    }

    /**
     * Request to validate access to a protected resource.
     *
     * @param request: Access request properties
     * @return: Async function object monitoring a DodgeBall Workflow Execution
     */
    public CompletableFuture<CheckpointResponse> checkpoint(
            CheckpointRequest request){
        Delegate toCall = this.new Delegate(request);
        return CompletableFuture.supplyAsync(toCall::call);
    }

    /**
     * Request to validate access to a protected resource.
     *
     * @param request: Access request properties
     * @return: Async function object monitoring a DodgeBall Workflow Execution
     */
    public CompletableFuture<Void> track(
            TrackRequest request){

        return null;
    }

    /**
     * Accessor to determine whether access is allowed
     *
     * @param checkpointResponse
     * @return: True if the Workflow allows access
     */
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

    /**
     * Accessor indicating whether the checkpoint workflow is still processing
     *
     * @param checkpointResponse
     * @return True if the workflow is still in process
     */
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

    /**
     * Accessor to determine whether the request was denied
     *
     * @param checkpointResponse
     * @return: True if the workflow denied access
     */
    public static boolean isDenied(CheckpointResponse checkpointResponse) {
        if (checkpointResponse.success && checkpointResponse.verification != null) {
            return stringsEqual(
                    checkpointResponse.verification.outcome,
                    Constants.VerificationOutcome.DENIED);
        }

        return false;
    }

    /**
     * Accessor to determine whether no conclusion was reached
     *
     * @param checkpointResponse
     * @return: True if the workflow completed successfully, but no access decision was
     * taken.
     */
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

    /**
     * Indicates that a Workflow Execution Error occurred.
     *
     * @param checkpointResponse
     * @return: True if the workflow received an error
     */
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

    /**
     * Utility class used to construct Dodgeball instances
     */
    public static class Builder{
        private static final String DEFAULT_DB_URL = "https://api.dodgeballhq.com";
        private static final String SANDBOX_DB_URL = "https://api.sandbox.dodgeballhq.com";

        /**
         * Method to convert input configuration into an active Runtime Object
         *
         * @return: Configured Dodgeball instance for checkpointing.
         */
        public Dodgeball build(){
            if(StringUtils.isEmpty(this.apiKey)){
                throw new IllegalStateException("API Keys must be set");
            }
            return new Dodgeball(
                    this.apiKey,
                    StringUtils.isEmpty(this.dbUrl)? DEFAULT_DB_URL: this.dbUrl
            );
        }

        /**
         * Method to pass in client API keys
         *
         * @param apiKey: Client secret API Key
         * @return: this
         */
        public Builder setApiKeys(String apiKey){
            this.apiKey = apiKey;
            return this;
        }

        /**
         * Method to indicate that this integration is performed in Sandbox
         *
         * @return: this
         */
        public Builder setSandbox(){
            return this.setDbUrl(SANDBOX_DB_URL);
        }

        /**
         * Method serving primarily for testing, redirecting to a specified DB Server.
         *
         * @param dbUrl: API server URL
         * @return this
         */
        public Builder setDbUrl(String dbUrl){
            this.dbUrl = dbUrl;
            return this;
        }

        private String dbUrl;
        private String apiKey;
    }


    /**
     * Utility class leveraged in Async calls.
     */
    public class Delegate{
        public final int BASE_CHECKPOINT_TIMEOUT_MS = 100;
        public final int BASE_MAX_CHECKPOINT_TIMEOUT_MS = 10000;
        public final int MAX_ACTIVE_TIMEOUT = 2000;
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
                    throw new RuntimeException(
                            "checkpointName: must not be null");
                }

                if (request.event == null) {
                    throw new IllegalArgumentException("event: must not be null");
                } else if (StringUtils.isEmpty(request.event.ip)) {
                    throw new RuntimeException("event.ip must be provided");
                }

                CheckpointRequest.Options options = request.options;

                boolean trivialTimeout = options == null ||
                        options.timeout <= 0;

                boolean largeTimeout = ((options != null) && (options.timeout > 5 * BASE_CHECKPOINT_TIMEOUT_MS));
                boolean mustPoll = trivialTimeout || largeTimeout;
                int activeTimeout = mustPoll
                        ? BASE_CHECKPOINT_TIMEOUT_MS
                        : options.timeout;

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
                            request.sourceToken,
                            request.sessionExternalId,
                            request.customerExternalId,
                            internalOptions,
                            request.priorCheckpointId);

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


                int maxTimeout = BASE_MAX_CHECKPOINT_TIMEOUT_MS;
                if (options != null) {
                    maxTimeout = options.timeout;
                }

                isTimeout = (maxTimeout <= cumulativeTime);
                while ((trivialTimeout ||
                        !isTimeout) &&
                        !isResolved &&
                        numFailures < MAX_RETRY_COUNT) {
                    Thread.sleep(activeTimeout);
                    cumulativeTime += activeTimeout;

                    activeTimeout =
                            activeTimeout < MAX_ACTIVE_TIMEOUT ? 2 * activeTimeout : activeTimeout;

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

                if(response == null || response.verification == null){
                    throw new Exception("Failed to Evaluate");
                }

                if(stringsEqual(response.verification.status, Constants.VerificationStatus.PENDING)) {
                    response.isTimeout = response.isTimeout || isTimeout;
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

    private static boolean stringsEqual(String lhs, String rhs){
        if(lhs == null || rhs == null){
            return lhs == null && rhs == null;
        }

        return lhs.equals(rhs);
    }

    String baseUrl;
    String apiKey;
}
