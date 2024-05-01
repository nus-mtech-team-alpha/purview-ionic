package com.apple.jmet.purview.domain;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Tolerate;

@Data
@SuperBuilder
@ToString
@Entity
public class ProductAppConfig extends GenericConfig {
    
    @Tolerate
    public ProductAppConfig() {
        /* no-arg constructor */ }
}
