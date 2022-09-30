package com.dodgeballhq.protect.messages;

/**
 * Class defining a client Access Response
 */
public class CheckpointResponse {
    /**
     * Constructor
     *
     */
    public CheckpointResponse(){
    }

    /**
     * Constructor
     *
     * @param exc as checkpoint response
     */
    public CheckpointResponse(Throwable exc){
        this.success = false;
        StackTraceElement[] stackTraceElement = exc.getStackTrace();
        String stackTrace = stackTraceElement == null? null: stackTraceElement.toString();
        DodgeballApiError error = new DodgeballApiError(exc.getMessage(), stackTrace);
        this.errors = new DodgeballApiError[]{error};
    }

    /**
     * Constructor
     *
     * @param success Client context, for example email, transaction amount, product ids, etc.
     * @param errors Checkpoint execution errors
     * @param verification Checkpoint verification response
     * @param isTimeout Checkpoint timed out
     */
    public CheckpointResponse(
            boolean success,
            DodgeballApiError[] errors,
            DodgeballVerification verification,
            boolean isTimeout
            ){
        this.success = success;
        this.errors = errors;
        this.verification = verification;
        this.isTimeout = isTimeout;
        this.version = Constants.ApiVersion.V1;
    }

    /**
     * Result of the checkpoint execution
     */
    public boolean success;

    /**
     * Array of errors during checkpoint execution
     */
    public DodgeballApiError[] errors;

    /**
     * API version
     */
    public String version;

    /**
     * DodgeballVerification response
     */
    public DodgeballVerification verification;

    /**
     * Current steps
     */
    public VerificationStepData stepData;

    /**
     * Next Steps
     */
    public VerificationStep[] nextSteps;

    /**
     * Is checkpoint timed out
     */
    public boolean isTimeout;
}
