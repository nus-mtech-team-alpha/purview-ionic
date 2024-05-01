package com.apple.jmet.purview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apple.jmet.purview.domain.ProductApp;

@Repository
public interface ProductAppRepository extends JpaRepository<ProductApp, Long> {
    
    List<ProductApp> findByProductId(Long productId);
    List<ProductApp> findByAppId(Long appId);
    Optional<ProductApp> findByProductIdAndAppId(Long productId, Long appId);
    
}
