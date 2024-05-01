package com.apple.jmet.purview.integration;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class ReferenceGenerator implements ExternalCallable {
    private static final String PREFIX_URI = "";
    
    public String createTicket(Map<String, String> detailsMap) {
        return PREFIX_URI + RandomStringUtils.randomAlphanumeric(8).toUpperCase();
    }
}
