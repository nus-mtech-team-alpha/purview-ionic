package com.apple.jmet.purview.domain;

import java.io.Serializable;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Site extends TraceableEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String code;
    private String company;
    private String country;
    private String city;
    private String category;
    private String infra;
    private String status;
    private String productCategories;

    @Tolerate
    public Site() {
        /* no-arg constructor */ }
}
