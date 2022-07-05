package com.dodgeballhq.protect.messages;

public class DodgeballVerification {
    public DodgeballVerification()
    {
    }

    public String id;
    public String status;
    public String outcome;
    public VerificationStep[] nextSteps;
    public VerificationStepData   stepData;
}
