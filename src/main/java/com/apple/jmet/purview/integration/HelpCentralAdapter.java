package com.apple.jmet.purview.integration;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class HelpCentralAdapter implements ExternalCallable {
    
    private static final String PREFIX_URI = "CHG";

    public String createTicket(Map<String, String> detailsMap) {
        //TODO: To replace with internal API call
        return PREFIX_URI + StringUtils.leftPad(RandomStringUtils.randomNumeric(6), 9);
    }

}
