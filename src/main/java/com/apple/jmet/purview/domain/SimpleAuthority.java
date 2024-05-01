package com.apple.jmet.purview.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class SimpleAuthority {
    private String authority;
}
