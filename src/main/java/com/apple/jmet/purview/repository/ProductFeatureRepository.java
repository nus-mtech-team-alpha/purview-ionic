package com.apple.jmet.purview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apple.jmet.purview.domain.ProductFeature;

@Repository
public interface ProductFeatureRepository extends JpaRepository<ProductFeature, Long> {
    
    List<ProductFeature> findByProductId(Long productId);
    List<ProductFeature> findByFeatureId(Long featureId);
    Optional<ProductFeature> findByProductIdAndFeatureId(Long productId, Long featureId);
    
}
