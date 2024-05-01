package com.apple.jmet.purview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apple.jmet.purview.domain.SiteApp;

@Repository
public interface SiteAppRepository extends JpaRepository<SiteApp, Long> {
    List<SiteApp> findBySiteId(Long siteId);

    List<SiteApp> findBySiteIdAndEnvironment(Long siteId, String environment);

    Optional<SiteApp> findBySiteIdAndAppIdAndEnvironment(Long siteId, Long appId, String environment);
    boolean existsBySiteIdAndAppIdAndEnvironment(Long siteId, Long appId, String environment);
}
