package com.apple.jmet.purview.dto;

import java.util.Date;

public interface RequestListDto {
    String getReferenceId();
    String getSiteCode();
    String getProductCode();
    String getFeatureName();
    String getStatus();
    Date getNeedByDate();
}
