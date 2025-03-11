package com.dodgeballhq.protect.messages;

public class NotificationRequest {

        /**
         * Constructor
         */
        public NotificationRequest(){
        }

        /**
         * Constructor
         *
         * @param event Client context, for example email, transaction amount, product ids, etc.
         * @param eventName Name of workflow to execute
         * @param sessionExternalId  Session ID provided by the App
         */
        public NotificationRequest(
                Event event,
                String eventName,
                String sessionExternalId){
            this.event = event;
            this.eventName = eventName;
            this.sessionExternalId = sessionExternalId;
        }

        /**
         * Constructor
         *
         * @param event Client context, for example email, transaction amount, product ids, etc.
         * @param eventName Name of workflow to execute
         * @param sessionExternalId  Session ID provided by the App
         * @param customerExternalId  Customer ID provided by the App
         */
        public NotificationRequest(
                Event event,
                String eventName,
                String sessionExternalId,
                String customerExternalId){
            this.event = event;
            this.eventName = eventName;
            this.sessionExternalId = sessionExternalId;
            this.customerExternalId = customerExternalId;
        }

        /**
         * Event encapsulating this checkpoint.
         */
        public Event event;

        /**
         * Logical name of this checkpoint.  Examples are "LOGIN", "PURCHASE", "SIGNUP"
         */
        public String eventName;

        /**
         * Session ID provided by the App
         */
        public String sessionExternalId;

        /**
         * Customer ID provided by the App
         */
        public String customerExternalId;

    }

