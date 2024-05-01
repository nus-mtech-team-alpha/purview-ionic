package com.apple.jmet.purview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apple.jmet.purview.domain.SiteProduct;

@Repository
public interface SiteProductRepository extends JpaRepository<SiteProduct, Long> {
    List<SiteProduct> findBySiteId(Long siteId);
    List<SiteProduct> findByProductId(Long productId);
    Optional<SiteProduct> findBySiteIdAndProductId(Long siteId, Long productId);
    Optional<SiteProduct> findBySiteIdAndProductIdAndEnvironmentAndDateSupportedNotNull(Long siteId, Long productId, String environment);
    Optional<SiteProduct> findByRequestReferenceId(String requestReferenceId);
    void deleteByRequestReferenceId(String requestReferenceId);
}
