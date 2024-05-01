package com.apple.jmet.purview.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apple.jmet.purview.domain.Request;
import com.apple.jmet.purview.enums.RequestStatus;

public class EmailNotificationListener implements RequestEventListener {

    Logger logger = LoggerFactory.getLogger(EmailNotificationListener.class);

    @Override
    public void update(RequestStatus requestStatus, Request request) {
        //TODO: Implement
        switch(requestStatus){
            case OPEN:
                logger.debug("Send email for OPEN status...");
                break;
            case CANCELLED:
                logger.debug("Send email for CANCELLED status...");
                break;
            case COMPLETED:
                logger.debug("Send email for COMPLETED status...");
                break;
            case IN_PROGRESS:
                logger.debug("Send email for IN_PROGRESS status...");
                break;
            case REJECTED:
                logger.debug("Send email for REJECTED status...");
                break;
            case REVIEW:
                logger.debug("Send email for REVIEW status...");
                break;
            default:
                logger.error("Unsupported state provided");
                break;
        }
    }
    
    
}
