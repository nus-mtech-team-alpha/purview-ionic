package com.apple.jmet.purview.repository;


import java.util.List;

import org.springframework.data.repository.Repository;

import com.apple.jmet.purview.domain.Feature;
import com.apple.jmet.purview.dto.FeatureListDto;

public interface FeatureListDtoRepository extends Repository<Feature, Long> {
    List<FeatureListDto> findAllBy();
}
