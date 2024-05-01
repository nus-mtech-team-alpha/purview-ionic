package com.apple.jmet.purview.domain;

import java.io.Serializable;
import java.util.Date;

import com.apple.jmet.purview.enums.ActionStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Tolerate;

@Data
@Builder
@ToString
@Entity
public class Task extends TraceableEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String description;
    @ManyToOne
    @JoinColumn(nullable = false)
    private AppConfig appConfig;
    @Column(name = "APP_CONFIG_VALUE")
    private String appConfigSetValue;
    private String status;
    private Integer durationInHours;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Person assignee;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(nullable = false)
    private Action action;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedDate;
    
    @Tolerate
    public Task() {
        /* no-arg constructor */ }

    public void setTaskStatus(ActionStatus status) {
        this.status = status.getValue();
    }

}
