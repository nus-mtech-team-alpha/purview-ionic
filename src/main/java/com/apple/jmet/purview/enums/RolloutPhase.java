package com.apple.jmet.purview.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RolloutPhase {

    NEW("NEW","NEW"),
    DECK("DECK","DECK"),
    STAGING("STAGING","STG"),
    PRODUCTION("PRODUCTION","PROD");

    @Getter
    private String value;

    @Getter
    private String shortForm;

}
