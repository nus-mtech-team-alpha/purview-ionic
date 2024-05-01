package com.apple.jmet.purview.domain;

import java.io.Serializable;

import org.hibernate.annotations.DynamicUpdate;

import com.apple.jmet.purview.enums.BuildStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Tolerate;

@Data
@Builder
@ToString
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"CODE"})})
@DynamicUpdate
public class Product extends TraceableEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String code;
    private String category;
    private Integer yearStarted;
    private String status;
    @ManyToOne
    private Person owner;
    private String radars;

    @Tolerate
    public Product(){ /* no-arg constructor */ }

    public void setBuildStatus(BuildStatus status) {
        this.status = status.getValue();
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public boolean isNpi() {
        return BuildStatus.NPI.getValue().equalsIgnoreCase(status);
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public boolean isMp() {
        return BuildStatus.MP.getValue().equalsIgnoreCase(status);
    }
}
