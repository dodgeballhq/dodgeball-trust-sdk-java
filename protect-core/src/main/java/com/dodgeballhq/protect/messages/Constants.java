package com.dodgeballhq.protect.messages;

public class Constants {
    public static class ApiVersion {
        public final static String V1 = "v1";
    }

    public static class VerificationStatus {
        /**
         * In Process on the server
         */
        public final static String PENDING ="PENDING";

        /**
         * Waiting on some action, for example MFA
          */
        public final static String BLOCKED ="BLOCKED";

        /**
         * Workflow evaluated successfully
         */
        public final static String COMPLETE ="COMPLETE";

        /**
         * Workflow execution failure
         */
        public final static String FAILED ="FAILED";
    }

    public static class VerificationOutcome {
        public final static String APPROVED = "APPROVED";
        public final static String DENIED ="DENIED";
        public static final String PENDING ="PENDING";
        public final static String ERROR ="ERROR";
    }
}
