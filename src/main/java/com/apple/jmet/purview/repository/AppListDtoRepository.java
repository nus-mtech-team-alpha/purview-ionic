package com.apple.jmet.purview.repository;


import java.util.List;

import org.springframework.data.repository.Repository;

import com.apple.jmet.purview.domain.App;
import com.apple.jmet.purview.dto.AppListDto;

public interface AppListDtoRepository extends Repository<App, Long> {
    List<AppListDto> findAllBy();
    List<AppListDto> findAllBySreId(Long sreId);
}
