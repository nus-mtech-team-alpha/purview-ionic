package com.apple.jmet.purview.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apple.jmet.purview.domain.DashboardData;
import com.apple.jmet.purview.enums.RequestStatus;
import com.apple.jmet.purview.repository.ProductRepository;
import com.apple.jmet.purview.repository.RequestRepository;
import com.apple.jmet.purview.repository.SiteRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing {@link com.apple.jmet.purview.domain.DashboardData}.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@Transactional
public class DashboardResource {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private SiteRepository siteRepository;

    @GetMapping("/dashboard/stats")
    public DashboardData getDashboardData(){

        int activeRequestsCount = this.requestRepository.countByStatusNotIn(
                List.of(
                    RequestStatus.COMPLETED.getValue(), 
                    RequestStatus.CANCELLED.getValue(),
                    RequestStatus.REJECTED.getValue()
                ));
        int activeSitesCount = this.siteRepository.countByStatus("ACTIVE");
        int npiProductsCount = this.productRepository.countByStatus("NPI");
        int mpProductsCount = this.productRepository.countByStatus("MP");

        return DashboardData.builder()
                    .activeRequestsCount(activeRequestsCount)
                    .activeSitesCount(activeSitesCount)
                    .npiProductsCount(npiProductsCount)
                    .mpProductsCount(mpProductsCount)
                    .build();
    }
}
