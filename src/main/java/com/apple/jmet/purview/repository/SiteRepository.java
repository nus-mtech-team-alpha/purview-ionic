package com.apple.jmet.purview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apple.jmet.purview.domain.Site;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {
    Integer countByStatus(String status);
    List<Site> findByProductCategoriesContaining(String productCategory);
    Optional<Site> findByCode(String code);
}
