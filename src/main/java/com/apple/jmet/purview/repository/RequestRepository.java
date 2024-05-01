package com.apple.jmet.purview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apple.jmet.purview.domain.Request;
import com.apple.jmet.purview.domain.Site;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequestorId(Long requestorId);

    Optional<Request> findByReferenceId(String referenceId);

    Integer countByStatus(String status);

    Integer countByStatusNotIn(List<String> statuses);

    Integer countByStatusAndRequestorId(String status, Long requestorId);

    Optional<Request> findTop1BySiteAndStatusOrderByCompletedDateDesc(Site site, String status);

    List<Request> findByProductId(Long productId);

    List<Request> findByFeatureId(Long featureId);

    List<Request> findBySiteId(Long siteId);

    List<Request> findBySiteIdAndIdNot(Long siteId, Long id);

    List<Request> findBySiteCodeAndProductCode(String siteCode, String productCode);

    List<Request> findBySiteCodeAndFeatureName(String siteCode, String featureName);

    List<Request> findByProductCodeInAndStatusNotIn(List<String> productCodes, List<String> excludeStatus);
}
