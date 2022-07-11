package com.dodgeballhq.protect.messages;

/**
 * Input class defining a client Access Request
 */
public class CheckpointRequest {

    /**
     * Invocation options.  Mostly used to pass in timeout information and callback URLs
     */
    public static class Options{
        public Options(
        ){
        }

        /**
         * Constructor
         *
         * @param sync: Whether the server should block until this workflow has executed
         * @param timeout: Bounded timeout
         * @param webhook: Callback URL
         */
        public Options(boolean sync, int timeout, String webhook){
            this.timeout = timeout;
            this.sync = sync;
            this.webhook = webhook;
        }

        /**
         * Request Timeout in MS
         */
        public int timeout = -1;

        /*
         * True if Dodgeball should block until a response is provided or timeout is reached
         */
        public boolean sync = true;

        /*
         * Webhook URL in case of async execution.
         */
        public String webhook;
    }

    public CheckpointRequest(){
        this.options = new Options();
    }

    /**
     * Constructor
     *
     * @param event Client context, for example email, transaction amount, product ids, etc.
     * @param checkpointName Name of workflow to execute
     * @param sourceToken Session ID information
     * @param userId  ID of the underlying user.
     */
    public CheckpointRequest(
            Event event,
            String checkpointName,
            String sourceToken,
            String sessionExternalId,
            String customerExternalId){
        this.event = event;
        this.checkpointName = checkpointName;
        this.sourceToken = sourceToken;
        this.sessionExternalId = sessionExternalId;
        this.customerExternalId = customerExternalId;
        this.options = new Options();
    }

    public CheckpointRequest(
            Event event,
            String checkpointName,
            String sourceToken,
            String sessionExternalId,
            String customerExternalId,
            Options options){
        this.event = event;
        this.checkpointName = checkpointName;
        this.sourceToken = sourceToken;
        this.sessionExternalId = sessionExternalId;
        this.customerExternalId = customerExternalId;
        this.options = (options == null)?new Options():options;
    }

    public CheckpointRequest(
            Event event,
            String checkpointName,
            String sourceToken,
            String sessionExternalId,
            String customerExternalId,
            Options options,
            String priorCheckpointId){
        this.event = event;
        this.checkpointName = checkpointName;
        this.sourceToken = sourceToken;
        this.sessionExternalId = sessionExternalId;
        this.customerExternalId = customerExternalId;
        this.options = (options == null)?new Options():options;
        this.priorCheckpointId = priorCheckpointId;
    }

    /**
     * Event encapsulating this checkpoint.
     */
    public Event event;

    /**
     * Logical name of this checkpoint.  Examples are "LOGIN", "PURCHASE", "SIGNUP"
     */
    public String checkpointName;

    /**
     * Source Token provided by DodgeBall to the Front End
     */
    public String sourceToken;

    /**
     * Session ID provided by the App
     */
    public String sessionExternalId;

    /**
     * Customer ID provided by the App
     */
    public String customerExternalId;

    /**
     * ID provided by an earlier checkpoint if this query is based off that.
     */
    public String priorCheckpointId;

    /**
     * Optional invocation options
     */
    public Options options;
}
