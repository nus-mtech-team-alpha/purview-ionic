package com.apple.jmet.purview.domain;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"PRODUCT_ID", "APP_ID"})})
public class ProductApp implements Serializable {

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
    private App app;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductAppConfig> productAppConfigs;

    private String radars;

    @Transient
    private Long productIdToSave;
    @Transient
    private Long appIdToSave;
    @Transient
    private boolean toRemove;

    @Tolerate
    public ProductApp() {
        /* no-arg constructor */ }

    
    public String getProductAppConfigValue(String configName){
        for (ProductAppConfig productAppConfig : productAppConfigs) {
            if (productAppConfig.getName().equals(configName) && StringUtils.isNotBlank(productAppConfig.getVal())) {
                return productAppConfig.getVal();
            }
        }
        return null;
    }

}
