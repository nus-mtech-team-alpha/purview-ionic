package com.apple.jmet.purview.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"SITE_ID", "APP_ID", "ENVIRONMENT"})})
public class SiteApp implements Serializable {

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
    private App app;
    @Column(name = "ENVIRONMENT")
    private String environment;
    private String vip;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SiteAppConfig> siteAppConfigs;

    @Transient
    private Long siteIdToSave;
    @Transient
    private Long appIdToSave;

    @Tolerate
    public SiteApp() {
        /* no-arg constructor */ }

}
