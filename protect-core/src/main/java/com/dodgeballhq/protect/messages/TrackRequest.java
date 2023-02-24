package com.dodgeballhq.protect.messages;

/**
 * Input class defining a client Access Request
 */
public class TrackRequest {

    /**
     * Constructor
     *
     * @param event Client context, for example email, transaction amount, product ids, etc.
     * @param sourceToken Session ID information
     * @param sessionExternalId  Session ID provided by the App
     * @param customerExternalId  Customer ID provided by the App
     */
    public TrackRequest(
            Event event,
            String sourceToken,
            String sessionExternalId,
            String customerExternalId){
        this.event = event;
        this.sourceToken = sourceToken;
        this.sessionExternalId = sessionExternalId;
        this.customerExternalId = customerExternalId;
    }

    /**
     * Event encapsulating this checkpoint.
     */
    public Event event;

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

}
