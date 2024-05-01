package com.apple.jmet.purview.domain;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Tolerate;

@Data
@SuperBuilder
@ToString
@MappedSuperclass
public class GenericConfig implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Column(columnDefinition = "boolean default false")
    private boolean multi;
    @Temporal(TemporalType.DATE)
    private Date dateAdded;
    private String val;

    @Transient
    private boolean toDelete;

    @Tolerate
    public GenericConfig() {
        /* no-arg constructor */ }
}
