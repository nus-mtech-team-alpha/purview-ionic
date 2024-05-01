package com.apple.jmet.purview.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.apple.jmet.purview.enums.ActionStatus;
import com.apple.jmet.purview.enums.Environment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Tolerate;

@Data
@Builder
@ToString
@Entity
public class Action extends TraceableEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String category;
    private String description;
    private String radar;
    private String status;
    @ManyToOne
    @JoinColumn(nullable = false)
    private App app;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Task> tasks;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(nullable = false)
    private Request request;
    private String environment;
    private String crNumbers;
    private String radars;

    private boolean verified;
    private String verifiedBy;
    @Temporal(TemporalType.TIMESTAMP)
    private Date verifiedDate;
    private boolean toIgnore;

    @Transient
    private Person verifier;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Transient
    private Site site;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Transient
    private Product product;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Transient
    private Feature feature;

    @Tolerate
    public Action() {
        /* no-arg constructor */ }

    public void setActionStatus(ActionStatus status) {
        this.status = status.getValue();
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public boolean isForProduction() {
        return Environment.PRODUCTION.getValue().equals(environment);
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public boolean isForStaging() {
        return Environment.STAGING.getValue().equals(environment);
    }

    public void updateCrAndRadarIds(Action similarAction) {
        if (similarAction != null) {
            if (StringUtils.isNotEmpty(similarAction.getCrNumbers())) {
                this.setCrNumbers(this.combineIds(this.crNumbers, similarAction.getCrNumbers()));
            }
            if (StringUtils.isNotEmpty(similarAction.getRadars())) {
                this.setRadars(this.combineIds(this.radars, similarAction.getRadars()));
            }
        }
    }

    private String combineIds(String sourceIds, String addIds) {
        sourceIds = StringUtils.defaultString(sourceIds);
        addIds = StringUtils.defaultString(addIds);
        Set<String> sourceIdsSet = new HashSet<>(Arrays.asList(StringUtils.split(sourceIds, ",")));
        Set<String> addIdsSet = new HashSet<>(Arrays.asList(StringUtils.split(addIds, ",")));
        sourceIdsSet.addAll(addIdsSet);
        return sourceIdsSet.stream().reduce((a, b) -> a + "," + b).orElse("");
    }

    public void updateVerifiedInfo(boolean verified, String verifiedBy) {
        this.verified = verified;
        if(verified){
            this.verifiedBy = verifiedBy;
            this.verifiedDate = new Date();
        }else{
            this.verifiedBy = null;
            this.verifiedDate = null;
        }
    }
}
