package com.apple.jmet.purview.dto;

import com.apple.jmet.purview.domain.Person;

public interface AppListDto {
    Long getId();
    String getInternalName();
    String getExternalName();
    String getStatus();
    Person getSre();
}
