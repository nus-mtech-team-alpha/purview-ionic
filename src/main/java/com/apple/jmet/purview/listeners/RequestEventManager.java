package com.apple.jmet.purview.listeners;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apple.jmet.purview.domain.Request;
import com.apple.jmet.purview.enums.RequestStatus;

public class RequestEventManager {

    Logger logger = LoggerFactory.getLogger(RequestEventManager.class);

    Map<RequestStatus, List<RequestEventListener>> listenersMap = new EnumMap<>(RequestStatus.class);

    public RequestEventManager(RequestStatus... requestStatusList) {
        for (RequestStatus requestStatus : requestStatusList) {
            this.listenersMap.put(requestStatus, new ArrayList<>());
        }
    }

    public void subscribe(RequestStatus requestStatus, RequestEventListener listener) {
        List<RequestEventListener> subscribers = listenersMap.get(requestStatus);
        subscribers.add(listener);
    }

    public void unsubscribe(RequestStatus requestStatus, RequestEventListener listener) {
        List<RequestEventListener> subscribers = listenersMap.get(requestStatus);
        subscribers.remove(listener);
    }

    public void notify(RequestStatus requestStatus, Request request) {
        List<RequestEventListener> subscribers = listenersMap.get(requestStatus);
        for (RequestEventListener subscriber : subscribers) {
            subscriber.update(requestStatus, request);
        }
    }
}
