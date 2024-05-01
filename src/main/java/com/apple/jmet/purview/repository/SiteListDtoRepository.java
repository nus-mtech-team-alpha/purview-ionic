package com.apple.jmet.purview.repository;


import java.util.List;

import org.springframework.data.repository.Repository;

import com.apple.jmet.purview.domain.Site;
import com.apple.jmet.purview.dto.SiteListDto;

public interface SiteListDtoRepository extends Repository<Site, Long> {
    List<SiteListDto> findAllBy();
}
