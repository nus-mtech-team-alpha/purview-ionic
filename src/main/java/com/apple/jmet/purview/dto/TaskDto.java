package com.apple.jmet.purview.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDto {
    String appConfigName;
    @JsonIgnore
    boolean appConfigMulti;
    String appConfigSetValue;
}
