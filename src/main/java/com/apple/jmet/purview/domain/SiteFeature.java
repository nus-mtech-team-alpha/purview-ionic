package com.apple.jmet.purview.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"SITE_ID", "FEATURE_ID"})})
public class SiteFeature implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne
    @JoinColumn(nullable = false)
    private Site site;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne
    @JoinColumn(nullable = false)
    private Feature feature;

    @Temporal(TemporalType.DATE)
    private Date dateSupported;
    private String environment;
    private String requestReferenceId;

    @Transient
    private Long siteIdToAdd;
    @Transient
    private Long featureIdToAdd;

    @Tolerate
    public SiteFeature() {
        /* no-arg constructor */ }

}
