package com.apple.jmet.purview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apple.jmet.purview.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryIn(List<String> categories);

    Integer countByStatus(String status);

    Optional<Product> findByCode(String code);
}
