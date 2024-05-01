package com.apple.jmet.purview.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ActionCategory {

    DEPLOYMENT("DEPLOYMENT"),
    UI_CONFIG("UI_CONFIG"),
    DB_CONFIG("DB_CONFIG");

    @Getter
    private String value;

}
