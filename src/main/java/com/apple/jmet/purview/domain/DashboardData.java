package com.apple.jmet.purview.domain;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class DashboardData implements Serializable {
    
    private int activeRequestsCount;
    private int activeSitesCount;
    private int npiProductsCount;
    private int mpProductsCount;
    
}
