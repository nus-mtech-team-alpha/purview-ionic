package com.apple.jmet.purview.repository;


import java.util.List;

import org.springframework.data.repository.Repository;

import com.apple.jmet.purview.domain.Product;
import com.apple.jmet.purview.dto.ProductListDto;

public interface ProductListDtoRepository extends Repository<Product, Long> {
    List<ProductListDto> findAllBy();
}
