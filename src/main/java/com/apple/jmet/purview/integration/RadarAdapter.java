package com.apple.jmet.purview.integration;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class RadarAdapter implements ExternalCallable {

    private static final String PREFIX_URI = "rdar://";
    
    public String createTicket(Map<String, String> detailsMap) {
        //TODO: To replace with internal API call
        return PREFIX_URI + RandomStringUtils.randomNumeric(8);
    }

}
