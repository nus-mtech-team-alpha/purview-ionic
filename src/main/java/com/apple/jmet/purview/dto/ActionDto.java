package com.apple.jmet.purview.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionDto {
    String appInternalName;
    String environment;
    boolean toIgnore;
    List<TaskDto> tasks;
}
