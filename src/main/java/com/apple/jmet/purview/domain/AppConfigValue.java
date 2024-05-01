package com.apple.jmet.purview.domain;

import java.io.Serializable;
import java.util.Date;

import com.apple.jmet.purview.enums.RolloutPhase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Tolerate;

@Data
@Builder
@ToString
@Entity
public class AppConfigValue implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String val;
    @Temporal(TemporalType.DATE)
    private Date dateAdded;
    private String rolloutPhase;

    @Transient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private RolloutPhase rolloutPhaseEnum;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(nullable = false)
    private AppConfig appConfig;

    @Tolerate
    public AppConfigValue() {
        /* no-arg constructor */ }

    public void setRolloutPhase(RolloutPhase rolloutPhase) {
        this.rolloutPhase = rolloutPhase.getValue();
    }

    public void promoteConfig(RolloutPhase newRolloutPhase) {
        switch (newRolloutPhase) {
            case DECK:
                if (RolloutPhase.NEW.getValue().equals(this.rolloutPhase)) {
                    this.setRolloutPhase(newRolloutPhase);
                }
                break;
            case STAGING:
                if (RolloutPhase.NEW.getValue().equals(this.rolloutPhase)
                        || RolloutPhase.DECK.getValue().equals(this.rolloutPhase)) {
                    this.setRolloutPhase(newRolloutPhase);
                }
                break;
            default:
                this.setRolloutPhase(newRolloutPhase);
                break;
        }
    }

    @JsonIgnore
    public boolean isProduction() {
        return RolloutPhase.PRODUCTION.getValue().equals(rolloutPhase);
    }

    @JsonIgnore
    public boolean isStaging() {
        return RolloutPhase.STAGING.getValue().equals(rolloutPhase);
    }

    @JsonIgnore
    public boolean isDeck() {
        return RolloutPhase.DECK.getValue().equals(rolloutPhase);
    }

    @JsonIgnore
    public boolean isNew() {
        return RolloutPhase.NEW.getValue().equals(rolloutPhase);
    }

    public RolloutPhase getRolloutPhaseEnum(){
        return RolloutPhase.valueOf(rolloutPhase);
    }
}
