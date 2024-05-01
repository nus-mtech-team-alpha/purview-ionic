package com.apple.jmet.purview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apple.jmet.purview.domain.FeatureApp;

@Repository
public interface FeatureAppRepository extends JpaRepository<FeatureApp, Long> {
    
    List<FeatureApp> findByFeatureId(Long featureId);
    List<FeatureApp> findByAppId(Long appId);
    Optional<FeatureApp> findByFeatureIdAndAppId(Long featureId, Long appId);
    
}
