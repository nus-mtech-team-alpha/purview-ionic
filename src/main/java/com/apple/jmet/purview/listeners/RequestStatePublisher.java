package com.apple.jmet.purview.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apple.jmet.purview.domain.Request;
import com.apple.jmet.purview.enums.RequestStatus;

public class RequestStatePublisher {

    Logger logger = LoggerFactory.getLogger(RequestStatePublisher.class);

    private static final RequestStatePublisher instance = new RequestStatePublisher();

    public static RequestStatePublisher getInstance() {
        return instance;
    }

    public final RequestEventManager events;

    private RequestStatePublisher() {
        this.events = new RequestEventManager(RequestStatus.values());

        // setup listeners
        // this.events.subscribe(RequestStatus.OPEN, new GenerateActionsListener());
        // all status change need email notification, hence subscribed for all events
        // Arrays.asList(RequestStatus.values()).forEach(requestStatus ->
        // this.events.subscribe(requestStatus, new EmailNotificationListener())
        // );
    }

    public void openRequest(Request request) {
        this.events.notify(RequestStatus.OPEN, request);
    }

    public void reviewRequest(Request request) {
        this.events.notify(RequestStatus.REVIEW, request);
    }

    public void processRequest(Request request) {
        this.events.notify(RequestStatus.IN_PROGRESS, request);
    }

    public void rejectRequest(Request request) {
        this.events.notify(RequestStatus.REJECTED, request);
    }

    public void cancelRequest(Request request) {
        this.events.notify(RequestStatus.CANCELLED, request);
    }

    public void completeRequest(Request request) {
        this.events.notify(RequestStatus.COMPLETED, request);
    }
}
