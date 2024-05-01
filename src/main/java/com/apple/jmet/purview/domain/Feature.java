package com.apple.jmet.purview.domain;

import java.io.Serializable;

import org.hibernate.annotations.DynamicUpdate;

import com.apple.jmet.purview.enums.RolloutPhase;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
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
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"NAME"})})
@DynamicUpdate
public class Feature extends TraceableEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String rolloutPhase;
    private String radars;

    @Transient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private RolloutPhase rolloutPhaseEnum;

    @Tolerate
    public Feature(){ /* no-arg constructor */ }

    public RolloutPhase getRolloutPhaseEnum(){
        return RolloutPhase.valueOf(rolloutPhase);
    }

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

}
