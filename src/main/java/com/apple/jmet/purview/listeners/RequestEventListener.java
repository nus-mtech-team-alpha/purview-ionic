package com.apple.jmet.purview.listeners;

import com.apple.jmet.purview.domain.Request;
import com.apple.jmet.purview.enums.RequestStatus;

public interface RequestEventListener {
    
    void update(RequestStatus requestStatus, Request request);

}
