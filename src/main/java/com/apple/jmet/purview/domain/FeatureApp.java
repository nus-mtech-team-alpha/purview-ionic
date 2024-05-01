package com.apple.jmet.purview.domain;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Tolerate;

@Data
@Builder
@ToString
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"FEATURE_ID", "APP_ID"})})
public class FeatureApp implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne
    @JoinColumn(nullable = false)
    private Feature feature;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne
    @JoinColumn(nullable = false)
    private App app;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeatureAppConfig> featureAppConfigs;

    private String radars;

    @Transient
    private Long featureIdToSave;
    @Transient
    private Long appIdToSave;
    @Transient
    private boolean toRemove;

    @Tolerate
    public FeatureApp() {
        /* no-arg constructor */ }

    
    public String getFeatureAppConfigValue(String configName){
        for (FeatureAppConfig featureAppConfig : featureAppConfigs) {
            if (featureAppConfig.getName().equals(configName) && StringUtils.isNotBlank(featureAppConfig.getVal())) {
                return featureAppConfig.getVal();
            }
        }
        return null;
    }

}
