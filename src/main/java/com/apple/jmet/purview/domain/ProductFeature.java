package com.apple.jmet.purview.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"PRODUCT_ID", "FEATURE_ID"})})
public class ProductFeature implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne
    @JoinColumn(nullable = false)
    private Product product;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne
    @JoinColumn(nullable = false)
    private Feature feature;
    
    @Transient
    private Long productIdToSave;
    @Transient
    private Long featureIdToSave;
    
    @Tolerate
    public ProductFeature() {
        /* no-arg constructor */ }

}
