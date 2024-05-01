package com.apple.jmet.purview.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Environment {

    STAGING("STAGING"),
    PRODUCTION("PRODUCTION");

    @Getter 
    private String value;
    
}
