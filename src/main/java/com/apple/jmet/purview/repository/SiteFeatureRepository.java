package com.apple.jmet.purview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apple.jmet.purview.domain.SiteFeature;

@Repository
public interface SiteFeatureRepository extends JpaRepository<SiteFeature, Long> {
    List<SiteFeature> findBySiteId(Long siteId);
    List<SiteFeature> findByFeatureId(Long featureId);
    Optional<SiteFeature> findBySiteIdAndFeatureId(Long siteId, Long featureId);
    Optional<SiteFeature> findBySiteIdAndFeatureIdAndEnvironmentAndDateSupportedNotNull(Long siteId, Long featureId, String environment);
    Optional<SiteFeature> findByRequestReferenceId(String requestReferenceId);
    void deleteByRequestReferenceId(String requestReferenceId);
}
