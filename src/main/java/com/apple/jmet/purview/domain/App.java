package com.apple.jmet.purview.domain;

import java.io.Serializable;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(uniqueConstraints={@UniqueConstraint(columnNames = {"INTERNAL_NAME"})})
@DynamicUpdate
public class App extends TraceableEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "INTERNAL_NAME")
    private String internalName;
    private String externalName;
    private String status;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne
    private Person dev;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne
    private Person epm;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne
    private Person sre;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Person> backupSres;

    @OneToMany(cascade = CascadeType.ALL)
    private List<AppConfig> appConfigs;

    private String category;

    @Transient
    private long devIdToSave;
    @Transient
    private long epmIdToSave;
    @Transient
    private long sreIdToSave;

    @Tolerate
    public App() {
        /* no-arg constructor */ }
}
