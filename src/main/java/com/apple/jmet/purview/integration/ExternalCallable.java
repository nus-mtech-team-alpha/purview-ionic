package com.apple.jmet.purview.integration;

import java.util.Map;

public interface ExternalCallable {
    
    String createTicket(Map<String, String> detailsMap);
}
