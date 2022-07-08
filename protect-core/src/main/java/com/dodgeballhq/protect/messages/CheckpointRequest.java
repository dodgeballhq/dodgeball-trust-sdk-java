package com.dodgeballhq.protect.messages;

public class CheckpointRequest {
    public static class Options{
        public Options(
        ){
        }

        public Options(boolean sync, int timeout, String webhook){
            this.timeout = timeout;
            this.sync = sync;
            this.webhook = webhook;
        }

        public int timeout = -1;
        public boolean sync = true;
        public String webhook;
    }

    public CheckpointRequest(){
        this.options = new Options();
    }

    public CheckpointRequest(
            Event event,
            String checkpointName,
            String dodgeballId,
            String userId){
        this.event = event;
        this.checkpointName = checkpointName;
        this.dodgeballId = dodgeballId;
        this.userId = userId;
        this.options = new Options();
    }

    public CheckpointRequest(
            Event event,
            String checkpointName,
            String dodgeballId,
            String userId,
            Options options){
        this.event = event;
        this.checkpointName = checkpointName;
        this.dodgeballId = dodgeballId;
        this.userId = userId;
        this.options = (options == null)?new Options():options;
    }

    public CheckpointRequest(
            Event event,
            String checkpointName,
            String dodgeballId,
            String userId,
            Options options,
            String priorCheckpointId){
        this.event = event;
        this.checkpointName = checkpointName;
        this.dodgeballId = dodgeballId;
        this.userId = userId;
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
     * Session ID provided by DodgeBall on SDK initialization
     */
    public String dodgeballId;

    /**
     * Permanent ID for this customer.
     */
    public String userId;

    /**
     * ID provided by an earlier checkpoint if this query is based off that.
     */
    public String priorCheckpointId;

    /**
     * Optional invocation options
     */
    public Options options;
}
