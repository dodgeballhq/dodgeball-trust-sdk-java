package com.dodgeballhq.protect.messages;

import java.util.Arrays;
import java.util.Objects;

public class DodgeballVerification {
    public DodgeballVerification()
    {
    }

    public String id;
    public String status;
    public String outcome;
    public VerificationStep[] nextSteps;
    public VerificationStepData   stepData;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DodgeballVerification that = (DodgeballVerification) o;
        return Objects.equals(id, that.id) && Objects.equals(status, that.status) && Objects.equals(outcome, that.outcome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, outcome);
    }
}
