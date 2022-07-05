package com.dodgeballhq.protect.messages;

public class CheckpointResponse {
    public CheckpointResponse(){
    }

    public CheckpointResponse(Throwable exc){
        this.success = false;
        StackTraceElement[] stackTraceElement = exc.getStackTrace();
        String stackTrace = stackTraceElement == null? null: stackTraceElement.toString();
        DodgeballApiError error = new DodgeballApiError(exc.getMessage(), stackTrace);
        this.errors = new DodgeballApiError[]{error};
    }
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

    public boolean success;
    public DodgeballApiError[] errors;
    public String version;
    public DodgeballVerification verification;

    public boolean isTimeout;
}
