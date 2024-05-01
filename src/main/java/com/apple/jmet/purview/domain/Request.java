package com.apple.jmet.purview.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.DynamicUpdate;

import com.apple.jmet.purview.enums.RequestStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
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
@DynamicUpdate
public class Request extends TraceableEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String referenceId;
    private String description;
    private String radar;
    private String status;
    private int version;

    @Temporal(TemporalType.DATE)
    private Date needByDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne
    private Product product;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne
    private Feature feature;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne
    @JoinColumn(nullable = false)
    private Site site;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne
    @JoinColumn(nullable = false)
    private Person requestor;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToMany
    private Set<App> apps;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Action> actions;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedDate;

    // Form fields only
    @Transient
    private long siteIdToSave;
    @Transient
    private long productIdToSave;
    @Transient
    private long featureIdToSave;
    @Transient
    private long userId;

    @Tolerate
    public Request() {
        /* no-arg constructor */ }

    public void setRequestStatus(RequestStatus status) {
        this.status = status.getValue();
    }

    public void incrementVersion() {
        this.version++;
    }

    public boolean notCancelledNorRejected() {
        return !RequestStatus.CANCELLED.getValue().equals(this.status)
            && !RequestStatus.REJECTED.getValue().equals(this.status);
    }
}
