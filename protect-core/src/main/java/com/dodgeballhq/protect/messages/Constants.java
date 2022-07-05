package com.dodgeballhq.protect.messages;

public class Constants {
    public static class ApiVersion {
        public static String V1 = "v1";
    }

    public static class VerificationStatus {
        /**
         * In Process on the server
         */
        public static String PENDING ="PENDING";

        /**
         * Waiting on some action, for example MFA
          */
        public static String BLOCKED ="BLOCKED";

        /**
         * Workflow evaluated successfully
         */
        public static String COMPLETE ="COMPLETE";

        /**
         * Workflow execution failure
         */
        public static String FAILED ="FAILED";
    }

    public static class VerificationOutcome {
        public static String APPROVED = "APPROVED";
        public static String DENIED ="DENIED";
        public static String PENDING ="PENDING";
        public static String ERROR ="ERROR";
    }
}
