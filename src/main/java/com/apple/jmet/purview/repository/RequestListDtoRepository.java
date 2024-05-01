package com.apple.jmet.purview.repository;


import java.util.List;

import org.springframework.data.repository.Repository;

import com.apple.jmet.purview.domain.Request;
import com.apple.jmet.purview.dto.RequestListDto;

public interface RequestListDtoRepository extends Repository<Request, Long> {
    List<RequestListDto> findAllBy();
    List<RequestListDto> findAllByProductIdNotNull();
    List<RequestListDto> findAllByFeatureIdNotNull();
    List<RequestListDto> findAllByRequestorId(Long requestorId);
}
