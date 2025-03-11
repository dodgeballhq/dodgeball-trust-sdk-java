package com.dodgeballhq.protect.messages;

public class NotificationResponse {

    /**
     * Constructor
     */
    public NotificationResponse() {
    }

    /**
     * Constructor
     *
     * @param exc as checkpoint response
     */
    public NotificationResponse(Throwable exc) {
        this.success = false;
        StackTraceElement[] stackTraceElement = exc.getStackTrace();
        String stackTrace = stackTraceElement == null ? null : stackTraceElement.toString();
        DodgeballApiError error = new DodgeballApiError(exc.getMessage(), stackTrace);
        this.errors = new DodgeballApiError[]{error};
    }

    /**
     * Constructor
     *
     * @param success Client context, for example email, transaction amount, product ids, etc.
     * @param errors  Checkpoint execution errors
     */
    public NotificationResponse(
            boolean success,
            DodgeballApiError[] errors
    ) {
        this.success = success;
        this.errors = errors;
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
}