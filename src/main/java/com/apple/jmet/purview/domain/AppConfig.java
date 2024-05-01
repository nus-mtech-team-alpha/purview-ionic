package com.apple.jmet.purview.domain;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Tolerate;

@Data
@SuperBuilder
@ToString
@Entity
public class AppConfig extends GenericConfig {

    @Column(columnDefinition = "boolean default false")
    private boolean recommend;
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<AppConfigValue> values;

    @Tolerate
    public AppConfig() {
        /* no-arg constructor */ }
}
