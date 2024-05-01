package com.apple.jmet.purview.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BuildStatus {

    NPI("NPI"),
    MP("MP"),
    DECOMMISSION("DECOMMISSION");

    @Getter 
    private String value;
    
}
