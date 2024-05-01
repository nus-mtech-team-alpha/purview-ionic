package com.apple.jmet.purview.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ActionStatus {

    OPEN("OPEN"),
    PREPARE("PREPARE"),
    REVIEW("REVIEW"),
    IN_PROGRESS("IN_PROGRESS"),
    REJECTED("REJECTED"),
    CANCELLED("CANCELLED"),
    COMPLETED("COMPLETED");

    @Getter 
    private String value;
    
}
